
(ns validator.side-effects
    (:require [validator.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn disable-validator!
  ; @description
  ; Turns off the validator.
  ;
  ; @usage
  ; (disable-validator!)
  []
  (reset! state/ENABLED? false))

(defn enable-validator!
  ; @description
  ; Turns on the validator.
  ;
  ; @usage
  ; (enable-validator!)
  []
  (reset! state/ENABLED? true))
