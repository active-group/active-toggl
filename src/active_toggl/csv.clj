(ns active-toggl.csv
  (:require [clojure-csv.core :as csv]
            [clojure.set :as set]))

(defn compile-csv [row-data & [columns]]
  (let [columns (or columns (apply clojure.set/union (map (comp set keys) row-data)))
        headers (map name columns)
        rows (sort-by first (mapv (fn [r] (mapv #(str (r %)) columns)) row-data))]
    (csv/write-csv (cons headers rows) :force-quote true)))
