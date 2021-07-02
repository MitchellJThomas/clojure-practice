(ns mercury
  (:require [main :as main]
            [crux.api :as crux]
            ))


(defn filter-type
  [crux-db type]
  (crux/q crux-db
        {:find '[name]
         :where '[[e :type t]
                  [e :common-name name]]
         :args [{'t type}]}))

(defn filter-appearance
  [crux-db description]
  (crux/q crux-db
        {:find '[name IUPAC]
         :where '[[e :common-name name]
                  [e :IUPAC-name IUPAC]
                  [e :appearance ?appearance]]
         :args [{'?appearance description}]}))

(comment
  ;; https://nextjournal.com/crux-tutorial/datalog-queries
  (def crux-node (main/start-crux!))
  (crux/entity (crux/db crux-node) :manifest)

  (def data
    [{:crux.db/id :commodity/Pu
      :common-name "Plutonium"
      :type :element/metal
      :density 19.816
      :radioactive true}

     {:crux.db/id :commodity/N
      :common-name "Nitrogen"
      :type :element/gas
      :density 1.2506
      :radioactive false}

     {:crux.db/id :commodity/CH4
      :common-name "Methane"
      :type :molecule/gas
      :density 0.717
      :radioactive false}

     {:crux.db/id :commodity/Au
      :common-name "Gold"
      :type :element/metal
      :density 19.300
      :radioactive false}

     {:crux.db/id :commodity/C
      :common-name "Carbon"
      :type :element/non-metal
      :density 2.267
      :radioactive false}

     {:crux.db/id :commodity/borax
      :common-name "Borax"
      :IUPAC-name "Sodium tetraborate decahydrate"
      :other-names ["Borax decahydrate" "sodium borate" "sodium tetraborate" "disodium tetraborate"]
      :type :mineral/solid
      :appearance "white solid"
      :density 1.73
      :radioactive false}])

  (main/easy-ingest crux-node data)

  (crux/q (crux/db crux-node)
          '{:find [element]
            :where [[element :type :element/metal]]})

  (=
   (crux/q (crux/db crux-node)
           '{:find [element]
             :where [[element :type :element/metal]]})

   (crux/q (crux/db crux-node)
           {:find '[element]
            :where '[[element :type :element/metal]]})

   (crux/q (crux/db crux-node)
           (quote
            {:find [element]
             :where [[element :type :element/metal]]})))

  (crux/q (crux/db crux-node)
          '{:find [name]
            :where [[e :type :element/metal]
                    [e :common-name name]]})

  (crux/q (crux/db crux-node)
          '{:find [name rho]
            :where [[e :density rho]
                    [e :common-name name]]})

  (crux/q (crux/db crux-node)
          {:find '[name]
           :where '[[e :type t]
                    [e :common-name name]]
           :args [{'t :element/metal}]})

  (filter-type (crux/db crux-node) :element/metal)

  (filter-appearance (crux/db crux-node) "white solid")

  (crux/submit-tx crux-node [[:crux.tx/put
                              {:crux.db/id :manifest
                               :pilot-name "Johanna"
                               :id/rocket "SB002-sol"
                               :id/employee "22910x2"
                               :badges ["SETUP" "PUT" "DATALOG-QUERIES"]
                               :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])

  (crux/entity (crux/db crux-node) :manifest)
  (crux/entity (crux/db crux-node #inst "2021-06-15") :manifest)

  (main/stop-crux! crux-node)
  )
