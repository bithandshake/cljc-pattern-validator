
(ns validator.api
    (:require [validator.core :as core]
              [validator.side-effects :as side-effects]
              [validator.reg :as reg]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (validator.core/*)
(def valid?   core/valid?)
(def invalid? core/invalid?)

; @redirect (validator.reg/*)
(def reg-test! reg/reg-test!)

; @redirect (validator.side-effects/*)
(def turn-off! side-effects/turn-off!)
