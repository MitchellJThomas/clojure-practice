(ns explore
  (:import (java.net URLDecoder))
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(def server "https://confluence.tripwire.com")
(def rest-api (str server "/rest/api"))

(defn rest-get
  ([url]
   (rest-get url nil))

  ([uri opts]
   (let [handler (fn
                   [body status resp]
                   (if (< 199 status 300)
                     (assoc resp :body (json/read-str body))
                     resp)
                   )]
     (rest-get uri opts handler)
     )
   )
  ([url opts handler]
   (let [personal-access-token "abc123"
         options (conj {:user-agent "mthomas-clojure/0.0.1"
                        :oauth-token personal-access-token} opts)
         {:keys [body status] :as resp} @(http/get url options)]
     (handler body status resp)
     )
   )
  )

(defn get-attachments-for-space
  [server space-key]
  (let [content-url (str server "/rest/api/content")]
    (->>
     ;; TODO only getting the first 200 children, will definitely need to get more
     (get-in (rest-get content-url
                       {:query-params {:spaceKey space-key
                                       :expand "children"
                                       :limit 200
                                       :start 0
                                       }})
             [:body "results"])
     (map #(get-in % ["children" "_expandable" "attachment"]))
     (map #(str server %))
     ;; TODO only getting the first 100 attachments, might need to chunk this up
     (map #(get-in (rest-get % {:query-params {:limit 100
                                               :start 0}}) [:body "results"]))
     (remove empty?)
     (flatten)
     ;; remove those attachments that will migrate
     (remove #(get (set ["application/drawio" "image/png" "image/jpeg"]) (get-in % ["metadata" "mediaType"])))
     )
    )
  )

(defn file-from-url
  [url]
  (-> url
      (io/as-url)
      .getPath
      (URLDecoder/decode "UTF-8")
      (string/split #"/")
      last))

(defn download-url-as-file
  [url]
  (let [filename (file-from-url url)
        handler (fn [body status resp]
                  (if (< 199 status 300)
                    (do
                      (if (.exists (io/as-file filename))
                        (assoc resp :download-message (str "File " filename " exists, skipping"))
                        (do
                          (with-open [reader body
                                      writer (io/output-stream filename)]
                            (io/copy reader writer))
                          (assoc resp :filename filename))
                        )
                      )
                    resp))]
    (rest-get url {:as :stream} handler)))

(comment
  (def c (:body (rest-get (str rest-api "/content")
                          {:query-params {:spaceKey "architecture"
                                          :expand "children"
                                          :limit 200
                                          :start 0
                                          }})))

  (clojure.pprint/pprint c)


  (def uri2 "/download/attachments/121148968/Tripwire%20SCM%20SaaS%20%20Workshop.pdf")
  (def uri1 "/download/attachments/59868963/ActiveMQ%20Training%20Day%20Two-20181002%202003-2.mp4?version=1&modificationDate=1538677298977&api=v2")
  (download-url-as-file (str server uri1))

  (def e (get-attachments-for-space server "architecture"))
  (->> e
       (map #(str server (get-in % ["_links" "download"])))
       (map download-url-as-file)
       )

  
  

  ) ;; comment
