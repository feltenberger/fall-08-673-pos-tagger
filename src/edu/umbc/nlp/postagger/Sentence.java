package edu.umbc.nlp.postagger;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Sentence implements a representation of a sentence, where each
 * element of a sentence is annotated with a PoS-tag.
 */
public class Sentence
{
	private List<String> sentence;
	private List<String> tags;

	/**
	 * Constructor:
	 * @param List<String> sentence - a sentence where each word of a sentence is an entry in the list.
	 * 								  the sentence should be ordered, i.e. the first word is the first
	 * 								  element in the list
	 * 		  List<String> tags - a list of PoS tags that correspond to the individual words 
	 * 							  in List<String> sentence.
	 * Both list have to have the same size (since there is a one-to-one mapping between the two).
	 */
	public Sentence(List<String> sentence, List<String> tags) throws Exception
	{
		if (sentence.size()!=tags.size())
		{
			throw new Exception("Each word must have a corresponding tag.");
		}
		this.sentence = sentence;
		this.tags = tags;
	}
	
	public Sentence()
	{
		sentence = new ArrayList<String>();
		tags = new ArrayList<String>();		
	}
	
	public List<String> getSentence() { return sentence; }
	public String[] getSentenceAsArray()
	{
		String[] array = new String[sentence.size()];
		for (int i = 0; i < sentence.size(); i++)
		{
			array[i] = sentence.get(i);
		}
		return array;
	}
	
	public List<String> getTags() { return tags; }
	public String[] getTagsAsArray()
	{
		String[] array = new String[tags.size()];
		for (int i = 0; i < tags.size(); i++)
		{
			array[i] = tags.get(i);
		}
		return array;
	}
	
	public void addWordAndTag(String word, String tag)
	{
		sentence.add(word);
		tags.add(tag);
	}
	
}
	