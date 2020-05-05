(ns ^:figwheel-hooks cellular-automata.core
  (:require
   [cellular-automata.components :refer [automata cell inc-text-input-dec rules]]
   [cellular-automata.helpers :refer [rule-name->rule create-rows-memo]]
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom create-class]]))

(defn get-app-element []
  (gdom/getElement "app"))

(defonce has-initially-loaded (atom false))

(def rule (atom []))
(def rule-name (atom 0))
(def cells-rendered (atom 0))
(def num-rows (atom 0))

(defn bump-cells-rendered! []
  (reset! cells-rendered (+ @cells-rendered (* @num-rows (+ (* @num-rows 2) 1)))))

(defn inc-rows! []
  (do
    (reset! num-rows (-> @num-rows js/parseInt inc))
    (bump-cells-rendered!)))

(defn dec-rows! []
  (do
    (swap! num-rows dec)
    (bump-cells-rendered!)))

(defn reset-rows! [new-num-rows]
  (when (>= new-num-rows 0) (do (reset! num-rows new-num-rows)
                                (bump-cells-rendered!))))

(defn inc-rule-name! []
  (let [new-rule-name (inc (js/parseInt @rule-name))]
    (do
      (reset! rule-name (if (> new-rule-name 255) 0 new-rule-name))
      (reset! rule (rule-name->rule @rule-name))
      (bump-cells-rendered!))))

(defn dec-rule-name! []
  (do
    (let [new-rule-name (if (< (dec (js/parseInt @rule-name)) 0) 255 (dec (js/parseInt @rule-name)))]
      (reset! rule-name new-rule-name))
    (reset! rule (rule-name->rule @rule-name))
    (bump-cells-rendered!)))

(defn reset-rule-name! [new-rule-name]
  (do
    (reset! rule-name (if (> new-rule-name 255) (mod new-rule-name 255) new-rule-name))
    (reset! rule (rule-name->rule @rule-name))
    (bump-cells-rendered!)))

(defn toggle-rule! [rule-num]
  (let [new-rule (assoc @rule rule-num (if (zero? (@rule rule-num)) 1 0))]
    (do
      (reset! rule-name (js/parseInt (reduce str new-rule) 2))
      (reset! rule new-rule)
      (bump-cells-rendered!))))

(defn main []
  (create-class
   {:component-did-mount (fn [] (js/setTimeout #(reset! has-initially-loaded true) 0))
    :reagent-render
    (fn [this]
      (let [data (create-rows-memo @num-rows @rule)
            len (count (last data))]
        [:div.main.fade-in-1 {:class [(if @has-initially-loaded "has-initially-loaded")]}
         [:div.automata-container [automata data]
          [:div.stats
           [:p.stats.left (do (reduce #(+ % (count %2)) 0 data)) " cells showing"]
           [:p.stats.right @cells-rendered " cells rendered"]]]
         [:div.controllers
          [:div.controller
           [:h2 "rule"]
           [inc-text-input-dec @rule-name false dec-rule-name! inc-rule-name! reset-rule-name!]]
          [:div.controller
           [:h2 "rows"]
           (let [is-dec-inactive (<= @num-rows 2)]
             [inc-text-input-dec @num-rows is-dec-inactive dec-rows! inc-rows! reset-rows!])]]
         [rules @rule #(toggle-rule! %)]]))}))

(defn mount [el]
  (reagent/render-component [main] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (do
      (reset-rule-name! (rand-int 256))
      (reset! num-rows (+ (rand-int 20) 3))
      (bump-cells-rendered!)
      (mount el))))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
