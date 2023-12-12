
(ns validator.core
    (:require [validator.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn valid?
  ; @description
  ; Returns TRUE if the given data passes the given test.
  ;
  ; @param (*) n
  ; @param (keyword or map) test
  ; Defines a set of functions for testing the given data.
  ; If keyword it identifies a previously registered reusable test.
  ; {:allowed* (vector)(opt)
  ;   Defines the allowed keys of the value (for map type values).
  ;  :and* (functions in vector)(opt)
  ;   All functions in this vector must return TRUE.
  ;  :e* (string)
  ;   The error message (printed if any test fails).
  ;  :f* (function)(opt)
  ;   This function must return TRUE.
  ;  :ign* (boolean)(opt)
  ;   If TRUE, all tests will be ignored.
  ;  :nand* (functions in vector)(opt)
  ;   At least one function in this vector must return FALSE.
  ;  :nor* (functions in vector)(opt)
  ;   All functions in this vector must return FALSE.
  ;  :not* (function)(opt)
  ;   This function must return FALSE.
  ;  :opt* (boolean)(opt)
  ;   If TRUE, the value is allowed to be NIL.
  ;  :or* (functions in vector)(opt)
  ;   At least one function in this vector must return TRUE.
  ;  :required* (vector)(opt)
  ;   Defines the required keys of the value (for map type values).
  ;  :xor* (functions in vector)(opt)
  ;   At most one function in this vector can return TRUE.
  ;  :my-key (map)(opt)
  ;   Test functions under custom keys are applied on the corresponding value (for map type data).
  ;   Custom keys must be present in the given data also.
  ;   {:rep* (vector)(opt)
  ;     Vector of keys that could replace a specific key in the value (if missing or NIL).}}
  ; @param (map)(opt) options
  ; {:explain? (boolean)(opt)
  ;   If TRUE, the error messages will be printed.
  ;   Default: true
  ;  :prefix (string)(opt)
  ;   Prepended to the error message.}
  ;
  ; @example
  ; (valid? "abc"
  ;         {:f* string? :e* "Value must be a string!"})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a "A"}
  ;         {:f* map? :e* "Value must be a map!"}
  ;          :a {:f* string? :not* empty? :e* "Key :a must be a nonempty string!"})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a "A"}
  ;         {:a {:or* [keyword? string?] :e* "Key :a must be a keyword or a string!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a ""}
  ;         {:a {:and* [string? empty?] :e* "Key :a must be an empty string!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:b "B"}
  ;         {:a {:rep* [:b] :e* "Value must contain at least one of key :a or key :b!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {}
  ;         {:a {:rep* [:b] :e* "Value must contain at least one of key :a or key :b!"}})
  ; =>
  ; false
  ;
  ; @example
  ; (valid? {:a "a"}
  ;         {:required* [:a :b] :e* "Value must contain key :a and key :b!"}
  ; =>
  ; false
  ;
  ; @example
  ; (valid? {:a "a"}
  ;         {:allowed* [:a :b] :e* "Value can contain only key :a and key :b!"}
  ; =>
  ; true
  ;
  ; @example
  ; (reg-test! :my-test {:a {:f* string?}})
  ; (valid? {:a "A"} :my-test)
  ; =>
  ; true
  ;
  ; @return (boolean)
  ([n test]
   (valid? n test {}))

  ([n test {:keys [explain? prefix] :or {explain? true}}]
   (letfn [; If the given 'test' value ...
           ; ... is a map, returns it.
           ; ... is a keyword (ID of a registered test), returns the registered test.
           (tst [test] (cond (map? test) test (keyword? test) (get @state/TESTS test)))

           ; Assembles the error message.
           (asm> [x test t] (str "\n\nvalidation failed on test:\n"  (if (nil? t) "NIL" t)
                                 "\n\nvalidation failed on value:\n" (if (nil? x) "NIL" x)
                                 "\n\nvalidation failed on data:\n"  (if (nil? n) "NIL" n)
                                 "\n\n" prefix (if prefix " ") (:e* test) "\n"))

           ; Throws an error.
           (err> [x test t] #?(:clj  (throw (Exception. (do (println (asm> x test t)) (str test))))
                               :cljs (throw (js/Error.  (do (println (asm> x test t)) (str test))))))

           ; Returns TRUE if the validator has been turned off.
           (off? [] @state/TURNED-OFF?)

           ; Returns TRUE if the given test must be ignored.
           (ign? [_ {:keys [ign*]}] ign*)

           ; Returns TRUE if the given value is NIL, but optional.
           (opt? [x {:keys [opt*]}]
                 (and opt* (nil? x)))

           ; Returns TRUE if the given value is NIL, but replaced with another key in the given data.
           (rep? [x {:keys [rep*]}]
                 (and rep* (nil? x) (rep* n rep*)))

           ; Type-checks the given value. Throws an error if fails.
           (chk* [x chk*]
                 (or ((:type* chk*) x) (err> x chk* :chk*)))

           ; Returns TRUE if the given value passes the given rep* test
           ; (i.e., the given value contains any key from the given 'rep*' vector).
           (rep* [x rep*]
                 (chk* rep* {:type* vector?  :e* :rep*-must-be-vector})
                 (chk* x    {:type* seqable? :e* :unable-check-replacement-key/non-seqable-data})
                 (some #(get x %) rep*))

           ; Returns TRUE if the given value passes the given awd* test
           ; (i.e., the given value contains keys only from the given 'awd*' vector).
           (awd* [x awd*]
                 (chk* awd* {:type* vector?  :e* :allowed*-must-be-vector})
                 (chk* x    {:type* seqable? :e* :unable-check-allowed-key/non-seqable-data})
                 (-> awd* set (remove (keys x)) empty?))

           ; Returns TRUE if the given value passes the given rqd* test
           ; (i.e., the given value contains all keys from the given 'rqd*' vector).
           (rqd* [x rqd*]
                 (chk* rqd* {:type* vector?  :e* :required*-must-be-vector})
                 (chk* x    {:type* seqable? :e* :unable-check-required-keys/non-seqable-data})
                 (-> x keys set (remove rqd*) empty?))

           ; Returns TRUE if the given value passes the given and* test
           ; (i.e., all function in the given 'and*' vector returns TRUE).
           (and* [x and*]
                 (chk* and* {:type* vector? :e* :and*-must-be-vector})
                 (or (every? #(f* x %) and*)))

           ; Returns TRUE if the given value passes the given f* test
           ; (i.e., the given 'f*' function returns TRUE).
           (f* [x f*]
               (chk* f* {:type* ifn? :e* :f*-must-be-function})
               (f* x))

           ; Returns TRUE if the given value passes the given nand* test
           ; (i.e., at least one function in the given 'nand*' vector returns FALSE).
           (nand* [x nand*]
                  (chk* nand* {:type* vector? :e* :nand*-must-be-vector})
                  (some #(not* x %) nand*))

           ; Returns TRUE if the given value passes the given nor* test
           ; (i.e., all function in the given 'nor*' vector returns FALSE).
           (nor* [x nor*]
                 (chk* nor* {:type* vector? :e* :nor*-must-be-vector})
                 (every? #(not* x %) nor*))

           ; Returns TRUE if the given value passes the given not* test
           ; (i.e., the given 'not*' function returns TRUE).
           (not* [x not*]
                 (chk* not* {:type* ifn? :e* :not*-must-be-function})
                 (-> x not* not))

           ; Returns TRUE if the given value passes the given or* test
           ; (i.e., at least one function in the given 'or*' vector returns TRUE).
           (or* [x or*]
                (chk* or* {:type* vector? :e* :or*-must-be-vector})
                (some #(f* x %) or*))

           ; Returns TRUE if the given value passes the given xor* test
           ; (i.e., at most one function in the given 'xor*' vector returns TRUE).
           (xor* [x xor*]
                 (chk* xor* {:type* vector? :e* :xor*-must-be-vector})
                 (loop [? false [% :as xor*] xor*]
                       (cond (-> xor* count zero?) true
                             (f* x %) (if ? false (recur true (drop xor*))))))

           ; Returns TRUE if the given value passes the given test stage.
           (tst* [x k t]
                 (case k :e*        :non-test-key
                         :opt*      :non-test-key
                         :rep*      :non-test-key
                         :and*      (and*  x t)
                         :f*        (f*    x t)
                         :nand*     (nand* x t)
                         :nor*      (nor*  x t)
                         :not*      (not*  x t)
                         :or*       (or*   x t)
                         :xor*      (xor*  x t)
                         :allowed*  (awd*  x t)
                         :required* (rqd*  x t)
                                    (vld? (get x k) (tst t)))) ; <- Recursive test for a custom key.

           ; Returns TRUE if the given value passes all stages from the given test.
           (vld> [x {:keys [e*] :as test}]
                 (if (nil? x) (err> x test :nil?))
                 (every? (fn [[k t]] (or (tst* x k t) (err> x test k))) test))

           ; Returns TRUE if the given value is valid.
           (vld? [x test]
                 (chk* test {:type* map? :e* :test-must-be-map})
                 (cond (off?)        :validation-turned-off
                       (ign? x test) :skip-validation
                       (opt? x test) :nil-value-but-optional
                       (rep? x test) :nil-value-but-replaced
                       :validation (vld> x test)))]

          ; ...
          #?(:clj  (boolean (try (vld? n (tst test)) (catch Exception e (if explain? (-> e         println)))))
             :cljs (boolean (try (vld? n (tst test)) (catch :default  e (if explain? (-> e .-stack println)))))))))

(defn invalid?
  ; @description
  ; Returns TRUE if the given data fails the given test.
  ;
  ; @param (*) n
  ; @param (keyword or map) test
  ; @param (map)(opt) options
  ;
  ; @return (boolean)
  ([n test]
   (invalid? n test {}))

  ([n test options]
   (-> n (valid? test options) not)))
