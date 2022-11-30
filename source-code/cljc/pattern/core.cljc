
(ns pattern.core
    (:require [pattern.state :as state]
              [string.api    :as string]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn ignore!
  ; @usage
  ; (ignore!)
  []
  ; Turning off the validator ...
  (reset! state/IGNORED? true))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn reg!
  ; @param (keyword) pattern-id
  ; @param (map) pattern
  ; {:my-key (map)
  ;   {:and* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with true.
  ;    :e* (string)
  ;     The error message.
  ;    :f* (function)(opt)
  ;     The function has to be returns with true.
  ;    :ign* (function)(opt)
  ;     If this function returns with true, the value will be ignored.
  ;    :not* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with false.
  ;    :opt* (boolean)(opt)
  ;     If this set to true, the value will be handled as optional.
  ;    :or* (functions in vector)(opt)
  ;     At least one of the functions in this vector has to be returns with true.
  ;    :rep* (vector)(opt)
  ;     If the tested key does not exist in the map, at least one of
  ;     the keys in this vector has to be in the n map.}}
  ;
  ; @usage
  ; (reg! :my-pattern {...})
  ;
  ; @usage
  ; (reg! :my-pattern {:a {:f* string?
  ;                       :e* ":a must be a string!"}})
  [pattern-id pattern]
  (swap! state/PATTERNS assoc pattern-id pattern))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn valid?
  ; @param (*) n
  ; @param (map) options
  ; {:explain* (boolean)(opt)
  ;   Default: true
  ;  :pattern* (map)
  ;   {:my-key (map)
  ;     {:and* (functions in vector)(opt)
  ;       All of the functions in this vector has to be returns with true.
  ;      :e* (string)
  ;       The error message.
  ;      :f* (function)(opt)
  ;       The function has to be returns with true.
  ;      :ign* (function)(opt)
  ;       If this function returns with true, the value will be ignored.
  ;      :not* (functions in vector)(opt)
  ;       All of the functions in this vector has to be returns with false.
  ;      :opt* (boolean)(opt)
  ;       If this set to true, the value will be handled as optional.
  ;      :or* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to be returns with true.
  ;      :rep* (vector)(opt)
  ;       If the tested key does not exist in the map, at least one of
  ;       the keys in this vector has to be in the n map.}}
  ;  :prefix* (string)(opt)
  ;   The :prefix* will be prepend to the value of :e* when an expection occurs.
  ;  :test* (map)(opt)
  ;   {:and* (functions in vector)(opt)
  ;    :e* (string)
  ;    :ign* (boolean)(opt)
  ;    :not* (functions in vector)(opt)
  ;    :opt* (boolean)(opt)
  ;    :or* (functions in vector)(opt)}
  ;  :strict* (boolean)(opt)
  ;   If this set to true, other keys than passed in the pattern will be not allowed!
  ;   Default: false
  ;   W/ {:pattern* ...}}
  ;
  ; @usage
  ; (valid? {:a "A"}
  ;         {:pattern* {:a {:f* string?}}})
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
  (letfn [(p> [] (if (map? pattern*) pattern* (get @state/PATTERNS pattern*)))
          (e> [e x] (-> e (string/prefix prefix* " ")
                          (string/use-replacement x {:ignore? false})))
          (t> [e x] ; <- Throwing an error
              #?(:clj  (throw (Exception. (e> e x)))
                 :cljs (throw (js/Error.  (e> e x)))))
          (i? [] (not @state/IGNORED?)) ; <- Returns true if the validator has not been turned off
          (t? [x {:keys [and* e* f* ign* not* opt* or* rep*]}]
              (cond ign*                                     ; <- Skipping the validation by the ign* switch
                    :validation-skipped
                    (and (not opt*)                          ; <- If the key is not optional,
                         (not (and rep* (some #(% n) rep*))) ; <- and not replaced by another key,
                         (-> x nil?))                        ; <- and it's value is nil, ...
                    (t>  e* x)
                    (and not* (some #(-> x %) not*))         ; <- If at least one function in the not* vector returns with false, ...
                    (t>  e* x)
                    (and f* (-> x f* not))                   ; <- If the f* function returns with false, ...
                    (t>  e* x)
                    (and and* (some #(-> x % not) and*))     ; <- If at least on function in the and* vector does not return with true, ...
                    (t>  e* x)
                    (and or*  (not (some #(-> x %) or*)))    ; <- If no function in the or* vector returns with true, ...
                    (t>  e* x)
                    :else :key-passed-all-of-the-tests))
          (v? [[k test*]]
              (t? (k n) test*))
          (s? [] (or (not strict*) ; <- Strict-matching only happens in strict* mode
                     (= (keys  n)
                        (keys (p>)))
                     (t> :strict-matching-failed nil))) ; <- If there are some extra keys in the n
          (m? [] (or (map? n) ; <- The n has to be a map
                     (when explain* (println "Expected a map but got:" (-> n type)))
                     (t> :invalid-value nil))) ; <- The println skipped (it returns nil), and throwing an error
          (p? [] (or (map?     pattern*) ; <- The pattern* has to be a map ...
                     (keyword? pattern*) ; <- ... or a keyword
                     (when explain* (println "Expected a keyword type pattern-id or a map type pattern but got:" (-> pattern* type))
                                    (println pattern*))
                     (t> :invalid-pattern nil)))] ; <- The println skipped (it returns nil), and throwing an error
         (boolean (try (and (i?) ; <- Checking the validator state
                            (or (not pattern*)
                                (m?)             ; <- Type-checking the n (before the pattern* is getting processed)
                                (p?)             ; <- Type-checking the pattern* (before it's getting processed)
                                (every? v? (p>)) ; <- Validating the n with every key of the pattern*
                                (s?))            ; <- After the validation and only in strict* mode, searching for extra keys in the map
                            (or (not  test*)
                                (t? n test*)))
                       #?(:clj  (catch Exception e (if explain* (do (-> n         println)
                                                                    (-> e         println))))
                          :cljs (catch :default  e (if explain* (do (-> n         println)
                                                                    (-> e .-stack println)))))))))

(defn invalid?
  ; @param (*) n
  ; @param (map) options
  ; {:explain* (boolean)(opt)
  ;   Default: true
  ;  :pattern* (map)
  ;   {:my-key (map)
  ;     {:and* (functions in vector)(opt)
  ;       All of the functions in this vector has to be returns with true.
  ;      :e* (string)
  ;       The error message.
  ;      :f* (function)(opt)
  ;       The function has to be returns with true.
  ;      :ign* (function)(opt)
  ;       If this function returns with true, the value will be ignored.
  ;      :not* (functions in vector)(opt)
  ;       All of the functions in this vector has to be returns with false.
  ;      :opt* (boolean)(opt)
  ;       If this set to true, the value will be handled as optional.
  ;      :or* (functions in vector)(opt)
  ;       At least one of the functions in this vector has to be returns with true.
  ;      :rep* (vector)(opt)
  ;       If the tested key does not exist in the map, at least one of
  ;       the keys in this vector has to be in the n map.}}
  ;  :prefix* (string)(opt)
  ;   The :prefix* will be prepend to the value of :e* when an expection occurs.
  ;  :strict* (boolean)(opt)
  ;   If this set to true, other keys than passed in the pattern will be not allowed!
  ;   Default: false}
  ;
  ; @usage
  ; (invalid? {:a "A"}
  ;           {:pattern* {:a {:f* string?}}})
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
