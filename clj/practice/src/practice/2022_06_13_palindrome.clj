
;; eg. given 'ababac', result will be 'ababa', not 'aba'

;; Hello world!
;; '#a#b#c#c#b#a#z#'

;; "aaaaa" => "aaaaa"
;; "aaaa" => "aaa"
;; "cababa" => "ababa"

;; "ababac"
;; [1 3 5 3 1 1]
(defn lr [indexed index]
  [(nth indexed (- index 1)) (nth indexed (+ index 1))]
  )

(defn scan [input-string index]
  (let [d (map-indexed (fn [idx itm] [idx itm]) input-string)]
    (apply == (lr d index))))

(scan "ababac" 1)



