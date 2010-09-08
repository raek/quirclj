(ns se.raek.quirclj.message
  {:author "Rasmus Svensson (raek)"}
  (:use [clojure.contrib.def :only [defvar-]]))

(declare parse-params)

(defvar- message-regex
  #"^(?::([^ ]+) +)?([^ ]+)(?: +(.+))?$")

(defn parse [s]
  (when-let [[_ prefix command param-str] (re-find message-regex s)]
    (let [params (parse-params param-str)]
      {:source prefix
       :command command
       :params params})))

(defvar- param-regex
  #"(?:(?<!:)[^ :][^ ]*|(?<=:).*)")

(defn- parse-params [param-str]
  (when param-str
    (re-seq param-regex param-str)))
