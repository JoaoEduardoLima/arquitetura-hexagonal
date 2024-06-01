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

(defn dados-cadastro-aluno-valido? [nome-aluno documento-aluno]
  (cond
    (or (not (string? nome-aluno)) (not (re-matches #"^[a-zA-ZÀ-ÿ\s]+$" nome-aluno)))   {:erro "Nome do aluno inválido"}
    (or (not (string? documento-aluno)) (not (re-matches #"^\d{11}$" documento-aluno))) {:erro "Documento do aluno inválido"}
    :else true))

(defn gera-ra []
  (let [ra (rand-int 99999)]
    (format "RA%08d" ra)))

(defn dados-atualizacao-aluno-valido? [nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]
  (cond
    (and nome (or (not (string? nome)) (not (re-matches #"^[a-zA-ZÀ-ÿ\s]+$" nome))))         {:erro "Nome do aluno inválido"}
    (and status-matricula (not (#{"ativo" "desativo"} status-matricula)))                    {:erro "Status da matrícula inválido"}
    (and nota-cursoa (or (not (number? nota-cursoa)) (neg? nota-cursoa) (> nota-cursoa 10))) {:erro "Nota do curso A inválida"}
    (and status-cursoa (not (#{"matriculado" "reprovado" "nao matriculado"} status-cursoa))) {:erro "Status do curso A inválido"}
    (and nota-cursob (or (not (number? nota-cursob)) (neg? nota-cursob) (> nota-cursob 10))) {:erro "Nota do curso B inválida"}
    (and status-cursob (not (#{"matriculado" "reprovado" "nao matriculado"} status-cursob))) {:erro "Status do curso B inválido"}
    (and nota-cursoc (or (not (number? nota-cursoc)) (neg? nota-cursoc) (> nota-cursoc 10))) {:erro "Nota do curso C inválida"}
    (and status-cursoc (not (#{"matriculado" "reprovado" "nao matriculado"} status-cursoc))) {:erro "Status do curso C inválido"}
    :else true))
