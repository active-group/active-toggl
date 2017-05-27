(defproject active-toggl "0.1.0-SNAPSHOT"
  :description "REST API client for toggl"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [clj-http "3.6.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clojure-csv/clojure-csv "2.0.1"]]
  :main active-toggl.main)
