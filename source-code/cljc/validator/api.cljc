
(ns validator.api
    (:require [validator.core         :as core]
              [validator.reg          :as reg]
              [validator.side-effects :as side-effects]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (validator.core/*)
(def valid?   core/valid?)
(def invalid? core/invalid?)

; @redirect (validator.reg/*)
(def reg-test! reg/reg-test!)

; @redirect (validator.side-effects/*)
(def disable-validator! side-effects/disable-validator!)
(def enable-validator!  side-effects/enable-validator!)
