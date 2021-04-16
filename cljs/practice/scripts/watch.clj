(require '[cljs.build.api :as b])

(b/watch "src"
  {:main 'practice.core
   :output-to "out/practice.js"
   :output-dir "out"})
