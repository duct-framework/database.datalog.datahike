(ns duct.database.datalog.datahike
  (:require [datahike.api :as d]
            [integrant.core :as ig]))

(defmethod ig/init-key :duct.database.datalog/datahike [_ config]
  (when-not (d/database-exists? config)
    (d/create-database config))
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
