# AppsScriptLib

Write Testable Google Apps Script with Clojurescript!
This repo contains one example that send emails to everyone who puts their email
on a spreadsheet.

## More info

https://blog.laurentcharignon.com/post/mail-merge-in-100-lines-of-clojure/

## Usage

1) Create a spreadsheet that looks like https://bit.ly/2EsCCFL (or copy this one)

2) Modify the code in this repo, search for "REPLACEME_WITH_A_REAL_URL", replace that string
with the URL of your spreadsheet

3) Compile the code

```sh
lein cljsbuild once main
cat export/Code.gs | pbcopy
# Paste it in the Apps Script IDE, save and run
# WARNING, this will send an email to everyone on that list from your Google Account: https://bit.ly/2EsCCFL
```

4) Go to your spreadsheet, add your email there, click on Tools -> Script Editor, paste your code and
hit run.

If you get that error: "Cannot call SpreadsheetApp.getUi() from this context."
Then you are running a script that isn't attached to the spreadsheet. You can
either remove the call to .getUi() and your script will still send emails or you
can attach it to a spreadsheet (Tools -> Script Editor).

5) This should send email to everyone on that spreadsheet and display a dialog

## Unit Tests

``` sh
lein test
```

## Generating wrappers for more API

Install `python3` and the python libraries: `jinja2` and `pyyaml`.

Add entries to `resources/proto.yml` following examples that are currently
there. Run `make gen` to generate the code.
