(ns diosbot.db
  (:require [konserve.filestore :refer [new-fs-store]]
            [konserve.core :as k]
            [clojure.core.async :refer [<!!, <!, go]]
            [clojure.string :refer [join]]
            [clj-time.core :as t]
            [clj-time.coerce :as c]))

(def store (delay (<!! (new-fs-store "diosdb.tmp"))))

(defn cache-value [cache key value]
  (let [kk (join "" [cache key])]
    (go
      (<! (k/assoc-in @store [kk :value] value))
      (<! (k/assoc-in @store [kk :date] (c/to-long (t/now))))))
  value)

(defn get-cache-value-timeout [cache key]
  (let [kk (join "" [cache key])]
    (if-let [date (c/from-long (<!! (k/get-in @store [kk :date])))]
      (if (t/after? (t/now) (t/plus date (t/days 1)))
        nil
        (<!! (k/get-in @store [kk :value])))
      nil)))

