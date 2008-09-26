package edu.umbc.nlp.postagger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dave
 *
 */
public class SimpleTaggedSentence {
	private List<TaggedWord> listOfTaggedWords = new ArrayList<TaggedWord>();

	/**
	 * @param taggedSentence
	 */
	public SimpleTaggedSentence(String taggedSentence) {
		String[] taggedWords = taggedSentence.split(" ");
		for(String taggedWord : taggedWords) {
			String[] twArr = taggedWord.split("\\/");
			String word = twArr[0];
			String tag = twArr[1];
			TaggedWord tw = new TaggedWord(word, tag);
			listOfTaggedWords.add(tw);
		}
	}

	/**
	 * @return the listOfTaggedWords
	 */
	public List<TaggedWord> getListOfTaggedWords() {
		return listOfTaggedWords;
	}

	/**
	 * @param listOfTaggedWords the listOfTaggedWords to set
	 */
	public void setListOfTaggedWords(List<TaggedWord> listOfTaggedWords) {
		this.listOfTaggedWords = listOfTaggedWords;
	}

	/**
	 * @param tw
	 */
	public void addTaggedWord(TaggedWord tw) {
		this.listOfTaggedWords.add(tw);
	}

	/**
	 * @return
	 */
	public String getBaseSentence() {
		String sentence = "";
		for(TaggedWord tw : this.listOfTaggedWords) {
			if(!"".equals(sentence))
				sentence += " ";
			sentence += tw.getWord();
		}
		return sentence;
	}

	/**
	 * @return
	 */
	public List<String> getBaseSentenceList() {
		List<String> s = new ArrayList<String>();
		for(TaggedWord tw : this.listOfTaggedWords)
			s.add(tw.getWord());
		return s;
	}
}

