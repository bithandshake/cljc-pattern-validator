
(ns patterns.api
    (:require [patterns.core :as core]
              [patterns.reg  :as reg]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; patterns.core
(def ignore!  core/ignore!)
(def valid?   core/valid?)
(def invalid? core/invalid?)

; patterns.reg
(def reg-pattern! reg/reg-pattern!)
(def reg-test!    reg/reg-test!)
