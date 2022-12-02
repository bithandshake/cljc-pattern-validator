
# <strong>pattern.api</strong> namespace

<strong>[README](../../../README.md) > [DOCUMENTATION](../../COVER.md) > </strong>source-code/cljc/pattern/api.cljc

### ignore!

```
@usage
(ignore!)
```

<details>
<summary>Source code</summary>

```
(defn ignore!
  []
  (reset! state/IGNORED? true))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [pattern.api :refer [ignore!]]))

(pattern.api/ignore!)
(ignore!)
```

</details>

---

### invalid?

```
@param (*) n
@param (map) options
{:explain* (boolean)(opt)
  Default: true
 :pattern* (map)
  {:my-key (map)
    {:and* (functions in vector)(opt)
      All of the functions in this vector has to be returns with true.
     :e* (string)
      The error message.
     :f* (function)(opt)
      The function has to be returns with true.
     :ign* (function)(opt)
      If this function returns with true, the value will be ignored.
     :nand* (functions in vector)(opt)
      At least of the functions in this vector has to be returns with false.
     :not* (function)(opt)
      The function has to be returns with false.
     :nor* (functions in vector)(opt)
      All of the functions in this vector has to be returns with false.
     :opt* (boolean)(opt)
      If this set to true, the value will be handled as optional.
     :or* (functions in vector)(opt)
      At least one of the functions in this vector has to be returns with true.
     :rep* (vector)(opt)
      If the tested key does not exist in the map, at least one of
      the keys in this vector has to be in the n map.
     :xor* (functions in vector)(opt)
      At most one of the functions in this vector can returns with true.}}
 :prefix* (string)(opt)
  The :prefix* will be prepend to the value of :e* when an expection occurs.
 :test* (map)(opt)
  {:and* (functions in vector)(opt)
   :e* (string)
   :ign* (boolean)(opt)
   :nand* (functions in vector)(opt)
   :nor* (functions in vector)(opt)
   :not* (function)(opt)
   :opt* (boolean)(opt)
   :or* (functions in vector)(opt)
   :xor* (functions in vector)(opt)}
 :strict* (boolean)(opt)
  If this set to true, other keys than passed in the pattern will be not allowed!
  Default: false
  W/ {:pattern* ...}}
```

```
@usage
(invalid? {:a "A"}
          {:pattern* {:a {:f* string?}}})
```

```
@example
(invalid? {:a "A"}
          {:pattern* {:a {:f* string?
                          :e* ":a must be a string!"}}})
=>
false
```

```
@example
(invalid? {:a "A"}
          {:pattern* {:a {:or* [keyword? string?]
                          :e* ":a must be a keyword or a string!"}}})
=>
false
```

```
@example
(invalid? {:a ""}
          {:pattern* {:a {:and* [string? empty?]
                          :e* ":a must be an empty string!"}}})
=>
false
```

```
@example
(invalid? {:b "B"}
          {:pattern* {:a {:rep* [:b]
                          :e* "The map has to contains at least :a or :b!"}}})
=>
false
```

```
@example
(invalid? {}
          {:pattern* {:a {:rep* [:b]
                          :e* "The map has to contains at least :a or :b!"}}})
=>
true
```

```
@example
(reg :my-pattern {:a {:f* string?}})
(invalid? {:a "A"} {:pattern* :my-pattern})
=>
false
```

```
@return (boolean)
```

<details>
<summary>Source code</summary>

```
(defn invalid?
  [n options]
  (not (valid? n options)))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [pattern.api :refer [invalid?]]))

(pattern.api/invalid? ...)
(invalid?             ...)
```

</details>

---

### reg!

```
@param (keyword) pattern-id
@param (map) pattern
{:my-key (map)
  {:and* (functions in vector)(opt)
    All of the functions in this vector has to be returns with true.
   :e* (string)
    The error message.
   :f* (function)(opt)
    The function has to be returns with true.
   :ign* (function)(opt)
    If this function returns with true, the value will be ignored.
   :nand* (functions in vector)(opt)
    At least of the functions in this vector has to be returns with false.
   :not* (function)(opt)
    The function has to be returns with false.
   :nor* (functions in vector)(opt)
    All of the functions in this vector has to be returns with false.
   :opt* (boolean)(opt)
    If this set to true, the value will be handled as optional.
   :or* (functions in vector)(opt)
    At least one of the functions in this vector has to be returns with true.
   :rep* (vector)(opt)
    If the tested key does not exist in the map, at least one of
    the keys in this vector has to be in the n map.
   :xor* (functions in vector)(opt)
    At most one of the functions in this vector can returns with true.}}
```

```
@usage
(reg! :my-pattern {...})
```

```
@usage
(reg! :my-pattern {:a {:f* string?
                      :e* ":a must be a string!"}})
```

<details>
<summary>Source code</summary>

```
(defn reg!
  [pattern-id pattern]
  (swap! state/PATTERNS assoc pattern-id pattern))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [pattern.api :refer [reg!]]))

(pattern.api/reg! ...)
(reg!             ...)
```

</details>

---

### valid?

