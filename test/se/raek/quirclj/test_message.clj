(ns se.raek.quirclj.test-message
  {:author "Rasmus Svensson (raek)"}
  (:use clojure.test)
  (:require [se.raek.quirclj.message :as msg]))

(defn in? [x coll]
  (contains? coll x))

(deftest raw-message-string-and-map
  
  (testing "parsing command only"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "FOO")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "FOO"))
      (is (nil? params))))

  (testing "formatting command only"
    (is (= (msg/format {:command "FOO"})
           "FOO")))
  
  (testing "parsing prefix and command"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":tortoise BAR")]
      (is (= source "tortoise"))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "BAR"))
      (is (nil? params))))

  (testing "formatting prefix and command"
    (is (= (msg/format {:source "tortoise"
                        :command "BAR"})
           ":tortoise BAR")))
  
  (testing "parsing single parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "BAZ param")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "BAZ"))
      (is (= params ["param"]))))

  (testing "formatting single parameter"
    (is (in? (msg/format {:command "BAZ"
                          :params ["param"]})
             #{"BAZ param"
               "BAZ :param"})))
  
  (testing "parsing prefix, command and single parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":achilles QUUX param")]
      (is (= source "achilles"))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "QUUX"))
      (is (= params ["param"]))))

  (testing "formatting prefix, command and single parameter"
    (is (in? (msg/format {:source "achilles"
                          :command "QUUX"
                          :params ["param"]})
             #{":achilles QUUX param"
               ":achilles QUUX :param"})))
  
  (testing "parsing three parameters"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND one two three")]
      (is (= source nil))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["one" "two" "three"]))))

  (testing "formatting three parameters"
    (is (in? (msg/format {:command "COMMAND"
                          :params ["one" "two" "three"]})
             #{"COMMAND one two three"
               "COMMAND one two :three"})))
  
  (testing "parsing rest parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND :rest")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["rest"]))))
  
  (testing "parsing empty rest parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND :")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params [""]))))

  (testing "formatting emty rest parameter"
    (is (= (msg/format {:command "COMMAND"
                        :params [""]})
           "COMMAND :")))
  
  (testing "parsing rest parameter with spaces"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND :one two three")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["one two three"]))))

  (testing "formatting rest parameter with spaces"
    (is (= (msg/format {:command "COMMAND"
                        :params ["one two three"]})
           "COMMAND :one two three")))
  
  (testing "parsing normal parameters and rest parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND one two :three three three")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["one" "two" "three three three"]))))

  (testing "formatting normal parameters and rest parameter"
    (is (= (msg/format {:command "COMMAND"
                        :params ["one" "two" "three three three"]})
           "COMMAND one two :three three three")))

  (testing "parsing client source with user name"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":foo!bar COMMAND")]
      (is (= source "foo"))
      (is (= source-user "bar"))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params nil))))
  
  (testing "formatting client source with user name"
    (is (= (msg/format {:source "foo"
                        :source-user "bar"
                        :command "COMMAND"})
           ":foo!bar COMMAND")))

  (testing "parsing client source with host name"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":foo@example.com COMMAND")]
      (is (= source "foo"))
      (is (nil? source-user))
      (is (= source-host "example.com"))
      (is (= command "COMMAND"))
      (is (= params nil))))
  
  (testing "formatting client source with host name"
    (is (= (msg/format {:source "foo"
                        :source-host "example.com"
                        :command "COMMAND"})
           ":foo@example.com COMMAND")))

  (testing "parsing client source with user name and host name"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":foo!bar@example.com COMMAND")]
      (is (= source "foo"))
      (is (= source-user "bar"))
      (is (= source-host "example.com"))
      (is (= command "COMMAND"))
      (is (= params nil))))
  
  (testing "formatting client source with user name and host name"
    (is (= (msg/format {:source "foo"
                        :source-user "bar"
                        :source-host "example.com"
                        :command "COMMAND"})
           ":foo!bar@example.com COMMAND")))

  (testing "parsing server source"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":irc.example.com COMMAND")]
      (is (= source "irc.example.com"))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params nil))))
  
  (testing "formatting server source"
    (is (= (msg/format {:source "irc.example.com"
                        :command "COMMAND"})
           ":irc.example.com COMMAND"))))
