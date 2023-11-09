
# cljc-validator

### Overview

The <strong>cljc-validator</strong> is a simple Clojure/ClojureScript tool that
helps you to check and validate every kind of data in your application.

### deps.edn

```
{:deps {bithandshake/cljc-validator {:git/url "https://github.com/bithandshake/cljc-validator"
                                     :sha     "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"}}
```

### Current version

Check out the latest commit on the [release branch](https://github.com/bithandshake/cljc-validator/tree/release).

### Documentation

The <strong>cljc-validator</strong> functional documentation is [available here](documentation/COVER.md).

### Changelog

You can track the changes of the <strong>cljc-validator</strong> library [here](CHANGES.md).

# Usage

> Some parameters of the following functions and some further functions are not discussed in this file.
  To learn more about the available functionality, check out the [functional documentation](documentation/COVER.md)!

### Index

- [How to validate a data?](#how-to-validate-a-data)

- [How to turn off the validator?](#how-to-turn-off-the-validator)

### How to validate a data?

The [`validator.api/valid?`](documentation/cljc/pattern/API.md/#valid) function
checks whether the given data is valid or not.

- By usign the `:allowed*` parameter you can define which keys are allowed in the
  given data (use only with map type data).
- By using the `{:explain* true}` setting (default: `true`) the error message of the
  first failed test will be printed on the console.
- By using the `{:prefix* "..."}` setting, a prefix will be prepended to the
  beginning of the printed error message.
- By usign the `:required*` parameter you can define which keys are required in the
  given data (use only with map type data).
- By using the `{:strict* true}` setting, other keys in the data than that are in
  the pattern will not be allowed!  
  (Using the `:allowed*` parameter could replace using the {`:strict* true`} setting.)

You can check the given data by specifying the `:test*` set. A set of test functions
and logic gates. The `:e*` key in the test will be the printed error message in case
of any of the tests fails.

In the following (3) examples the `valid?` function returns `true` in all cases,
because the given data (`"My string"`) passes all the tests (string type and not empty).

```
(valid? "My string" {:test {:e* "This value must be a string!"
                            :f* string?}})
; =>
; true

(valid? "My string" {:test {:e* "This value must be nonempty!"
                            :not* empty?}})
; =>
; true

(valid? "My string" {:test {:e* "This value must be a nonempty string!"
                            :f* string?
                            :not* empty?}})
; =>
; true
```

In the following example the `valid?` function returns `false`, because the given
data (`:my-keyword`) does not passes the test (not string type).

```
(valid? :my-keyword {:test {:e* "This value must be a string!"
                            :f* string?}})
; =>
; false
; "This value must be a string!"
```

The `{:opt* true}` setting allows the value of a key to be `nil`.

> If the `:required*` parameter is in use, no need to mark optional keys in the pattern.
  Key existence checking will be done with using the `:required*` parameter.

In the following example the `valid?` function returns `true`,  because the given data
is `nil` but the `{:opt* true}` setting allows it to be empty.

```
(valid? nil {:test {:e* "This value must be a string!"
                    :opt* true
                    :f* string?}})
; =>
; true                       
```

By using the `{:ign* true}` setting the data will be declared as valid, because
all the tests defined for the key will be skipped (ignored).

In the following examples (2) the `valid?` function returns `true` in both cases,
no matter if the data could pass the tests or not.

```
(def CIRCUMSTANCE true)

(valid? "My string" {:test {:e* "This value must be a string!"
                            :ign* CIRCUMSTANCE
                            :f* string?}})
; =>
; true

(valid? :keyword {:test {:e* "This value must be a string!"
                         :ign* CIRCUMSTANCE
                         :f* string?}})
; =>
; true                       
```

The `:prefix*` key helps you to use shorter error messages. It's very useful
when you use patterns with long error messages.

```
(valid? "My string" {:prefix* "This value"
                     :test {:e* "must be a string!"
                            :f* string?}})
; =>
; true
```

```
(valid? :my-keyword {:prefix* "This value"
                     :test {:e* "must be a string!"
                            :f* string?}})
; =>
; false
; "This value must be a string!"
```

You can compose complex tests by using the following logic gates:
`:and*`, `:nand*`, `:not*`, `:nor*`, `:or*`, `:xor*`.

```
(valid? ["A" "B"] {:test {:e* "This value must be a nonempty vector or map!"
                          :not* empty?
                          :or* [map? vector?]}})
; =>
; true

(valid? ["A" "B"] {:test {:e* "This value must be a nonempty vector with string items!"
                          :and* [vector? #(every? string?)]
                          :not* empty?}})
; =>
; true

(valid? ["A" "B"] {:test {:e* "This value must be a nonempty vector or an empty value!"
                          :xor* [vector? empty?]}})
; =>
; true
```

By using the `:pattern*` key you can use patterns to validate map type data.
Patterns must be a maps and during the validation their keys will be matched with
the keys of the given data.

Every value in a pattern must be a test map (logic gates and test functions) that
tests the value of the same key in the given data.

In the following example the `valid?` function returns `true`, because all values
of the given data passes their tests defined in the given pattern.

```
(valid? {:a "A" :b :b :c 2} {:prefix* "This map key"
                             :pattern* {:a {:e* ":a must be a string!"
                                            :f* string?}
                                        :b {:e* ":b must be a keyword!"
                                            :f* keyword?
                                            :opt* true}
                                        :c {:e* ":c must be an integer, greater than 1"
                                            :and* [integer? #(> % 1)]}}})
; =>
; true                                       
```

By using the `:rep*` key in a test map of a pattern, you can specify which other keys
of the given data can replace the tested key.

> If the `:required*` parameter is in use, no need to specify replacement keys
  for any keys in the pattern. Key existence checking will be done with using the
  `:required*` parameter.

In the following example, the `:a` key can replace the `:b` key and vica versa.

```
(valid? {:a "A" :b :b} {:prefix* "This map key"
                        :pattern* {:a {:e* ":a must be a string!"
                                       :f* string?
                                       :rep* [:b]}
                                   :b {:e* ":b must be a keyword!"
                                       :f* keyword?
                                       :rep* [:a]}}})
; =>
; true                                  
```

By using the `{:strict* true}` setting, only the given pattern's keys will be allowed
to presence in the data.

> Using the `:allowed*` parameter could replace the using of the `{:strict* true}` setting.

In the following example the `valid?` function returns `false` because the `:b` key is not
defined in the pattern and the `{:strict* true}` setting doesn't allow extra keys in the data.

```
(valid? {:a "A" :b "B"} {:prefix* "This map key"
                         :pattern* {:a {:e* ":a must be a string!"
                                        :f* string?}}
                         :strict* true})
; =>
; false
```

By using the `:allowed*` parameter you can specify which keys are allowed to presence
in the given data.

In the following example the `valid?` function returns `false` because the `:b` key
is not specified in the `allowed*` keys vector.

```
(valid? {:a "A" :b "B"} {:prefix* "This map key"
                         :pattern* {:a {:e* ":a must be a string!"
                                        :f* string?}}
                         :allowed* [:a]})
; =>
; false
```

By using the `:required*` parameter you can specify which keys are required to
presence in the given data.

In the following example the `valid?` function returns `false` because the `:c` key
is specified in the `required*` keys vector.

```
(valid? {:a "A" :b "B"} {:prefix* "This map key"
                         :pattern* {:a {:e* ":a must be a string!"
                                        :f* string?}}
                         :required* [:a :b :c]})
; =>
; false
```


### How the turn off the validator?

The [`validator.api/ignore!`](documentation/cljc/pattern/API.md/#ignore) function
turns off the validator. Every checked data will be declared as valid.

Recommended for product releases and stable versions.

```
(ignore!)
```

 
