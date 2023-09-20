# with-http

A powerful macro to stub HTTP calls with a local Jetty server. Declarative,
flexible, and extremely useful.

**ToC**

<!-- toc -->

- [Installation](#installation)
- [About](#about)
- [Usage](#usage)
  * [Basic](#basic)
  * [Maps and functions](#maps-and-functions)
  * [JSON](#json)
  * [Slow responses](#slow-responses)
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

## About

I've been copying that macro through many project and now it's time to ship it
as a standalone library.

The library provides a `with-http` macro of the following form:

~~~clojure
(with-http [port app]
  ...body)
~~~

where `port` is the number (1..65535) and the `app` is a map of routes. When
entering the macro, it spawns a local Jetty server on that port in the
background. The `app` map tells the server how to respond on calls.

[aero]: https://github.com/juxt/aero

Now that you have a running server, point your HTTP API clients to
`http://localhost:<port>` to imitate real network interaction. For example, for
prod, a third-party base URL is https://api.some.cool.service but for test, it
is http://localhost:8088. This can be easily done using environment variables or
the [Aero library][aero].

Why not using `with-redefs`, would you ask? Well, although `with-redefs` looks
like a solution at first glance, it's questionable. Using `with-redefs` meaning
lying to yourself. In fact, you temporarily waste some pieces of code pretending
it's OK, but it's not.

Often, bugs lurk in the code that you substitute `with-redefs`, namely:

- you've messed up with MD5/SHA/etc algorithms that sign a request. Calling
  localhost would trigger that code and lead to an exception, but `with-redefs`
  would not.

- TODO

- TODO

The good news is, the `with-http` macro can test all the cases mentioned above
and much more. Below, please find the examples of its usage.

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

### Slow responses

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
