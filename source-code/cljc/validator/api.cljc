
(ns validator.api
    (:require [validator.core :as core]
              [validator.reg  :as reg]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (validator.core)
(def ignore!  core/ignore!)
(def valid?   core/valid?)
(def invalid? core/invalid?)

; @redirect (validator.reg)
(def reg-pattern! reg/reg-pattern!)
(def reg-test!    reg/reg-test!)
