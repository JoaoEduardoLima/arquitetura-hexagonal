(ns core.domain.model
  (:require 
   [buddy.core.codecs :as codecs]
   [buddy.core.mac :as mac]))

(def SECRET-KEY (System/getenv "SECRET_KEY"))

(defn senha-encode
  "codifica a senha do usuário para salvar no banco de dados"
  [senha]
  (-> (mac/hash senha {:key SECRET-KEY :alg :hmac+sha256}) (codecs/bytes->hex)))

(defn senha-valida?
  "decodifica a senha salva no banco de dados e compara com a senha recebida na requisição.
   Quando não passado a senha-db é simulado com uma string vazia para não dar pistas sobre a existência do usuário (timing attack)"
  [senha-req senha-db]
  (let [senha-db' (or senha-db "")]
   (mac/verify senha-req (codecs/hex->bytes senha-db')  {:key SECRET-KEY :alg :hmac+sha256})))