
(ns validator.api
    (:require [validator.core :as core]
              [validator.reg  :as reg]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; validator.core
(def ignore!  core/ignore!)
(def valid?   core/valid?)
(def invalid? core/invalid?)

; validator.reg
(def reg-pattern! reg/reg-pattern!)
(def reg-test!    reg/reg-test!)
