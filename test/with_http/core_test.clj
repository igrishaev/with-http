(ns with-http.core-test
  (:require
   [clj-http.client :as client]
   [clojure.test :refer [deftest is]]
   [with-http.core :refer [with-http]]))


(deftest test-with-http-test

  (let [app
        {:get {"/foo" {:status 200
                       :body {:hello [1 "test" true]}}}}

        {:keys [status body]}
        (with-http [8089 app]
          (client/get "http://localhost:8089/foo"
                      {:as :json}))]

    (is (= 200 status))
    (is (= {:hello [1 "test" true]} body))))
