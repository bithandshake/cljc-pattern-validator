
(ns pattern.api
    (:require [pattern.core :as core]
              [pattern.reg  :as reg]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; pattern.core
(def ignore!  core/ignore!)
(def valid?   core/valid?)
(def invalid? core/invalid?)

; pattern.reg
(def reg-pattern! reg/reg-pattern!)
(def reg-test!    reg/reg-test!)
