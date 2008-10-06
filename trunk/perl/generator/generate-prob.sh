#!/bin/bash

SETS="50 80 90"

IN_DIR=./IN
OUT_DIR=./OUT

IN_PREFIX=training

for current_set in ${SETS}; do

    echo "Processing set #: $current_set"

    echo "==============================================================================="
    echo "Generating C(Tj,Tk) counts (TAG, PREVIOUS TAG - COUNTS)"
    echo "==============================================================================="
    for i in `seq 1 10`; do 
	OUT=${current_set}-tag_prevtag-count-${i}.dat
	./perl/bigram_counts.pl < ${IN_DIR}/${current_set}${IN_PREFIX}${i}.pos > ${OUT_DIR}/${OUT}
	echo "Generated: $OUT"
    done 
    echo "Done!"

    echo "==============================================================================="
    echo "Generating C(Tk,Wk) counts (TAG, WORD - COUNTS)"
    echo "==============================================================================="
    for i in `seq 1 10`; do 
	OUT=${current_set}-tag_word-count-${i}.dat
	./perl/tcw.pl -i ${IN_DIR}/${current_set}${IN_PREFIX}${i}.pos -o ${OUT_DIR}/${OUT} > ${OUT_DIR}/${current_set}-words-$i
	echo "Generated: $OUT , Word count in: ${OUT_DIR}/${current_set}words-$i"
    done
    echo "Done!"

    echo "==============================================================================="
    echo "Generating Smoothed Probabilities for all files"
    echo "==============================================================================="
    for i in `seq 1 10`; do 
	WORDS=$(cat $OUT_DIR/${current_set}-words-$i)
	./perl/smoothing.pl -i ${OUT_DIR}/${current_set}-tag_word-count-${i}.dat -o ${OUT_DIR}/${current_set}-tag_word-prob-${i}.dat -c $WORDS
	./perl/smoothing.pl -i ${OUT_DIR}/${current_set}-tag_prevtag-count-${i}.dat -o ${OUT_DIR}/${current_set}-tag_prevtag-prob-${i}.dat
	echo "Generating probabilities for set: $i which contains $WORDS words."
	echo " 1. ${current_set}-tag_word-prob-${i}.dat"
	echo " 2. ${current_set}-tag_prevtag-prob-${i}.dat"
    done
    echo "Done!"

    echo "==============================================================================="
    echo "Cleaning up counts, leaving probabilities and word counts"
    echo "==============================================================================="
    rm -v ${OUT_DIR}/*count*

done






