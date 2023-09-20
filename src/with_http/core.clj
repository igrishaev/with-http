(ns with-http.core
  (:import
   java.io.File
   java.net.URL)
  (:require
   [cheshire.core :as json]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [ring.adapter.jetty :refer [run-jetty]]
   [ring.middleware.json :refer [wrap-json-response wrap-json-params]]
   [ring.middleware.keyword-params :refer [wrap-keyword-params]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]
   [ring.middleware.params :refer [wrap-params]]
   [ring.util.mime-type :as mime-type]))


;; vector path
;; path get handler
;; rename resources

(def NOT-FOUND
  {:status 404
   :body {:error "with-http: route not found"}})


(defn make-url
  ([port]
   (make-url port "/"))
  ([port path]
   (format "http://localhost:%s%s" port path)))


(defn file? [x]
  (instance? File x))


(defn resource? [x]
  (instance? URL x))


(defn file-response [^File file]
  (let [content-type
        (-> file (.getName) (mime-type/ext-mime-type))]
    {:status 200
     :body file
     :headers {"Content-Type" content-type}}))


(defn resource-response [^URL url]
  (let [content-type
        (-> url (.getFile) (mime-type/ext-mime-type))]
    {:status 200
     :body (.openStream url)
     :headers {"Content-Type" content-type}}))


(defn make-app [method->path->response]
  (fn [request]

    (let [{:keys [default]}
          method->path->response

          {:keys [params]}
          request

          {:keys [request-method uri]}
          request

          _
          (log/infof "HTTP %s %s %s"
                     (-> request-method name str/upper-case)
                     uri params)

          response
          (get-in method->path->response
                  [request-method uri]
                  default)]

      (cond

        (map? response)
        response

        (fn? response)
        (response request)

        (file? response)
        (file-response response)

        (resource? response)
        (resource-response response)

        :else
        (throw (new Exception "Wrong response type"))))))


(defn add-default [routes]
  (if (:default routes)
    routes
    (assoc routes :default NOT-FOUND)))


(defmacro with-http
  [[port method->path->response] & body]
  `(let [app# (-> ~method->path->response
                  add-default
                  make-app
                  wrap-keyword-params
                  wrap-json-params
                  wrap-multipart-params
                  wrap-params
                  wrap-json-response)

         server#
         (run-jetty app# {:port ~port :join? false})]

     (try
       ~@body
       (finally
         (.stop server#)))))
