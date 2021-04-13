(ns clojure-practice.2020-09-12.transduce-comp)

;; In this exercise I explore how to compose functions in different ways using
;; a set of monthly expenses and calculating 3, 6 and 12 month totals
;; Each function/solution is intended to compute the same value (or nearly the same due to floating point math)
(def monthly-expenses
  {:house 1350
   :amex 2000
   :car 150
   :madeline 700
   :awol 250
   :internet 70
   :phone 120
   :power 150
   :gas 80
   :water 360/4
   :garbage 20
   :food 1000
   :car-insurance 400/6
   :home-insurance 700/12
   :life-insurance 100
   :property-taxes 10000/12
   :ira 100
   :college-fund 100
   :donations 120
   }
)

(defn expenses-per-1 [months expenses]
  [(->>
    expenses
    vals
    (reduce +)
    float
    ) 
   (->>
    expenses
    vals
    (map #(* months %))
    (reduce +)
    float
   )]
  )

(defn expenses-per-2 [months expenses]
  (let [agg (comp float (partial reduce +) vals)
        a-month (agg expenses)]
      [a-month
       (* a-month months)]
    ))

(defn expenses-per-3 [months expenses]
  (let [v (vals expenses)
        mly (comp
             (map #(* months %))
             )]
    [(float (reduce + v)) 
     (float (transduce mly + v))]
    ))


(map #(% 3 monthly-expenses) [expenses-per-1 expenses-per-2 expenses-per-3])

(for [f [expenses-per-1 expenses-per-2 expenses-per-3]
      m [3 6 12]]
  (clojure.string/join " " [(type f) "months:" m (f m monthly-expenses)])
  )


(comment
    (let [e (vals expenses)
        one-month (reduce + e)
        all-months (reduce + (map #(* months %) e))]
      [one-month all-months])

    )
