
(ns validator.reg
    (:require [validator.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn reg-pattern!
  ; @description
  ; Registers a reusable pattern with ID.
  ;
  ; @param (keyword) pattern-id
  ; @param (map) pattern
  ; {:my-key (map)
  ;   {:and* (functions in vector)(opt)
  ;     All functions in this vector must return TRUE.
  ;    :e* (string)
  ;     The error message.
  ;    :f* (function)(opt)
  ;     The function must be return TRUE.
  ;    :ign* (boolean)(opt)
  ;     If TRUE, the value will be ignored.
  ;    :nand* (functions in vector)(opt)
  ;     At least one of the functions in this vector must return FALSE.
  ;    :not* (function)(opt)
  ;     The function must be return FALSE.
  ;    :nor* (functions in vector)(opt)
  ;     All functions in this vector must return FALSE.
  ;    :opt* (boolean)(opt)
  ;     If TRUE, the value will be handled as optional.
  ;    :or* (functions in vector)(opt)
  ;     At least one of the functions in this vector must return TRUE.
  ;    :rep* (vector)(opt)
  ;     If the tested key does not exist in the map, at least one of
  ;     the keys in this vector must be in the n map.
  ;    :xor* (functions in vector)(opt)
  ;     At most one of the functions in this vector can returns with TRUE.}}
  ;
  ; @usage
  ; (reg-pattern! :my-pattern {...})
  ;
  ; @usage
  ; (reg-pattern! :my-pattern {:a {:f* string?
  ;                                :e* ":a must be a string!"}})
  [pattern-id pattern]
  (swap! state/PATTERNS assoc pattern-id pattern))

(defn reg-test!
  ; @description
  ; Registers a reusable test with ID.
  ;
  ; @param (keyword) test-id
  ; @param (map) test
  ; {:and* (functions in vector)(opt)
  ;   All functions in this vector must return TRUE.
  ;  :e* (string)
  ;   The error message.
  ;  :f* (function)(opt)
  ;   The function must be return TRUE.
  ;  :nand* (functions in vector)(opt)
  ;   At least one of the functions in this vector must return FALSE.
  ;  :not* (function)(opt)
  ;   The function must be return FALSE.
  ;  :nor* (functions in vector)(opt)
  ;   All functions in this vector must return FALSE.
  ;  :or* (functions in vector)(opt)
  ;   At least one of the functions in this vector must return TRUE.
  ;  :xor* (functions in vector)(opt)
  ;   At most one of the functions in this vector can returns with TRUE.}
  ;
  ; @usage
  ; (reg-test! :my-test {...})
  ;
  ; @usage
  ; (reg-test! :my-test {:f* string?
  ;                      :e* ":a must be a string!"})
  [test-id test]
  (swap! state/TESTS assoc test-id test))
