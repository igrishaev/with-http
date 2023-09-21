(ns with-http.core-test
  (:require
   [clojure.java.io :as io]
   [clj-http.client :as client]
   [clojure.test :refer [deftest is]]
   [with-http.core :refer [with-http make-url]]))


(def PORT 8899)


(deftest test-with-http-test-json

  (let [body
        {:hello [1 "test" true]}

        app
        {"/foo" {:get {:status 200
                       :body body}}}

        url
        (make-url PORT "/foo")

        {:keys [status body]}
        (with-http [PORT app]
          (client/get url {:as :json}))]

    (is (= 200 status))
    (is (= {:hello [1 "test" true]} body))))


(deftest test-with-http-function

  (let [capture!
        (atom nil)

        app
        {"/foo" {:get (fn [{:keys [params]}]
                        (reset! capture! params)
                        {:status 200 :body "OK"})}}

        url
        (make-url PORT "/foo?a=1&b=2")

        {:keys [status body]}
        (with-http [PORT app]
          (client/get url))]

    (is (= 200 status))
    (is (= "OK" body))
    (is (= {:a "1" :b "2"} @capture!))))


(deftest test-with-http-not-found

  (let [app
        {"/foo" {:get {:status 200 :body "OK"}}}

        url
        (make-url PORT "/dunno/lol")

        {:keys [status body]}
        (with-http [PORT app]
          (client/get url {:throw-exceptions? false
                           :as :json
                           :coerce :always}))]

    (is (= 404 status))
    (is (= {:error "with-http: route not found"} body))))


(deftest test-with-http-file-txt

  (let [app
        {"/foo" {:get (io/file "dev-resources/test.txt")}}

        url
        (make-url PORT "/foo?a=1&b=2")

        {:keys [status headers body]}
        (with-http [PORT app]
          (client/get url))

        {:strs [Content-Type]}
        headers]

    (is (= 200 status))
    (is (= "text/plain" Content-Type))
    (is (= "123456\n" body))))


(deftest test-with-http-file-json

  (let [app
        {"/foo" {:get (io/file "dev-resources/test.json")}}

        url
        (make-url PORT "/foo?a=1&b=2")

        {:keys [status headers body]}
        (with-http [PORT app]
          (client/get url {:as :json}))

        {:strs [Content-Type]}
        headers]

    (is (= 200 status))
    (is (= "application/json" Content-Type))
    (is (= {:foo [1 2 3]} body))))


(deftest test-with-http-resource-json

  (let [app
        {"/foo" {:get (io/resource "test.json")}}

        url
        (make-url PORT "/foo?a=1&b=2")

        {:keys [status headers body]}
        (with-http [PORT app]
          (client/get url {:as :json}))

        {:strs [Content-Type]}
        headers]

    (is (= 200 status))
    (is (= "application/json" Content-Type))
    (is (= {:foo [1 2 3]} body))))


(deftest test-with-http-custom-default

  (let [app
        {"/foo" {:get {:status 200 :body "hello"}}
         :default (fn [_]
                    {:status 202 :body "I'm the default!"})}

        url
        (make-url PORT "/test")

        {:keys [status body]}
        (with-http [PORT app]
          (client/get url))]

    (is (= 202 status))
    (is (= "I'm the default!" body))))


(deftest test-with-http-vector-path

  (let [path
        ["/foo/bar/" 42 "/test"]

        app
        {path
         {:get {:status 200 :body "hello"}}}

        url
        (make-url PORT path)

        {:keys [status body]}
        (with-http [PORT app]
          (client/get url))]

    (is (= 200 status))
    (is (= "hello" body))))


(deftest test-with-http-string-response

  (let [app
        {"/foo" {:get "AAA"}}

        url
        (make-url PORT "/foo?a=1&b=2")

        {:keys [status headers body]}
        (with-http [PORT app]
          (client/get url))

        {:strs [Content-Type]}
        headers]

    (is (= 200 status))
    (is (= "text/plain" Content-Type))
    (is (= "AAA" body))))
