
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
  ; (reg-test! :my-test {...})
  ;
  ; @usage
  ; (reg-test! :my-test {:f* string? :e* "Key :a must be a string!"})
  [test-id test]
  (swap! state/TESTS assoc test-id test))
