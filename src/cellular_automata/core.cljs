(ns ^:figwheel-hooks cellular-automata.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))

(defn get-app-element []
  (gdom/getElement "app"))

(def rule-30 [false false false true true true true false])
(def rule-110 [false true true false true true true false])
(def rule-250 [false false false true true true true false])
(def rule-254 [true true true true true true true false])

(defn rule-translate [rule-set L C R]
  (let [a (rule-set 0)
        b (rule-set 1)
        c (rule-set 2)
        d (rule-set 3)
        e (rule-set 4)
        f (rule-set 5)
        g (rule-set 6)
        h (rule-set 7)]
    (cond (and L C R) a
          (and L C (not R)) b
          (and L (not C) R) c
          (and L (not C) (not R)) d
          (and (not L) C R) e
          (and (not L) C (not R)) f
          (and (not L) (not C) R) g
          (and (not L) (not C) (not R)) h
          :else (println "DEBUG"))))

(defn create-new-row [prev-row rule]
  (let [prev-row-expanded (flatten (conj [false false] prev-row [false false]))
        times (+ (count prev-row) 2)]
    (map-indexed #(let [left (nth prev-row-expanded %)
                        center (nth prev-row-expanded (inc %))
                        right (nth prev-row-expanded (+ % 2))]
                    (rule-translate rule left center right))
                 (repeat times 0))))

(defn create-rows [number-of-rows rule]
  (loop [row [true] acc [] n (dec number-of-rows) row-number 0]
    (if (> row-number n)
      acc
      (let [new-row (vec (create-new-row row rule))]
        (recur new-row (conj (if (empty? acc) [row] acc) new-row) n (inc row-number))))))

(defn cell [value]
  [:svg {:view-box [0 0 10 10] :style {:fill (if value "black" "white")}} [:rect {:width 10 :height 10}]])

(defn automata [data]
  (let [len (count (last data))]
    [:div.container {:style {:display "grid" :grid-template-columns (str "repeat(" len ", " (/ 100 len) "%)")}}
     (for [row data]
       (let [expansion-per-side (/ (- len (count row)) 2)
             row-expanded (vec (concat (repeat expansion-per-side false) row (repeat expansion-per-side false)))]
         (map-indexed (fn [idx value] ^{:key (str idx value)} [cell value]) row-expanded)))]))

(defn main []
  (let [data (create-rows 20 rule-250)
        len (count (last data))]
    (do
      ;; (println "main " data len)
      [:div.main [automata data]])))

(defn mount [el]
  (reagent/render-component [main] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )
