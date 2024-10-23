(ns frontend.todo
  (:require [uix.core :refer [defui $]]
            [frontend.state-util :refer [jotai-atom jotai-atom-family use-atom use-atom-value use-set-atom]]))

(defonce get-todo-state
  (jotai-atom-family (constantly {:done false})))

(defonce todo-list
  (jotai-atom []))

(defui todo-item [{:keys [id]}]
  (let [[todo set-todo!] (use-atom (get-todo-state id))]
    ($ :li.todo {:on-click (fn [] (set-todo! update :done not))}
       ($ :span
          (cond->> (str "Todo: " id)
            (:done todo)
            ($ :s))))))

(defui render-todos []
  (let [todos (use-atom-value todo-list)]
    (when (seq todos)
      ($ :ul
         (for [id todos]
           ($ todo-item {:id id
                         :key id}))))))

(defui new-todo-button []
  (let [set-todo-list! (use-set-atom todo-list)]
    ($ :button {:on-click (fn [] (set-todo-list! #(conj % (inc (count %)))))}
       "New todo")))
