(ns frontend.todo
  (:require [clojure.string :as str]
            ["jotai" :as jotai]
            ["jotai/utils" :as jotai-utils]
            [uix.core :refer [defui $ use-callback use-state]]
            [frontend.util :refer [not-blank]]))

(defonce !all-todo-ids
  (jotai/atom (vec (range 1 10))))

;; function that returns cached atom state for todo id
(defonce get-todo-state
  (jotai-utils/atomFamily #(jotai/atom {:value "" :done false})))

(defui todo-item [{:keys [id]}]
  (let [[todo set-todo!] (jotai/useAtom (get-todo-state id))
        toggle-todo! (->> [set-todo!]
                          (use-callback (fn []
                                          (set-todo! #(update % :done not)))))]
    ($ :li.todo
       ($ :button {:tabIndex "0"
                   :on-click toggle-todo!}
          (cond->> (or (not-blank (:value todo))
                       (str "Todo: " id))
            (:done todo) ($ :s))))))

(defonce !todos
  (jotai/atom (fn [get]
                (->> (get !all-todo-ids)
                     (remove (comp :done get get-todo-state))))))

(defonce !dones
  (jotai/atom (fn [get]
                (->> (get !all-todo-ids)
                     (filter (comp :done get get-todo-state))))))

(defui render-todos []
  (let [todos (jotai/useAtomValue !todos)]
    ($ :div#todos.flex.flex-col.m-1
       ($ :h2.text-xl "Todos:")
       ($ :ul.px-6.py-1.list-disc.border
          (for [id todos]
            ($ todo-item {:id id
                          :key id}))))))

(defui render-done-todos []
  (let [dones (jotai/useAtomValue !dones)]
    ($ :div#done-todos.flex.flex-col.m-1
       ($ :h2.text-xl "Done:")
       ($ :ul.px-6.py-1.list-disc.border
          (for [id dones]
            ($ todo-item {:id id
                          :key id}))))))

(defui new-todo []
  (let [[todo-value set-todo-value!] (use-state "")
        submit-todo! (jotai-utils/useAtomCallback
                      (->> [todo-value]
                           (use-callback (fn [get set]
                                           (let [todos (get !all-todo-ids)
                                                 idx (inc (count todos))
                                                 new-todo (-> (get (get-todo-state idx))
                                                              (assoc :value todo-value))]
                                             (set-todo-value! "")
                                             (set (get-todo-state idx) new-todo)
                                             (set !all-todo-ids (conj todos idx)))))))]
    ($ :div#new-todo.flex.my-4.gap-2
       ($ :button.px-4.py-3.rounded-md.bg-indigo-500.text-white
          {:class "hover:bg-indigo-600 ease-linear duration-100 disabled:bg-indigo-400"
           :disabled (str/blank? todo-value)
           :on-click submit-todo!}
          "New todo")
       ($ :input
          {:value todo-value
           :on-key-down (fn [^js e]
                          (when (= "Enter" (.. e -key))
                            (submit-todo!)))
           :on-change (fn [^js e]
                        (set-todo-value! (.. e -target -value)))}))))
