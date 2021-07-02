(ns neptune
  (:require [main :as main]
            [crux.api :as crux]
            ))


(comment
  ;; https://nextjournal.com/crux-tutorial/bitemporality
  (def crux-node (main/start-crux!))
  (crux/entity (crux/db crux-node) :manifest)

  (crux/entity (crux/db crux-node) :consumer/RJ29sUU)

  (crux/submit-tx
   crux-node
   [[:crux.tx/put
     {:crux.db/id :consumer/RJ29sUU
      :consumer-id :RJ29sUU
      :first-name "Jay"
      :last-name "Rose"
      :cover? true
      :cover-type :Full}
     #inst "2114-12-03"]])

  (crux/submit-tx
   crux-node
   [[:crux.tx/put	;; (1)
     {:crux.db/id :consumer/RJ29sUU
      :consumer-id :RJ29sUU
      :first-name "Jay"
      :last-name "Rose"
      :cover? true
      :cover-type :Full}
     #inst "2113-12-03" ;; Valid time start
     #inst "2114-12-03"] ;; Valid time end

    [:crux.tx/put  ;; (2)
     {:crux.db/id :consumer/RJ29sUU
      :consumer-id :RJ29sUU
      :first-name "Jay"
      :last-name "Rose"
      :cover? true
      :cover-type :Full}
     #inst "2112-12-03"
     #inst "2113-12-03"]

    [:crux.tx/put	;; (3)
     {:crux.db/id :consumer/RJ29sUU
      :consumer-id :RJ29sUU
      :first-name "Jay"
      :last-name "Rose"
      :cover? false}
     #inst "2112-06-03"
     #inst "2112-12-02"]

    [:crux.tx/put ;; (4)
     {:crux.db/id :consumer/RJ29sUU
      :consumer-id :RJ29sUU
      :first-name "Jay"
      :last-name "Rose"
      :cover? true
      :cover-type :Promotional}
     #inst "2111-06-03"
     #inst "2112-06-03"]])

  (crux/entity (crux/db crux-node #inst "2112-12-01") :consumer/RJ29sUU)

  (crux/q (crux/db crux-node #inst "2114-01-01")
          '{:find [cover type]
            :where [[e :consumer-id :RJ29sUU]
                    [e :cover? cover]
                    [e :cover-type type]]})

  (crux/q (crux/db crux-node #inst "2111-07-03")
        '{:find [cover type]
          :where [[e :consumer-id :RJ29sUU]
                  [e :cover? cover]
                  [e :cover-type type]]})

  (crux/q (crux/db crux-node #inst "2112-07-03")
        '{:find [cover type]
          :where [[e :consumer-id :RJ29sUU]
                  [e :cover? cover]
                  [e :cover-type type]]})

  (crux/submit-tx crux-node
                  [[:crux.tx/put
                    {:crux.db/id :manifest
                     :pilot-name "Johanna"
                     :id/rocket "SB002-sol"
                     :id/employee "22910x2"
                     :badges ["SETUP" "PUT" "DATALOG-QUERIES" "BITEMP"]
                     :cargo ["stereo" "gold fish" "slippers" "secret note"]}]])

  (main/stop-crux! crux-node)

  )
