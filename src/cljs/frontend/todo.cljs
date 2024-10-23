(ns frontend.todo
  (:require ["jotai" :as jotai]
            ["jotai/utils" :as jotai-utils]
            [uix.core :refer [defui $]]))

(defonce get-todo-state
  (jotai-utils/atomFamily #(jotai/atom {:done false})))

(defonce todo-list
  (jotai/atom []))

(defui todo-item [{:keys [id]}]
  (let [[todo set-todo!] (jotai/useAtom (get-todo-state id))]
    ($ :li.todo {:on-click (fn [] (set-todo! #(update % :done not)))}
       ($ :span
          (cond->> (str "Todo: " id)
            (:done todo)
            ($ :s))))))

(defui render-todos []
  (let [todos (jotai/useAtomValue todo-list)]
    (when (seq todos)
      ($ :ul
         (for [id todos]
           ($ todo-item {:id id
                         :key id}))))))

(defui new-todo-button []
  (let [set-todo-list! (jotai/useSetAtom todo-list)]
    ($ :button {:on-click (fn [] (set-todo-list! #(conj % (inc (count %)))))}
       "New todo")))
