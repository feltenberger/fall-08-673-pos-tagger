package edu.umbc.nlp.postagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Driver {

	private static final Logger log = Logger.getLogger(TaggerTest.class);
	
	private static String aMatrixFilename = "/prev_tag_prob.dat";
	private static String bMatrixFilename = "/tag_word_prob.dat";
	private static String startMatrixFilename = "/start_tags_prob.txt";
	
	private static String testSetFilename = "/wsj/evaluation.pos";
	private static String trainingSetFilename = "/wsj/training.pos";
	private static String corpusFilename = "/wsj/combined.pos";
	private static File bMatrixFile = null;
	private static File aMatrixFile = null;
	
	
	/**
	 * @param args
	 */
	private static void print_stats(TaggedSentence new_s)
	{		
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

	
	public static void main(String[] args) throws Exception
	{	
		//create a new tagger
		BaselineTagger myBaselineTagger = new BaselineTagger(bMatrixFilename);
		HMMTagger hmmTagger = new HMMTagger(bMatrixFilename, aMatrixFilename, startMatrixFilename);
			
				
		TaggerHelper helper = new TaggerHelper();
		List<Sentence> sentences = helper.parseEvalCorpus(testSetFilename);
		//THIS IS TO SPLIT THE ORIGINAL WSJ CORPUS
		//List<Sentence> sentences = helper.parseCorpus(corpusFilename);
		//helper.splitCorpus(trainingFilename, testFilename, sentences);

		
		List<TaggedSentence> baselineTaggedSentences = new ArrayList<TaggedSentence>();
		List<TaggedSentence> hmmTaggedSentences = new ArrayList<TaggedSentence>();
		
		for (Sentence s : sentences)
		{		
			//THIS EVENTUALLY NEED TO BE THE tagSentenceImproved METHOD
			TaggedSentence baselineTS = myBaselineTagger.tagSentence(s);			
			baselineTaggedSentences.add( baselineTS );
			//print_stats(baselineTS);
			
			TaggedSentence hmmTS = hmmTagger.tagSentence(s);
			hmmTaggedSentences.add( hmmTS );
			print_stats(hmmTS);
			
		}
		
		Evaluator myBaselineEvaluator = new Evaluator(baselineTaggedSentences);				
		System.out.println("------------------------------------------");		
		System.out.println("Word Error Rate = " + myBaselineEvaluator.getWordErrorRate());
		System.out.println("Known Word WER = " + myBaselineEvaluator.getKnownWord_WordErrorRate());
		System.out.println("Unknown Word WER = " + myBaselineEvaluator.getUnknownWord_WordErrorRate());
		
		Evaluator myHMMEvaluator = new Evaluator(hmmTaggedSentences);				
		System.out.println("------------------------------------------");		
		System.out.println("Word Error Rate = " + myHMMEvaluator.getWordErrorRate());
		System.out.println("Known Word WER = " + myHMMEvaluator.getKnownWord_WordErrorRate());
		System.out.println("Unknown Word WER = " + myHMMEvaluator.getUnknownWord_WordErrorRate());		
	}

}
