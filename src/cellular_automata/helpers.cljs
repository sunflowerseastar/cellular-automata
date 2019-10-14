(ns cellular-automata.helpers)

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
          (and (not L) (not C) (not R)) h)))

(defn rule-name->rule [rule-name]
  (let [binary-str (-> (js/Number rule-name) (.toString 2))
        binary (map js/parseInt binary-str)
        fill-count (- 8 (count binary))
        fill (repeat fill-count 0)]
    (-> (conj binary fill) flatten vec)))

(defn create-new-row [prev-row rule]
  (let [prev-row-expanded (flatten (conj [0 0] prev-row [0 0]))
        times (+ (count prev-row) 2)]
    (map-indexed #(let [left (nth prev-row-expanded %)
                        center (nth prev-row-expanded (inc %))
                        right (nth prev-row-expanded (+ % 2))]
                    (rule-translate rule left center right))
                 (repeat times 0))))

(def create-new-row-memo (memoize create-new-row))

(defn create-rows [number-of-rows rule]
  (loop [row [1] acc [] n (dec number-of-rows) row-number 0]
    (if (> row-number n)
      acc
      (let [new-row (vec (create-new-row-memo row rule))]
        (recur new-row (conj (if (empty? acc) [row] acc) new-row) n (inc row-number))))))

(def create-rows-memo (memoize create-rows))
