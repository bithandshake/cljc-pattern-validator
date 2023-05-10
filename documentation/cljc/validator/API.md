
# validator.api isomorphic namespace

##### [README](../../../README.md) > [DOCUMENTATION](../../COVER.md) > validator.api

### Index

- [ignore!](#ignore)

- [invalid?](#invalid)

- [reg-pattern!](#reg-pattern)

- [reg-test!](#reg-test)

- [valid?](#valid)

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
{:explain* (boolean)(opt)
  If set to true the error message will be printed.
  Default: true
 :pattern* (map)
  {:my-key (map)
    {:and* (functions in vector)(opt)
      All of the functions in this vector has to return with true.
     :e* (string)
      The error message.
     :f* (function)(opt)
      The function has to return with true.
     :ign* (function)(opt)
      If this function returns with true, the value will be ignored.
     :nand* (functions in vector)(opt)
      At least one of the functions in this vector has to return with false.
     :not* (function)(opt)
      The function has to return with false.
     :nor* (functions in vector)(opt)
      All of the functions in this vector has to return with false.
     :opt* (boolean)(opt)
      If set to true, the value will be handled as optional.
     :or* (functions in vector)(opt)
      At least one of the functions in this vector has to return with true.
     :rep* (vector)(opt)
      If the tested key does not exist in the map, at least one of
      the keys in this vector has to be in the n map.
     :xor* (functions in vector)(opt)
      At most one of the functions in this vector can returns with true.}}
 :prefix* (string)(opt)
  The ':prefix*' will be prepend to the value of ':e*' when an expection occurs.
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
   :xor* (functions in vector)(opt)}
 :strict* (boolean)(opt)
  If set to true, other keys than passed in the pattern will not be allowed!
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
Registers a reusable pattern with id.
```

```
@param (keyword) pattern-id
@param (map) pattern
{:my-key (map)
  {:and* (functions in vector)(opt)
    All of the functions in this vector has to return with true.
   :e* (string)
    The error message.
   :f* (function)(opt)
    The function has to be return with true.
   :ign* (function)(opt)
    If this function returns with true, the value will be ignored.
   :nand* (functions in vector)(opt)
    At least one of the functions in this vector has to return with false.
   :not* (function)(opt)
    The function has to be return with false.
   :nor* (functions in vector)(opt)
    All of the functions in this vector has to return with false.
   :opt* (boolean)(opt)
    If set to true, the value will be handled as optional.
   :or* (functions in vector)(opt)
    At least one of the functions in this vector has to return with true.
   :rep* (vector)(opt)
    If the tested key does not exist in the map, at least one of
    the keys in this vector has to be in the n map.
   :xor* (functions in vector)(opt)
    At most one of the functions in this vector can returns with true.}}
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
Registers a reusable test with id.
```

```
@param (keyword) test-id
@param (map) test
{:and* (functions in vector)(opt)
  All of the functions in this vector has to return with true.
 :e* (string)
  The error message.
 :f* (function)(opt)
  The function has to be return with true.
 :nand* (functions in vector)(opt)
  At least one of the functions in this vector has to return with false.
 :not* (function)(opt)
  The function has to be return with false.
 :nor* (functions in vector)(opt)
  All of the functions in this vector has to return with false.
 :or* (functions in vector)(opt)
  At least one of the functions in this vector has to return with true.
 :xor* (functions in vector)(opt)
  At most one of the functions in this vector can returns with true.}
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
{:explain* (boolean)(opt)
  If set to true the error messages will be printed.
  Default: true
 :pattern* (map)
  {:my-key (map)
    {:and* (functions in vector)(opt)
      All of the functions in this vector has to return with true.
     :e* (string)
      The error message.
     :f* (function)(opt)
      The function has to return with true.
     :ign* (function)(opt)
      If this function returns with true, the value will be ignored.
     :nand* (functions in vector)(opt)
      At least one of the functions in this vector has to return with false.
     :not* (function)(opt)
      The function has to return with false.
     :nor* (functions in vector)(opt)
      All of the functions in this vector has to return with false.
     :opt* (boolean)(opt)
      If set to true, the value will be handled as optional.
     :or* (functions in vector)(opt)
      At least one of the functions in this vector has to return with true.
     :rep* (vector)(opt)
      If the tested key does not exist in the map, at least one of
      the keys in this vector has to be in the n map.
     :xor* (functions in vector)(opt)
      At most one of the functions in this vector can returns with true.}}
 :prefix* (string)(opt)
  The ':prefix*' will be prepended to the value of ':e*' when an expection occurs.
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
   :xor* (functions in vector)(opt)}
 :strict* (boolean)(opt)
  If set to true, other keys in data than passed in the pattern will not be allowed!
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
  [n {:keys [explain* pattern* prefix* strict* test*] :or {explain* true}}]
  (letfn [
          (p> [] (if (map? pattern*) pattern* (get @state/PATTERNS pattern*)))

          (e> [e x t] (println)
                      (if (nil? t) (println (str "validation failed on test:\nNIL"))
                                   (println (str "validation failed on test:\n" t)))
                      (println)
                      (if (nil? x) (println (str "validation failed on value:\nNIL"))
                                   (println (str "validation failed on value:\n" x)))
                      (println)
                      (if (nil? n) (println (str "validation failed in data:\nNIL"))
                                   (println (str "validation failed in data:\n" n)))
                      (println)
                      (if prefix* (str prefix* " " e)
                                  (str             e)))

          (t> [e x t]
              #?(:clj  (throw (Exception. (e> e x t)))
                 :cljs (throw (js/Error.  (e> e x t)))))

          (i? [] @state/IGNORED?)

          (c? [f*] (if (fn? f*) f* (t> :testing-method-must-be-a-function nil :f*)))

          (opt? [x {:keys [opt*]}]
                (and (nil? x) opt*))

          (rep? [x {:keys [rep*]}]
                (and (nil? x)
                     (some #(% n) rep*)))

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
                    (opt?  x test*) :not-passed-but-optional
                    (rep?  x test*) :not-passed-but-replaced
                    (nil?  x)       (t> e* x :nil?)
                    (and?  x test*) (t> e* x :and*)
                    (f?    x test*) (t> e* x :f*)
                    (nand? x test*) (t> e* x :nand*)
                    (nor?  x test*) (t> e* x :nor*)
                    (not?  x test*) (t> e* x :not*)
                    (or?   x test*) (t> e* x :or*)
                    :else :key-passed-all-of-the-tests))

          (v? [[k test* :as x]]
              (t? (k n) test*))

          (s? [] (or (not strict*)
                     (= (keys  n)
                        (keys (p>)))
                     (t> :strict-matching-failed/data-not-match-with-pattern nil :strict*)))

          (m? [] (or (map? n)
                     (when explain* (println "Expected a map but got:" (-> n type)))
                     (t> :invalid-value nil nil)))

          (p? [] (or (map?     pattern*)
                     (keyword? pattern*)
                     (when explain* (println "Expected a keyword type pattern-id or a map type pattern but got:" (-> pattern* type))
                                    (println pattern*))
                     (t> :invalid-pattern nil :pattern*)))]

         (if (i?) :validating-ignored
                  (boolean (try (and (or (not pattern*)
                                         (and (m?)
                                              (p?)
                                              (every? v? (p>))
                                              (s?)))
                                     (or (not  test*)
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

This documentation is generated with the [clj-docs-generator](https://github.com/bithandshake/clj-docs-generator) engine.

