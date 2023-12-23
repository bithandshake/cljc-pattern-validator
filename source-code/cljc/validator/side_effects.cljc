
(ns validator.side-effects
    (:require [validator.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn disable-validator!
  ; @description
  ; Turns off the validator.
  ;
  ; @usage
  ; (turn-off!)
  []
  (reset! state/ENABLED? false))

(defn enable-validator!
  ; @description
  ; Turns on the validator.
  ;
  ; @usage
  ; (turn-off!)
  []
  (reset! state/ENABLED? true))
