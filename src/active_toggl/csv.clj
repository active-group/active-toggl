(ns active-toggl.csv
  (:require [clojure-csv.core :as csv]
            [active-toggl.toggl :as toggl]
            [clojure.set :as set]))

(defn compile-csv [row-data & [columns]]
  (let [columns (or columns (apply clojure.set/union (map (comp set keys) row-data)))
        headers (map name columns)
        rows (sort-by first (mapv (fn [r] (mapv #(str (r %)) columns)) row-data))]
    (csv/write-csv (cons headers rows) :force-quote true)))

(defn detailed-report
  [api-token since & [until project]]
  (let [data (toggl/detailed-report api-token since until project)
        row-data
        (map
         #(reduce-kv
           (fn [m k v]
             (assoc m k (cond
                          (= "Date" k) (subs v 0 10)
                          (= "Duration" k) (format "%.2f" (/ v 3600000.0))
                          :else v)))
           {}
           %)
         (map
          #(-> %
               (select-keys ["start" "dur" "description"])
               (set/rename-keys {"start" "Date"
                                 "dur" "Duration"
                                 "description" "Description"}))
          data))]
    (println (compile-csv row-data ["Date" "Duration" "Description"]))))
