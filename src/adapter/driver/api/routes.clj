(ns adapter.driver.api.routes
  (:require
   [compojure.core :refer [defroutes context GET POST]]
   [compojure.route :as route]
   [adapter.driver.api.middleware :refer [middleware-auth]]
   [adapter.driver.api.controller :refer [handler-ok post-handler-login post-handler-logout]]))

(defroutes app
  (POST "/login"  [] post-handler-login)
  (middleware-auth
   (context "/api/v1" []
     (GET    "/"        [] handler-ok)
     (POST    "/logout" [] post-handler-logout)))
  (route/not-found "<h1>Page not found</h1>"))