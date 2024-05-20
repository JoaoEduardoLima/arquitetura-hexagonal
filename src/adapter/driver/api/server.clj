(ns adapter.driver.api.server
  (:require
   [ring.adapter.jetty :as jetty]
   [ring.middleware.cookies :refer [wrap-cookies]]
   [ring.middleware.json :refer [wrap-json-body wrap-json-response]]
   [ring.middleware.multipart-params :refer [wrap-multipart-params]]
   [ring.middleware.params :refer [wrap-params]]
   [jumblerg.middleware.cors :refer [wrap-cors]]
   [adapter.driver.api.routes :refer [app]])
  (:gen-class))

(defn -main
  "Start the server"
  [& args]
  (jetty/run-jetty (-> app
                       (wrap-params :params)
                       (wrap-json-body {:keywords? true})
                       wrap-json-response
                       wrap-multipart-params
                       wrap-cookies
                       (wrap-cors #"http://localhost"))
                   {:port 3010}))