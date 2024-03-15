
(ns validator.api
    (:require [validator.core         :as core]
              [validator.env          :as env]
              [validator.reg          :as reg]
              [validator.side-effects :as side-effects]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @tutorial Tests
;
; @title As a map
; ;; A test can be provided as a map that defines a set of functions for testing data.
; (def MY-TEST
;      {:allowed* (vector)(opt)           ;; <- Defines the allowed keys of the data (for map type data).
;       :and* (functions in vector)(opt)  ;; <- All functions in this vector must return TRUE.
;       :e* (string)                      ;; <- The error message (printed if any test fails).
;       :f* (function)(opt)               ;; <- This function must return TRUE.
;       :ign* (boolean)(opt)              ;; <- If TRUE, all tests will be ignored.
;       :nand* (functions in vector)(opt) ;; <- At least one function in this vector must return FALSE.
;       :nor* (functions in vector)(opt)  ;; <- All functions in this vector must return FALSE.
;       :not* (function)(opt)             ;; <- This function must return FALSE.
;       :opt* (boolean)(opt)              ;; <- If TRUE, the value is allowed to be NIL.
;       :or* (functions in vector)(opt)   ;; <- At least one function in this vector must return TRUE.
;       :required* (vector)(opt)          ;; <- Defines the required keys of the data (for map type data).
;       :xor* (functions in vector)(opt)  ;; <- At most one function in this vector can return TRUE.
;       :my-custom-key (map)(opt)         ;; <- Test functions under custom keys are applied on the corresponding value (for map type data).
;        {:rep* (vector)(opt)}})          ;; <- Vector of keys that could replace a specific key (if missing or NIL) in the data.
;
; @title As a keyword
; ;; A test can be provided as a keyword, that identifies a registered reusable test.
; (reg-test! :my-test MY-TEST)

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @tutorial Options
;
; {:explain? (boolean)(opt) ;; <- If TRUE, error messages are printed to the console.
;  :prefix (string)(opt)}   ;; <- Prepended to error messages.

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

; @redirect (validator.core/*)
(def valid?   core/valid?)
(def invalid? core/invalid?)

; @redirect (validator.env/*)
(def get-test  env/get-test)
(def disabled? env/disabled?)

; @redirect (validator.reg/*)
(def reg-test! reg/reg-test!)

; @redirect (validator.side-effects/*)
(def disable-validator! side-effects/disable-validator!)
(def enable-validator!  side-effects/enable-validator!)
