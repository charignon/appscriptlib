(ns appscriptlib.protocols)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; This file has been automatically generated
;; from APIs definition, please do not edit it by hand
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defprotocol DocElement
  (add [_ a] "Add an element at the end of this")
  (clearContent [_] "Remove all the content of the nested elements")
  (copy [_] "Copy an element to insert it in another one")
  (getChild [_ a] "Get a child element")
  (getNumChildren [_] "Get the number of child elements")
  (getText [_] "Return the content of all the nested elements as text")
  (getType [_] "Return the type (a string) of this element")
  (insert [_ a b] "Insert an element at a given point")
  (replaceText [_ a b] "Replace text across element and all its descendant recursively"))

(defprotocol Document
  (getBody [_] "Get the body element, root of the document")
  (getDocumentName [_] "Return the name of the Document")
  (setDocumentName [_ a] "Set the name of the Document")
  (getId [_] "Get the Id of this document"))

(defprotocol Sheet
  (appendRow [_ a] "Append a row [a b c ...] to this sheet")
  (getName [_] "Return the name of the sheet")
  (clearSheet [_] "Remove all the elements of this sheet")
  (getSheetValues [_ a b c d] "Get the value of a square identified by coordinates"))

(defprotocol SpreadsheetDocument
  (getSheets [_] "Get the list of sheets for this spreadsheet document")
  (getSheetByName [_ a] "Get a sheet by name"))

(defprotocol UI
  (alert [_ a] "Display a message to the user"))

(defprotocol ConsoleLogger
  (info [_ a] "Log a message to the console"))

(defprotocol DocumentApp
  (openByUrl [_ a] "Returns a document matching <url> or nil")
  (getUI [_] "Get a UI associated with this app"))

(defprotocol DriveFile
  (getFileUrl [_] "Get the URL of the drive file")
  (makeCopy [_ a] "Copy a file, returns a copy"))

(defprotocol DriveApp
  (getFileById [_ a] "Get a file by <id> or nil, returns a Drive File"))

(defprotocol GmailApp
  (sendEmail [_ a b c] "Send an email"))
