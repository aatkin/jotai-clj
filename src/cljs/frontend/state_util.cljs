(ns frontend.state-util
  "Utilities and wrapper functions for Jotai."
  (:require ["jotai" :as jotai]
            ["jotai/utils" :as jotai-utils]))

(defn jotai-atom
  "Creates a new jotai-atom. Optional `init` is converted from js->clj."
  ([] (jotai/atom))
  ([init] (jotai/atom (clj->js init))))

(defn jotai-atom-family
  "Returns a function that takes single param and creates or returns cached jotai-atom.
   When `init-atom-fn` is used, jotai-atom is created using 
   (init-atom-fn (js->clj o :keywordize-keys true)) instead."
  ([]
   (jotai-utils/atomFamily (fn [_] (jotai-atom))))
  ([init-atom-fn]
   (jotai-utils/atomFamily (fn [^js o] (jotai-atom (-> (js->clj o :keywordize-keys true)
                                                       init-atom-fn))))))

(defn set-atom [set-atom-fn f & args]
  (set-atom-fn
   #(-> (js->clj % :keywordize-keys true)
        (as-> m (apply f m args))
        clj->js)))

(defn use-atom-value
  "Hook that returns state for jotai-atom `a`. State is converted from js->clj."
  [^js a]
  (let [state (jotai/useAtomValue a)]
    (js->clj state :keywordize-keys true)))

(defn use-set-atom
  "Hook that returns set-state! for jotai-atom `a`. Setter function input/outputs are
   converted from js->clj, and back."
  [^js a]
  (let [set-state! (jotai/useSetAtom a)]
    (partial set-atom set-state!)))

(defn use-atom
  "Hook that returns [state set-state!] for jotai-atom `a`. Both state and setter function
   input/outputs are converted from js->clj, and back."
  [^js a]
  (let [[state set-state!] (jotai/useAtom a)]
    [(js->clj state :keywordize-keys true)
     (partial set-atom set-state!)]))

(defn debug-get-atom [^js store ^js a]
  (-> (store.get a)
      (js->clj :keywordize-keys true)))

(defn debug-set-atom [^js store ^js a f]
  (store.set a #(-> (js->clj % :keywordize-keys true)
                    (as-> m (f m))
                    clj->js)))
