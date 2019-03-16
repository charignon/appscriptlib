(ns appscriptlib.real-google-apis
  (:require [clojure.string :as str]
            [appscriptlib.utils :refer [add-element insert-element]]
            [appscriptlib.protocols :refer
             [DocElement add clearContent copy getChild getNumChildren getText getType insert replaceText Document getBody getDocumentName setDocumentName getId Sheet appendRow getName clearSheet getSheetValues SpreadsheetDocument getSheets getSheetByName UI alert ConsoleLogger info DocumentApp openByUrl getUI openByUrl getUI DriveFile getFileUrl makeCopy DriveApp getFileById GmailApp sendEmail]]))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; This file has been automatically generated
;; from APIs definition, please do not edit it by hand
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defrecord GoogleDocElement [k]
    DocElement
    (add [{:keys [k]} a] (add-element k (:k (copy a))))
    (clearContent [{:keys [k]}] (.clear k))
    (copy [{:keys [k]}] (GoogleDocElement. (.copy k)))
    (getChild [{:keys [k]} a] (GoogleDocElement. (.getChild k a)))
    (getNumChildren [{:keys [k]}] (.getNumChildren k))
    (getText [{:keys [k]}] (.getText k))
    (getType [{:keys [k]}] (.getType k))
    (insert [{:keys [k]} a b] (insert-element k a (:k (copy b))))
    (replaceText [{:keys [k]} a b] (.replaceText k a b)))

(defrecord GoogleDocument [k]
    Document
    (getBody [{:keys [k]}] (GoogleDocElement. (.getBody k)))
    (getDocumentName [{:keys [k]}] (.getName k))
    (setDocumentName [{:keys [k]} a] (.setName k a))
    (getId [{:keys [k]}] (.getId k)))

(defrecord GoogleSheet [k]
    Sheet
    (appendRow [{:keys [k]} a] (.appendRow k a))
    (getName [{:keys [k]}] (.getName k))
    (clearSheet [{:keys [k]}] (.clear k))
    (getSheetValues [{:keys [k]} a b c d] (.getSheetValues k a b c d)))

(defrecord GoogleSpreadsheetDocument [k]
    SpreadsheetDocument
    (getSheets [{:keys [k]}] (map #(GoogleSheet. %) (.getSheets k)))
    (getSheetByName [{:keys [k]} a] (GoogleSheet. (.getSheetByName k a))))

(defrecord GoogleUI [k]
    UI
    (alert [{:keys [k]} a] (.alert k a)))

(defrecord GoogleLogger [k]
    ConsoleLogger
    (info [{:keys [k]} a] (.log k a)))

(defn google-log [k]
  (GoogleLogger. k))

(defrecord GoogleDocumentApp [k]
    DocumentApp
    (openByUrl [{:keys [k]} a] (GoogleDocument. (.openByUrl k a)))
    (getUI [{:keys [k]}] (GoogleUI. (.getUi k))))

(defn google-doc-app [k]
  (GoogleDocumentApp. k))
(defrecord GoogleSheetApp [k]
    DocumentApp
    (openByUrl [{:keys [k]} a] (GoogleSpreadsheetDocument. (.openByUrl k a)))
    (getUI [{:keys [k]}] (GoogleUI. (.getUi k))))

(defn google-sheet-app [k]
  (GoogleSheetApp. k))

(defrecord GoogleDriveFile [k]
    DriveFile
    (getFileUrl [{:keys [k]}] (.getUrl k))
    (makeCopy [{:keys [k]} a] (GoogleDriveFile. (.makeCopy k a))))

(defrecord GoogleDriveApp [k]
    DriveApp
    (getFileById [{:keys [k]} a] (GoogleDriveFile. (.getFileById k a))))

(defn google-drive-app [k]
  (GoogleDriveApp. k))

(defrecord GoogleGmailApp [k]
    GmailApp
    (sendEmail [{:keys [k]} a b c] (.sendEmail k a b c)))

(defn google-mail-app [k]
  (GoogleGmailApp. k))
