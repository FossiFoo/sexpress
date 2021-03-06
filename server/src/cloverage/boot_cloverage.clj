(ns cloverage.boot-cloverage
  (:require [boot.core :as core]
            [boot.pod :as pod]
            [boot.util :as util]
            [clojure.string :as s]
            [taoensso.timbre :refer [log spy] :as timbre]
            [clojure.set :as set]))

(def ^:private deps
  '[[cloverage "1.0.9"]])

(core/deftask cloverage
  "Run cloverage in a pod.

  Expects test namespaces to be included in :source-paths."

  [m test-matcher VAL regex "Regex used to select test namespaces"
   o opts         VAL str   "other arguments passed to cloverage"]
  (let [pod (-> (core/get-env)
                (update-in [:dependencies] into deps)
                pod/make-pod
                future)]
    (core/with-post-wrap fileset
      (let [all-namespaces (core/fileset-namespaces fileset)
            _ (log :error (core/input-dirs fileset))
            namespaces (remove #(= "cloverage.boot-cloverage" (name %)) all-namespaces)
            test-matcher-or-default (or test-matcher #".*-test")
            [code-namespaces test-namespaces] ((juxt remove filter) #(re-matches test-matcher-or-default (name %)) namespaces)
            code-ns-names (filter #(re-matches #"server.*" %) (map name code-namespaces))
            test-ns-names (map name test-namespaces)
            test-args (interleave (repeat "-x") test-ns-names)
            more-opts (when opts (s/split opts #"\s+"))
            args (concat more-opts test-args code-ns-names)]
        (util/info "running cloverage with %s%n" (pr-str args))

        (pod/with-eval-in @pod
          (doseq [ns '~code-namespaces] (require ns))    ; puts the code-namespaces on the classpatch and requires them
          (require 'cloverage.coverage)                  ; we also need to resolve cloverage itself
          (binding [cloverage.coverage/*exit-after-test* false]
            (cloverage.coverage/-main ~@args)))))))         ; args include code-namespace names, so cloverage does not need to resolve them, test-dir will be scanned for tests
