(ns intro
  (:require [main :as main]
            [crux.api :as crux]
            ))

(comment
    (def crux-node (main/start-crux!))

    (def manifest
      {:crux.db/id :manifest
       :pilot-name "Johanna"
       :id/rocket "SB002-sol"
       :id/employee "22910x2"
       :badges "SETUP"
       :cargo ["stereo" "gold fish" "slippers" "secret note"]})

    (crux/submit-tx crux-node [[:crux.tx/put manifest]])

    (crux/entity (crux/db crux-node) :manifest)

    (main/stop-crux! crux-node)
  )
