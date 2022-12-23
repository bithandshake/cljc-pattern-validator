
# pattern-api

### Overview

The <strong>pattern-api</strong> is a simple Clojure/ClojureScript tool which
helps you to check and validate every kind of data in your application.

### deps.edn

```
{:deps {bithandshake/pattern-api {:git/url "https://github.com/bithandshake/pattern-api"
                                  :sha     "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"}}
```

### Current version

Check out the latest commit on the [release branch](https://github.com/bithandshake/pattern-api/tree/release).

### Documentation

The <strong>pattern-api</strong> functional documentation is [available here](documentation/COVER.md).

### Changelog

You can track the changes of the <strong>pattern-api</strong> library [here](CHANGES.md).

### Index

- [How to validate a data?](#how-to-validate-a-data)

- [How to turn off the validator?](#how-to-turn-off-the-validator)

# Usage

### How to validate a data?

The [`pattern.api/valid?`](documentation/cljc/pattern/API.md/valid) function
checks whether the give data is valid or not.

- By using the `{:explain* true}` setting (default: true) the function will
  print the error messages on the console.
- By using the `{:prefix* "..."}` setting, a prefix will be prepended to the
  printed error messages.
- By using the `{:strict* true}` setting, other keys in data than passed in the
  pattern will not be allowed!  

You can check the given data by specifying the `:test*` set. A set of test functions
and logic gates. The `:e*` key in the set will be the printed error message if
any test fails.

```
(valid? "My string" {:test {:e* "This value must be a string!"
                            :f* string?}})
=>
true

(valid? "My string" {:test {:e* "This value must be nonempty!"
                            :not* empty?}})
=>
true

(valid? "My string" {:test {:e* "This value must be a nonempty string!"
                            :f*   string?
                            :not* empty?}})
=>
true
```

You can loose the leash on your data by using the `:opt*` and `:ign*` keys.
The `{:opt* true}` setting allows the data to be nil.
The `{:ign* true}` setting skips any other validator and the data will be qualified
as valid.

```
(valid? "My string" {:test {:e* "This value must be a string!"
                            :opt* true
                            :f*   string?}})
=>
true

(valid? nil {:test {:e* "This value must be a string!"
                    :opt* true
                    :f*   string?}})
=>
true                            
```

```
(def CIRCUMSTANCE true)

(valid? "My string" {:test {:e* "This value must be a string!"
                            :ign* CIRCUMSTANCE
                            :f*   string?}})
=>
true

(valid? :keyword {:test {:e* "This value must be a string!"
                         :ign* CIRCUMSTANCE
                         :f*   string?}})
=>
true                            
```

The `:prefix*` key helps you to use shorter error messages. It will be very useful
when you use patterns for testing with multiple error messages.

```
(valid? "My string" {:prefix* "This value"
                     :test {:e* "must be a string!"
                            :f* string?}})
=>
true    
```

You can compose complex tests by using the following logic gates:
`:and*`, `:nand*`, `:not*`, `:nor*`, `:or*`, `:xor*`.

```
(valid? ["A" "B"] {:test {:e* "This value must be a nonempty vector or map!"
                          :not* empty?
                          :or*  [map? vector?]}})
=>                          
true

(valid? ["A" "B"] {:test {:e* "This value must be a nonempty vector with string items!"
                          :and* [vector? #(every? string?)]
                          :not* empty?}})
=>                          
true

(valid? ["A" "B"] {:test {:e* "This value must be a nonempty vector or an empty value!"
                          :xor* [vector? empty?]}})
=>                          
true
```

You can use patterns to validate maps by using the `:pattern*` key.
The pattern must be a map. It's keys will be matched with the keys of the given
data and it's values must be maps with logic gates and test functions like the
`:test*` map in the previous examples.

```
(valid? {:a "A" :b :b :c 2} {:prefix* "This map key"
                             :pattern* {:a {:e* ":a must be a string!"
                                            :f* string?}
                                         :b {:e* ":b must be a keyword!"
                                            :f* keyword?
                                            :opt* true}
                                        :c {:e* ":c must be an integer, greater than 1"
                                            :and* [integer? #(> % 1)]}}})
=>
true                                            
```

In the test maps of a pattern, you can specify which keys can replace other keys
in the given data by using the `:rep*` key.
In the following example, the `:a` key can replace the `:b` key and vica versa.

```
(valid? {:a "A" :b :b} {:prefix* "This map key"
                        :pattern* {:a {:e* ":a must be a string!"
                                       :f* string?
                                       :rep* [:b]}
                                   :b {:e* ":b must be a keyword!"
                                       :f* keyword?
                                       :rep* [:a]}}})
=>
true                                       
```

By using the `{:strict* true}` setting, only the given pattern's keys will be allowed
to presence in the data.

```
(valid? {:a "A" :b "B"} {:prefix* "This map key"
                         :pattern* {:a {:e* ":a must be a string!"
                                        :f* string?}}
                         :strict* true})
=>
false                                 
```

### How the turn off the validator?

The [`pattern.api/ignore!`](documentation/cljc/pattern/API.md/ignore) function
turns off the validator. Every checked data will be qualified as valid.

For product releases and stable versions.

```
(ignore!)
```
