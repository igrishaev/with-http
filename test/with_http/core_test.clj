(ns with-http.core-test
  (:require
   [clj-http.client :as client]
   [clojure.test :refer [deftest is]]
   [with-http.core :refer [with-http make-url]]))


(def PORT 8899)


(deftest test-with-http-test-json

  (let [body
        {:hello [1 "test" true]}

        app
        {:get {"/foo" {:status 200
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
        {:get {"/foo" (fn [{:keys [params]}]
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
        {:get {"/foo" {:status 200 :body "OK"}}}

        url
        (make-url PORT "/dunno/lol")

        {:keys [status body]}
        (with-http [PORT app]
          (client/get url {:throw-exceptions? false
                           :as :json
                           :coerce :always}))]

    (is (= 404 status))
    (is (= {:error "with-http: page not found"} body))))
