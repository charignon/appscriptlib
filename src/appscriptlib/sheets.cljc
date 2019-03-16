;; Set of utilities to work with spreadsheets, and using them as KV stores
(ns appscriptlib.sheets
  (:require [clojure.string :as str]
            [appscriptlib.protocols :refer [openByUrl getSheetValues clearSheet appendRow getName getSheets]]))

(defn- cellmatrix->kv [matrix]
  ;; Takes a two columns matrix and interprets is as a dict
  (reduce (fn [h [k v]] (if (str/blank? k) h (assoc h k v))) {} matrix))

(defn- sheet->cellmatrix [sheet]
  ;; Takes a sheet and returns a matrix with the first 50 rows and 2 columns
  (getSheetValues sheet 1 1 50 2))

(def sheet->kv
  ;; Takes a sheet and return its content interpreted as a dict where the first
  ;; column are the keys and the second are the values
  (comp cellmatrix->kv sheet->cellmatrix))

(defn sheetdoc->kv
  ;; Read a google spreadsheet as a collection of kv per sheet
  ;; assuming the first column are keys and the second are values
  [doc url]
  {:url  url
   :data (->> doc
              (getSheets)
              (map #(merge (hash-map "sheet-name" (getName %)) (sheet->kv %))))})

(defn write-kv-conf
  ;; Write key-value in the first sheet at URL overriding existing
  [sheet transformer url h]
  (let [sh (->> (openByUrl sheet url) (getSheets) (first))]
    (clearSheet sh)
    (run! #(appendRow sh (transformer [(first %) (second %)])) h)))

(defn read-kv-conf
  ;; Read a key value configuration into a hash
  ([sheetdoc]
   (->> (getSheets sheetdoc)
        (first)
        (sheet->kv)))
  ([sheet url]
   (read-kv-conf (openByUrl sheet url))))
