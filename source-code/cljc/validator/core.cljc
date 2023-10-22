
(ns validator.core
    (:require [validator.state :as state]))

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
  ; {:allowed* (vector)(opt)
  ;   Defines the allowed keys of the given data (only use with map type data!).
  ;   Using the '{:strict* true}' setting could replace the using of the ':allowed*'
  ;   parameter.
  ;  :explain* (boolean)(opt)
  ;   If set to TRUE the error messages will be printed.
  ;   Default: true
  ;  :pattern* (map)(opt)
  ;   Defines the pattern of the given data (only use with map type data!).
  ;   {:my-key (map)
  ;     {:and* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with TRUE.
  ;      :e* (string)
  ;       The error message.
  ;      :f* (function)(opt)
  ;       The function has to return with TRUE.
  ;      :ign* (boolean)(opt)
  ;       If set to TRUE, the value will be ignored.
  ;      :nand* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with FALSE.
  ;      :not* (function)(opt)
  ;       The function has to return with FALSE.
  ;      :nor* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with FALSE.
  ;      :opt* (boolean)(opt)
  ;       If set to TRUE, the key will be handled as optional.
  ;       If the ':required*' parameter is in use, no need to mark optional keys
  ;       in the pattern. Key existence checking will be done with using the
  ;       ':required*' parameter.
  ;      :or* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with TRUE.
  ;      :rep* (vector)(opt)
  ;       If the tested key does not exist in the map, at least one of the keys
  ;       in this vector has to be in the n map in order to replace the missing key.
  ;       If the ':required*' parameter is in use, no need to specify replacement keys
  ;       for any keys in the pattern. Key existence checking will be done with
  ;       using the ':required*' parameter.
  ;      :xor* (functions in vector)(opt)
  ;       At most one of the functions in this vector can returns with TRUE.}}
  ;  :prefix* (string)(opt)
  ;   The ':prefix*' will be prepended to the value of ':e*' when an expection occurs.
  ;  :required* (vector)(opt)
  ;   Defines the required keys of the given data (only use with map type data!).
  ;  :strict* (boolean)(opt)
  ;   If set to TRUE, other keys in data than that are in the pattern will not be allowed!
  ;   Using the ':allowed*' parameter could replace the using of the '{:strict* true}' setting.
  ;   Default: false
  ;   W/ {:pattern* ...}
  ;  :test* (map)(opt)
  ;   {:and* (functions in vector)(opt)
  ;    :e* (string)
  ;    :f* (function)(opt)
  ;    :ign* (boolean)(opt)
  ;    :nand* (functions in vector)(opt)
  ;    :nor* (functions in vector)(opt)
  ;    :not* (function)(opt)
  ;    :opt* (boolean)(opt)
  ;    :or* (functions in vector)(opt)
  ;    :xor* (functions in vector)(opt)}}
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
  ;                         :e* "The map has to contain at least :a or :b!"}}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {}
  ;         {:pattern* {:a {:rep* [:b]
  ;                         :e* "The map has to contain at least :a or :b!"}}})
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
  [n {:keys [allowed* explain* pattern* prefix* required* strict* test*] :or {explain* true}}]
  (letfn [
          ; If the given ':pattern*' value ...
          ; ... is a map, returns it.
          ; ... is a keyword (ID of a registered pattern), reads the registered pattern and returns it.
          (p> [] (if (map? pattern*) pattern* (get @state/PATTERNS pattern*)))

          ; Returns the sorted keys of the pattern.
          ; Pattern keys only comparable with the keys of the given data if both vectors are sorted!
          (pks> [] (-> (p>) keys sort))

          ; Returns the sorted keys of the data.
          ; Keys of the given data are only comparable with pattern keys if both vectors are sorted!
          (nks> [] (-> n keys sort))

          ; Joins together the different parts of the error message.
          ; e: error message
          ; v: invalid value
          ; t: failed test
          (e> [e v t] (when strict* (println)
                                    (println "strict validation requires the following keys in data:")
                                    (println (pks>))
                                    (println)
                                    (println "validated data has the following keys:")
                                    (println (nks>)))
                      (println)
                      (if (nil? t) (println (str "validation failed on test:\nNIL"))
                                   (println (str "validation failed on test:\n" t)))
                      (println)
                      (if (nil? v) (println (str "validation failed on value:\nNIL"))
                                   (println (str "validation failed on value:\n" v)))
                      (println)
                      (if (nil? n) (println (str "validation failed in data:\nNIL"))
                                   (println (str "validation failed in data:\n" n)))
                      (println)
                      (if prefix* (str prefix* " " e)
                                  (str             e)))

          ; Throws an error.
          ; e: error message
          ; v: invalid value
          ; t: failed test
          (t> [e v t]
              #?(:clj  (throw (Exception. (e> e v t)))
                 :cljs (throw (js/Error.  (e> e v t)))))

          ; Returns TRUE if the validator has been turned off.
          (i? [] @state/IGNORED?)

          ; Returns with the given value if it is a function, otherwise throws an error.
          (c? [f*] (if (fn? f*) f* (t> :testing-method-must-be-a-function nil :f*)))

          ; Returns TRUE if the value is NIL but optional.
          (opt? [x {:keys [opt*]}]
                (and (nil? x) opt*))

          ; Returns TRUE if the value is NIL but replaced by another key's value
          ; from the ':rep*' vector (replacement keys).
          (rep? [x {:keys [rep*]}]
                (and (nil? x)
                     (some #(% n) rep*)))

          ; Returns TRUE if the value is NIL but the key is not in the 'required*'
          ; vector (required keys).
          (req? [x _]
                (and (nil? x) required*))

          ; Returns TRUE if at least one function in the 'and*' vector does not return
          ; with FALSE, ...
          (and? [x {:keys [and*]}]
                (and and* (some #(-> x % not) and*)))

          ; Returns TRUE if the 'f*' function returns with FALSE, ...
          (f? [x {:keys [f*]}]
              (and f* (-> x f* not)))

          ; Returns TRUE if all of the functions in the 'nand*' vector returns with TRUE, ...
          (nand? [x {:keys [nand*]}]
                 (and nand* (every? #(-> x %) nand*)))

          ; Returns TRUE if at least one function in the 'nor*' vector returns with TRUE, ...
          (nor? [x {:keys [nor*]}]
                (and nor* (some #(-> x %) nor*)))

          ; Returns TRUE if the 'not*' function returns with TRUE, ...
          (not? [x {:keys [not*]}]
                (and not* (not* x)))

          ; Returns TRUE if no function in the 'or*' vector returns with TRUE, ...
          (or? [x {:keys [or*]}]
               (and or* (not (some #(-> x %) or*))))

          ; Returns TRUE if not only one of the functions in the 'xor*' vector returns with TRUE, ...
          (xor? [x {:keys [xor*]}]
                (letfn [(f [r %] (if (% x) (inc r) r))]
                       (not= 1 (reduce f 0 xor*))))

          ; Runs all kind of tests on the passed 'x'.
          (t? [x {:keys [ign* e*] :as test*}]
              (cond ign*            :validation-skipped
                    (opt?  x test*) :missing-key-but-optional
                    (rep?  x test*) :missing-key-but-replaced
                    (req?  x test*) :missing-key-but-not-required
                    (nil?  x)       (t> e* x :nil?)
                    (and?  x test*) (t> e* x :and*)
                    (f?    x test*) (t> e* x :f*)
                    (nand? x test*) (t> e* x :nand*)
                    (nor?  x test*) (t> e* x :nor*)
                    (not?  x test*) (t> e* x :not*)
                    (or?   x test*) (t> e* x :or*)
                    :else :key-passed-all-the-tests))

          ; Takes a key and a 'test*' from the pattern and passes the value (get by the key)
          ; and the taken 'test*' to the testing function ('t?').
          (v? [[k test* :as x]]
              (t? (k n) test*))

          ; Strict-matching only happens in 'strict*' mode!
          ; Throws an error if the keys of 'n' map does not match the keys of pattern.
          (s? [] (or (= (nks>)
                        (pks>))
                     (t> :data-keys-does-not-match-with-pattern-keys nil :strict*)))

          ; ...
          (a? [] (or (-> allowed* set (remove (keys n)) empty?)
                     (t> :not-allowed-data-keys-found nil :allowed*)))

          ; ...
          (r? [] (or (-> n keys set (remove required*) empty?)
                     (t> :required-data-keys-missing nil :required*)))

          ; Throws an error if the 'n' is not a map.
          (m? [] (or (map? n)
                     (when explain* (println "Expected a map but got:" (-> n type)))
                     ; The println skipped (it returns with NIL), throwing an error
                     (t> :invalid-value nil nil)))

          ; Throws an error if the 'pattern*' is not a keyword or a map.
          (p? [] (or (map?     pattern*)
                     (keyword? pattern*)
                     (when explain* (println "Expected a keyword type pattern ID or a map type pattern but got:" (-> pattern* type))
                                    (println pattern*))
                     ; The println skipped (it returns with NIL), throwing an error ...
                     (t> :invalid-pattern nil :pattern*)))]

         (if (i?) :validation-turned-off
                  (boolean (try (and (or (not allowed*)
                                         (m?)                    ; <- Type-checking the 'n' (before searching for not allowed keys) ...
                                         (a?))                   ; <- Searching for (not allowed) keys in the map that are not defined
                                                                 ;    in the given ':allowed*' vector ...
                                     (or (not required*)
                                         (m?)                    ; <- Type-checking the 'n' (before searching for missing keys) ...
                                         (r?))                   ; <- Searching for missing (required) keys in the map that are defined
                                                                 ;    in the given ':required*' vector ...
                                     (or (not pattern*)
                                         (and (m?)               ; <- Type-checking the 'n' (before the 'pattern*' is getting processed) ...
                                              (p?)               ; <- Type-checking the 'pattern*' (before it's getting processed) ...
                                              (or (not strict*)  ; <- Only in 'strict*' mode, searching for extra keys in the map that
                                                  (s?))          ;    are not defined in the pattern ...
                                              (every? v? (p>)))) ; <- Validating the 'n' by every key of the 'pattern*' ...
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
  ; {:allowed* (vector)(opt)
  ;   Defines the allowed keys of the given data (only use with map type data!).
  ;   Using the '{:strict* true}' setting could replace the using of the ':allowed*'
  ;   parameter.
  ;  :explain* (boolean)(opt)
  ;   If set to TRUE the error message will be printed.
  ;   Default: true
  ;  :pattern* (map)(opt)
  ;   Defines the pattern of the given data (only use with map type data!).
  ;   {:my-key (map)
  ;     {:and* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with TRUE.
  ;      :e* (string)
  ;       The error message.
  ;      :f* (function)(opt)
  ;       The function has to return with TRUE.
  ;      :ign* (boolean)(opt)
  ;       If set to TRUE, the value will be ignored.
  ;      :nand* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with FALSE.
  ;      :not* (function)(opt)
  ;       The function has to return with FALSE.
  ;      :nor* (functions in vector)(opt)
  ;       All of the functions in this vector has to return with FALSE.
  ;      :opt* (boolean)(opt)
  ;       If set to TRUE, the key will be handled as optional.
  ;       If the ':required*' parameter is in use, no need to mark optional keys
  ;       in the pattern. Key existence checking will be done with using the
  ;       ':required*' parameter.
  ;      :or* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to return with TRUE.
  ;      :rep* (vector)(opt)
  ;       If the tested key does not exist in the map, at least one of the keys
  ;       in this vector has to be in the n map in order to replace the missing key.
  ;       If the ':required*' parameter is in use, no need to specify replacement keys
  ;       for any keys in the pattern. Key existence checking will be done with
  ;       using the ':required*' parameter.
  ;      :xor* (functions in vector)(opt)
  ;       At most one of the functions in this vector can returns with TRUE.}}
  ;  :prefix* (string)(opt)
  ;   The ':prefix*' will be prepend to the value of ':e*' when an expection occurs.
  ;  :required* (vector)(opt)
  ;   Defines the required keys of the given data (only use with map type data!).
  ;  :strict* (boolean)(opt)
  ;   If set to TRUE, other keys in data than that are in the pattern will not be allowed!
  ;   Using the ':allowed*' parameter could replace the using of the '{:strict* true}' setting.
  ;   Default: false
  ;   W/ {:pattern* ...}
  ;  :test* (map)(opt)
  ;   {:and* (functions in vector)(opt)
  ;    :e* (string)
  ;    :f* (function)(opt)
  ;    :ign* (boolean)(opt)
  ;    :nand* (functions in vector)(opt)
  ;    :nor* (functions in vector)(opt)
  ;    :not* (function)(opt)
  ;    :opt* (boolean)(opt)
  ;    :or* (functions in vector)(opt)
  ;    :xor* (functions in vector)(opt)}}
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
  ;                           :e* "The map has to contain at least :a or :b!"}}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {}
  ;           {:pattern* {:a {:rep* [:b]
  ;                           :e* "The map has to contain at least :a or :b!"}}})
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
