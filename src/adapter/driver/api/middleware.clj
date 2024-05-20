(ns adapter.driver.api.middleware
  (:require
   [buddy.sign.jwt :as jwt]
   [adapter.driver.api.controller :refer [handler-401 logout?]]))

(def SECRET-KEY (System/getenv "SECRET_KEY"))

(defn middleware-auth
  "middleware de autorização da api"
  [handler]
  (fn [req]
    (let [token-req (-> req :headers (get "authorization"))
          token (when token-req (let [[_ token] (re-find #"Bearer (.*)" token-req)] token))
          token-decoded (try
                          (jwt/unsign token SECRET-KEY {:now (-> (java.util.Date.) (.getTime))})
                          (catch Exception e
                            nil))
          logout? (logout? (:jti token-decoded))]
      (if (and token-decoded (not logout?))
        (handler req)
        (handler-401 req)))))