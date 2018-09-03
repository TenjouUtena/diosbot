(ns diosbot.vars
  (:require [clojure-ini.core :refer [read-ini]]))


(def ini (atom nil))

(defn get-config []
  (if @ini
    @ini
    (reset! ini (read-ini "dios.ini" :keywordize? true))))

(def server (delay (get (get-config) :server)))
(def port (delay (if-let [x (get (get-config) :port)] (read-string x) 6667)))
(def nickname (delay (get (get-config) :nickname)))
(def channel (delay (get (get-config) :channel)))
