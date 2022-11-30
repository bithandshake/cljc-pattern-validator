
(ns pattern.core
    (:require [pattern.state :as state]))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn reg
  ; @param (keyword) pattern-id
  ; @param (map) pattern
  ; {:my-key (map)
  ;   {:and* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with true.
  ;    :e* (string)
  ;     The error message.
  ;    :f* (function)(opt)
  ;     The function has to be returns with true.
  ;    :not* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with false.
  ;    :or* (functions in vector)(opt)
  ;     At least one of the functions in this vector has to be returns with true.
  ;    :rep* (vector)(opt)
  ;     If the tested key does not exist in the map, at least one of
  ;     the keys in this vector has to be in the n map.}}
  ;
  ; @usage
  ; (reg :my-pattern {...})
  ;
  ; @usage
  ; (reg :my-pattern {:a {:f* string?
  ;                       :e* ":a must be a string!"}})
  [pattern-id pattern]
  (swap! state/PATTERNS assoc pattern-id pattern))

;; ----------------------------------------------------------------------------
;; ----------------------------------------------------------------------------

(defn valid?
  ; @param (*) n
  ; @param (keyword or map) pattern-id or pattern
  ; {:my-key (map)
  ;   {:and* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with true.
  ;    :e* (string)
  ;     The error message.
  ;    :f* (function)(opt)
  ;     The function has to be returns with true.
  ;    :not* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with false.
  ;    :or* (functions in vector)(opt)
  ;     At least one of the functions in this vector has to be returns with true.
  ;    :rep* (vector)(opt)
  ;     If the tested key does not exist in the map, at least one of
  ;     the keys in this vector has to be in the n map.}}
  ; @param (map)(opt) options
  ; {:explain? (boolean)(opt)
  ;   Default: true
  ;  :prefix (string)(opt)
  ;   The :prefix will be prepend to the value of :e* when an expection occurs.}
  ;
  ; @usage
  ; (valid? {:a "A"}
  ;         {:a {:f* string?}})
  ;
  ; @example
  ; (valid? {:a "A"}
  ;         {:a {:f* string?
  ;              :e* ":a must be a string!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a "A"}
  ;         {:a {:or* [keyword? string?]
  ;              :e* ":a must be a keyword or a string!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:a ""}
  ;         {:a {:and* [string? empty?]
  ;              :e* ":a must be an empty string!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {:b "B"}
  ;         {:a {:rep* [:b]
  ;              :e* "The map has to contains :a or :b!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (valid? {}
  ;         {:a {:rep* [:b]
  ;              :e* "The map has to contains :a or :b!"}})
  ; =>
  ; false
  ;
  ; @example
  ; (reg :my-pattern {:a {:f* string?}})
  ; (valid? {:a "A"} :my-pattern)
  ; =>
  ; true
  ;
  ; @return (boolean)
  ([n p]
   (valid? n p {}))

  ([n p {:keys [explain?] :or {explain? true}}]
   (letfn [(t [e]
              #?(:clj  (throw (Exception. (str prefix e)))
                 :cljs (throw (js/Error.  (str prefix e)))))
           (f [[k {:keys [f* not* or* and* e* rep*]}]]
              (cond (and rep* (some #(% n) rep*))
                    :replaced-by-another-key
                    (-> n k nil?)
                    (t   e*)
                    (and not* (some #(-> n k %) not*))
                    (t   e*)
                    (and f* (-> n k f* not))
                    (t   e*)
                    (and and* (some #(-> n k % not) and*))
                    (t   e*)
                    (and or*  (not (some #(-> n k %) or*)))
                    (t   e*)
                    :else :key-passed-all-of-the-tests))]
          (boolean (try (cond (-> n map? not)
                              (when explain? (println "Expected a map but got:" (-> n type))
                                             (t :validation-error))
                              (every? f (cond (keyword? p) (get @state/PATTERNS p)
                                              (map?     p) p
                                              :else (when explain?)
                                                    (println "Expected a keyword type pattern-id or map type pattern but got:"
                                                             (-> p type))))
                              :valid)
                        #?(:clj  (catch Exception e (if explain? (do (-> n         println)
                                                                     (-> e         println))))
                           :cljs (catch :default  e (if explain? (do (-> n         println)
                                                                     (-> e .-stack println))))))))))

(defn invalid?
  ; @param (*) n
  ; @param (keyword or map) pattern-id or pattern
  ; {:my-key (map)
  ;   {:and* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with true.
  ;    :e* (string)
  ;     The error message.
  ;    :f* (function)(opt)
  ;     The function has to be returns with true.
  ;    :not* (functions in vector)(opt)
  ;     All of the functions in this vector has to be returns with false.
  ;    :or* (functions in vector)(opt)
  ;     At least one of the functions in this vector has to be returns with true.
  ;    :rep* (vector)(opt)
  ;     If the tested key does not exist in the map, at least one of
  ;     the keys in this vector has to be in the n map.}}
  ; @param (map)(opt) options
  ; {:explain? (boolean)(opt)
  ;   Default: true
  ;  :prefix (string)(opt)
  ;   The :prefix will be prepend to the value of :e* when an expection occurs.}
  ;
  ; @usage
  ; (invalid? {:a "A"}
  ;           {:a {:f* string?}})
  ;
  ; @example
  ; (invalid? {:a "A"}
  ;           {:a {:f* string?
  ;                :e* ":a must be a string!"}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {:a "A"}
  ;           {:a {:or* [keyword? string?]
  ;                :e* ":a must be a keyword or a string!"}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {:a ""}
  ;           {:a {:and* [string? empty?]
  ;                :e* ":a must be an empty string!"}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {:b "B"}
  ;           {:a {:rep* [:b]
  ;                :e* "The map has to contains :a or :b!"}})
  ; =>
  ; false
  ;
  ; @example
  ; (invalid? {}
  ;           {:a {:rep* [:b]
  ;                :e* "The map has to contains :a or :b!"}})
  ; =>
  ; true
  ;
  ; @example
  ; (reg :my-pattern {:a {:f* string?}})
  ; (invalid? {:a "A"} :my-pattern)
  ; =>
  ; false
  ;
  ; @return (boolean)
  ([n p]
   (invalid? n p {}))

  ([n p options]
   (not (valid? n p options))))
