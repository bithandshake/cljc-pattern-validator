
(ns validator.env
    (:require [common-state.api :as common-state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn get-test
  ; @description
  ; Returns a specific test.
  ;
  ; @param (keyword) test-id
  ;
  ; @usage
  ; (get-test :my-test)
  ; =>
  ; {:f* string? :e* "Value must be a string!"}
  ;
  ; @return (map)
  [test-id]
  (common-state/get-state :validator :tests test-id))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn validator-disabled?
  ; @description
  ; Returns TRUE if the validator is disabled.
  ;
  ; @usage
  ; (validator-disabled?)
  ; =>
  ; true
  ;
  ; @return (boolean)
  []
  (common-state/get-state :validator :disabled?))
