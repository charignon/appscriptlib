(ns appscriptlib.macros
  (:require [appscriptlib.protocols :refer [getUI]]))

(defmacro alert-text-is [msg]
  `(~'is (= ~msg (first @(:msgs (getUI ~'app))))))
