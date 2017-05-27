(ns active-toggl.main
  (:require [clojure.tools.cli :refer [parse-opts]]
            [active-toggl.csv :as csv])
  (:gen-class))

(def opts
  [["-h" "--help" "Show this help about the command line arguments."]])

(defn print-usage [opts-map]
  (do (println "Usage: active-toggl [options] <api-token> [project]")
      (println "Options:")
      (println (:summary opts-map))))

(defn -main [& args]
  (let [opts-map (parse-opts args opts)]
    (cond
      (:errors opts-map)
      (do (doall (map println (:errors opts-map)))
          (print-usage opts-map)
          (System/exit -1))

      (:help (:options opts-map))
      (print-usage opts-map)

      (not (first (get opts-map :arguments)))
      (do (println "Specify api token.")
          (print-usage opts-map)
          (System/exit -1))

      :else
      (apply csv/detailed-report (get opts-map :arguments)))))
