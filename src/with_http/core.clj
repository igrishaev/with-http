(ns with-http.core
  (:refer-clojure :exclude [update-keys])
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


(def NOT-FOUND
  {:status 404
   :body {:error "with-http: route not found"}})


(defn make-path [parts]
  (->> parts
       (map str)
       (str/join "/")))


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


(defn make-app [path->method->response]
  (fn [request]

    (let [{:keys [default]}
          path->method->response

          {:keys [params]}
          request

          {:keys [request-method uri]}
          request

          _
          (log/infof "HTTP %s %s %s"
                     (-> request-method name str/upper-case)
                     uri params)

          response
          (get-in path->method->response
                  [uri request-method]
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


;; for old Clojure versions
(defn update-keys
  [m f]
  (let [ret (persistent!
             (reduce-kv (fn [acc k v] (assoc! acc (f k) v))
                        (transient {})
                        m))]
    (with-meta ret (meta m))))


(defn compile-paths [routes]
  (update-keys routes (fn [path]
                        (if (sequential? path)
                          (make-path path)
                          path))))


(defmacro with-http
  [[port path->method->response] & body]
  `(let [app# (-> ~path->method->response
                  ;; prepare routes
                  compile-paths
                  add-default
                  make-app
                  ;; ring wrappers
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
