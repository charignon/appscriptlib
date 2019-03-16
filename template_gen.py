#!/usr/bin/env python3
# coding: utf-8

import jinja2
import yaml

def arggen(count=0):
    return " ".join([chr(ord('a') + c) for c in range(0, count)])

def argsdef(count=0):
    ret = "{:keys [k]}"
    if count != 0:
         ret += " "+arggen(count)
    return ret

def protoargsdef(count=0):
    ret = "_"
    if count != 0:
         ret += " "+arggen(count)
    return ret

def argscall(count=0):
    ret = "k"
    if count != 0:
         ret += " "+arggen(count)
    return ret

RECORD = jinja2.Template("""
{% macro function(name, oname, argscount=0, wrap=None, implementation=None) -%}
({{ name }} [{{ argsdef(argscount)}}]
{%- if implementation %} {{ implementation }}) {%- else -%}
{%- if wrap %} ({{ wrap }} (.{{ oname }} {{ argscall(argscount) }}))
{%- else %} (.{{ oname }} {{ argscall(argscount) }}){%- endif -%}){%- endif -%}
{%- endmacro -%}
(defrecord {{ concrete }} [k]
    {{ protocol }}
    {%- for m in methods %}
    {{function(name=m["name"], implementation=m["implementation"], oname=m["oname"], argscount=m["argscount"], wrap=m["wrap"])-}}
    {%- endfor -%})""")
PROTOCOL = jinja2.Template("""
(defprotocol {{ protocol }}
  {%- for m in methods %}
  ({{m["name"]}} [{{ protoargsdef(m["argscount"]) }}] "{{m["doc"]}}")
  {%- endfor -%})""")

CTOR = jinja2.Template("""
{% if ctor %}
(defn {{ ctor }} [k]
  ({{ concrete }}. k))
{%- endif -%}""")


with open("resources/proto.yml") as f:
    d = f.read()

definitions = yaml.safe_load(d)
AUTOGEN_HEADER = """;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; This file has been automatically generated
;; from APIs definition, please do not edit it by hand
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
"""

def build_require_str_real_google_apis(protocoldeps):
    return """  (:require [clojure.string :as str]
            [appscriptlib.utils :refer [add-element insert-element]]
            [appscriptlib.protocols :refer
             [%s]])""" % " ".join(protocoldeps)

protocoldeps = []
for d in definitions:
    protocoldeps.append(d['protocol'])
    if isinstance(d['concrete'], list):
        for u in d['concrete']:
            protocoldeps.extend(
                [m['name'] for m in u['methods']]
            )
    else:
        protocoldeps.extend(
            [m['name'] for m in d['methods']]
        )

protofile = ["(ns appscriptlib.protocols)", AUTOGEN_HEADER]
recordfile = ["(ns appscriptlib.real-google-apis\n%s)" % build_require_str_real_google_apis(protocoldeps),
              AUTOGEN_HEADER]
defaults = {
    'argsdef':argsdef,
    'argscall':argscall,
    'protoargsdef':protoargsdef
}

for d in definitions:
    d = {**d, **defaults}
    if isinstance(d['concrete'], list):
        k = d.copy()
        k = {**k, **d['concrete'][0]}
        protofile.append(PROTOCOL.render(**k).strip())
        for c in d['concrete']:
            k = d.copy()
            k = {**k, **c}
            recordfile.append(RECORD.render(**k).strip())
            ctor = CTOR.render(**k).strip()
            if ctor:
                recordfile.extend(["", ctor])
    else:
        protofile.append(PROTOCOL.render(**d).strip())
        recordfile.append(RECORD.render(**d).strip())
        ctor = CTOR.render(**d).strip()
        if ctor:
            recordfile.extend(["", ctor])
    recordfile.append("")
    protofile.append("")

proto_content = "\n".join(protofile)
record_content = "\n".join(recordfile)
proto_fname = "src/appscriptlib/protocols.cljc"
record_fname = "src/appscriptlib/real_google_apis.cljc"
fileset = set([proto_fname, record_fname])

with open(proto_fname, "r") as f:
    d = f.read()
if proto_content != d:
    with open(proto_fname, "w") as f:
        f.write(proto_content)
    print(f"Updated content of '{proto_fname}'")
else:
    print(f"No need to update content of '{proto_fname}'")

with open(record_fname, "r") as f:
    d = f.read()
if record_content != d:
    with open(record_fname, "w") as f:
        f.write(record_content)
    print(f"Updated content of '{record_fname}'")
else:
    print(f"No need to update content of '{record_fname}'")
