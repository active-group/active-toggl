(ns active-toggl.arbeitszeit
  (:require [active-toggl.csv :as csv]
            [active-toggl.toggl :as toggl]))

(defn earlier
  [t1 t2]
  (if (<= (compare t1 t2) 0)
    t1
    t2))

(defn later
  [t1 t2]
  (if (<= (compare t2 t1) 0)
    t1
    t2))

(defn per-day
  [detailed-data]
  (let [entries
        (mapv (fn [entry]
                {"Date" (subs (get entry "start") 0 10)
                 "Start" (subs (get entry "start") 11 19)
                 "End" (subs (get entry "end") 11 19)
                 "Duration" (/ (get entry "dur" 0) 3600000.0)})
              detailed-data)
        by-date
        (group-by (fn [entry] (get entry "Date")) entries)
        by-date-accumulated
        (reduce-kv (fn [m k v]
                     (assoc m k (reduce (fn [result entry]
                                          {"Date" (get entry "Date")
                                           "Start" (earlier (get result "Start") (get entry "Start"))
                                           "End" (later (get result "End") (get entry "End"))
                                           "Duration" (+ (get result "Duration") (get entry "Duration"))})
                                        v)))
                   {}
                   by-date)]
    (vals by-date-accumulated)))

(defn arbeitszeit
  [api-token since & [until]]
  (let [detailed-data (toggl/detailed-report api-token since until)
        row-data (per-day detailed-data)]
    (println (csv/compile-csv row-data ["Date" "Start" "End" "Duration"]))))
