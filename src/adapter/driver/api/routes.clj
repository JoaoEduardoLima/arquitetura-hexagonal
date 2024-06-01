(ns adapter.driver.api.routes
  (:require
   [compojure.core :refer [defroutes context GET POST]]
   [compojure.route :as route]
   [adapter.driver.api.middleware :refer [middleware-auth]]
   [adapter.driver.api.controller :as ctr]))

(defroutes app
  (POST "/login" [] ctr/post-handler-login)
  (middleware-auth
   (context "/api/v1" []
     (POST  "/logout"              [] ctr/post-handler-logout)
     (POST  "/cadastro"            [] ctr/post-handler-cadastro)
     (POST  "/atualiza/:ra"        [] ctr/post-handler-atualiza)
     (GET   "/consulta/:ra"        [] ctr/get-handler-consulta-ra)
     (GET   "/consulta/:documento" [] ctr/get-handler-consulta-documento)
     (GET   "/consulta"            [] ctr/get-handler-consulta)))
  (route/not-found "<h1>Page not found</h1>"))