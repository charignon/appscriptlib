;; Set of utilities to work with docs and manipulate their elements
(ns appscriptlib.docs
  (:require [appscriptlib.protocols :refer [clearContent insert add getChild getNumChildren openByUrl getBody getType]]))

(defn docurl->body
  ;; Get the body of a google doc by url
  ([doc url] (->> (openByUrl doc url) (getBody)))
  ([document] (getBody document)))

(defn clear-doc
  ;; Clear the content of a Google doc
  [doc url]
  (->> (docurl->body doc url)
       (clearContent)))

(defn append-doc-elements
  ;; Append doc elements
  [doc url elts]
  (let [body (docurl->body doc url)]
    (doall (map #(add body %) elts))))

(defn insert-doc-elements
  ;; Insert element at a given offset [this url index elts]
  [document index elts]
  (let [body (getBody document)]
    (mapv #(insert body index %) (reverse elts))))

(defn read-doc-elements
  ;; Read all the elements of a Google Doc
  ([document] (let [body (getBody document)] (map #(getChild body %) (range (getNumChildren body)))))
  ([doc url] (read-doc-elements (openByUrl doc url))))

(defn is-horizontal-line?[e]
  (and (> (getNumChildren e) 0)
       (= (str (getType (getChild e 0))) "HORIZONTAL_RULE")))
