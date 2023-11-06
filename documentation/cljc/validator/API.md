
### validator.api

Functional documentation of the validator.api isomorphic namespace

---

##### [README](../../../README.md) > [DOCUMENTATION](../../COVER.md) > validator.api

### Index

- [ignore!](#ignore)

- [invalid?](#invalid)

- [reg-pattern!](#reg-pattern)

- [reg-test!](#reg-test)

- [valid?](#valid)

---

### ignore!

```
@description
Turns off the validating.
After using it, the validating functions will return as in case with a valid data.
```

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
(ns my-namespace (:require [validator.api :refer [ignore!]]))

(validator.api/ignore!)
(ignore!)
```

</details>

---

### invalid?

```
@description
Checks whether the given data is invalid or not.
```

```
@param (*) n
@param (map) options
{:allowed* (vector)(opt)
  Defines the allowed keys of the given data (only use with map type data!).
  Using the '{:strict* true}' setting could replace the using of the ':allowed*'
  parameter.
 :explain* (boolean)(opt)
  If set to TRUE the error message will be printed.
  Default: true
 :pattern* (map)(opt)
  Defines the pattern of the given data (only use with map type data!).
  {:my-key (map)
    {:and* (functions in vector)(opt)
      All of the functions in this vector has to return with TRUE.
     :e* (string)
      The error message.
     :f* (function)(opt)
      The function has to return with TRUE.
     :ign* (boolean)(opt)
      If set to TRUE, the value will be ignored.
     :nand* (functions in vector)(opt)
      At least one of the functions in this vector has to return with FALSE.
     :not* (function)(opt)
      The function has to return with FALSE.
     :nor* (functions in vector)(opt)
      All of the functions in this vector has to return with FALSE.
     :opt* (boolean)(opt)
      If set to TRUE, the key will be handled as optional.
      If the ':required*' parameter is in use, no need to mark optional keys
      in the pattern. Key existence checking will be done with using the
      ':required*' parameter.
     :or* (functions in vector)(opt)
      At least one of the functions in this vector has to return with TRUE.
     :rep* (vector)(opt)
      If the tested key does not exist in the map, at least one of the keys
      in this vector has to be in the n map in order to replace the missing key.
      If the ':required*' parameter is in use, no need to specify replacement keys
      for any keys in the pattern. Key existence checking will be done with
      using the ':required*' parameter.
     :xor* (functions in vector)(opt)
      At most one of the functions in this vector can returns with TRUE.}}
 :prefix* (string)(opt)
  The ':prefix*' will be prepend to the value of ':e*' when an expection occurs.
 :required* (vector)(opt)
  Defines the required keys of the given data (only use with map type data!).
 :strict* (boolean)(opt)
  If set to TRUE, other keys in data than that are in the pattern will not be allowed!
  Using the ':allowed*' parameter could replace the using of the '{:strict* true}' setting.
  Default: false
  W/ {:pattern* ...}
 :test* (map)(opt)
  {:and* (functions in vector)(opt)
   :e* (string)
   :f* (function)(opt)
   :ign* (boolean)(opt)
   :nand* (functions in vector)(opt)
   :nor* (functions in vector)(opt)
   :not* (function)(opt)
   :opt* (boolean)(opt)
   :or* (functions in vector)(opt)
   :xor* (functions in vector)(opt)}}
```

```
@usage
(invalid? {:a "A"}
          {:pattern* {:a {:f* string?}}})
```

```
@example
(invalid? "A"
          {:test* {:f* string?
                   :e* "Value must be a string!"}})
=>
false
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
                          :e* "The map has to contain at least :a or :b!"}}})
=>
false
```

```
@example
(invalid? {}
          {:pattern* {:a {:rep* [:b]
                          :e* "The map has to contain at least :a or :b!"}}})
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
(ns my-namespace (:require [validator.api :refer [invalid?]]))

(validator.api/invalid? ...)
(invalid?               ...)
```

</details>

---

### reg-pattern!

```
@description
Registers a reusable pattern with ID.
```

```
@param (keyword) pattern-id
@param (map) pattern
{:my-key (map)
  {:and* (functions in vector)(opt)
    All of the functions in this vector has to return with TRUE.
   :e* (string)
    The error message.
   :f* (function)(opt)
    The function has to be return with TRUE.
   :ign* (boolean)(opt)
    If set to TRUE, the value will be ignored.
   :nand* (functions in vector)(opt)
    At least one of the functions in this vector has to return with FALSE.
   :not* (function)(opt)
    The function has to be return with FALSE.
   :nor* (functions in vector)(opt)
    All of the functions in this vector has to return with FALSE.
   :opt* (boolean)(opt)
    If set to TRUE, the value will be handled as optional.
   :or* (functions in vector)(opt)
    At least one of the functions in this vector has to return with TRUE.
   :rep* (vector)(opt)
    If the tested key does not exist in the map, at least one of
    the keys in this vector has to be in the n map.
   :xor* (functions in vector)(opt)
    At most one of the functions in this vector can returns with TRUE.}}
```

```
@usage
(reg-pattern! :my-pattern {...})
```

```
@usage
(reg-pattern! :my-pattern {:a {:f* string?
                               :e* ":a must be a string!"}})
```

<details>
<summary>Source code</summary>

```
(defn reg-pattern!
  [pattern-id pattern]
  (swap! state/PATTERNS assoc pattern-id pattern))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [validator.api :refer [reg-pattern!]]))

