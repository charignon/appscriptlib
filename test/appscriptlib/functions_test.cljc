(ns appscriptlib.functions-test
  (:require
   [appscriptlib.functions :as f]
   [appscriptlib.protocols :refer :all]
   [appscriptlib.fake-google-apis :refer :all]
   [appscriptlib.macros :refer :all]
   [clojure.test :refer :all]))

(deftest clojure-meetup-test
  (testing "Can send email"
    (let [emailssheet (fake-sheet [["Emails"]
                                   ["foo@bar.com"]
                                   ["foo2@bar.com"]] "Emails")
          url         "mailmerge"
          shdoc       (fake-spreadsheet-document url [emailssheet])
          allfiles    (atom {url shdoc})
          gmail       (fake-gmail)
          app         (fake-document-app-backed-by-store allfiles)]
      (f/send-email-to-attendees {:gmail gmail
                                  :sheet app
                                  :url   url})
      (alert-text-is "Sent 2 email(s)"))))
