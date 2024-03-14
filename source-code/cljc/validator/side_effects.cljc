
(ns validator.side-effects
    (:require [common-state.api :as common-state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn disable-validator!
  ; @description
  ; Disables the validator, causing every test to pass.
  ;
  ; @usage
  ; (disable-validator!)
  []
  (common-state/assoc-state! :validator :disabled? true))

(defn enable-validator!
  ; @description
  ; Re-enables the validator.
  ;
  ; @usage
  ; (enable-validator!)
  []
  (common-state/dissoc-state! :validator :disabled?))
