package edu.umbc.nlp.postagger;

import java.util.List;
import java.util.ArrayList;

public class Evaluator
{
	private List<TaggedSentence> sentences;
	
	private double WER;
	private double UnknownWER;
	private double KnownWER;
	
	public Evaluator(List<TaggedSentence> sentences)
	{
		this.sentences = sentences;
		computeWordErrorRates();
	}
	
	public double getWordErrorRate() { return 100 * WER; }
	public double getKnownWord_WordErrorRate() { return 100 * KnownWER; }
	public double getUnknownWord_WordErrorRate() { return 100 * UnknownWER; }	
	
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
	
	public static void main(String[] args) throws Exception
	{
		//create a new tagger
		BaselineTagger myBaselineTagger = new BaselineTagger("/tagWordProb.txt");
		//convert the corpus to nice sentences
		List<Sentence> sentences = myBaselineTagger.parseCorpus("/wsj/01/wsj_0101.pos");
		
		List<TaggedSentence> tagged_sentences = new ArrayList<TaggedSentence>();
		
		for (Sentence s : sentences)
		{						
			//tag the sentence
			//THIS EVENTUALLY NEED TO BE THE tagSentenceImproved METHOD
			TaggedSentence new_s = myBaselineTagger.tagSentence(s);
			
			tagged_sentences.add( new_s );
			System.out.println("Control:");
			for (int i = 0; i < new_s.getSentence().size(); i++)
			{
				System.out.print(new_s.getSentence().get(i) 
						+ "/" + new_s.getTags().get(i) + " ");							
			}
			System.out.println("\nTagger Output:");
			for (int i = 0; i < new_s.getSentence().size(); i++)
			{
				System.out.print(new_s.getSentence().get(i) 
						+ "/" + new_s.getHypothesisedTags().get(i) + " ");							
			}
			System.out.println("\n------------------------------------------");	
		}
		
		Evaluator myEvaluator = new Evaluator(tagged_sentences);
		
		System.out.println("------------------------------------------");		
		System.out.println("Word Error Rate = " + myEvaluator.getWordErrorRate());
		System.out.println("Known Word WER = " + myEvaluator.getKnownWord_WordErrorRate());
		System.out.println("Unknown Word WER = " + myEvaluator.getUnknownWord_WordErrorRate());			
	}

}
