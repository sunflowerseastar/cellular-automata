(ns ^:figwheel-hooks cellular-automata.core
  (:require
   [cellular-automata.components :refer [automata cell inc-text-input-dec rules]]
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))

(defn get-app-element []
  (gdom/getElement "app"))

(def rule-30 [0 0 0 1 1 1 1 0])
(def rule-110 [0 1 1 0 1 1 1 0])
(def rule-250 [0 0 0 1 1 1 1 0])
(def rule-254 [1 1 1 1 1 1 1 0])

(def rule (atom rule-110))
(def rule-name (atom 10))
(def stats (atom {:computed "asdf"}))
(def num-rows (atom 3))

(defn rule-translate [rule-set L C R]
  (let [L (pos? L)
        C (pos? C)
        R (pos? R)
        a (rule-set 0)
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
  (let [prev-row-expanded (flatten (conj [0 0] prev-row [0 0]))
        times (+ (count prev-row) 2)]
    (map-indexed #(let [left (nth prev-row-expanded %)
                        center (nth prev-row-expanded (inc %))
                        right (nth prev-row-expanded (+ % 2))]
                    (rule-translate rule left center right))
                 (repeat times 0))))

(defn create-rows [number-of-rows rule]
  (loop [row [1] acc [] n (dec number-of-rows) row-number 0]
    (if (> row-number n)
      acc
      (let [new-row (vec (create-new-row row rule))]
        (recur new-row (conj (if (empty? acc) [row] acc) new-row) n (inc row-number))))))

(defn inc-rows []
  (swap! num-rows inc))

(defn dec-rows []
  (swap! num-rows dec))

(defn reset-rows [new-num-rows]
  (reset! num-rows new-num-rows))

(defn rule-name->rule [rule-name]
  (let [binary-str (-> (js/Number rule-name) (.toString 2))
        binary (map js/parseInt binary-str)
        fill-count (- 8 (count binary))
        fill (repeat fill-count 0)]
    (-> (conj binary fill) flatten vec)))

(defn inc-rule-name []
  (swap! rule-name inc)
  (reset! rule (rule-name->rule @rule-name)))

(defn dec-rule-name []
  (swap! rule-name dec)
  (reset! rule (rule-name->rule @rule-name)))

(defn reset-rule-name [new-rule-name]
  (reset! rule-name new-rule-name)
  (reset! rule (rule-name->rule @rule-name)))

(defn toggle-rule [rule-num]
  (let [new-rule (assoc @rule rule-num (if (zero? (@rule rule-num)) 1 0))]
    (reset! rule new-rule)))

(defn main []
  (let [data (create-rows (- @num-rows 1) @rule)
        len (count (last data))]
    [:div.main
     [automata data]
     [:p.stats "# computed: " (reduce #(+ % (count %2)) 0 data)]
     [:div.controllers
      [:div.controller
       [:h2 "rule"]
       [inc-text-input-dec @rule-name inc-rule-name dec-rule-name reset-rule-name]]
      [:div.controller
       [:h2 "rows"]
       [inc-text-input-dec @num-rows inc-rows dec-rows reset-rows]]]
     [rules @rule #(toggle-rule %)]]))

(defn mount [el]
  (reagent/render-component [main] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
