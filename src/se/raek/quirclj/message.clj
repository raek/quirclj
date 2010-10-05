(ns se.raek.quirclj.message
  "Translate IRC messages between string and map form.

   The syntax of IRC message strings is defined in RFC 1459. This namespace
   introduces another representation for IRC messages using Clojure maps and
   functions for converting between the two.

   An IRC message has the following anatomy:

   - an optional source (called \"prefix\" in the RFC),
   - a command,
   - and zero or more parameters, of which the last one can contain spaces.

   The source can also have optional user name and host name information
   attached to it.

   The map form of a message has the following keys:

   :source       source as a string (without user name and user host) or nil
   :source-user  user name of the source as a string or nil
   :source-host  host name of the source as a string or nil
   :command      as-is command name as string
   :params       parameters as a sequence of strings or nil if none

   Examples:

     (parse \":achilles!~achilles@example.com PRIVMSG #quirclj :Hello there!\")
     => {:source \"achilles\"
         :source-user \"~achilles\"
         :source-host \"example.com\"
         :command \"PRIVMSG\"
         :params (\"#quiclj\" \"Hello there!\")}

     (format {:source \"achilles\"
              :source-user \"~achilles\"
              :source-host \"example.com\"
              :command \"PRIVMSG\"
              :params [\"#quiclj\" \"Hello there!\"]})
     => \":achilles!~achilles@example.com PRIVMSG #quirclj :Hello there!\"
  "
  {:author "Rasmus Svensson (raek)"}
  (:refer-clojure :exclude [format])
  (:use [clojure.contrib.def :only [defvar-]]))

(declare parse-source parse-params)

(defvar- message-regex
  #"^(?::([^ ]+) +)?([^ ]+)(?: +(.+))?$")

(defn parse
  "Parses an IRC message in in the form of a string into a map."
  [s]
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

(declare format-source format-params)

(defn format
  "Formats an IRC message in the form of a map into a string."
  [msg]
  (let [{:keys [source source-user source-host command params]} msg]
    (str (format-source source source-user source-host)
         command
         (format-params params))))

(defn- format-source [source source-user source-host]
  (if source
    (str \:
         source
         (if source-user
           (str \! source-user)
           "")
         (if source-host
           (str \@ source-host)
           "")
         \space)
    ""))

(defn- format-params [params]
  (if (seq params)
    (->> (concat (butlast params)
                 [(str \: (last params))])
         (interpose \space)
         (apply str \space))
    ""))
