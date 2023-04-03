
(ns patterns.reg
    (:require [patterns.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn reg-pattern!
  ; @description
  ; Registers a reusable pattern with id.
  ;
  ; @param (keyword) pattern-id
  ; @param (map) pattern
  ; {:my-key (map)
  ;   {:and* (functions in vector)(opt)
  ;     All of the functions in this vector has to return with true.
  ;    :e* (string)
  ;     The error message.
  ;    :f* (function)(opt)
  ;     The function has to be return with true.
  ;    :ign* (function)(opt)
  ;     If this function returns with true, the value will be ignored.
  ;    :nand* (functions in vector)(opt)
  ;     At least one of the functions in this vector has to return with false.
  ;    :not* (function)(opt)
  ;     The function has to be return with false.
  ;    :nor* (functions in vector)(opt)
  ;     All of the functions in this vector has to return with false.
  ;    :opt* (boolean)(opt)
  ;     If set to true, the value will be handled as optional.
  ;    :or* (functions in vector)(opt)
  ;     At least one of the functions in this vector has to return with true.
  ;    :rep* (vector)(opt)
  ;     If the tested key does not exist in the map, at least one of
  ;     the keys in this vector has to be in the n map.
  ;    :xor* (functions in vector)(opt)
  ;     At most one of the functions in this vector can returns with true.}}
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
  ; Registers a reusable test with id.
  ;
  ; @param (keyword) test-id
  ; @param (map) test
  ; {:and* (functions in vector)(opt)
  ;   All of the functions in this vector has to return with true.
  ;  :e* (string)
  ;   The error message.
  ;  :f* (function)(opt)
  ;   The function has to be return with true.
  ;  :nand* (functions in vector)(opt)
  ;   At least one of the functions in this vector has to return with false.
  ;  :not* (function)(opt)
  ;   The function has to be return with false.
  ;  :nor* (functions in vector)(opt)
  ;   All of the functions in this vector has to return with false.
  ;  :or* (functions in vector)(opt)
  ;   At least one of the functions in this vector has to return with true.
  ;  :xor* (functions in vector)(opt)
  ;   At most one of the functions in this vector can returns with true.}
  ;
  ; @usage
  ; (reg-test! :my-test {...})
  ;
  ; @usage
  ; (reg-test! :my-test {:f* string?
  ;                      :e* ":a must be a string!"})
  [test-id test]
  (swap! state/TESTS assoc test-id test))
