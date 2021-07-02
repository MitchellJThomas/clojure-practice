(ns main
  (:require [crux.api :as crux]
            [clojure.java.io :as io]))

(defn start-crux! []
  (letfn [(kv-store [dir]
            {:kv-store {:crux/module 'crux.rocksdb/->kv-store
	                :db-dir      (io/file dir)
                        :sync?       true}})]
    (crux/start-node
     {:crux/tx-log              (kv-store "data/dev/tx-log")
      :crux/document-store      (kv-store "data/dev/doc-store")
      :crux/index-store         (kv-store "data/dev/index-store")
      ;; optional:
      ;; :crux.lucene/lucene-store {:db-dir "data/dev/lucene-dir"}
      ;; :crux.http-server/server  {:port 9999}
      })))

(defn easy-ingest
  "Uses Crux put transaction to add a vector of documents to a specified
  node"
  [node docs]
  (crux/submit-tx node
                  (vec (for [doc docs]
                         [:crux.tx/put doc]))))

(defn stop-crux! [crux-node]
  (.close crux-node))