(validator.api/reg-pattern! ...)
(reg-pattern!               ...)
```

</details>

---

### reg-test!

```
@description
Registers a reusable test with ID.
```

```
@param (keyword) test-id
@param (map) test
{:and* (functions in vector)(opt)
  All of the functions in this vector has to return with TRUE.
 :e* (string)
  The error message.
 :f* (function)(opt)
  The function has to be return with TRUE.
 :nand* (functions in vector)(opt)
  At least one of the functions in this vector has to return with FALSE.
 :not* (function)(opt)
  The function has to be return with FALSE.
 :nor* (functions in vector)(opt)
  All of the functions in this vector has to return with FALSE.
 :or* (functions in vector)(opt)
  At least one of the functions in this vector has to return with TRUE.
 :xor* (functions in vector)(opt)
  At most one of the functions in this vector can returns with TRUE.}
```

```
@usage
(reg-test! :my-test {...})
```

```
@usage
(reg-test! :my-test {:f* string?
                     :e* ":a must be a string!"})
```

<details>
<summary>Source code</summary>

```
(defn reg-test!
  [test-id test]
  (swap! state/TESTS assoc test-id test))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [validator.api :refer [reg-test!]]))

(validator.api/reg-test! ...)
(reg-test!               ...)
```

</details>

---

### valid?

```
@description
Checks whether the given data is valid or not.
```

```
@param (*) n
@param (map) options
{:allowed* (vector)(opt)
  Defines the allowed keys of the given data (only use with map type data!).
  Using the '{:strict* true}' setting could replace the using of the ':allowed*'
  parameter.
 :explain* (boolean)(opt)
  If set to TRUE the error messages will be printed.
  Default: true
 :pattern* (map)(opt)
  Defines the pattern of the given data (only use with map type data!).
  {:my-key (map)
    {:and* (functions in vector)(opt)
      All of the functions in this vector has to return with TRUE.
     :e* (string)
      The error message.
     :f* (function)(opt)
      The function has to return with TRUE.
     :ign* (boolean)(opt)
      If set to TRUE, the value will be ignored.
     :nand* (functions in vector)(opt)
      At least one of the functions in this vector has to return with FALSE.
     :not* (function)(opt)
      The function has to return with FALSE.
     :nor* (functions in vector)(opt)
      All of the functions in this vector has to return with FALSE.
     :opt* (boolean)(opt)
      If set to TRUE, the key will be handled as optional.
      If the ':required*' parameter is in use, no need to mark optional keys
      in the pattern. Key existence checking will be done with using the
      ':required*' parameter.
     :or* (functions in vector)(opt)
      At least one of the functions in this vector has to return with TRUE.
     :rep* (vector)(opt)
      If the tested key does not exist in the map, at least one of the keys
      in this vector has to be in the n map in order to replace the missing key.
      If the ':required*' parameter is in use, no need to specify replacement keys
      for any keys in the pattern. Key existence checking will be done with
      using the ':required*' parameter.
     :xor* (functions in vector)(opt)
      At most one of the functions in this vector can returns with TRUE.}}
 :prefix* (string)(opt)
  The ':prefix*' will be prepended to the value of ':e*' when an expection occurs.
 :required* (vector)(opt)
  Defines the required keys of the given data (only use with map type data!).
 :strict* (boolean)(opt)
  If set to TRUE, other keys in data than that are in the pattern will not be allowed!
  Using the ':allowed*' parameter could replace the using of the '{:strict* true}' setting.
  Default: false
  W/ {:pattern* ...}
 :test* (map)(opt)
  {:and* (functions in vector)(opt)
   :e* (string)
   :f* (function)(opt)
   :ign* (boolean)(opt)
   :nand* (functions in vector)(opt)
   :nor* (functions in vector)(opt)
   :not* (function)(opt)
   :opt* (boolean)(opt)
   :or* (functions in vector)(opt)
   :xor* (functions in vector)(opt)}}
