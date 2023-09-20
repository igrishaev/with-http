# with-http

A powerful macro to stub HTTP calls with a local Jetty server. Declarative,
flexible, and extremely useful.

**ToC**

<!-- toc -->

- [Installation](#installation)
- [Why](#why)
- [Usage](#usage)
  * [Basic](#basic)
  * [Maps and functions](#maps-and-functions)
  * [JSON](#json)
  * [Files](#files)
  * [Resources](#resources)
  * [Capturing requests](#capturing-requests)
  * [Vector paths](#vector-paths)
  * [Default handler](#default-handler)
- [License](#license)

<!-- tocstop -->

## Installation

Lein:

```clojure
[com.github.igrishaev/with-http "0.1.0"]
```

Deps.edn

```clojure
{com.github.igrishaev/with-http {:mvn/version "0.1.0"}}
```

Pay attention: since the library is mostly used for tests, put the dependency in
the corresponding profile or alias. Storing it in global dependencies is not a
good idea is it becomes a part of the production code.

## Why


## Usage

~~~clojure
(ns some.test-namespace
  (:require
   [clj-http.client :as client]
   [clojure.test :refer [deftest is]]
   [with-http.core :refer [with-http make-url]]))
~~~

~~~clojure
(deftest test-local-server

  (let [app
        {"/foo" {:get {:status 200
                       :body {:hello [1 "test" true]}}}}

        {:keys [status body]}
        (with-http [8080 app]
          (client/get "http://localhost:8080/foo" {:as :json}))]

    (is (= 200 status))
    (is (= {:hello [1 "test" true]} body))))
~~~

### Basic

### Maps and functions

### JSON

### Files

### Resources

### Capturing requests

### Vector paths

### Default handler

## License

Copyright © 2023 Ivan Grishaev

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
