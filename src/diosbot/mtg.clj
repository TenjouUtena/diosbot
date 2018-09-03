(ns diosbot.mtg
  (:require [cheshire.core :refer :all]
            [clj-http.client :as client]
            [diosbot.db :refer []]
            [irclj.core :as i]
            [slingshot.slingshot :refer :all]
            [diosbot.db :refer [get-cache-value-timeout cache-value]]
            [clojure.string :refer [starts-with? lower-case upper-case] :as s]))

(defn choose_face [card_obj text]
  (let [face1 (first (:card_faces card_obj))
        face2 (nth (:card_faces card_obj) 1)]
    (cond
      (starts-with? (lower-case (:name face1))
                    (lower-case text))
      face1
      (starts-with? (lower-case (:name face2))
                    (lower-case text))
      face2
      :default
      face1)))

(defn extract_card_data [card_obj text]
  (let [face (if (:card_faces card_obj)
               (choose_face card_obj text)
               card_obj)
        [name cost power tough loyality type]
        ((juxt :name :mana_cost :power :toughness :loyalty :type_line) face)
        text (s/replace (:oracle_text face) #"\n" "  ")
        [price uri] ((juxt :usd :scryfall_uri) card_obj)
        rarity (upper-case (first (:rarity card_obj)))
        pt (if power (format "(%s/%s) " power tough) "")
        loy (if loyality (format "(%s) " loyality) "")]
    [name uri text rarity price type cost pt loy]))

(defn call_card_query [query]
  (try+
   (parse-string (:body (client/get "https://api.scryfall.com/cards/search"
                                    {:query-params {:q query
                                                    :unique "cards"}}
                                    {:as :json})) true)
   (catch [:status 404] {:keys [request-time headers body]}
     {:notfound true})))

(defn run_card_query [query]
  (or
   (get-cache-value-timeout "mtg" query)
   (cache-value "mtg" query (call_card_query query))))

(defn format_card [card_data text]
  (let [[name uri text rarity price type cost pt loy] (extract_card_data card_data text)]
    (format "%s (%s) %s %s%s%s: %s $%s %s"
            name rarity
            type pt loy cost
            text price uri)))

(defn do_mtg [irc channel nick command text debug?]
  (let [resp (run_card_query (lower-case text))]
    (if (:notfound resp)
      (i/message irc channel (format "%s, not found." nick))
      (try+ (let [card_data (first (:data resp))]
              (if debug? (clojure.pprint/pprint card_data))
              (i/message irc channel (format_card card_data text)))
            (catch Object _
              (do (.printStackTrace (:throwable &throw-context))
                  (i/message irc channel "Something Stupid Happened.")))))))