```
@param (*) n
@param (map) options
{:explain* (boolean)(opt)
  Default: true
 :pattern* (map)
  {:my-key (map)
    {:and* (functions in vector)(opt)
      All of the functions in this vector has to be returns with true.
     :e* (string)
      The error message.
     :f* (function)(opt)
      The function has to be returns with true.
     :ign* (function)(opt)
      If this function returns with true, the value will be ignored.
     :nand* (functions in vector)(opt)
      At least of the functions in this vector has to be returns with false.
     :not* (function)(opt)
      The function has to be returns with false.
     :nor* (functions in vector)(opt)
      All of the functions in this vector has to be returns with false.
     :opt* (boolean)(opt)
      If this set to true, the value will be handled as optional.
     :or* (functions in vector)(opt)
      At least one of the functions in this vector has to be returns with true.
     :rep* (vector)(opt)
      If the tested key does not exist in the map, at least one of
      the keys in this vector has to be in the n map.
     :xor* (functions in vector)(opt)
      At most one of the functions in this vector can returns with true.}}
 :prefix* (string)(opt)
  The :prefix* will be prepend to the value of :e* when an expection occurs.
 :test* (map)(opt)
  {:and* (functions in vector)(opt)
   :e* (string)
   :ign* (boolean)(opt)
   :nand* (functions in vector)(opt)
   :nor* (functions in vector)(opt)
   :not* (function)(opt)
   :opt* (boolean)(opt)
   :or* (functions in vector)(opt)
   :xor* (functions in vector)(opt)}
 :strict* (boolean)(opt)
  If this set to true, other keys than passed in the pattern will be not allowed!
  Default: false
  W/ {:pattern* ...}}
```

```
@usage
(valid? {:a "A"}
        {:pattern* {:a {:f* string?}}})
```

```
@example
(valid? {:a "A"}
        {:pattern* {:a {:f* string?
                        :e* ":a must be a string!"}}})
=>
true
```

```
@example
(valid? {:a "A"}
        {:pattern* {:a {:or* [keyword? string?]
                        :e* ":a must be a keyword or a string!"}}})
=>
true
```

```
@example
(valid? {:a ""}
        {:pattern* {:a {:and* [string? empty?]
                        :e* ":a must be an empty string!"}}})
=>
true
```

```
@example
(valid? {:b "B"}
        {:pattern* {:a {:rep* [:b]
                        :e* "The map has to contains at least :a or :b!"}}})
=>
true
```

```
@example
(valid? {}
        {:pattern* {:a {:rep* [:b]
                        :e* "The map has to contains at least :a or :b!"}}})
=>
false
```

```
@example
(valid? {:a "A"}
        {:pattern* {:a {:f* [keyword?]
                        :e* ":a must be a keyword but got: %"}}})
=>
false
```

```
@example
(reg :my-pattern {:a {:f* string?}})
(valid? {:a "A"} {:pattern* :my-pattern})
=>
true
```

```
@return (boolean)
```

<details>
<summary>Source code</summary>

```
(defn valid?
  [n {:keys [explain* pattern* prefix* strict* test*] :or {explain* true}}]
  (letfn [
          (p> [] (if (map? pattern*) pattern* (get @state/PATTERNS pattern*)))

          (e> [e x] (println)
                    (println (str "validation failed on value:\n" x))
                    (println (str "validation failed in data:\n"  n))
                    (println)
                    (if prefix* (str prefix* " " e)
                                (str             e)))

          (t> [e x]
              #?(:clj  (throw (Exception. (e> e x)))
                 :cljs (throw (js/Error.  (e> e x)))))

          (e? [] (not @state/IGNORED?))

          (c? [f*] (if (fn? f*) f* (t> :testing-method-must-be-a-function)))

          (req? [x {:keys [opt* rep*]}]
                (and (not opt*)
                     (not (and rep* (some #(% n) rep*)))
                     (-> x nil?)))

          (opt? [x {:keys [opt*]}]
                (and opt* (nil? x)))

          (and? [x {:keys [and*]}]
                (and and* (some #(-> x % not) and*)))

          (f? [x {:keys [f*]}]
              (and f* (-> x f* not)))

          (nand? [x {:keys [nand*]}]
                 (and nand* (every? #(-> x %) nand*)))

          (nor? [x {:keys [nor*]}]
                (and nor* (some #(-> x %) nor*)))

          (not? [x {:keys [not*]}]
                (and not* (not* x)))

          (or? [x {:keys [or*]}]
               (and or* (not (some #(-> x %) or*))))

          (xor? [x {:keys [xor*]}]
                (letfn [(f [r %] (if (% x) (inc r) r))]
                       (not= 1 (reduce f 0 xor*))))

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

          (v? [[k test* :as x]]
              (t? (k n) test*))

          (s? [] (or (not strict*)
                     (= (keys  n)
                        (keys (p>)))
                     (t> :strict-matching-failed nil)))

          (m? [] (or (map? n)
                     (when explain* (println "Expected a map but got:" (-> n type)))
                     (t> :invalid-value nil)))

          (p? [] (or (map?     pattern*)
                     (keyword? pattern*)
                     (when explain* (println "Expected a keyword type pattern-id or a map type pattern but got:" (-> pattern* type))
                                    (println pattern*))
                     (t> :invalid-pattern nil)))]

         (boolean (try (and (e?)                            (or (not pattern*)
                                (and (m?)                                     (p?)                                     (every? v? (p>))                                     (s?)))                            (or (not  test*)
                                (t? n test*)))
                       #?(:clj  (catch Exception e (if explain* (do (-> e         println))))
                          :cljs (catch :default  e (if explain* (do (-> e .-stack println)))))))))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [pattern.api :refer [valid?]]))

(pattern.api/valid? ...)
(valid?             ...)
```

</details>
