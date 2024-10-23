(ns frontend.core
  (:require ["jotai" :as jotai]
            [uix.core :refer [defui $]]
            [uix.dom]
            [frontend.todo]
            [frontend.state-util]))

(defonce ^{:doc "Root-level store for all Jotai atoms. Useful for debugging."} store
  (jotai/createStore))

(comment
  (def debug-get-atom (partial frontend.state-util/debug-get-atom store))
  (debug-get-atom frontend.todo/todo-list)
  (debug-get-atom (frontend.todo/get-todo-state 1))

  (def debug-set-atom (partial frontend.state-util/debug-set-atom store))
  (-> (frontend.todo/get-todo-state 1)
      (debug-set-atom #(update % :done not))))

(defui app []
  ($ uix.core/strict-mode
     ($ jotai/Provider {:store store}
        ($ :<>
           ($ :h1 "Todo")
           ($ frontend.todo/new-todo-button)
           ($ frontend.todo/render-todos)))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn mount []
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (mount))
