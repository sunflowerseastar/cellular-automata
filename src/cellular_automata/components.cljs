(ns cellular-automata.components)

(def rule-triads [[1 1 1]
                  [1 1 0]
                  [1 0 1]
                  [1 0 0]
                  [0 1 1]
                  [0 1 0]
                  [0 0 1]
                  [0 0 0]])

(defn cell [value]
  [:svg {:view-box [0 0 10 10] :style {:fill (if (pos? value) "black" "white")}} [:rect {:width 10 :height 10}]])

(defn automata [data]
  (let [len (count (last data))]
    [:div.automata {:style {:display "grid" :grid-template-columns (str "repeat(" len ", " (/ 100 len) "%)")}}
     (for [row data]
       (let [expansion-per-side (/ (- len (count row)) 2)
             row-expanded (vec (concat (repeat expansion-per-side 0) row (repeat expansion-per-side 0)))]
         (map-indexed (fn [idx value] ^{:key (str idx value)} [cell value]) row-expanded)))]))

(defn rules [current-rule-set on-click]
  [:div.rules
   (map-indexed
    (fn [rule-idx rule-triad] ^{:key (str rule-triad)}
      [:div.rule {:on-click #(on-click rule-idx)}
       [:div.rule-row (map-indexed (fn [idx value] ^{:key (str idx value)} [cell value]) rule-triad)]
       [:div.rule-row (map-indexed (fn [idx value] ^{:key (str idx value)} [cell value]) [0 (current-rule-set rule-idx) 0])]])
    rule-triads)])

(defn inc-text-input-dec [num-rows inc-rows dec-rows reset-rows]
  [:div.inc-text-input-dec
   [:button {:on-click dec-rows} "-"]
   [:input {:type :text :value num-rows
            :on-change #(reset-rows (-> % .-target .-value))}]
   [:button {:on-click inc-rows} "+"]])
