(ns appscriptlib.fake-google-apis
  (:require
;;   [appscriptlib.instrumentation :refer [gettime extend-with-instrumentation]]
   [appscriptlib.protocols :refer
              [ConsoleLogger
               DocElement
               DocumentApp
               DriveApp
               GmailApp
               DriveFile
               Sheet Document
               SpreadsheetDocument
               UI
               add
               alert
               appendRow
               clearContent
               clearSheet
               copy
               getBody
               getChild
               getFileById
               getFileUrl
               getId
               getName
               getNumChildren
               getSheetValues
               getSheets
               getText
               getType
               getUI
               info
               insert
               makeCopy
               openByUrl
               replaceText]]))
(declare insert-in-vector)
(declare replace-text)
(declare fake-body)
(declare render)
(declare fake-document)
(declare fake-element)
(declare doGetSheetValues)

(defrecord FakeUI [msgs]
  UI
  (alert [{:keys [msgs]} msg]
    (swap! msgs #(conj % msg))))

(defrecord FakeLogger [msgs]
  ConsoleLogger
  (info [{:keys [msgs]} msg]
    (swap! msgs #(conj % msg))))

(defrecord FakeDocumentApp [url_to_doc ui]
  DocumentApp
  (openByUrl [{:keys [url_to_doc]} url] (get @url_to_doc url))
  (getUI [{:keys [ui]}] ui))

(defrecord FakeDocument [url body name]
  Document
  (getBody [{:keys [body]}] body)
  (getId [{:keys [url]}] url)
  (getDocumentName [{:keys [name]}] @name)
  (setDocumentName [{:keys [name]} newname] (reset! name newname)))

(defrecord FakeSpreadsheetDocument [url sheets]
  SpreadsheetDocument
  (getSheets [{:keys [sheets]}] sheets)
  (getSheetByName [{:keys [sheets]} name]
    (first (filter #(= (getName %) name) sheets))))

(defrecord FakeDriveFile [drive f]
  DriveFile
  (getFileUrl [{:keys [f]}] (:url f))

  ;; Only supports google doc
  (makeCopy [{:keys [drive f]} desttitle]
    (let [newurl (str (:url f) "-copy")
          newdoc (fake-document newurl (copy (getBody f)))]
      (swap! (:docmap drive) #(assoc % newurl newdoc))
      (FakeDriveFile. drive newdoc))))

(defrecord FakeDriveApp [docmap]
  DriveApp
  (getFileById [this id] (FakeDriveFile. this (get @docmap id))))

(defrecord FakeGmailApp [emailqueue]
  GmailApp
  (sendEmail
    [{:keys [emailqueue] :as this} recipient subject body]
    (do
      (swap!
       emailqueue
       #(conj % {:recipient recipient :subject subject :body body}))
      this)))

(defn fake-gmail []
  (FakeGmailApp. (atom [])))

(defrecord FakeDocElement [type children attrs]
  DocElement
  (add [{:keys [children]} elt] (swap! children #(conj % elt)))
  (clearContent [this] (reset! children []))
  (getChild [{:keys [children]} idx] (nth @children idx))
  (getNumChildren [{:keys [children]}] (count @children))
  (getText [this] (render this))
  (copy [{:keys [type children attrs]}] (FakeDocElement. type (atom @children) attrs))
  (getType [{:keys [type]}] type)
  (insert[{:keys [children]} idx elt] (swap! children #(insert-in-vector % idx elt)))
  (replaceText [this from to] (reset! children @(:children (replace-text this from to)))))

(defrecord FakeSheet [content name]
  Sheet
  (appendRow [{:keys [content]} row] (swap! content (fn [content]
                                                      (if (= content [[]])
                                                        [row]
                                                        (conj content row)))))
  (getName [{:keys [name]}] name)
  (clearSheet [{:keys [content]}] (reset! content [(vector)]))
  (getSheetValues
    [{:keys [content]} x1 y1 x2 y2] ;; Example 1 1 50 2
    ;; We need to get the rows between x1 and x2 and col between y1 and y2
    (doGetSheetValues @content x1 y1 x2 y2)))

(defn render [e]
  (let [this-element (case (getType e)
                       "BODY"            [nil]
                       "PARAGRAPH"       [(:text (:attrs e))]
                       "HORIZONTAL_RULE" ["-"])]
    (clojure.string/join "" (remove nil? (concat this-element (map render @(:children e)))))))

(defn replace-text [e from to]
  (let [attrs        (:attrs e)
        text         (:text attrs)
        children     @(:children e)
        new-children (map #(replace-text % from to) children)]
    (if (nil? text)
      (fake-element (:type e) new-children (:attrs e))
      (fake-element (:type e) new-children (assoc (:attrs e) :text (clojure.string/replace text from to))))))

(defn fake-element
  ([type children attrs] (FakeDocElement. type (atom children) attrs))
  ([type children] (FakeDocElement. type (atom children) {}))
  ([type] (FakeDocElement. type (atom []) {})))

(defn insert-in-vector
  "Insert an element in a vector (inefficiently)"
  [v idx elt]
  (apply vector (concat (take idx v) [elt] (drop idx v))))

(defn doGetSheetValues [m x1 y1 x2 y2]
  (let [xlen (inc (- x2 x1))
        ylen (inc (- y2 y1))
        xoff (dec x1) ;; 0 indexed vs 1 indexed
        yoff (dec y1) ;; 0 indexed vs 1 indexed
        rows (take xlen (drop xoff m))
        cols (map #(apply vector (take ylen (drop yoff %))) rows)]
    (apply vector cols)))

(defn fake-document
  ([url body] (FakeDocument. url body (atom "docname")))
  ([url body name] (FakeDocument. url body (atom name))))

(defn fake-body [elts] (fake-element "BODY" elts))
(defn fake-ui [] (FakeUI. (atom [])))
(defn fake-logger [] (FakeLogger. (atom [])))

(defn do-fake-document-app
  [url-to-doc]
  (FakeDocumentApp. (atom url-to-doc) (fake-ui)))

(defn fake-document-app
  [url-to-doc]
  (do-fake-document-app url-to-doc))

(defn do-fake-document-app-backed-by-store
  [url-to-doc]
  (FakeDocumentApp. url-to-doc (fake-ui)))

(defn fake-document-app-backed-by-store
  [url-to-doc]
  (do-fake-document-app-backed-by-store url-to-doc))

(defn fake-spreadsheet-document [url sheets]
  (FakeSpreadsheetDocument. url sheets))

(defn fake-drive-app-backed-by-store
  [m] (FakeDriveApp. m))

(defn fake-paragraph
  ([elts text] (fake-element "PARAGRAPH" elts {:text text}))
  ([elts] (fake-element "PARAGRAPH" elts)))
(defn fake-horizontal-line [] (fake-element "HORIZONTAL_RULE"))

(defn fake-sheet [data name]
  (FakeSheet. (atom data) name))
