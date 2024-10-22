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
  ($ :<>
     ($ :h1 "UIx & Jotai app")
     ($ :div
        ($ frontend.todo/new-todo-button))
     ($ frontend.todo/render-todos)))

(defonce root
  (uix.dom/create-root (js/document.getElementById "app")))

(defn mount []
  (js/console.log "mount")
  (->> root
       (uix.dom/render-root ($ uix.core/strict-mode
                               ($ jotai/Provider {:store store}
                                  ($ app))))))

;; start is called by init and after code reloading finishes
(defn ^:dev/after-load start []
  (js/console.log "start")
  (mount))

(defn init []
  ;; init is called ONCE when the page loads
  ;; this is called in the index.html and must be exported
  ;; so it is available even in :advanced release builds
  (js/console.log "init")
  (start))

;; this is called before any code is reloaded
(defn ^:dev/before-load stop []
  (js/console.log "stop"))
