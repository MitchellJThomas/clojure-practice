(ns pluto
  (:require [main :as main]
            [crux.api :as crux]
            ))

(comment
  ;; https://nextjournal.com/crux-tutorial/put
  (def crux-node (main/start-crux!))

  (crux/submit-tx crux-node
                  [[:crux.tx/put
                    {:crux.db/id :commodity/Pu
                     :common-name "Plutonium"
                     :type :element/metal
                     :density 19.816
                     :radioactive true}]

                   [:crux.tx/put
                    {:crux.db/id :commodity/N
                     :common-name "Nitrogen"
                     :type :element/gas
                     :density 1.2506
                     :radioactive false}]

                   [:crux.tx/put
                    {:crux.db/id :commodity/CH4
                     :common-name "Methane"
                     :type :molecule/gas
                     :density 0.717
                     :radioactive false}]])

  (crux/entity (crux/db crux-node) :commodity/Pu)

  (crux/submit-tx crux-node
                  [[:crux.tx/put
                    {:crux.db/id :stock/Pu
                     :commod :commodity/Pu
                     :weight-ton 21 }
                    #inst "2115-02-13T18"]

                   [:crux.tx/put
                    {:crux.db/id :stock/Pu
                     :commod :commodity/Pu
                     :weight-ton 23 }
                    #inst "2115-02-14T18"]

                   [:crux.tx/put
                    {:crux.db/id :stock/Pu
                     :commod :commodity/Pu
                     :weight-ton 22.2 }
                    #inst "2115-02-15T18"]

                   [:crux.tx/put
                    {:crux.db/id :stock/Pu
                     :commod :commodity/Pu
                     :weight-ton 24 }
                    #inst "2115-02-18T18"]

                   [:crux.tx/put
                    {:crux.db/id :stock/Pu
                     :commod :commodity/Pu
                     :weight-ton 24.9 }
                    #inst "2115-02-19T18"]])

  (crux/submit-tx crux-node
                  [[:crux.tx/put
                    {:crux.db/id :stock/N
                     :commod :commodity/N
                     :weight-ton 3 }
                    #inst "2115-02-13T18"
                    #inst "2115-02-19T18"]

                   [:crux.tx/put
                    {:crux.db/id :stock/CH4
                     :commod :commodity/CH4
                     :weight-ton 92 }
                    #inst "2115-02-15T18"
                    #inst "2115-02-19T18"]])

  (crux/entity (crux/db crux-node) :manifest)
  (crux/entity (crux/db crux-node #inst "2115-02-14") :stock/Pu)
  (crux/entity (crux/db crux-node #inst "2115-02-18") :stock/Pu)

  (crux/submit-tx crux-node
                  [[:crux.tx/put
                    {:crux.db/id :manifest
                     :pilot-name "Johanna"
                     :id/rocket "SB002-sol"
                     :id/employee "22910x2"
                     :badges ["SETUP" "PUT"]
                     :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])

  (crux/entity (crux/db crux-node) :manifest)
  (crux/entity (crux/db crux-node #inst "2021-06-15") :manifest)

  (main/stop-crux! crux-node)
  )
