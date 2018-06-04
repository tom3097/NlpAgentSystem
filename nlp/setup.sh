#!/bin/sh

SCRIPT=$(readlink -f "$0")
SCRIPTPATH=$(dirname "$SCRIPT")

if [ ! -d $SCRIPTPATH/neuralcoref ]; then
    mkdir $SCRIPTPATH/neuralcoref
    git clone https://github.com/huggingface/neuralcoref.git $SCRIPTPATH/neuralcoref
    pip install neuralcoref/.
    python -m spacy download en
fi

if [ ! -d $SCRIPTPATH/stanford-parser-full-2018-02-27 ]; then
    wget https://nlp.stanford.edu/software/stanford-parser-full-2018-02-27.zip -P $SCRIPTPATH
    unzip $SCRIPTPATH/stanford-parser-full-2018-02-27.zip -d $SCRIPTPATH
    rm $SCRIPTPATH/stanford-parser-full-2018-02-27.zip
fi

if [ ! -d $SCRIPTPATH/stanford-postagger-2018-02-27 ]; then
    wget https://nlp.stanford.edu/software/stanford-postagger-2018-02-27.zip -P $SCRIPTPATH
    unzip $SCRIPTPATH/stanford-postagger-2018-02-27.zip -d $SCRIPTPATH
    rm $SCRIPTPATH/stanford-postagger-2018-02-27.zip
fi

if [ ! -d $SCRIPTPATH/stanford-ner-2018-02-27 ]; then
    wget https://nlp.stanford.edu/software/stanford-ner-2018-02-27.zip -P $SCRIPTPATH
    unzip $SCRIPTPATH/stanford-ner-2018-02-27.zip -d $SCRIPTPATH
    rm $SCRIPTPATH/stanford-ner-2018-02-27.zip
fi