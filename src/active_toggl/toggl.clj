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
        (json->clj (client/get "https://www.toggl.com/api/v8/workspaces" (basic-auth api-token))))
       "id"))

(defn detailed-report
  [api-token since & [until project]]
  (let [workspace-id (get-workspace-id api-token)
        data
        (loop [page 1
               accu []]
          (let [_ (Thread/sleep 1000) ;; avoid api lock out for too frequent requests
                result (json->clj
                        (client/get (str "https://toggl.com/reports/api/v2/details?workspace_id=" workspace-id
                                         "&page=" page
                                         "&user_agent=active-toggl&display_hours=decimal&since=" since
                                         (when until (str "&until=" until)))
                                    (basic-auth api-token)))
                total-count (get result "total_count")
                per-page (get result "per_page")
                data (concat accu (get result "data"))]
            (if (> total-count (* page per-page))
              (recur (inc page) data)
              (filter #(if project
                         (re-matches (re-pattern (str "(?i)" project)) (get % "project"))
                         true)
                      data))))]
    data))
