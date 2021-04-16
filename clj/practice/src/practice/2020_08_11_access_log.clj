(ns clojure-practice.2020-08-11.access-log
  (:require clojure.string))
;; You have access log from a host serving foo.com traffic, with
;; information about request timestamps, UserIdentifiers, the product page
;; url and Status Code of the request.
;; $ tail -f access.log
;; 02 Mar 2016 21:55:05,379    User:user1  URL:canonT2i.html StatusCode:200
;; 02 Mar 2016 21:55:06,379    User:user2  URL:nikonD5000.html StatusCode:200
;; 02 Mar 2016 21:55:07,379    User:user1  URL:tripod.html StatusCode:200
;; 02 Mar 2016 21:55:07,380    User:user3  URL:canonT2i.html StatusCode:200
;; 02 Mar 2016 21:55:07,381    User:user2  URL:tripod.html StatusCode:200
;; 02 Mar 2016 21:55:08,381    User:user3  URL:tripod.html StatusCode:200
;; Given such an access log, you have to find the most popular page
;; sequence consisting of 2 pages across all users.
;; In this sample log, you see that the most popular page sequence is
;; canonT2i.html -> tripod.html
;; This was the page sequence that was visited by user1 and user3.

(def log-chunk
"02 Mar 2016 21:55:05,379    User:user1  URL:canonT2i.html StatusCode:200
 02 Mar 2016 21:55:06,379    User:user2  URL:nikonD5000.html StatusCode:200
 02 Mar 2016 21:55:07,379    User:user1  URL:tripod.html StatusCode:200
 02 Mar 2016 21:55:07,380    User:user3  URL:canonT2i.html StatusCode:200
 02 Mar 2016 21:55:07,381    User:user2  URL:tripod.html StatusCode:200
 02 Mar 2016 21:55:08,381    User:user3  URL:tripod.html StatusCode:200
")

(def log-chunk-2
"02 Mar 2016 21:55:05,379    User:user1  URL:canonT2i.html StatusCode:200
 02 Mar 2016 21:55:06,379    User:user2  URL:nikonD5000.html StatusCode:200
 02 Mar 2016 21:55:07,379    User:user1  URL:tripod.html StatusCode:200
 02 Mar 2016 21:55:07,380    User:user3  URL:canonT2i.html StatusCode:200
 02 Mar 2016 21:55:07,381    User:user2  URL:tripod.html StatusCode:200
 02 Mar 2016 21:55:08,381    User:user3  URL:tripod.html StatusCode:200
 02 Mar 2016 21:55:08,382    User:user4  URL:nikonD500.html StatusCode:200
 02 Mar 2016 21:55:08,383    User:user4  URL:canonT2i.html StatusCode:200
 02 Mar 2016 21:55:08,384    User:user4  URL:tripod.html StatusCode:200
")

(defn split-page-visits [log-line]
  (let [[_ user url] (re-matches #".*User:(\w+)\s+URL:(.*) .*" log-line)]
    [user url]))

(defn parse-user-urls [log-chunk]
  (map split-page-visits (clojure.string/split-lines log-chunk)))

(defn build-user-sequences [user-urls]
  (reduce (fn [user-map user-map-tuple]
            (let [user (first user-map-tuple) url (second user-map-tuple)]
              (if-let [user-urls (user-map user)]
                (assoc user-map user (cons url user-urls))
                (assoc user-map user [url]))
              ))
          {} user-urls))


(defn partition-user-sequences [user-sequences]
  (reduce-kv (fn [user-sequences user sequences]
                    (assoc user-sequences user (partition 2 1 sequences))) {} user-sequences))

(defn count-unique-sequences [user-sequences]
  (reduce (fn [m x]
            (if-let [count (m x)]
              (assoc m x (inc count))
              (assoc m x 1)
              )
            )
          {}
          (if (map? user-sequences) (vals user-sequences) user-sequences)))

(defn sort-unique-sequences [count-unique-sequences]
  (into (sorted-map-by (fn [key1 key2]
                         (compare [(count-unique-sequences key2) key2]
                                  [(count-unique-sequences key1) key1])))) count-unique-sequences)


(defn tuplfy [sequence]
  (mapcat #(if (<= (count %1) 2) [%1] (partition 2 1 %1)) sequence))


(defn most-popular-page-sequence [log-chunk]
  (-> log-chunk
      parse-user-urls
      build-user-sequences
      vals
      tuplfy
      count-unique-sequences
      sort-unique-sequences
      first))

(comment
  (most-popular-page-sequence log-chunk)
  (most-popular-page-sequence log-chunk-2)
)