```

```
@usage
(valid? {:a "A"}
        {:pattern* {:a {:f* string?}}})
```

```
@example
(valid? "A"
        {:test* {:f* string?
                 :e* "Value must be a string!"}})
=>
true
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
                        :e* "The map has to contain at least :a or :b!"}}})
=>
true
```

```
@example
(valid? {}
        {:pattern* {:a {:rep* [:b]
                        :e* "The map has to contain at least :a or :b!"}}})
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
  [n {:keys [allowed* explain* pattern* prefix* required* strict* test*] :or {explain* true}}]
  (letfn [
          (p> [] (if (map? pattern*) pattern* (get @state/PATTERNS pattern*)))

          (pks> [] (-> (p>) keys sort))

          (nks> [] (-> n keys sort))

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

          (t> [e v t]
              #?(:clj  (throw (Exception. (e> e v t)))
                 :cljs (throw (js/Error.  (e> e v t)))))

          (i? [] @state/IGNORED?)

          (c? [f*] (if (fn? f*) f* (t> :testing-method-must-be-a-function nil :f*)))

          (opt? [x {:keys [opt*]}]
                (and (nil? x) opt*))

          (rep? [x {:keys [rep*]}]
                (and (nil? x)
                     (some #(% n) rep*)))

          (req? [x _]
                (and (nil? x) required*))

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

          (v? [[k test* :as x]]
              (t? (k n) test*))

          (s? [] (or (= (nks>)
                        (pks>))
                     (t> :data-keys-does-not-match-with-pattern-keys nil :strict*)))

          (a? [] (or (-> allowed* set (remove (keys n)) empty?)
                     (t> :not-allowed-data-keys-found nil :allowed*)))

          (r? [] (or (-> n keys set (remove required*) empty?)
                     (t> :required-data-keys-missing nil :required*)))

          (m? [] (or (map? n)
                     (when explain* (println "Expected a map but got:" (-> n type)))
                     (t> :invalid-value nil nil)))

          (p? [] (or (map?     pattern*)
                     (keyword? pattern*)
                     (when explain* (println "Expected a keyword type pattern ID or a map type pattern but got:" (-> pattern* type))
                                    (println pattern*))
                     (t> :invalid-pattern nil :pattern*)))]

         (if (i?) :validation-turned-off
                  (boolean (try (and (or (not allowed*)
                                         (m?)                                         (a?))                                     (or (not required*)
                                         (m?)                                         (r?))                                     (or (not pattern*)
                                         (and (m?)                                              (p?)                                              (or (not strict*)                                                  (s?))                                              (every? v? (p>))))                                     (or (not  test*)
                                         (t? n test*)))
                                #?(:clj  (catch Exception e (if explain* (do (-> e         println))))
                                   :cljs (catch :default  e (if explain* (do (-> e .-stack println))))))))))
```

</details>

<details>
<summary>Require</summary>

```
(ns my-namespace (:require [validator.api :refer [valid?]]))

(validator.api/valid? ...)
(valid?               ...)
```

</details>

---

<sub>This documentation is generated with the [clj-docs-generator](https://github.com/bithandshake/clj-docs-generator) engine.</sub>

