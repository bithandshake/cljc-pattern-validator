
(ns pattern.core
    (:require [pattern.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn ignore!
  ; @description
  ; Turns off the validating.
  ; After using it, the validating functions will return as in case with a valid data.
  ;
  ; @usage
  ; (ignore!)
  []
  (reset! state/IGNORED? true))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn valid?
  ; @description
  ; Checks whether the given data is valid or not.
  ;
  ; @param (*) n
  ; @param (map) options
  ; {:explain* (boolean)(opt)
  ;   If set to true the error message will be printed.
  ;   Default: true
  ;  :pattern* (map)
  ;   {:my-key (map)
  ;     {:and* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with true.
  ;      :e* (string)
  ;       The error message.
  ;      :f* (function)(opt)
  ;       The function has to return with true.
  ;      :ign* (function)(opt)
  ;       If this function returns with true, the value will be ignored.
  ;      :nand* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with false.
  ;      :not* (function)(opt)
  ;       The function has to return with false.
  ;      :nor* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with false.
  ;      :opt* (boolean)(opt)
  ;       If set to true, the value will be handled as optional.
  ;      :or* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with true.
  ;      :rep* (vector)(opt)
  ;       If the tested key does not exist in the map, at least one of
  ;       the keys in this vector has to be in the n map.
  ;      :xor* (functions in vector)(opt)
  ;       At most one of the functions in this vector can returns with true.}}
  ;  :prefix* (string)(opt)
  ;   The :prefix* will be prepend to the value of :e* when an expection occurs.
  ;  :test* (map)(opt)
  ;   {:and* (functions in vector)(opt)
  ;    :e* (string)
  ;    :ign* (boolean)(opt)
  ;    :nand* (functions in vector)(opt)
  ;    :nor* (functions in vector)(opt)
  ;    :not* (function)(opt)
  ;    :opt* (boolean)(opt)
  ;    :or* (functions in vector)(opt)
  ;    :xor* (functions in vector)(opt)}
  ;  :strict* (boolean)(opt)
  ;   If set to true, other keys than passed in the pattern will be not allowed!
  ;   Default: false
  ;   W/ {:pattern* ...}}
  ;
  ; @usage
  ; (valid? {:a "A"}
  ;         {:pattern* {:a {:f* string?}}})
  ;
  ; @example
  ; (valid? "A"
  ;         {:test* {:f* string?
  ;                  :e* "Value must be a string!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a "A"}
  ;         {:pattern* {:a {:f* string?
  ;                         :e* ":a must be a string!"}}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a "A"}
  ;         {:pattern* {:a {:or* [keyword? string?]
  ;                         :e* ":a must be a keyword or a string!"}}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a ""}
  ;         {:pattern* {:a {:and* [string? empty?]
  ;                         :e* ":a must be an empty string!"}}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:b "B"}
  ;         {:pattern* {:a {:rep* [:b]
  ;                         :e* "The map has to contains at least :a or :b!"}}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {}
  ;         {:pattern* {:a {:rep* [:b]
  ;                         :e* "The map has to contains at least :a or :b!"}}})
  ; =>
  ; false
  ;
  ; @example
  ; (valid? {:a "A"}
  ;         {:pattern* {:a {:f* [keyword?]
  ;                         :e* ":a must be a keyword but got: %"}}})
  ; =>
  ; false
  ;
  ; @example
  ; (reg :my-pattern {:a {:f* string?}})
  ; (valid? {:a "A"} {:pattern* :my-pattern})
  ; =>
  ; true
  ;
  ; @return (boolean)
  [n {:keys [explain* pattern* prefix* strict* test*] :or {explain* true}}]
  (letfn [
          ; Returns the pattern by itself or by the pattern-id
          (p> [] (if (map? pattern*) pattern* (get @state/PATTERNS pattern*)))

          ; Joins together the parts of the error message
          (e> [e x] (println)
                    (println (str "validation failed on value:\n" x))
                    (println)
                    (println (str "validation failed in data:\n"  n))
                    (println)
                    (if prefix* (str prefix* " " e)
                                (str             e)))

          ; Throws an error
          (t> [e x]
              #?(:clj  (throw (Exception. (e> e x)))
                 :cljs (throw (js/Error.  (e> e x)))))

          ; Returns true if the validator has been turned off
          (i? [] @state/IGNORED?)

          ; Returns back with the given value if it is a function, throws an error if it is not.
          (c? [f*] (if (fn? f*) f* (t> :testing-method-must-be-a-function)))

          ; Returns true if the key is not optional or not replaced by another key,
          ; and its value is nil, ...
          (req? [x {:keys [opt* rep*]}]
                (and (not opt*)
                     (not (and rep* (some #(% n) rep*)))
                     (-> x nil?)))

          ; Returns true if the is optional and its is nil
          (opt? [x {:keys [opt*]}]
                (and opt* (nil? x)))

          ; Returns true if at least one function in the and* vector does not return with false, ...
          (and? [x {:keys [and*]}]
                (and and* (some #(-> x % not) and*)))

          ; Returns true if the f* function returns with false, ...
          (f? [x {:keys [f*]}]
              (and f* (-> x f* not)))

          ; Returns true if all of the functions in the nand* vector returns with true, ...
          (nand? [x {:keys [nand*]}]
                 (and nand* (every? #(-> x %) nand*)))

          ; Returns true if at least one function in the nor* vector returns with true, ...
          (nor? [x {:keys [nor*]}]
                (and nor* (some #(-> x %) nor*)))

          ; Returns true if the not* function returns with true, ...
          (not? [x {:keys [not*]}]
                (and not* (not* x)))

          ; Returns true if no function in the or* vector returns with true, ...
          (or? [x {:keys [or*]}]
               (and or* (not (some #(-> x %) or*))))

          ; Returns true if not only one of the functions in the xor* vector returns with true, ...
          (xor? [x {:keys [xor*]}]
                (letfn [(f [r %] (if (% x) (inc r) r))]
                       (not= 1 (reduce f 0 xor*))))

          ; Runs all kind of tests on the passed x
          (t? [x {:keys [ign* e*] :as test*}]
              (cond ign*            :validation-skipped
                    (opt?  x test*) :optional-and-not-passed
                    (req?  x test*) (t> e* x)
                    (and?  x test*) (t> e* x)
                    (f?    x test*) (t> e* x)
                    (nand? x test*) (t> e* x)
                    (nor?  x test*) (t> e* x)
                    (not?  x test*) (t> e* x)
                    (or?   x test*) (t> e* x)
                    :else :key-passed-all-of-the-tests))

          ; Takes a key and a test* from the pattern and passes the value (get by the key)
          ; and the taken test* to the testing function (t?)
          (v? [[k test* :as x]]
              (t? (k n) test*))

          ; Strict-matching only happens in strict* mode!
          ; Throws an error if there are some extra keys in the n
          (s? [] (or (not strict*)
                     (= (keys  n)
                        (keys (p>)))
                     (t> :strict-matching-failed nil)))

          ; Throws an error if the n is not a map
          (m? [] (or (map? n)
                     (when explain* (println "Expected a map but got:" (-> n type)))
                     ; The println skipped (it returns with nil), throwing an error
                     (t> :invalid-value nil)))

          ; Throws an error if the pattern* is not a keyword or a map
          (p? [] (or (map?     pattern*)
                     (keyword? pattern*)
                     (when explain* (println "Expected a keyword type pattern-id or a map type pattern but got:" (-> pattern* type))
                                    (println pattern*))
                     ; The println skipped (it returns with nil), throwing an error
                     (t> :invalid-pattern nil)))]

         (if (i?) :validating-ignored
                  (boolean (try (and (or (not pattern*)
                                         (and (m?)             ; <- Type-checking the n (before the pattern* is getting processed)
                                              (p?)             ; <- Type-checking the pattern* (before its getting processed)
                                              (every? v? (p>)) ; <- Validating the n with every key of the pattern*
                                              (s?)))           ; <- After the validation and only in strict* mode, searching for extra keys in the map
                                     (or (not  test*)
                                         (t? n test*)))
                                #?(:clj  (catch Exception e (if explain* (do (-> e         println))))
                                   :cljs (catch :default  e (if explain* (do (-> e .-stack println))))))))))

(defn invalid?
  ; @description
  ; Checks whether the given data is invalid or not.
  ;
  ; @param (*) n
  ; @param (map) options
  ; {:explain* (boolean)(opt)
  ;   If set to true the error message will be printed.
  ;   Default: true
  ;  :pattern* (map)
  ;   {:my-key (map)
  ;     {:and* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with true.
  ;      :e* (string)
  ;       The error message.
  ;      :f* (function)(opt)
  ;       The function has to return with true.
  ;      :ign* (function)(opt)
  ;       If this function returns with true, the value will be ignored.
  ;      :nand* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with false.
  ;      :not* (function)(opt)
  ;       The function has to return with false.
  ;      :nor* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with false.
  ;      :opt* (boolean)(opt)
  ;       If set to true, the value will be handled as optional.
  ;      :or* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with true.
  ;      :rep* (vector)(opt)
  ;       If the tested key does not exist in the map, at least one of
  ;       the keys in this vector has to be in the n map.
  ;      :xor* (functions in vector)(opt)
  ;       At most one of the functions in this vector can returns with true.}}
  ;  :prefix* (string)(opt)
  ;   The :prefix* will be prepend to the value of :e* when an expection occurs.
  ;  :test* (map)(opt)
  ;   {:and* (functions in vector)(opt)
  ;    :e* (string)
  ;    :ign* (boolean)(opt)
  ;    :nand* (functions in vector)(opt)
  ;    :nor* (functions in vector)(opt)
  ;    :not* (function)(opt)
  ;    :opt* (boolean)(opt)
  ;    :or* (functions in vector)(opt)
  ;    :xor* (functions in vector)(opt)}
  ;  :strict* (boolean)(opt)
  ;   If set to true, other keys than passed in the pattern will be not allowed!
  ;   Default: false
  ;   W/ {:pattern* ...}}
  ;
  ; @usage
  ; (invalid? {:a "A"}
  ;           {:pattern* {:a {:f* string?}}})
  ;
  ; @example
  ; (invalid? "A"
  ;           {:test* {:f* string?
  ;                    :e* "Value must be a string!"}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {:a "A"}
  ;           {:pattern* {:a {:f* string?
  ;                           :e* ":a must be a string!"}}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {:a "A"}
  ;           {:pattern* {:a {:or* [keyword? string?]
  ;                           :e* ":a must be a keyword or a string!"}}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {:a ""}
  ;           {:pattern* {:a {:and* [string? empty?]
  ;                           :e* ":a must be an empty string!"}}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {:b "B"}
  ;           {:pattern* {:a {:rep* [:b]
  ;                           :e* "The map has to contains at least :a or :b!"}}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {}
  ;           {:pattern* {:a {:rep* [:b]
  ;                           :e* "The map has to contains at least :a or :b!"}}})
  ; =>
  ; true
  ;
  ; @example
  ; (reg :my-pattern {:a {:f* string?}})
  ; (invalid? {:a "A"} {:pattern* :my-pattern})
  ; =>
  ; false
  ;
  ; @return (boolean)
  [n options]
  (not (valid? n options)))
