package edu.umbc.nlp.postagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Evaluator
{
	private List<TaggedSentence> sentences;
	
	private double WER;
	private double UnknownWER;
	private double KnownWER;
	private HashMap<String, Integer> confusionMatrix = new HashMap<String, Integer>();
	private List<String> correctTags = new ArrayList<String>();
	private List<String> incorrectTags = new ArrayList<String>();
		
	public Evaluator(List<TaggedSentence> sentences)
	{
		this.sentences = sentences;
		computeWordErrorRates();
			
	}
	
	public double getWordErrorRate() { return 100 * WER; }
	public double getKnownWord_WordErrorRate() { return 100 * KnownWER; }
	public double getUnknownWord_WordErrorRate() { return 100 * UnknownWER; }	
	
	
	
	public HashMap<String, Integer> getConfusionMatrix()
	{
		return this.confusionMatrix;
	}
	
	public void printConfusionMatrix()
	{
		Set<String> keys = this.confusionMatrix.keySet();
		Iterator<String> iter = keys.iterator();
		
		for (String incorrect : incorrectTags)
		{
			System.out.print("\t" + incorrect);
		}
		System.out.println();
		
		for (String correct : correctTags)
		{
			System.out.print(correct);
			for (String incorrect : incorrectTags)
			{
				
				if (confusionMatrix.containsKey(correct + "|" + incorrect))
				{
					Integer val = confusionMatrix.get(correct + "|" + incorrect);				
					System.out.print("\t" + val);
				}
				else
				{
					System.out.print("\t-");
				}
			}
			System.out.println();
		}		
		
	}
	
	/**
	 * Computes the Word Error Rate (WER) of a TaggedSentence.
	 * @param TaggedSentence s - a TaggedSentence object containing the control tags
	 * as well as the hypothesised tags of a sentence.
	 * @return double WER - the word error rate expressed as a double.
	 */
	private void computeWordErrorRates()
	{		
		int mismatch = 0;
		int total_words = 0;
		
		int known_word_count = 0;
		int mismatch_known = 0;
		
		int unknown_word_count = 0;
		int mismatch_unknown = 0;
		
		for (TaggedSentence s : sentences)
		{			
			for (int i = 0; i < s.getSentence().size(); i++)
			{
				total_words++;
				
				if (!s.getTags().get(i).equalsIgnoreCase(s.getHypothesisedTags().get(i)))
				{
					mismatch++;
					if (!confusionMatrix.containsKey(s.getTags().get(i)+"|"+ s.getHypothesisedTags().get(i)))
					{
						confusionMatrix.put(s.getTags().get(i)+"|"+s.getHypothesisedTags().get(i), 1);
					}
					else
					{
						Integer value = confusionMatrix.get(s.getTags().get(i)+"|"+ s.getHypothesisedTags().get(i));
						confusionMatrix.remove(s.getTags().get(i)+"|"+ s.getHypothesisedTags().get(i));						
						confusionMatrix.put(s.getTags().get(i)+"|"+ s.getHypothesisedTags().get(i), value + 1);
					}
					if (!correctTags.contains(s.getTags().get(i)))
					{
						correctTags.add(s.getTags().get(i));
					}
					if (!incorrectTags.contains(s.getHypothesisedTags().get(i)))
					{
						incorrectTags.add(s.getHypothesisedTags().get(i));
					}
					
				}				
				//if the flag is true, i.e. the word is known otherwise unknown
				if(s.getKnownWordFlag().get(i) == true) 
				{
					known_word_count++;
					
					if (!s.getTags().get(i).equalsIgnoreCase(s.getHypothesisedTags().get(i)))
					{
						mismatch_known++;
					}
				}
				else
				{
					unknown_word_count++;
					
					if (!s.getTags().get(i).equalsIgnoreCase(s.getHypothesisedTags().get(i)))
					{
						mismatch_unknown++;
					}
				}
				
			}
		}
		
		WER = (double)mismatch / (double) total_words;
		KnownWER = (double)mismatch_known / (double) known_word_count;
		UnknownWER = (double)mismatch_unknown / (double) unknown_word_count;		
	}
	
	
	
}
