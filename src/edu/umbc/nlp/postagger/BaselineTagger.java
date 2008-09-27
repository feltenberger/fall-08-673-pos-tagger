package edu.umbc.nlp.postagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import java.util.Random;


/**
 * Class BaselineTagger implements a PoS-tagger that is capable of tagging the words
 * of sentences with their most probable PoS tag, i.e. where P(tag|word) is maximized.
 */
public class BaselineTagger {
	
	private static final Logger log = Logger.getLogger(BaselineTagger.class);
	
	//the file to use for training
	private String trainingFile;
	
	//hashtable that contains each words most probable tag
	private Map<String, MostFrequentTag> word_pos_map;
	
	/**
	 * Constructor for class BaselineTagger.
	 * Performs initializations;
	 */
	public BaselineTagger(String trainingFile)throws IOException
	{
		this.trainingFile = trainingFile;	
		word_pos_map = new HashMap<String, MostFrequentTag>();
		this.train();
	}
		
	/**
	 * Splits a line of text into it's part. Uses global variable
	 * FILE_PARSING_FIELD_DELIMETER to mark the delimiting criterion.
	 * @param String s - a line of input text to be split
	 * @return String[] - an array containing the split items. String[0] = tag
	 * String[1] = word String[2] = count
	 */
	public String[] splitLine(String s)
	{
		String[] split = s.split(TaggerGlobals.FILE_PARSING_FIELD_DELIMETER);
		for(int i = 0; i < split.length; i++)
		{
			split[i] = split[i].trim();
		}
		return split;
	}
	
	/**
	 * Trains the BaseLineTagger.
	 * @param String filename - a file containing the probabilities for a word tag
	 * combination. It is assumed that the file has the following format:
	 * Each line in the file is of the form: "tag \t word \t probability" 
	 * @return void
	 */
	public void train()throws IOException
	{
		File file = new File(BaselineTagger.class.getResource(trainingFile).getFile());
		List<String> lines = FileUtils.readLines(file);
		
		for(String line : lines)
		{
			String[] lineItems = this.splitLine(line);
			String word = lineItems[1];
			String tag = lineItems[0];
			String count = lineItems[2];
			addToMap(word, tag, count);			
		}		
		
	}
	
	/**
	 * Adds a combination of a word, tag and probability to the global hashtable
	 * "word_pos_map". If a tag for a word already exists in the hashtable
	 * then the existing tag is compared to the presented tag, where the one with
	 * the highest probability is kept.
	 * @param String word - the key to enter
	 * 		  String tag - the tag that may be added to the hashtable
	 * 		  String count - the probability of the tag. 
	 * @return void
	 */
	private void addToMap(String word, String tag, String count)
	{
		double c = Double.parseDouble(count);
		
		if (!word_pos_map.containsKey(word))
		{
			MostFrequentTag t = new MostFrequentTag(tag, c);
			word_pos_map.put(word, t);
		}
		else
		{
			MostFrequentTag t = word_pos_map.get(word);
			if (t.getCount()< c)
			{
				word_pos_map.remove(word);
				MostFrequentTag newTag = new MostFrequentTag(tag, c);
				word_pos_map.put(word, newTag);
			}
		}
		
	}
	
	/**
	 * Tags a sentence with its respective part of speech. Unknown words are tagged as NN.
	 * The most probable tag is chosen, that is where P(tag|word) is maximized.
	 * @param String[] sentence - an array containing a sentence one word per index.
	 * @return String[] - an array containing the tags for the sentence, where there
	 * is a one-to-one of the index of a word in the input and the index of a tag
	 * in the output.
	 */
	public TaggedSentence tagSentence(Sentence sentence)
	{
		TaggedSentence new_sentence = new TaggedSentence();
		
		
		for (int i = 0; i < sentence.getSentence().size(); i++)
		{		
			if (word_pos_map.containsKey(sentence.getSentence().get(i)))
			{
				MostFrequentTag t = word_pos_map.get(sentence.getSentence().get(i));
				new_sentence.add(sentence.getSentence().get(i),
						sentence.getTags().get(i), t.getTag(), true);				
			}
			else
			{
				System.out.println(sentence.getSentence().get(i));
				new_sentence.add(sentence.getSentence().get(i),
						sentence.getTags().get(i), "NN", false);				
			}
		}
		
		return new_sentence;
	}
	
	/**
	 * Tags a sentence with its respective part of speech.
	 * The most probable tag is chosen, that is where P(tag|word) is maximized.
	 * @param String[] sentence - an array containing a sentence one word per index.
	 * @return String[] - an array containing the tags for the sentence, where there
	 * is a one-to-one of the index of a word in the input and the index of a tag
	 * in the output.
	 */
	public TaggedSentence tagSentenceImproved(Sentence sentence)
	{
		TaggedSentence new_sentence = new TaggedSentence();
		
		
		for (int i = 0; i < sentence.getSentence().size(); i++)
		{		
			if (word_pos_map.containsKey(sentence.getSentence().get(i)))
			{
				MostFrequentTag t = word_pos_map.get(sentence.getSentence().get(i));
				new_sentence.add(sentence.getSentence().get(i),
						sentence.getTags().get(i), t.getTag(), true);				
			}
			else
			{
				//NEED MORE RULES TO TAG UNKNOWN WORDS
				
				new_sentence.add(sentence.getSentence().get(i),
						sentence.getTags().get(i), "NN", false);				
			}
		}
		
		return new_sentence;
	}
}
