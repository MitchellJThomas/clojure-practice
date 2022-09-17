(ns db
  (:require [clojure.java.io :as io]
            [xtdb.api :as xt]))


(def xtdb-node)

(defn start-xtdb! []
  (letfn [(kv-store [dir]
            {:kv-store {:xtdb/module 'xtdb.rocksdb/->kv-store
                        :db-dir (io/file dir)
                        :sync? true}})]
    ;; This env var relates to these issues when running on a Mac (which I do)
    ;; https://github.com/xtdb/xtdb-in-a-box/blob/d7d56f546e6c63565709bbfd8fc3f97499a4f846/java/Makefile#L7
    ;; https://github.com/xtdb/xtdb/blob/e2f51ed99fc2716faa8ad254c0b18166c937b134/core/src/xtdb/hash.clj#L12
    ;; And is somewhat related to
    ;; https://github.com/xtdb/xtdb/issues/1518
    (def xtdb-node (xt/start-node
                     {:xtdb/tx-log (kv-store "data/dev/tx-log")
                      :xtdb/document-store (kv-store "data/dev/doc-store")
                      :xtdb/index-store (kv-store "data/dev/index-store")}))))

(defn stop-xtdb! []
  (.close xtdb-node))

(defn q [query & args]
  (apply xt/q (xt/db xtdb-node) query args))


(defn load-artifacts! []
    (let [artifacts [
                   {
                    :xt/id 0
                    :spdx/document-id "abc123"
                    :spdx/version "SPDX-2.2"
                    :spdx/document-name "Document for Product A"
                    :spdx/document-namespace "https://github.com/MitchellJThomas/clojure-practice/clj/xtdb"
                    :spdx/package-name "Package A"
                    }
                   ]]
    (xt/submit-tx xtdb-node (for [doc artifacts] [::xt/put doc]))
    (xt/sync xtdb-node))  
)

(defn load-peeps! []  
  (let [peeps [{
                :xt/id -1
                :name "Tracy Anderson"                
                :role :senor-director
                :group :integrations
                :department :rnd
                :company :help-systems
                }
               {
                :xt/id -2
                :name "Matt Bresnan"
                :role :executive-vice-president
                :department :rnd
                :company :help-systems
                :reports-to -20
                },
               {
                :xt/id -3
                :name "Andrew Wagner"
                :role :vice-president
                :department :rnd
                :reports-to -5
                :company :tripwire
                },
               {
                :xt/id -4
                :name "Mitch Thomas"
                :role :senior-architect
                :department :rnd
                :reports-to -3
                :company :tripwire
                },
               {
                :xt/id -5
                :name "Subhajit Bagshee"
                :role :president
                :company :tripwire             
                },
               {
                :xt/id -6
                :name "Matt Reynolds"
                :role :architect
                :department :rnd
                :reports-to -4
                :company :tripwire                
                },
               {
                :xt/id -7
                :name "Andrew Graham"
                :role :architect
                :department :rnd
                :reports-to -4
                :company :tripwire                
                },
               {
                :xt/id -8
                :name "Nathan Stoltenberg"
                :role :intern
                :department :rnd
                :reports-to -4
                :company :tripwire
                },
               {
                :xt/id -9
                :name "Adam Simantel"
                :role :manager
                :department :rnd
                :reports-to -3
                :company :tripwire
                },
               {
                :xt/id -10,
                :name "Yung"
                :role :manager
                :department :rnd
                :reports-to -9
                :company :tripwire
                },
               {
                :xt/id -11,
                :name "Johnny"
                :role :ux-designer
                :department :rnd
                :reports-to -10
                :company :tripwire
                },
               {
                :xt/id -12,
                :name "Andrew"
                :role :ux-researcher
                :department :rnd
                :reports-to -10
                :company :tripwire
                },
               {
                :xt/id -13,
                :name "Jodah"
                :role :ux-designer
                :department :rnd
                :reports-to -10
                :company :tripwire
                },
               {
                :xt/id -14,
                :name "Guy"
                :role :principle-software-engineer
                :department :rnd
                :reports-to -9
                :company :tripwire
                },
               {
                :xt/id -15,
                :name "Chris Wilkins"
                :role :senior-technical-writer
                :department :rnd
                :reports-to -9
                :company :tripwire
                },              
               {
                :xt/id -16,
                :name "Tracy Anderson"
                :role :director
                :department :rnd
                :company :help-systems
                :reports-to -2
                },              
               {
                :xt/id -17,
                :name "Erick Lichtas"
                :role :associate-director
                :department :architecture
                :company :help-systems
                :reports-to -22
                },
               {
                :xt/id -18,
                :name "Bob Erdman"
                :role :manager
                :department :rnd
                :company :help-systems
                :reports-to -16
                :notes "Data loke"
                },              
               {
                :xt/id -19,
                :name "Alfredo Rodriguez"
                :role :associate-vice-president
                :department :mergeers-and-acquisitions
                :company :help-systems
                :reports-to -20
                },
               {
                :xt/id -20,
                :name "John Grancarich"
                :role :executive-vice-president
                :department :product-management
                :company :help-systems
                :reports-to -20
                },
               {
                :xt/id -21,
                :name "Kate Bolseth"
                :role :ceo
                :company :help-systems            
                },
               {
                :xt/id -22,
                :name "Steve Luebbe"
                :role :cheif-architect
                :company :help-systems
                :department :architecture
                :business-unit :none
                :reports-to -2
                },
               {
                :xt/id -23,
                :name "Joel Cruise"
                :role :qa-director
                :company :help-systems
                :department :rnd
                :reports-to -16
                },
               {
                :xt/id -24,
                :name "Mark Young"
                :role :ux-manager
                :company :help-systems
                :department :rnd
                :reports-to -16
                },
               {
                :xt/id -25,
                :name "Johnathan Strom"
                :role :qa
                :company :help-systems
                :department :rnd
                :reports-to -22
                },
               
               ]]    
    (xt/submit-tx xtdb-node (for [doc peeps] [::xt/put doc]))
    (xt/sync xtdb-node)
    )    
  )

(comment
  ;; use C-q to stop smartparents from creating paired quote
  
  (start-xtdb!)

  (load-peeps!)

  ;; All entites by id
  (= (q '{:find [id] :where [[_ :xt/id id]]})
     (q '{:find [e] :where [[e :xt/id]]})
     )
  
  ;; Two level of direct reports starting and Andrew W.
    (q '{:find [name1 name2]
       :in [boss-name]
       :where [[e :name name1]
               [b2 :name name2]
               [e :reports-to b2]
               [b2 :reports-to b1]
               [b1 :name boss-name]] }
       "Andrew Wagner")

    (q '{:find [name1 name2]
         :in [boss-name]
         :where [[e1 :name name1]
                 [e2 :name name2]
                 [e1 :reports-to e2]
                 [e2 :reports-to e3]
                 [e3 :name boss-name]] }
       "Subhajit Bagshee")
  
  (q '{:find [name] :where [[e :name name]
                            [e :company #{:tripwire :cisco}]
                               [e :department :rnd]]}) 
  
  )
