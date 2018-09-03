(defproject diosbot "0.1.0-SNAPSHOT"
  :description "DiosBOT: An IRC Bot, mostly for mtgoon"
  :url "none"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [irclj "0.5.0-alpha4"]
                 [clj-http "3.9.0"]
                 [slingshot "0.12.2"]
                 [cheshire "5.8.0"]
                 [clojure-ini "0.0.2"]
                 [io.replikativ/konserve "0.5.0-beta3"]
                 [clj-time "0.14.4"]]
  :main diosbot.core)
