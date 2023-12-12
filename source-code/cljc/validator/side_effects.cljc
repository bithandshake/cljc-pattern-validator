
(ns validator.side-effects
    (:require [validator.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn turn-off!
  ; @description
  ; Turns off the validator.
  ;
  ; @usage
  ; (turn-off!)
  []
  (reset! state/TURNED-OFF? true))
