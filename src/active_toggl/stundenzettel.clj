(ns active-toggl.stundenzettel
  (:require [active-toggl.csv :as csv]
            [active-toggl.toggl :as toggl]
            [clojure.set :as set]))


(defn per-entry
  [detailed-data]
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
         (update "description" (fn [dsc] (str (get % "project") ": " dsc)))
         (set/rename-keys {"start" "Date"
                           "dur" "Duration"
                           "description" "Description"}))
    detailed-data)))

(defn stundenzettel
  [api-token since & [until project]]
  (let [detailed-data (toggl/detailed-report api-token since until project)
        row-data (per-entry detailed-data)]
    (println (csv/compile-csv row-data ["Date" "Duration" "Description"]))))
