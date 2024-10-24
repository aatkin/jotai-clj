(ns frontend.todo
  (:require [clojure.string :as str]
            ["jotai" :as jotai]
            ["jotai/utils" :as jotai-utils]
            [uix.core :refer [defui $ use-state]]))

(defonce all-todos
  (jotai/atom (vec (range 1 10))))

;; function that returns cached atom state for todo id
(defonce get-todo-state
  (jotai-utils/atomFamily #(jotai/atom {:value "" :done false})))

(defui todo-item [{:keys [id]}]
  (let [[todo set-todo!] (jotai/useAtom (get-todo-state id))
        toggle-todo (fn [] (set-todo! #(update % :done not)))
        {:keys [done value]} todo
        value (if-not (str/blank? value)
                value
                (str "Todo: " id))]
    ($ :li.todo
       ($ :button {:tabIndex "0"
                   :on-click (fn [] (toggle-todo))}
          (cond->> value
            done ($ :s))))))

;; subscribes to all-todos atom
(defonce todo-list
  (jotai/atom (fn [getter]
                (->> (getter all-todos)
                     (remove (comp :done getter get-todo-state))))))

(defui render-todos []
  (let [todos (jotai/useAtomValue todo-list)]
    ($ :div#todos.flex.flex-col.m-1
       ($ :h2.text-xl "Todos:")
       ($ :ul.px-6.py-1.list-disc.border
          (for [id todos]
            ($ todo-item {:id id
                          :key id}))))))

;; subscribes to all-todos atom
(defonce done-todos
  (jotai/atom (fn [getter]
                (->> (getter all-todos)
                     (filter (comp :done getter get-todo-state))))))

(defui render-done-todos []
  (let [dones (jotai/useAtomValue done-todos)]
    ($ :div#done-todos.flex.flex-col.m-1
       ($ :h2.text-xl "Done:")
       ($ :ul.px-6.py-1.list-disc.border
          (for [id dones]
            ($ todo-item {:id id
                          :key id}))))))

(defonce create-new-todo
  (jotai/atom nil
              (fn write-fn [getter setter payload]
                (let [todos (getter all-todos)
                      idx (inc (count todos))
                      new-todo (getter (get-todo-state idx))]
                  (setter (get-todo-state idx) (assoc new-todo :value payload))
                  (setter all-todos (conj todos idx))))))

(defui new-todo []
  (let [create-new-todo! (jotai/useSetAtom create-new-todo)
        [new-todo-value set-new-todo-value!] (use-state "")
        submit-todo (fn []
                      (when-not (str/blank? new-todo-value)
                        (set-new-todo-value! "")
                        (create-new-todo! new-todo-value)))]
    ($ :div#new-todo.flex.my-4.gap-2
       ($ :button.px-4.py-3.rounded-md.bg-indigo-500.text-white
          {:class "hover:bg-indigo-600 ease-linear duration-100 disabled:bg-indigo-400"
           :disabled (str/blank? new-todo-value)
           :on-click (fn [] (submit-todo))}
          "New todo")
       ($ :input
          {:value new-todo-value
           :on-key-down (fn [^js e]
                          (when (= "Enter" (.. e -key))
                            (submit-todo)))
           :on-change (fn [^js e]
                        (set-new-todo-value! (.. e -target -value)))}))))
