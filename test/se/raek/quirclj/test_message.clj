(ns se.raek.quirclj.test-message
  {:author "Rasmus Svensson (raek)"}
  (:use clojure.test)
  (:require [se.raek.quirclj.message :as msg]))

(deftest test-parse
  
  (testing "command only"
    (let [{:keys [source command params]}
          (msg/parse "FOO")]
      (is (= source nil))
      (is (= command "FOO"))
      (is (= params nil))))
  
  (testing "prefix and command"
    (let [{:keys [source command params]}
          (msg/parse ":tortoise BAR")]
      (is (= source "tortoise"))
      (is (= command "BAR"))
      (is (= params nil))))
  
  (testing "single parameter"
    (let [{:keys [source command params]}
          (msg/parse "BAZ param")]
      (is (= source nil))
      (is (= command "BAZ"))
      (is (= params ["param"]))))
  
  (testing "prefix, command and single parameter"
    (let [{:keys [source command params]}
          (msg/parse ":achilles QUUX param")]
      (is (= source "achilles"))
      (is (= command "QUUX"))
      (is (= params ["param"]))))
  
  (testing "three parameters"
    (let [{:keys [source command params]}
          (msg/parse "COMMAND one two three")]
      (is (= source nil))
      (is (= command "COMMAND"))
      (is (= params ["one" "two" "three"]))))
  
  (testing "rest parameter"
    (let [{:keys [source command params]}
          (msg/parse "COMMAND :rest")]
      (is (= source nil))
      (is (= command "COMMAND"))
      (is (= params ["rest"]))))
  
  (testing "empty rest parameter"
    (let [{:keys [source command params]}
          (msg/parse "COMMAND :")]
      (is (= source nil))
      (is (= command "COMMAND"))
      (is (= params [""]))))
  
  (testing "rest parameter with spaces"
    (let [{:keys [source command params]}
          (msg/parse "COMMAND :one two three")]
      (is (= source nil))
      (is (= command "COMMAND"))
      (is (= params ["one two three"]))))
  
  (testing "normal parameters and rest parameter"
    (let [{:keys [source command params]}
          (msg/parse "COMMAND one two :three three three")]
      (is (= source nil))
      (is (= command "COMMAND"))
      (is (= params ["one" "two" "three three three"])))))
