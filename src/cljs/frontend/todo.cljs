(ns frontend.todo
  (:require ["jotai" :as jotai]
            ["jotai/utils" :as jotai-utils]
            [uix.core :refer [defui $]]))

;; holds simple integer list of todo ids, initialized with dummy data
(defonce all-todos
  (jotai/atom (vec (range 1 10))))

;; function that returns cached atom state for todo id
(defonce get-todo-state
  (jotai-utils/atomFamily #(jotai/atom {:done false})))

(defui todo-item [{:keys [id]}]
  (let [[todo set-todo!] (jotai/useAtom (get-todo-state id))]
    ($ :li.todo.cursor-pointer {:on-click (fn [] (set-todo! #(update % :done not)))}
       ($ :span
          (cond->> (str "Todo: " id)
            (:done todo)
            ($ :s))))))

;; subscribes to all-todos atom
(defonce todo-list
  (jotai/atom (fn [get-fn]
                (->> (get-fn all-todos)
                     (remove (comp :done get-fn get-todo-state))))))

(defui render-todos []
  (let [todos (jotai/useAtomValue todo-list)]
    (when (seq todos)
      ($ :div.flex.flex-col.m-1
         ($ :h2.text-xl "Todos:")
         ($ :ul.px-6.py-1.list-disc.border
            (for [id todos]
              ($ todo-item {:id id
                            :key id})))))))

;; subscribes to all-todos atom
(defonce done-todos
  (jotai/atom (fn [get-fn]
                (->> (get-fn all-todos)
                     (filter (comp :done get-fn get-todo-state))))))

(defui render-done-todos []
  (let [dones (jotai/useAtomValue done-todos)]
    (when (seq dones)
      ($ :div.flex.flex-col.m-1
         ($ :h2.text-xl "Done:")
         ($ :ul.px-6.py-1.list-disc.border
            (for [id dones]
              ($ todo-item {:id id
                            :key id})))))))

(defui new-todo-button []
  (let [set-todo-list! (jotai/useSetAtom all-todos)]
    ($ :button {:on-click (fn [] (set-todo-list! #(conj % (inc (count %)))))}
       "New todo")))
