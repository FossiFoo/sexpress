(ns server.schema
  (:require [clojure.spec :as s]))

(s/def :sexpress/user-name string?)
(s/def :sexpress/account (s/keys :req [:sexpress/user-name]))

(def +account+ {:account {:user-name [{:type :string
                                       :cardinality :one
                                       :unique :value
                                       :required true}]}})

;; clojureisms

(s/def :sexpress/metadata (s/map-of keyword? any?))
(s/def :sexpress/symbol string?)

;; namespace

(s/def :sexpress/namespace-metadata :sexpress/metadata)
(s/def :sexpress/namespace-data (s/keys :opt-un [:sexpress/interns :sexpress/refers :sexpress/imports :sexpress/aliases]))
(s/def :sexpress/namespace-name string?)
(s/def :sexpress/namespace (s/keys :req [:sexpress/namespace-name :sexpress/namespace-data] :opt [:sexpress/metadata]))

(def +namespace+ {:namespace {:namespace-name [{:type :string
                                                :unique :identity
                                                :required true}]
                              :namespace-data [{:type :string
                                                :required true}]}})

;; vars

(s/def :sexpress/var-data (s/keys))
(s/def :sexpress/var (s/keys :req [:sexpress/symbol :sexpress/namespace-name :sexpress/var-data] :opt [:sexpress/metadata]))

(def +var+ {:var {:ns-symbol [{:type :string
                               :unique :identity
                               :required :true}]
                  :symbol [{:type :string
                            :required true}]
                  :namespace [{:type :ref
                               :ref {:ns :namespace}
                               :required true}]
                  :var-data [{:type :string
                              :required true}]}})

;; edit

(def +edit+ {:edit {:editor [{:type :ref
                              :ref {:ns :account}}]
                    :operation [{:type :enum
                                 :enum {:ns :edit.operation
                                        :values #{:ns-create}}}]
                    :version [{:type :long}]
                    :session [{:type :string}]}})

;; project

(s/def :sexpress/project-name string?)
(s/def :sexpress/namespace-by-name (s/map-of :sexpress/namespace-name :sexpress/namespace))
(s/def :sexpress/project (s/keys :req [:sexpress/project-name :sexpress/namespace-by-name]))

(def +project+ {:project {:project-name [{:type :string
                                          :unique :identity
                                          :required true}]
                          :namespaces [{:type :ref
                                        :cardinality :many
                                        :ref {:ns :namespace}}]}})

(def +session+ {:session {:name [{:type :string
                                  :unique :identity
                                  :required true}]
                          :projects [{:type :ref
                                      :cardinality :many
                                      :ref {:ns :project}}]
                          :users [{:type :ref
                                   :cardinality :many
                                   :required true
                                   :ref {:ns :account}}]}})

(def +schema+ (merge {} +account+ +edit+ +namespace+ +var+ +project+ +session+))
