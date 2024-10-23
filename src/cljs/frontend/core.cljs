(ns frontend.core
  (:require ["jotai" :as jotai]
            [uix.core :refer [defui $]]
            [uix.dom]
            [frontend.todo]))

(defonce ^{:doc "Root-level store for all Jotai atoms. Useful for debugging."} store
  (jotai/createStore))

(comment
  (defn- debug-atom [^js a]
    (some-> a
            store.get
            (js->clj :keywordize-keys true)))

  (debug-atom frontend.todo/todo-list)
  (debug-atom (frontend.todo/get-todo-state 1)))

(defui app []
  ($ uix.core/strict-mode
     ($ jotai/Provider {:store store}
        ($ :<>
           ($ :h1 "UIx & Jotai app")
           ($ :div
              ($ frontend.todo/new-todo-button))
           ($ frontend.todo/render-todos)))))

(defonce root
  (uix.dom/create-root (js/document.getElementById "root")))

(defn mount []
  (uix.dom/render-root ($ app) root))

(defn ^:export init []
  (mount))
