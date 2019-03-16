;; This file contains the top level functions that apps script can call in
;; entrypoint.js
(ns appscriptlib.core
  (:require [appscriptlib.entry-points]
            [appscriptlib.real-google-apis :refer [google-sheet-app google-mail-app]]
            [appscriptlib.functions :as f]))

;; Email attendees of the clojure meetup!
(defn ^:export clojure_meetup []
  (f/send-email-to-attendees
   {:url "REPLACEME_WITH_A_REAL_URL"
    :sheet (google-sheet-app js/SpreadsheetApp)
    :gmail (google-mail-app js/GmailApp)}))
