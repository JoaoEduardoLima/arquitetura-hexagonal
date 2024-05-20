(ns core.usecase.usecase
  (:require [core.ports.driven.interface-db :refer [IUsuarioDB IJtiDB]]
            [adapter.driven.infra.db :refer [select-user-db insert-jti-token-logout-db select-jti-db]]
            [core.domain.model :refer [senha-valida?]]))

(defrecord UsuarioDB []
  IUsuarioDB
  (select-usuario [_ email]
    (select-user-db email)))

(defrecord JtiDB []
  IJtiDB
  (insert-jti-token-logout [_ jti email]
    (insert-jti-token-logout-db jti email))
  (select-jti [_ jti]
    (select-jti-db jti)))

(defn- select-usuario-db [email]
  (.select-usuario (->UsuarioDB) email))

(defn- insert-token-jti-logout-db [jti email]
  (.insert-jti-token-logout (->JtiDB) jti email))

(defn- select-token-jti-db [jti]
  (.select-jti (->JtiDB) jti))

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