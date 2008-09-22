#!/bin/bash

ROOT_DIR="Corpus/wsj/*"

for file in $(find ${ROOT_DIR} -iname "*.pos"); do
    cat $file >> merged.list
done
