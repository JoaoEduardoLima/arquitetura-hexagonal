(ns core.ports.driven.interface-db)

(defprotocol IUsuarioDB
  (select-usuario [this email]))

(defprotocol IJtiDB
  (insert-jti-token-logout [this jti email])
  (select-jti [this jti]))

(defprotocol IAlunoDB
  (insert-aluno [this nome-aluno documento-aluno status-matricula ra-aluno])
  (insert-ra-documento [this documento-aluno ra-aluno])
  (select-aluno-ra [this ra-aluno])
  (select-ra-documento [this documento-aluno])
  (select-alunos [this])
  (update-aluno [this ra nome status-matricula nota-cursoa status-cursoa nota-cursob status-cursob nota-cursoc status-cursoc]))