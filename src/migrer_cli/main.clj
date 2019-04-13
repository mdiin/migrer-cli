(ns migrer-cli.main
  (:gen-class)
  (:require
   [migrer.core]
   [cli-matic.core :refer [run-cmd]]))

(defn- conn
  [opts]
  (select-keys opts #{:dbtype :dbname :host :port :user :password}))

(defn init!
  [opts]
  (let [c (conn opts)]
    (migrer.core/init! c {:migrer/table-name (:metadata-table opts)})))

(defn migrate!
  [opts]
  (let [c (conn opts)]
    (migrer.core/migrate! c {:migrer/table-name (:metadata-table opts)
                             :migrer/root (:path opts)})))

(def cli-configuration
  {:app {:command "migrer"
         :description "Database migrations that play nice with VCS"
         :version "1"}
   :global-opts [{:option "dbtype"
                  :short "t"
                  :type #{"postgresql" "sap"}
                  :as "Database type"
                  :default "postgresql"}
                 {:option "dbname"
                  :short "d"
                  :type :string
                  :as "Database name"}
                 {:option "host"
                  :short "h"
                  :as "Host name"
                  :type :string
                  :default "localhost"}
                 {:option "port"
                  :short "p"
                  :type :int
                  :default 5432}
                 {:option "user"
                  :type :string
                  :short "U"}
                 {:option "password"
                  :short "P"
                  :type :string}
                 {:option "metadata-table"
                  :short "m"
                  :as "Table where migrer stores metadata"
                  :type :string
                  :default "migrations"}]
   :commands [{:command "init"
               :short "i"
               :description "Initialise migrations table in database"
               :runs init!}
              {:command "migrate"
               :short "m"
               :opts [{:option "path"
                       :as "Path to migrations"
                       :type :string
                       :default "migrations"}]
               :description "Perform migrations"
               :runs migrate!}]})

(defn -main
  [& args]
  (run-cmd args cli-configuration))
