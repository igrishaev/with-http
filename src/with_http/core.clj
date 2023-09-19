(ns with-http.core
  (:require
   [cheshire.core :as json]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.params :refer [wrap-params]]))


(def not-found
  {:status 404
   :body {:error "with-http: page not found"}})


(defn make-url
  ([port]
   (make-url port "/"))
  ([port path]
   (format "http://localhost:%s%s" port path)))


(defn make-app [method->path->response]
  (fn [request]

    (let [{:keys [params]}
          request

          {:keys [request-method uri]}
          request

          _
          (log/infof "HTTP %s %s %s"
                     (-> request-method name str/upper-case)
                     uri params)

          response
          (get-in method->path->response [request-method uri])]

      (cond

        (nil? response)
        not-found

        (map? response)
        response

        (fn? response)
        (response request)

        :else
        (throw (new Exception "Wrong response type"))))))


(defmacro with-http
  [[port method->path->response] & body]
  `(let [app# (-> ~method->path->response
                  make-app
                  wrap-keyword-params
                  wrap-json-params
                  wrap-params
                  wrap-json-response)

         server#
         (run-jetty app# {:port ~port :join? false})]

     (try
       ~@body
       (finally
         (.stop server#)))))
