(ns ^:figwheel-hooks cellular-automata.core
  (:require
   [cellular-automata.components :refer [automata cell inc-text-input-dec rules]]
   [cellular-automata.helpers :refer [rule-name->rule create-rows-memo]]
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))

(defn get-app-element []
  (gdom/getElement "app"))

(def rule (atom []))
(def rule-name (atom 1))
(def stats (atom {:computed ""}))
(def num-rows (atom 1))

(defn inc-rows! []
  (swap! num-rows inc))

(defn dec-rows! []
  (swap! num-rows dec))

(defn reset-rows! [new-num-rows]
  (when (>= new-num-rows 0) (reset! num-rows new-num-rows)))

(defn inc-rule-name! []
  (let [new-rule-name (inc (js/parseInt @rule-name))]
    (do
      (reset! rule-name (if (> new-rule-name 255) 0 new-rule-name))
      (reset! rule (rule-name->rule @rule-name)))))

(defn dec-rule-name! []
  (do
    (let [new-rule-name (if (< (dec @rule-name) 0) 255 (dec @rule-name))]
      (reset! rule-name new-rule-name))
    (reset! rule (rule-name->rule @rule-name))))

(defn reset-rule-name! [new-rule-name]
  (do
    (reset! rule-name (if (> new-rule-name 255) (mod new-rule-name 255) new-rule-name))
    (reset! rule (rule-name->rule @rule-name))))

(defn toggle-rule! [rule-num]
  (let [new-rule (assoc @rule rule-num (if (zero? (@rule rule-num)) 1 0))]
    (do
      (reset! rule-name (js/parseInt (reduce str new-rule) 2))
      (reset! rule new-rule))))

(defn main []
  (let [data (create-rows-memo (- @num-rows 1) @rule)
        len (count (last data))]
    [:div.main
     [:div.automata-container [automata data]
      [:p.stats "# computed: " (reduce #(+ % (count %2)) 0 data)]]
     [:div.controllers
      [:div.controller
       [:h2 "rule"]
       [inc-text-input-dec @rule-name false false dec-rule-name! inc-rule-name! reset-rule-name!]]
      [:div.controller
       [:h2 "rows"]
       (let [is-dec-inactive (<= @num-rows 0)]
         [inc-text-input-dec @num-rows is-dec-inactive false dec-rows! inc-rows! reset-rows!])]]
     [rules @rule #(toggle-rule! %)]]))

(defn mount [el]
  (reagent/render-component [main] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (do
      (reset-rule-name! (rand-int 256))
      (reset! num-rows (+ (rand-int 20) 3))
      (mount el))))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
