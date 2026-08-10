(ns duct.database.datalog.datahike-test
  (:require [clojure.test :refer [deftest is]]
            [datahike.api :as d]
            [datahike.core :as dcore]
            [duct.database.datalog.datahike]
            [integrant.core :as ig]))

(deftest init-halt-test
  (let [conn (ig/init-key :duct.database.datalog/datahike
                          {:backend :memory, :id (random-uuid)})]
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
