(ns adapter.driver.api.controller
  (:require
   [core.usecase.usecase :as usecase]
   [buddy.sign.jwt :as jwt]))

(def SECRET-KEY (System/getenv "SECRET_KEY"))

(defn handler-ok []
  {:status 200 :headers {"Content-Type" "application/json"} :body {:msg "Ok"}})

(defn handler-200 [body]
  {:status 200 :headers {"Content-Type" "application/json"} :body body})

(defn handler-401 []
  {:status 401 :headers {"Content-Type" "application/json"} :body {:msg "Não autorizado!"}})

(defn handler-400 [erro]
  {:status 400 :headers {"Content-Type" "application/json"} :body {:msg erro}})

(defn claim
  "cria o claim do token jwt"
  [email]
  (let [time-now (-> (java.util.Date.) (.getTime))]
    {:iss "api-jwt-cors"                    ;; issuer (emissor): quem emitiu o token
     :sub email                             ;; subject (assunto): 
     :aud "api-jwt-cors"                    ;; audience (público): quem deve receber o token
     :exp (+ time-now (* 1000 1200))         ;; expiration (validade): tempo de vida do token (neste caso 20 minutos)
     :nbf time-now                          ;; not before (não antes): a partir de quando o token é válido
     :iat time-now                          ;; issued at (emitido em): data e hora de emissão do token
     :jti (str (java.util.UUID/randomUUID)) ;; JWT ID (identificador do token)
     }))

(defn logout? [token]
  (boolean (usecase/select-jti-usecase token)))

(defn post-handler-login [req]
  (let [email-req (get (:params req) "login")
        senha-req (get (:params req) "senha")
        usuario-db (usecase/select-usuario-usecase email-req senha-req)]
    (if usuario-db
      (let [jwt (jwt/sign (claim email-req) SECRET-KEY)]
        {:status 200
         :headers {"Content-Type" "application/json" "Authorization" (str "Bearer " jwt)}
         :body {:msg "Login efetuado com sucesso!"}})
      (handler-401))))

(defn post-handler-logout [req]
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
        (usecase/insert-token-jti-logout-usecase id-token email)
        (handler-ok))
      (handler-401))))

(defn post-handler-cadastro [req]
  (let [nome-aluno (get (:params req) "nome")
        documento-aluno (get (:params req) "documento")
        cadastro-aluno (usecase/insert-aluno-usecase nome-aluno documento-aluno)]
    (if (:erro cadastro-aluno)
      (handler-400 (:erro cadastro-aluno))
      (handler-200 {:msg "Aluno cadastrado com sucesso!"
                    :ra (:ra cadastro-aluno)}))))

(defn post-handler-atualiza [req]
  (let [{:keys [ra nome status_matricula nota_cursoa status_cursoa nota_cursob status_cursob nota_cursoc status_cursoc]} (:body req)
        atualiza-aluno (usecase/update-aluno-usecase ra nome status_matricula nota_cursoa status_cursoa nota_cursob status_cursob nota_cursoc status_cursoc)]
    (if (:erro atualiza-aluno)
      (handler-400 (:erro atualiza-aluno))
      (handler-200 (:msg atualiza-aluno)))))

(defn get-handler-consulta-ra [req]
  (if-let [aluno (usecase/select-aluno-ra-usecase (:ra (:params req)))]
    (handler-200 aluno)
    (handler-400 "Aluno não encontrado!")))

(defn get-handler-consulta-documento [req]
  (if-let [ra (usecase/select-ra-documento-usecase (:documento (:params req)))]
    (handler-200 ra)
    (handler-400 "RA não encontrado!")))

(defn get-handler-consulta [_req]
  (if-let [alunos (usecase/select-alunos-usecase)]
    (handler-200 alunos)
    (handler-400 "Nenhum aluno encontrado!")))
