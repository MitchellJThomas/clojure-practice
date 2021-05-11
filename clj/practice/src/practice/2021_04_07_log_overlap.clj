(ns practice.2021-04-07-log-overlap
  (:require [java-time :as jt]
            [clojure.test :refer [is deftest]]))

(defn get-logs
  "Get an iterator of simulated logs lines. Each log items is a map with :start and :end instances."
  ([] (get-logs (jt/local-date-time)))
  ([start-time] (let [add-delta #(jt/plus %1 (jt/minutes (rand-int 60)))
                      start-times (iterate add-delta start-time) 
                      end-time-shift #(jt/minutes (rand-int 120))]
                  (map (fn [s] {:start s :end (jt/plus s (end-time-shift))}) start-times))))

(defn count-occurs-after [sorted-timestamp-map log-item-timestamp]
  (count (subseq sorted-timestamp-map >= log-item-timestamp)))

(defn count-occurs-before [sorted-timestamp-map log-item-timestamp]
  (count (subseq sorted-timestamp-map <= log-item-timestamp)))

(defn count-overlaps [sorted-start-timestamps sorted-end-timestamps log-item]
  (-
   (count-occurs-after sorted-start-timestamps (:end log-item))
   (count-occurs-before sorted-end-timestamps (:start log-item)))
  )

(defn update-count-map [count-map log-item-ts]
  (update count-map log-item-ts (fn [v] (inc (or v 0)))))

(defn sorted-timestamp-map [timestamps]
  (reduce update-count-map (sorted-map) timestamps))


(def daytime {:start (jt/local-date-time 2021 04 20  8  0  0)
              :end   (jt/local-date-time 2021 04 20 21  0  0)})
(def morning {:start (jt/local-date-time 2021 04 20  0  0  0)
              :end   (jt/local-date-time 2021 04 20  7 30  0)})
(def sunrise {:start (jt/local-date-time 2021 04 20  7 30  1)
              :end   (jt/local-date-time 2021 04 20  8 30  0)})

(def midday  {:start (jt/local-date-time 2021 04 20  8 30  1)
              :end   (jt/local-date-time 2021 04 20 20 30  0)})
(def sunset  {:start (jt/local-date-time 2021 04 20 20 30  1)
              :end   (jt/local-date-time 2021 04 20 21 30  0)})
(def night   {:start (jt/local-date-time 2021 04 20 21 30  1)
              :end   (jt/local-date-time 2021 04 20 23 59 59)})

(deftest overlapping
  (let [end-o-day (sorted-timestamp-map [(:end daytime)])
        start-o-day (sorted-timestamp-map [(:start daytime)])
        fullday-end-map (sorted-timestamp-map (map :end [morning sunrise midday sunset night]))
        fullday-start-map (sorted-timestamp-map (map :start [morning sunrise midday sunset night]))]
    (is (= 0 (count-overlaps start-o-day end-o-day morning)) "morning starts and ends before day starts")
    (is (= 1 (count-occurs-after end-o-day (:start sunrise))) "sunrise has left overlap with daytime")
    (is (= 1 (count-occurs-after end-o-day (:start midday))) "midday is totally overlapped by daytime")
    (is (= 1 (count-occurs-after end-o-day (:start sunset))) "sunset has right overlap with daytime")
    (is (= 0 (count-occurs-after end-o-day (:start night))) "night is after daytime")
    (is (= 3 (count-occurs-after fullday-end-map (:start daytime)))) "daytime overlaps with sunrise, midday and sunset")
  )

(comment
  (let [start-date (:start daytime)
        fullday (sorted-logs [morning sunrise midday sunset night])
                sub-full (subseq fullday > start-date)]
    (count sub-full)
    )
  
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
  
   
  )
