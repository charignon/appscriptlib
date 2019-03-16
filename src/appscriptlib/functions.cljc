(ns appscriptlib.functions
  (:require [clojure.string :as str]
            [appscriptlib.protocols :refer
             [openByUrl alert getUI getSheetValues getSheetByName sendEmail]]))

(defn send-email-to-attendees [{:keys [gmail sheet url]}]
  (let [mail-sheet-doc (openByUrl sheet url)
        mail-sheet (getSheetByName mail-sheet-doc "Emails")
        emails (getSheetValues mail-sheet 2 1 100 100)
        email-msg (str/join "\n"
                            '("And thanks for your attention today!"
                              "Learn more at https://blog.laurentcharignon.com/post/mail-merge-in-100-lines-of-clojure/"
                              "Repo: https://github.com/charignon/cljs-apps-script-lib"
                              ""
                              "Cheers!"))
        processed (count (for [[emailaddr] emails
                               :when (not (str/blank? emailaddr))]
                           (sendEmail
                            gmail
                            emailaddr
                            "Thanks for coming to the SF Clojure Meetup"
                            email-msg
                            )))
        msg (str "Sent " processed " email(s)")
        ui (getUI sheet)]
    (alert ui msg)))
