(ns duct.database.datalog.datahike-test
  (:require [clojure.test :refer [deftest is]]
            [datahike.api :as d]
            [datahike.core :as dcore]
            [duct.database.datalog :as datalog]
            [duct.database.datalog.datahike]
            [integrant.core :as ig]))

(deftest hierarchy-test
  (ig/load-hierarchy)
  (is (isa? :duct.database.datalog/datahike :duct.database/datalog))
  (is (isa? :duct.database/datalog :duct/database)))

(deftest datalog-protocol-test
  (let [conn (ig/init-key :duct.database.datalog/datahike
                          {:store {:backend :memory, :id (random-uuid)}})]
    (datalog/transact! conn [{:db/ident :test/name
                              :db/valueType :db.type/string
                              :db/cardinality :db.cardinality/one}
                             {:test/name "Alice"}
                             {:test/name "Bob"}])
    (is (= #{["Alice"] ["Bob"]}
           (datalog/q '[:find ?name :where [?e :test/name ?name]]
                      (datalog/db conn))))
    (d/release conn)))

(deftest init-halt-test
  (let [conn (ig/init-key :duct.database.datalog/datahike
                          {:store {:backend :memory, :id (random-uuid)}})]
    (is (dcore/conn? conn))
    (d/transact conn [{:db/ident :test/name
                       :db/valueType :db.type/string
                       :db/cardinality :db.cardinality/one}
                      {:test/name "Alice"}
                      {:test/name "Bob"}])
    (is (= #{["Alice"] ["Bob"]}
           (d/q '[:find ?name :where [?e :test/name ?name]] @conn)))
    (ig/halt-key! :duct.database.datalog/datahike conn)
    (is (thrown-with-msg? clojure.lang.ExceptionInfo
                          #"Connection has been released\." @conn))))

(deftest init-suspend-resume-test
  (let [config {:store {:backend :memory, :id (random-uuid)}}
        conn   (ig/init-key :duct.database.datalog/datahike config)]
    (ig/suspend-key! :duct.database.datalog/datahike conn)
    (let [new-conn (ig/resume-key :duct.database.datalog/datahike
                                  config config conn)]
      (is (identical? new-conn conn))
      (ig/suspend-key! :duct.database.datalog/datahike new-conn)
      (let [new-config {:store {:backend :memory, :id (random-uuid)}}
            newer-conn (ig/resume-key :duct.database.datalog/datahike
                                      new-config config new-conn)]
        (is (not (identical? newer-conn new-conn)))
        (is (dcore/conn? newer-conn))
        (is (thrown-with-msg? clojure.lang.ExceptionInfo
                              #"Connection has been released\." @new-conn))
        (ig/halt-key! :duct.database.datalog/datahike newer-conn)))))
