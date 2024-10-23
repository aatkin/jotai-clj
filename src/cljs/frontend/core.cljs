(ns frontend.core
  (:require ["jotai" :as jotai]
            [uix.core :refer [defui $]]
            [uix.dom]
            [frontend.todo]))

(defonce ^{:doc "Root-level store for all Jotai atoms. Useful for debugging."} store
  (jotai/createStore))

(comment
  (defn- debug-get-atom [^js a] (store.get a))
  (debug-get-atom frontend.todo/todo-list)
  (debug-get-atom (frontend.todo/get-todo-state 1))

  (defn- debug-set-atom [^js a f] (store.set a f))
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
