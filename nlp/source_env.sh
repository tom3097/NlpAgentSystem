#!/bin/sh
# You should source this file to keep the environment variables changes.

SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

export CLASSPATH=$CLASSPATH:$SCRIPTPATH/stanford-ner-2018-02-27:$SCRIPTPATH/stanford-parser-full-2018-02-27:$SCRIPTPATH/stanford-postagger-2018-02-27
export STANFORD_MODELS=$SCRIPTPATH/stanford-postagger-2018-02-27/models:$SCRIPTPATH/stanford-ner-2018-02-27/classifiers