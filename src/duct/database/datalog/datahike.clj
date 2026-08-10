(ns duct.database.datalog.datahike
  (:require [datahike.api :as d]
            [duct.database.datalog :as datalog]
            [integrant.core :as ig]))

(extend-protocol datalog/Connection
  datahike.connector.Connection
  (-db [conn] @conn)
  (-transact! [conn tx-data] (d/transact conn tx-data)))

(extend-protocol datalog/Database
  datahike.db.DB
  (-q [db query inputs] (apply d/q query db inputs)))

(defmethod ig/init-key :duct.database.datalog/datahike [_ config]
  (when-not (d/database-exists? config)
    (try
      (d/create-database config)
      (catch clojure.lang.ExceptionInfo ex
        (when-not (= :db-already-exists (:type (ex-data ex)))
          (throw ex)))))
  (d/connect config))

(defmethod ig/halt-key! :duct.database.datalog/datahike [_ conn]
  (d/release conn))

(defmethod ig/suspend-key! :duct.database.datalog/datahike [_ _])

(defmethod ig/resume-key :duct.database.datalog/datahike
  [_ old-config new-config conn]
  (if (= old-config new-config)
    conn
    (do (ig/halt-key! :duct.database.datalog/datahike conn)
        (ig/init-key :duct.database.datalog/datahike new-config))))
