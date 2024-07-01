(ns active-toggl.main
  (:require [clojure.tools.cli :refer [parse-opts]]
            [active-toggl.arbeitszeit :as arbeitszeit]
            [active-toggl.stundenzettel :as stundenzettel])
  (:gen-class))

(def opts
  [["-h" "--help" "Show this help about the command line arguments."]
   ["-s" "--stundenzettel" "Return data as Stundenzettel (date, duration, description) [default]"]
   ["-a" "--arbeitszeit" "Return data as Arbeitszeit (date, start, end, duration)"]])

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
      (cond
        (:arbeitszeit (:options opts-map))
        (apply arbeitszeit/arbeitszeit (get opts-map :arguments))

        :else #_(:stundenzettel (:options opts-map)) ;; default
        (apply stundenzettel/stundenzettel (get opts-map :arguments))))))
