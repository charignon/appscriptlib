(ns appscriptlib.fake-google-apis-test
  (:require [appscriptlib.fake-google-apis :as sut]
            [clojure.test :refer :all]
            [appscriptlib.protocols :refer :all]
            [appscriptlib.fake-google-apis :refer :all]))

(deftest test-ui-test
  (testing "Can store message"
    (let [ui (fake-ui)]
      (alert ui "foo")
      (alert ui "bar")
      (is (= @(:msgs ui) ["foo" "bar"])))))

(deftest test-logger-test
  (testing "Can store message"
    (let [l (fake-logger)]
      (appscriptlib.protocols/info l "foo")
      (appscriptlib.protocols/info l "bar")
      (is (= @(:msgs l) ["foo" "bar"])))))

(deftest fake-document-app-test
  (testing "Accessing a non existing doc returns nil"
    (is (nil? (openByUrl (fake-document-app {}) "fake_url"))))
  (testing "Accessing an existing doc returns it"
    (let [app (fake-document-app (hash-map "foo" :bar))]
      (is (= :bar (openByUrl app "foo")))))
  (testing "Accessing the ui returns a fake ui"
    (is (satisfies? UI (getUI (fake-document-app {}))))))

(deftest get-sheets-by-name
  (testing "Can access a sheet by name"
    (let [sh (fake-sheet [] "foo")
          shdoc (fake-spreadsheet-document "testurl" [(fake-sheet [] "foo")])]
      (is (= "foo" (getName (getSheetByName shdoc "foo")))))))

(defn fake-app-with-doc []
  (fake-document-app
   (hash-map "myurl"
             (fake-document
              "myurl"
              (fake-body
               [(fake-paragraph [] "foo")
                (fake-paragraph [(fake-horizontal-line)])
                (fake-paragraph [] "bar")
                (fake-paragraph [(fake-horizontal-line)])
                (fake-paragraph [] "template-1")
                (fake-paragraph [] "template-2")])))))

(deftest all-components-tests
  (testing "Check wiring of app, doc and body"
    (let [app (fake-app-with-doc)]
      (is (= 1 (-> app
                   (openByUrl "myurl")
                   (getBody)
                   (getChild 1)
                   (getNumChildren))))))
  (testing "that we can clear a document and get its text"
    (let [app (fake-app-with-doc)]
      (is (= "foo-bar-template-1template-2" (-> app (openByUrl "myurl") (getBody) (getText))))
      (-> app
          (openByUrl "myurl")
          (getBody)
          (clearContent))
      (is (= "" (-> app (openByUrl "myurl") (getBody) (getText))))))
  (testing "that we can do a search a replace"))
