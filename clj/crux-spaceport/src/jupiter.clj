(ns jupiter
  (:require [main :as main]
            [crux.api :as crux]
            ))

(comment
  ;; https://nextjournal.com/crux-tutorial/delete
  (def crux-node (main/start-crux!))
  (crux/entity (crux/db crux-node) :manifest)


  (crux/submit-tx crux-node
                  [[:crux.tx/put {:crux.db/id :kaarlang/clients
                                  :clients [:encompass-trade]}
                    #inst "2110-01-01T09"
                    #inst "2111-01-01T09"]

                   [:crux.tx/put {:crux.db/id :kaarlang/clients
                                  :clients [:encompass-trade :blue-energy]}
                    #inst "2111-01-01T09"
                    #inst "2113-01-01T09"]

                   [:crux.tx/put {:crux.db/id :kaarlang/clients
                                  :clients [:blue-energy]}
                    #inst "2113-01-01T09"
                    #inst "2114-01-01T09"]

                   [:crux.tx/put {:crux.db/id :kaarlang/clients
                                  :clients [:blue-energy :gold-harmony :tombaugh-resources]}
                    #inst "2114-01-01T09"
                    #inst "2115-01-01T09"]])

  (count (crux/entity-history
     (crux/db crux-node #inst "2116-01-01T09")
     :kaarlang/clients
     :desc
     {:with-docs true}))

    (crux/entity-history
     (crux/db crux-node)
     :manifest
     :desc
     {:with-docs true})

    (crux/submit-tx 
     crux-node
     [[:crux.tx/delete :kaarlang/clients #inst "2110-01-01" #inst "2116-01-01"]])

    (count (crux/entity-history
     (crux/db crux-node #inst "2115-01-01T08")
     :kaarlang/clients
     :desc
     {:with-docs? true}))

  (main/stop-crux! crux-node)
  )
