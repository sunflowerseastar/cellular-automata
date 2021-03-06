(ns cellular-automata.components)

(def rule-triads [[1 1 1]
                  [1 1 0]
                  [1 0 1]
                  [1 0 0]
                  [0 1 1]
                  [0 1 0]
                  [0 0 1]
                  [0 0 0]])

(defn cell [value is-last-column is-last-row x y]
  [:svg.cell {:view-box [0 0 10 10]
              :class [(if is-last-column "is-last-column") (if is-last-row "is-last-row")]
              :style {:fill (if (pos? value) "black" "white") :background (if (pos? value) "black" "white")
                      :grid-column (+ x 1) :grid-row (+ y 1)}}
   [:rect {:width 10 :height 10}]])

(defn automata [data]
  (let [len (count (last data))]
    [:div.automata.hide-borders {:style {:display "grid" :grid-template-columns (str "repeat(" len ", " (/ 100 len) "%)")}}
     (map-indexed (fn [y-idx row]
                    (map-indexed
                     (fn [x-idx value]
                       (let [is-last-column (zero? (mod (inc x-idx) len))
                             is-last-row (= (inc y-idx) (count data))]
                         ^{:key (str x-idx value)}
                         [cell value is-last-column is-last-row x-idx y-idx]))
                     row))
                  data)]))

(defn rules [current-rule-set on-click]
  [:div.rules.fade-in-2
   (map-indexed
    (fn [rule-idx rule-triad] ^{:key (str rule-triad)}
      [:div.rule {:on-click #(on-click rule-idx)}
       (map-indexed (fn [idx value] ^{:key (str idx value)} [cell value false false idx 0]) rule-triad)
       (map-indexed (fn [idx value] ^{:key (str idx value)} [cell value false false idx 1]) [0 (current-rule-set rule-idx) 0])])
    rule-triads)])

(defn inc-text-input-dec [num-rows is-dec-inactive dec-rows inc-rows reset-rows]
  [:div.inc-text-input-dec
   [:button {:on-click dec-rows :class (when is-dec-inactive "inactive")} "-"]
   [:input {:type :text :value num-rows
            :on-change #(reset-rows (-> % .-target .-value))}]
   [:button {:on-click inc-rows} "+"]])
