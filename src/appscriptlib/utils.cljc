(ns appscriptlib.utils)

;; Should this go in docs.cljc?
(defn add-element [body elt]
  (case (str (.getType elt))
    "PARAGRAPH" (.appendParagraph body elt)
    "HORIZONTAL_RULE" (.appendHorizontalRule body)
    "TABLE" (.appendTable body elt)
    "LIST_ITEM" (.appendListItem body elt)
    "PAGE_BREAK" (.appendPageBreak body)))

(defn insert-element [body index elt]
  (case (str (.getType elt))
    "PARAGRAPH" (.insertParagraph body index elt)
    "HORIZONTAL_RULE" (.insertHorizontalRule body index)
    "TABLE" (.insertTable body index elt)
    "LIST_ITEM" (.insertListItem body index elt)
    "PAGE_BREAK" (.insertPageBreak body index)))
