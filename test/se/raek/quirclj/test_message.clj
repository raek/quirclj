(ns se.raek.quirclj.test-message
  {:author "Rasmus Svensson (raek)"}
  (:use clojure.test)
  (:require [se.raek.quirclj.message :as msg]))

(deftest test-parse
  
  (testing "command only"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "FOO")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "FOO"))
      (is (nil? params))))
  
  (testing "prefix and command"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":tortoise BAR")]
      (is (= source "tortoise"))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "BAR"))
      (is (nil? params))))
  
  (testing "single parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "BAZ param")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "BAZ"))
      (is (= params ["param"]))))
  
  (testing "prefix, command and single parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":achilles QUUX param")]
      (is (= source "achilles"))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "QUUX"))
      (is (= params ["param"]))))
  
  (testing "three parameters"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND one two three")]
      (is (= source nil))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["one" "two" "three"]))))
  
  (testing "rest parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND :rest")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["rest"]))))
  
  (testing "empty rest parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND :")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params [""]))))
  
  (testing "rest parameter with spaces"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND :one two three")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["one two three"]))))
  
  (testing "normal parameters and rest parameter"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse "COMMAND one two :three three three")]
      (is (nil? source))
      (is (nil? source-user))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params ["one" "two" "three three three"]))))

  (testing "prefix with user name"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":foo!bar COMMAND")]
      (is (= source "foo"))
      (is (= source-user "bar"))
      (is (nil? source-host))
      (is (= command "COMMAND"))
      (is (= params nil))))

  (testing "prefix with host name"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":foo@example.com COMMAND")]
      (is (= source "foo"))
      (is (nil? source-user))
      (is (= source-host "example.com"))
      (is (= command "COMMAND"))
      (is (= params nil))))

  (testing "prefix with user name and host name"
    (let [{:keys [source source-user source-host command params]}
          (msg/parse ":foo!bar@example.com COMMAND")]
      (is (= source "foo"))
      (is (= source-user "bar"))
      (is (= source-host "example.com"))
      (is (= command "COMMAND"))
      (is (= params nil)))))
