(ns active-toggl.toggl
  (:require [clj-http.client :as client]
            [clojure.data.json :as json]))

(defn json->clj
  [req]
  (json/read-str (:body req)))

(defn basic-auth
  [api-token]
  {:basic-auth [api-token "api_token"]})

(defn get-workspace-id
  [api-token]
  (get (first 
        (json->clj (client/get "https://api.track.toggl.com/api/v9/workspaces" (basic-auth api-token))))
       "id"))

(defn detailed-report
  [api-token since & [until project]]
  (let [workspace-id (get-workspace-id api-token)
        data
        (loop [page 1
               accu []]
          (let [_ (Thread/sleep 1000) ;; avoid api lock out for too frequent requests
                result (json->clj
                        (client/get (str "https://api.track.toggl.com/reports/api/v2/details?workspace_id=" workspace-id
                                         "&page=" page
                                         "&user_agent=active-toggl&display_hours=decimal&since=" since
                                         (when until (str "&until=" until)))
                                    (basic-auth api-token))) ;; reports-api can stay at 2 for the moment (see https://toggl.com/blog/toggl-track-reports-api-v3)
                total-count (get result "total_count")
                per-page (get result "per_page")
                data (concat accu (get result "data"))]
            (if (> total-count (* page per-page))
              (recur (inc page) data)
              (filter #(if project
                         (or (re-matches (re-pattern (str "(?i)" project)) (or (get % "project") ""))
                             (re-matches (re-pattern (str "(?i)" project)) (or (get % "client") "")))
                         true)
                      data))))]
    data))
