package edu.umbc.nlp.postagger;

import java.util.ArrayList;
import java.util.List;

public class TaggedSentence extends Sentence
{
	private List<String> hypothesised_tags;
	private List<Boolean> word_is_known;
	
	/**
	 * Constructor:
	 * @param List<String> sentence - a sentence where each word of a sentence is an entry in the list.
	 * 								  the sentence should be ordered, i.e. the first word is the first
	 * 								  element in the list
	 * 		  List<String> tags - a list of PoS tags that correspond to the individual words 
	 * 							  in List<String> sentence. The tags correspond to the tags found in a
	 * 							  control corpus.
	 * 		  List<String> hypothesised_tags - a list of PoS tags that correspond to the individual words 
	 * 							  in List<String> sentence. The hypothesized tags are the output of
	 * 							  a PoS tagger.
	 * 		  List<Boolean> word_is_known - an indicator of a PoS-tagger if a given word is in-vocabulary
	 * 										(i.e. known) or out-of-vocabulary (i.e. unknown)
	 * Both list have to have the same size (since there is a one-to-one mapping between the two).
	 */
	public TaggedSentence(List<String> sentence, List<String> tags, 
						  List<String> hypothesised_tags, List<Boolean> word_is_known) throws Exception
	{
		super(sentence,tags);
		if (sentence.size()!=hypothesised_tags.size() && sentence.size()!= word_is_known.size())
		{
			throw new Exception("Each word must have a corresponding tag, hypotesized tag and out-of-vocabulary indicator.");
		}
		this.hypothesised_tags = hypothesised_tags;
		this.word_is_known = word_is_known;
	}
	public TaggedSentence()
	{
		super();
		hypothesised_tags = new ArrayList<String>();
		word_is_known = new ArrayList<Boolean>();
	}
	
	public List<Boolean> getKnownWordFlag() { return word_is_known; }
	public Boolean[] getKnownWordFlagAsArray()
	{
		Boolean[] array = new Boolean[word_is_known.size()];
		for (int i = 0; i < word_is_known.size(); i++)
		{
			array[i] = word_is_known.get(i);
		}
		return array;
	}
	
	public List<String> getHypothesisedTags() { return hypothesised_tags; }
	public String[] getHypothesisedTagsAsArray()
	{
		String[] array = new String[hypothesised_tags.size()];
		for (int i = 0; i < hypothesised_tags.size(); i++)
		{
			array[i] = hypothesised_tags.get(i);
		}
		return array;
	}
	
	
	public void add(String word, String tag, String hypothesised_tag, Boolean known)
	{
		addWordAndTag(word, tag);
		hypothesised_tags.add(hypothesised_tag.trim());
		word_is_known.add(known);
	}
}
