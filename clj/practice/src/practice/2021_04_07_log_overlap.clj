(ns practice.2021-04-07-log-overlap
  (:require [java-time :as jt]))

(defn get-logs
  "Get an iterator of simulated logs lines. Each log items is a map with :start and :end instances."
  ([] (get-logs (jt/local-date-time)))
  ([start-time] (let [add-delta #(jt/plus %1 (jt/minutes (rand-int 60)))
                      start-times (iterate add-delta start-time) 
                      end-time-shift #(jt/minutes (rand-int 120))]
                  (map (fn [s] {:start s :end (jt/plus s (end-time-shift))}) start-times))))

(def test-data-zero-overlaps
  [{:start (jt/local-date-time 2021 04 20 20 00 00)
    :end   (jt/local-date-time 2021 04 20 20 10 01)}
   {:start (jt/local-date-time 2021 04 20 20 10 02)
    :end   (jt/local-date-time 2021 04 20 20 20 01)}
   {:start (jt/local-date-time 2021 04 20 20 20 02)
    :end   (jt/local-date-time 2021 04 20 20 30 01)}
   ])

(def test-data-two-overlaps-1
  [{:start (jt/local-date-time 2021 04 20 20 00 00)
    :end   (jt/local-date-time 2021 04 20 20 10 01)}
   {:start (jt/local-date-time 2021 04 20 20 10 00)
    :end   (jt/local-date-time 2021 04 20 20 20 01)}
   {:start (jt/local-date-time 2021 04 20 20 20 00)
    :end   (jt/local-date-time 2021 04 20 20 30 01)}
   ])

(defn add-new-item [sorted-thing log-item]
  (conj sorted-thing (:end log-item)))

(defn count-overlaps [sorted-thing log-item]
  (let [start-date (:start log-item)]
    (count (subseq sorted-thing >= start-date))))

(defn add-new-item-map [s-map log-item]
  (update s-map (:end log-item) (fn [v] (inc (or v 0)))))

(defn count-overlaps-map [s-map log-item]
  (let [start-date (:start log-item)]
    (count (subseq s-map >= start-date))))

(comment
  (def ss (apply sorted-set (map :end test-data-two-overlaps-1)))
  (def sm (sorted-map))
  
  (for [td test-data-two-overlaps-1]
    (add-new-item-map sm td))
  
  (def new-item {:start (jt/local-date-time 2021 04 20 20 20 00)
                 :end (jt/local-date-time 2021 04 20 20 21 01)})
  (count-overlaps ss new-item)
  (def ss (add-new-item ss new-item))
  (def sm (add-new-item-map sm new-item))
  (reduce + (subseq sm >= (:start new-item)))
  
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
