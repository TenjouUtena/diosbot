
(ns diosbot.core
  (:require [diosbot.mtg :refer [do_mtg]]
            [irclj.core :as i]
            [diosbot.vars :refer [server, port, nickname, channel]])
  (:gen-class))

(defn handle_message [irc channel nick message]
  (let [[command text] (clojure.string/split message #" " 2)]
    (case command
      "@mtg"
      (do_mtg irc channel nick command text false)
      "@mtgdebug"
      (do_mtg irc channel nick command text true)
      "@quit"
      (if (= (clojure.string/lower-case nick) "tenjoum")
        (i/quit irc))
      :default)))

(defn handle_callback [irc args]
  (if (= (:command args) "PRIVMSG")
    (handle_message irc (:target args) (:nick args) (:text args))))

(defn -main []
  (let [irc (i/connect @server @port @nickname :callbacks {:privmsg handle_callback})]
  (i/join irc @channel)))
