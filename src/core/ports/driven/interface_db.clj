(ns core.ports.driven.interface-db)

(defprotocol IUsuarioDB
  (select-usuario [this email]))

(defprotocol IJtiDB
  (insert-jti-token-logout [this jti email])
  (select-jti [this jti]))