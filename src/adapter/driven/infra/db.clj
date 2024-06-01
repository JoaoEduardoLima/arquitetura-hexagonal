(ns adapter.driven.infra.db
  (:require
   [qbits.alia :as alia]))

(def cluster (alia/cluster {:session-keyspace "app"
                            :contact-points ["host.docker.internal"]
                            :port 9042
                            :load-balancing-local-datacenter "Analytics"}))

(def session (alia/connect cluster))


;; ;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; STATEMENTS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def prepared-statement-select-user
  (alia/prepare session "select * from app.users where email=?;"))

(def prepared-statement-insert-jti-token
  (alia/prepare session "INSERT INTO app.jti_logout (jti, email) VALUES (?, ?) USING TTL 300;"))

(def prepared-statement-select-jti
  (alia/prepare session "SELECT * FROM app.jti_logout WHERE jti=?;"))

(def prepared-statement-select-ra-document
  (alia/prepare session "SELECT * FROM app.ra_por_doc WHERE documento=?;"))

(def prepared-statement-insert-student
  (alia/prepare session "INSERT INTO app.aluno_por_ra (nome, documento, status_matricula, ra) VALUES (?, ?, ?, ?);"))

(def prepared-statement-insert-ra-document
  (alia/prepare session "INSERT INTO app.ra_por_doc (documento, ra) VALUES (?, ?);"))

(def prepared-statement-select-student-ra
  (alia/prepare session "SELECT * FROM app.aluno_por_ra WHERE ra=?;"))

(def prepared-statement-select-students
  (alia/prepare session "SELECT * FROM app.aluno_por_ra;"))

(def prepared-statement-update-student
  (alia/prepare session "UPDATE app.aluno_por_ra SET nome=?, status_matricula=?, nota_cursoa=?, status_cursoa=?, nota_cursob=?, status_cursob=?, nota_cursoc=?, status_cursoc=? WHERE ra=?;"))

;; ;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; FUNCTIONS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn select-user-db [email]
  (-> (alia/execute session prepared-statement-select-user {:values [email]}) first))

(defn insert-jti-token-logout-db [jti email]
  (alia/execute session prepared-statement-insert-jti-token {:values [jti email]}))

(defn select-jti-db [jti]
  (-> (alia/execute session prepared-statement-select-jti {:values [jti]}) first))

(defn select-ra-document-db [documento-aluno]
  (-> (alia/execute session prepared-statement-select-ra-document {:values [documento-aluno]}) first))

(defn insert-student-db [nome-aluno documento-aluno status-matricula ra-aluno]
  (alia/execute session prepared-statement-insert-student {:values [nome-aluno documento-aluno status-matricula ra-aluno]}))

(defn insert-ra-document-db [documento-aluno ra-aluno]
  (alia/execute session prepared-statement-insert-ra-document {:values [documento-aluno ra-aluno]}))

(defn select-student-ra-db [ra]
  (-> (alia/execute session prepared-statement-select-student-ra {:values [ra]}) first))

(defn select-students-db []
  (alia/execute session prepared-statement-select-students {}))

(defn update-student-db [ra nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]
  (alia/execute session prepared-statement-update-student {:values [nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc ra]}))