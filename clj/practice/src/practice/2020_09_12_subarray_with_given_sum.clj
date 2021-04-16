(ns clojure-practice.2020-09-12.subarry-with-given-sum)
;; Taken from https://practice.geeksforgeeks.org/problems/subarray-with-given-sum/0
;; Given an unsorted array A of size N of non-negative integers, find a continuous sub-array which adds to a given number S.

;; Input:
;; The first line of input contains an integer T denoting the number of test cases. Then T test cases follow. Each test case consists of two lines. The first line of each test case is N and S, where N is the size of array and S is the sum. The second line of each test case contains N space separated integers denoting the array elements.

;; Output:
;; For each testcase, in a new line, print the starting and ending positions(1 indexing) of first such occuring subarray from the left if sum equals to subarray, else print -1.

;; Constraints:
;; 1 <= T <= 100
;; 1 <= N <= 107
;; 1 <= Ai <= 1010

;; Example:
;; Input:
;; 2
;; 5 12
;; 1 2 3 7 5
;; 10 15
;; 1 2 3 4 5 6 7 8 9 10
;; Output:
;; 2 4
;; 1 5

;; Explanation :
;; Testcase1: sum of elements from 2nd position to 4th position is 12
;; Testcase2: sum of elements from 1st position to 5th position is 15


(def T 100)
(def N 107)
(def Ai 1010)

(def t1 [1 2 3 7 5])
(def t2 (map inc (range 10)))

(defn create-test-case [size-of-array sum]
  (repeatedly size-of-array #(rand-int sum))
  )

(comment
  (create-test-case 5 12)
  (create-test-case 10 15)

  (reduce + t1)
  (count t1)
  (partition 1 1 t1)
  (partition 2 1 t1)
  (partition 2 2 t1) ;; next
  (partition 3 1 t1)
  (partition 3 2 t1) ;; next 
  (partition 4 1 t1)
  (partition 4 2 t1) ;; next
  (partition 5 1 t1)

  (count t2)
  (partition 1 1 t2)
  (partition 2 1 t2)
  (partition 3 1 t2)
  (partition 4 1 t2)
  (partition 5 1 t2)
  (partition 6 1 t2)
  (partition 7 1 t2)
  (partition 8 1 t2)
  (partition 9 1 t2)
  (partition 10 1 t2)

  (range (count t2))

  (map #(partition %1 1 t2) )

  (let [test-array t2
        p-sizes (map inc (range (count test-array)))
        sub-arrays (map #(partition %1 1 test-array) p-sizes)
        sumred (fn [par] (map #(reduce + %1) par))
        summed (map sumred sub-arrays)
        ]
    [(take 1 (second sub-arrays) ) 
     summed]
    )
)
