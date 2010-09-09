(ns se.raek.quirclj.message
  "Translate IRC messages between string and map form."
  {:author "Rasmus Svensson (raek)"}
  (:use [clojure.contrib.def :only [defvar-]]))

(declare parse-source parse-params)

(defvar- message-regex
  #"^(?::([^ ]+) +)?([^ ]+)(?: +(.+))?$")

(defn parse [s]
  (when-let [[_ source-str command param-str] (re-find message-regex s)]
    (let [source-map (parse-source source-str)
          params (parse-params param-str)]
      (assoc source-map
        :command command
        :params params))))

(defvar- source-regex
  #"^([^!@]*)(?:!([^@]*))?(?:@(.*))?$")

(defn- parse-source [source-str]
  (let [[_ source user host]
        (and source-str (re-find source-regex source-str))]
       {:source source
        :source-user user
        :source-host host}))

(defvar- param-regex
  #"(?:(?<!:)[^ :][^ ]*|(?<=:).*)")

(defn- parse-params [param-str]
  (when param-str
    (re-seq param-regex param-str)))
