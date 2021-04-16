(ns practice.2021-04-07-log-overlap
  (:require [java-time :as jt]))

(defn get-logs
  "Get an iterator of simulated logs lines. Each log items is a map with :start and :end instances."
  ([] (get-logs (jt/local-date-time)))
  ([start-time] (let [add-delta #(jt/plus %1 (jt/minutes (rand-int 60)))
                      start-times (iterate add-delta start-time) 
                      end-time-shift #(jt/minutes (rand-int 120))]
                  (map (fn [s] {:start s :end (jt/plus s (end-time-shift))}) start-times))))

(comment
  ;; java-time fun
  (jt/local-time 10)
  (jt/offset-date-time 2021 10)
  (jt/with-zone (jt/zoned-date-time 2021 10) "UTC")
  (jt/system-clock)
  (jt/with-clock (jt/system-clock "UTC") (jt/zoned-date-time 2021 10 2))
  (jt/instant)
  (def now (jt/local-date-time))
  (jt/max (jt/local-date now) (jt/local-date 2015 9 20) (jt/local-date 2015 9 28) (jt/local-date 2015 9 1))

  (take 10 (get-logs))
  (map :end (take 10 (get-logs)))

  (def sm (sorted-map 1 :old-one 8 :b 2 :c 3 :d 4 :e 5 :f 1 :one))
  sm
  (subseq sm >= 5)

  (def sorted-end-times (sorted-map))
  (def logs (take 40 (get-logs)))
  logs
  (defn inc-end-time [map end-time]
    (update map end-time (fn [v] (inc (or v 0))))
    )
  (reduce inc-end-time sorted-end-times (map :end logs))
   
  )
