(ns adapter.driver.api.controller
  (:require
   [core.usecase.usecase :refer [select-usuario-usecase insert-token-jti-logout-usecase select-jti-usecase]]
   [buddy.sign.jwt :as jwt]))

(def SECRET-KEY (System/getenv "SECRET_KEY"))

(defn handler-ok
  "retorna um response com status 200"
  [_req]
  {:status 200 :headers {"Content-Type" "application/json"} :body {:msg "Ok"}})

(defn handler-401
  "retorna um response com status 401"
  [_req]
  {:status 401 :headers {"Content-Type" "application/json"} :body {:msg "Não autorizado!"}})

(defn claim
  "cria o claim do token jwt"
  [email]
  (let [time-now (-> (java.util.Date.) (.getTime))]
    {:iss "api-jwt-cors"                    ;; issuer (emissor): quem emitiu o token
     :sub email                             ;; subject (assunto): 
     :aud "api-jwt-cors"                    ;; audience (público): quem deve receber o token
     :exp (+ time-now (* 1000 300))         ;; expiration (validade): tempo de vida do token (neste caso 5 minutos)
     :nbf time-now                          ;; not before (não antes): a partir de quando o token é válido
     :iat time-now                          ;; issued at (emitido em): data e hora de emissão do token
     :jti (str (java.util.UUID/randomUUID)) ;; JWT ID (identificador do token)
     }))

(defn logout? [token]
  (boolean (select-jti-usecase token)))

(defn post-handler-login
  "rota de login da api"
  [req]
  (let [email-req (get (:params req) "login")
        senha-req (get (:params req) "senha")
        usuario-db (select-usuario-usecase email-req senha-req)]
    (if usuario-db
      (let [jwt (jwt/sign (claim email-req) SECRET-KEY)]
        {:status 200
         :headers {"Content-Type" "application/json" "Authorization" (str "Bearer " jwt)}
         :body {:msg "Login efetuado com sucesso!"}})
      (handler-401 req))))

(defn post-handler-logout
  "rota de logout da api"
  [req]
  (let [token-req (-> req :headers (get "authorization"))
        token (when token-req (let [[_ token] (re-find #"Bearer (.*)" token-req)] token))
        token-decoded (try
                        (jwt/unsign token SECRET-KEY {:now (-> (java.util.Date.) (.getTime))})
                        (catch Exception e
                          nil))
        email (get token-decoded :sub)
        id-token (:jti token-decoded)]
    (if (and email id-token)
      (do
        (insert-token-jti-logout-usecase id-token email)
        (handler-ok req))
      (handler-401 req))))