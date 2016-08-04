(ns e2e.runner
  (require nw :as nw))

(let [client (nw/client)]
  (.start client))
