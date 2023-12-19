
(ns validator.reg
    (:require [validator.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn reg-test!
  ; @description
  ; Registers a reusable test identified by the given ID.
  ;
  ; @param (keyword) test-id
  ; @param (map) test
  ;
  ; @usage
  ; (reg-test! :my-test {:f* string? :e* "Value must be a string!"})
  ;
  ; @return (map)
  [test-id test]
  (-> state/TESTS (swap! assoc test-id test))
  (-> test))
