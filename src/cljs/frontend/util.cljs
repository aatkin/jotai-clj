(ns frontend.util
  (:require [clojure.string]))

(defn not-blank [s]
  (when (and (string? s)
             (not (clojure.string/blank? s)))
    s))
