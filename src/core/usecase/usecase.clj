(ns core.usecase.usecase
  (:require [core.ports.driven.interface-db :refer [IUsuarioDB IJtiDB IAlunoDB]]
            [core.ports.driver.interface-api :refer [IAluno]]
            [adapter.driven.infra.db :as db]
            [core.domain.model :refer [senha-valida? dados-cadastro-aluno-valido? gera-ra dados-atualizacao-aluno-valido?]]))

(defrecord UsuarioDB []
  IUsuarioDB
  (select-usuario [_ email]
    (db/select-user-db email)))

(defrecord JtiDB []
  IJtiDB
  (insert-jti-token-logout [_ jti email]
    (db/insert-jti-token-logout-db jti email))
  (select-jti [_ jti]
    (db/select-jti-db jti)))

(defrecord AlunoDB []
  IAlunoDB
  (select-ra-documento [_ documento-aluno]
    (db/select-ra-document-db documento-aluno))
  (select-aluno-ra [_ ra]
    (db/select-student-ra-db ra))
  (select-alunos [_]
    (db/select-students-db))
  (insert-aluno [_ nome-aluno documento-aluno status-matricula ra-aluno]
    (db/insert-student-db nome-aluno documento-aluno status-matricula ra-aluno))
  (insert-ra-documento [_ documento-aluno ra-aluno]
    (db/insert-ra-document-db documento-aluno ra-aluno))
  (update-aluno [_ ra nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]
    (db/update-student-db ra nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc)))

(defrecord Aluno []
  IAluno
  (valida-dados-cadastro [_ nome-aluno documento-aluno]
    (dados-cadastro-aluno-valido? nome-aluno documento-aluno))
  (valida-dados-atualizacao [_ nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]
    (dados-atualizacao-aluno-valido? nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc)))

(defn- select-usuario-db [email]
  (.select-usuario (->UsuarioDB) email))

(defn- insert-token-jti-logout-db [jti email]
  (.insert-jti-token-logout (->JtiDB) jti email))

(defn- select-token-jti-db [jti]
  (.select-jti (->JtiDB) jti))

(defn- select-ra-documento-db [documento-aluno]
  (.select-ra-documento (->AlunoDB) documento-aluno))

(defn- insert-aluno-db [nome-aluno documento-aluno status-matricula ra-aluno]
  (.insert-aluno (->AlunoDB) nome-aluno documento-aluno status-matricula ra-aluno)
  (.insert-ra-documento (->AlunoDB) documento-aluno ra-aluno))

(defn- select-aluno-ra-db [ra]
  (.select-aluno-ra (->AlunoDB) ra))

(defn- select-alunos-db []
  (.select-alunos (->AlunoDB)))

(defn- update-aluno-db [ra nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]
  (.update-aluno (->AlunoDB) ra nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc))

;; ;;;;;;;;;;;;;;;;;;;;; usecases ;;;;;;;;;;;;;;;;;;;;;

(defn select-usuario-usecase [email senha]
  (when-not (empty? email)
    (let [user-db (select-usuario-db email)
          senha-valida? (senha-valida? senha (:senha user-db))]
      (when senha-valida? user-db))))

(defn insert-token-jti-logout-usecase [jti email]
  (when-not (some empty? [jti email])
    (insert-token-jti-logout-db jti email)))

(defn select-jti-usecase [jti]
  (when-not (empty? jti)
    (select-token-jti-db jti)))

(defn insert-aluno-usecase [nome-aluno documento-aluno]
  (if-let [erro (:erro (.valida-dados-cadastro (->Aluno) nome-aluno documento-aluno))]
    {:erro erro}
    (let [aluno-ja-cadastrado? (select-ra-documento-db documento-aluno)]
      (if aluno-ja-cadastrado?
        {:erro "Aluno já cadastrado!"}
        (let [novo-ra (gera-ra)]
          (insert-aluno-db nome-aluno documento-aluno "ativo" novo-ra)
          {:ra novo-ra})))))

(defn select-aluno-ra-usecase [ra]
  (when-not (empty? ra)
    (when-let [aluno (select-aluno-ra-db ra)]
      aluno)))

(defn select-ra-documento-usecase [documento]
  (when-not (empty? documento)
    (when-let [ra (select-ra-documento-db documento)]
      ra)))

(defn select-alunos-usecase []
  (select-alunos-db))

(defn update-aluno-usecase [ra nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]
  (when-let [aluno (select-aluno-ra-usecase ra)]
    (if-let [erro (:erro (.valida-dados-atualizacao (->Aluno) nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc))]
      {:erro erro}
      (do
        (update-aluno-db (or ra (:ra aluno))
                         (or nome (:nome aluno))
                         (or status-matricula (:status_matricula aluno))
                         (float (or nota-cursoa (:nota_cursoa aluno) 0))
                         (or status-cursoa (:status_cursoa aluno) "nao matriculado")
                         (float (or nota-cursob (:nota_cursob aluno) 0))
                         (or status-cursob (:status_cursob aluno) "nao matriculado")
                         (float (or nota-cursoc (:nota_cursoc aluno) 0))
                         (or status-cursoc (:status_cursoc aluno) "nao matriculado"))
        {:msg "Aluno atualizado com sucesso!"}))))
  