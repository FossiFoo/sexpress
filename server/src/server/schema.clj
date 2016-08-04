(ns server.schema)

(def +account+ {:account {:user [{:type :string
                                  :cardinality :one
                                  :unique :value
                                  :required true}]}})

;; file
;; namespace

(def +edit+ {:edit {:editor [{:type :ref
                              :ref {:ns :account}}]
                    :operation [{:type :enum
                                 :enum {:ns :edit.operation
                                        :values #{:create}}}]
                    :version [{:type :long}]
                    :session [{:type :string}]}})

(def +project+ {:project {:name [{:type :string
                                  :unique :identity
                                  :required true}]}})

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

(def +schema+ (merge {} +account+ +edit+ +project+ +session+))
