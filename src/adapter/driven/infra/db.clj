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

;; ;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; FUNCTIONS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn select-user-db [email]
  (-> (alia/execute session prepared-statement-select-user {:values [email]}) first))

(defn insert-jti-token-logout-db [jti email]
  (alia/execute session prepared-statement-insert-jti-token {:values [jti email]}))

(defn select-jti-db [jti]
  (-> (alia/execute session prepared-statement-select-jti {:values [jti]}) first))
