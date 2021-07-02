(ns saturn
  (:require [main :as main]
            [crux.api :as crux]
            ))

(defn stock-check
  [crux-db company-id item]
  {:result (crux/q crux-db
                   {:find '[name funds stock]
                    :where ['[e :company-name name]
                            '[e :credits funds]
                            ['e item 'stock]]
                    :args [{'e company-id}]})
   :item item})


(defn format-stock-check
  [{:keys [result item] :as stock-check}]
  (for [[name funds commod] result]
    (str "Name: " name ", Funds: " funds ", " item " " commod)))

(comment
  ;; https://nextjournal.com/crux-tutorial/match
  (def crux-node (main/start-crux!))
  (crux/entity (crux/db crux-node) :manifest)
  (crux/entity (crux/db crux-node #inst "2021-06-15") :manifest)

  (def data [{:crux.db/id :gold-harmony
    :company-name "Gold Harmony"
    :seller? true
    :buyer? false
    :units/Au 10211
    :credits 51}

   {:crux.db/id :tombaugh-resources
    :company-name "Tombaugh Resources Ltd."
    :seller? true
    :buyer? false
    :units/Pu 50
    :units/N 3
    :units/CH4 92
    :credits 51}

   {:crux.db/id :encompass-trade
    :company-name "Encompass Trade"
    :seller? true
    :buyer? true
    :units/Au 10
    :units/Pu 5
    :units/CH4 211
    :credits 1002}

   {:crux.db/id :blue-energy
    :seller? false
    :buyer? true
    :company-name "Blue Energy"
    :credits 1000}])

  (main/easy-ingest crux-node data)

  (crux/entity (crux/db crux-node) :encompass-trade)
  (stock-check (crux/db crux-node) :encompass-trade :units/CH4)

  (crux/submit-tx
   crux-node
   [[:crux.tx/match
     :blue-energy
     {:crux.db/id :blue-energy
      :seller? false
      :buyer? true
      :company-name "Blue Energy"
      :credits 1000}]
    [:crux.tx/put
     {:crux.db/id :blue-energy
      :seller? false
      :buyer? true
      :company-name "Blue Energy"
      :credits 900
      :units/CH4 10}]

    [:crux.tx/match
     :tombaugh-resources
     {:crux.db/id :tombaugh-resources
      :company-name "Tombaugh Resources Ltd."
      :seller? true
      :buyer? false
      :units/Pu 50
      :units/N 3
      :units/CH4 92
      :credits 51}]
    [:crux.tx/put
     {:crux.db/id :tombaugh-resources
      :company-name "Tombaugh Resources Ltd."
      :seller? true
      :buyer? false
      :units/Pu 50
      :units/N 3
      :units/CH4 82
      :credits 151}]])

  (format-stock-check (stock-check (crux/db crux-node) :tombaugh-resources :units/CH4))
  (format-stock-check (stock-check (crux/db crux-node) :blue-energy :units/CH4))

  (crux/submit-tx
   crux-node
   [[:crux.tx/match
     :gold-harmony
     {:crux.db/id :gold-harmony
      :company-name "Gold Harmony"
      :seller? true
      :buyer? false
      :units/Au 10211
      :credits 51}]
    [:crux.tx/put
     {:crux.db/id :gold-harmony
      :company-name "Gold Harmony"
      :seller? true
      :buyer? false
      :units/Au 211
      :credits 51}]

    [:crux.tx/match
     :encompass-trade
     {:crux.db/id :encompass-trade
      :company-name "Encompass Trade"
      :seller? true
      :buyer? true
      :units/Au 10
      :units/Pu 5
      :units/CH4 211
      :credits 100002}]
    [:crux.tx/put
     {:crux.db/id :encompass-trade
      :company-name "Encompass Trade"
      :seller? true
      :buyer? true
      :units/Au 10010
      :units/Pu 5
      :units/CH4 211
      :credits 1002}]])

  (format-stock-check (stock-check (crux/db crux-node) :gold-harmony :units/Au))
  (format-stock-check (stock-check (crux/db crux-node) :encompass-trade :units/Au))

  (crux/submit-tx
   crux-node
   [[:crux.tx/put
     {:crux.db/id :manifest
      :pilot-name "Johanna"
      :id/rocket "SB002-sol"
      :id/employee "22910x2"
      :badges ["SETUP" "PUT" "DATALOG-QUERIES" "MATCH"]
      :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])

  (crux/q (crux/db crux-node)
          {:find '[belongings]
           :where '[[e :cargo belongings]]
           :args [{'belongings "secret note"}]})

  (main/stop-crux! crux-node)

  )
