
(ns validator.core
    (:require [validator.env :as env]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn valid?
  ; @description
  ; Returns TRUE if the given data passes the given test.
  ;
  ; @param (*) data
  ; @param (keywords and/or maps in vector) tests
  ; [(keyword or map) test
  ;    {:allowed* (vector)(opt)
  ;     :and* (functions in vector)(opt)
  ;     :e* (string)
  ;     :f* (function)(opt)
  ;     :ign* (boolean)(opt)
  ;     :nand* (functions in vector)(opt)
  ;     :nor* (functions in vector)(opt)
  ;     :not* (function)(opt)
  ;     :opt* (boolean)(opt)
  ;     :or* (functions in vector)(opt)
  ;     :required* (vector)(opt)
  ;     :xor* (functions in vector)(opt)
  ;     :my-custom-key (map)(opt)
  ;      {:rep* (vector)(opt)
  ;       ...}
  ;     ...}]
  ; @param (map)(opt) options
  ; {:explain? (boolean)(opt)
  ;   Default: true
  ;  :prefix (string)(opt)}
  ;
  ; @usage
  ; (valid? "abc"
  ;         [{:f* string? :e* "Value must be a string!"}])
  ; =>
  ; true
  ;
  ; @usage
  ; (valid? {:a "A"}
  ;         [{:f* map? :e* "Value must be a map!"}
  ;           :a {:f* string? :not* empty? :e* "Key ':a' must be a nonempty string!"}])
  ; =>
  ; true
  ;
  ; @usage
  ; (valid? {:a "A"}
  ;         [{:a {:or* [keyword? string?] :e* "Key ':a' must be a keyword or a string!"}}])
  ; =>
  ; true
  ;
  ; @usage
  ; (valid? {:a ""}
  ;         [{:a {:and* [string? empty?] :e* "Key ':a' must be an empty string!"}}])
  ; =>
  ; true
  ;
  ; @usage
  ; (valid? {:b "B"}
  ;         [{:a {:rep* [:b] :e* "Value must contain at least one of key ':a' or key ':b'!"}}])
  ; =>
  ; true
  ;
  ; @usage
  ; (valid? {}
  ;         [{:a {:rep* [:b] :e* "Value must contain at least one of key ':a' or key ':b'!"}}])
  ; =>
  ; false
  ;
  ; @usage
  ; (valid? {:a "a"}
  ;         [{:required* [:a :b] :e* "Value must contain key ':a' and key ':b'!"}])
  ; =>
  ; false
  ;
  ; @usage
  ; (valid? {:a "a"}
  ;         [{:allowed* [:a :b] :e* "Value cannot contain keys other than key ':a' or key ':b'!"}])
  ; =>
  ; true
  ;
  ; @usage
  ; (reg-test! :my-test {:a {:f* string?}})
  ; (valid? {:a "A"} [:my-test])
  ; =>
  ; true
  ;
  ; @return (boolean)
  ([data tests]
   (valid? data tests {}))

  ([data tests {:keys [explain? prefix] :or {explain? true}}]
   (letfn [; If the given 'test' value ...
           ; ... is a map, returns it.
           ; ... is a keyword (ID of a registered test), returns the registered test.
           (tst [test] (cond (map? test) test (keyword? test) (env/get-test test)))

           ; Assembles the error message.
           (asm> [x test t] (str "\n\nvalidation failed on test:\n"  (if (nil? test) "NIL" test)
                                 "\n\nvalidation failed at stage:\n" (if (nil? t)    "NIL" t)
                                 "\n\nvalidation failed on value:\n" (if (nil? x)    "NIL" x)
                                 "\n\nvalidation failed on data:\n"  (if (nil? data) "NIL" data)
                                 "\n\n" prefix (if prefix " ") (:e* test) "\n"))

           ; Throws an error.
           (err> [x test t] #?(:clj  (throw (Exception. (do (println (asm> x test t)) (str test))))
                               :cljs (throw (js/Error.  (do (println (asm> x test t)) (str test))))))

           ; Returns TRUE if the validator has been turned off.
           (dis? [] (env/validator-disabled?))

           ; Returns TRUE if the given test must be ignored.
           (ign? [_ test]
                 (:ign* test))

           ; Returns TRUE if the given value is NIL, but optional.
           (opt? [x test]
                 (and (:opt* test) (nil? x)))

           ; Returns TRUE if the given value is NIL, but replaced with another key within the given data.
           (rep? [x test]
                 (and (:rep* test) (nil? x) (rep* data (:rep* test))))

           ; Type-checks the given value. Throws an error if fails.
           (chk? [x test]
                 (or ((:type* test) x) (err> x test :type*)))

           ; Returns TRUE if the given value passes the given 'rep*' test
           ; (i.e., the given value contains any key from the given 'rep*' vector).
           (rep* [x rep*]
                 (chk? rep* {:type* vector?  :e* :rep*-must-be-a-vector})
                 (chk? x    {:type* seqable? :e* :unable-to-check-replacement-key/non-seqable-data-has-been-provided})
                 (some #(get x %) rep*))

           ; Returns TRUE if the given value passes the given 'awd*' test
           ; (i.e., the given value contains keys only from the given 'awd*' vector).
           (awd* [x awd*]
                 (chk? awd* {:type* vector?  :e* :allowed*-must-be-a-vector})
                 (chk? x    {:type* seqable? :e* :unable-to-check-allowed-key/non-seqable-data-has-been-provided})
                 (-> awd* set (remove (keys x)) empty?))

           ; Returns TRUE if the given value passes the given 'rqd*' test
           ; (i.e., the given value contains all keys from the given 'rqd*' vector).
           (rqd* [x rqd*]
                 (chk? rqd* {:type* vector?  :e* :required*-must-be-a-vector})
                 (chk? x    {:type* seqable? :e* :unable-to-check-required-keys/non-seqable-data-has-been-provided})
                 (-> x keys set (remove rqd*) empty?))

           ; Returns TRUE if the given value passes the given 'and*' test
           ; (i.e., all function in the given 'and*' vector returns TRUE).
           (and* [x and*]
                 (chk? and* {:type* vector? :e* :and*-must-be-a-vector})
                 (or (every? #(f* x %) and*)))

           ; Returns TRUE if the given value passes the given 'f*' test
           ; (i.e., the given 'f*' function returns TRUE).
           (f* [x f*]
               (chk? f* {:type* ifn? :e* :f*-must-be-a-function})
               (f* x))

           ; Returns TRUE if the given value passes the given 'nand*' test
           ; (i.e., at least one function in the given 'nand*' vector returns FALSE).
           (nand* [x nand*]
                  (chk? nand* {:type* vector? :e* :nand*-must-be-a-vector})
                  (some #(not* x %) nand*))

           ; Returns TRUE if the given value passes the given 'nor*' test
           ; (i.e., all function in the given 'nor*' vector returns FALSE).
           (nor* [x nor*]
                 (chk? nor* {:type* vector? :e* :nor*-must-be-a-vector})
                 (every? #(not* x %) nor*))

           ; Returns TRUE if the given value passes the given 'not*' test
           ; (i.e., the given 'not*' function returns TRUE).
           (not* [x not*]
                 (chk? not* {:type* ifn? :e* :not*-must-be-a-function})
                 (-> x not* not))

           ; Returns TRUE if the given value passes the given 'or*' test
           ; (i.e., at least one function in the given 'or*' vector returns TRUE).
           (or* [x or*]
                (chk? or* {:type* vector? :e* :or*-must-be-a-vector})
                (some #(f* x %) or*))

           ; Returns TRUE if the given value passes the given 'xor*' test
           ; (i.e., at most one function in the given 'xor*' vector returns TRUE).
           (xor* [x xor*]
                 (chk? xor* {:type* vector? :e* :xor*-must-be-a-vector})
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
                                    (vld? (get x k) (tst t)))) ; <- Any custom key of test (for testing nested values).

           ; Returns TRUE if the given value passes all stages from the given test.
           (vld> [x {:keys [e*] :as test}]
                 (if (nil? x) (err> x test :nil?))
                 (every? (fn [[k t]] (or (tst* x k t) (err> x test k))) test))

           ; Returns TRUE if the given value is valid.
           (vld? [x test]
                 (let [test (tst test)]
                      (chk? test {:type* map? :e* :test-must-be-a-map})
                      (cond (dis?)        :validation-is-turned-off
                            (ign? x test) :ignoring-validation
                            (opt? x test) :value-is-nil-but-optional
                            (rep? x test) :value-is-nil-but-replaced
                            :validating-data (vld> x test))))]

          ; ...
          #?(:clj  (boolean (try (every? (fn [%] (vld? data %)) tests) (catch Exception e (if explain? (-> e         println)))))
             :cljs (boolean (try (every? (fn [%] (vld? data %)) tests) (catch :default  e (if explain? (-> e .-stack println)))))))))

(defn invalid?
  ; @description
  ; Returns TRUE if the given data fails the given test.
  ;
  ; @param (*) data
  ; @param (keywords and/or maps in vector) tests
  ; [(keyword or map) test
  ;    {:allowed* (vector)(opt)
  ;     :and* (functions in vector)(opt)
  ;     :e* (string)
  ;     :f* (function)(opt)
  ;     :ign* (boolean)(opt)
  ;     :nand* (functions in vector)(opt)
  ;     :nor* (functions in vector)(opt)
  ;     :not* (function)(opt)
  ;     :opt* (boolean)(opt)
  ;     :or* (functions in vector)(opt)
  ;     :required* (vector)(opt)
  ;     :xor* (functions in vector)(opt)
  ;     :my-custom-key (map)(opt)
  ;      {:rep* (vector)(opt)
  ;       ...}
  ;     ...}]
  ; @param (map)(opt) options
  ; {:explain? (boolean)(opt)
  ;   Default: true
  ;  :prefix (string)(opt)}
  ;
  ; @return (boolean)
  ([data tests]
   (invalid? data tests {}))

  ([data tests options]
   (-> data (valid? tests options) not)))
