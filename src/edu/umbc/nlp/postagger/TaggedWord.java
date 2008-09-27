package edu.umbc.nlp.postagger;

import java.text.DecimalFormat;

/**
 * @author dave
 *
 */
public class TaggedWord {
	private Double prob = null;
	private String word;
	private PartOfSpeech pos;
	private boolean knownWord = false;
	/**
	 * @param prob
	 * @param word
	 * @param partOfSpeech
	 */
	public TaggedWord(Double prob, String word, PartOfSpeech partOfSpeech, boolean knownWord) {
		this.prob = prob;
		this.word = word;
		this.pos = partOfSpeech;
		this.knownWord = knownWord;
	}
	public TaggedWord(String word, String partOfSpeech) {
		this.word = word;
		this.pos = new PartOfSpeech(partOfSpeech);
		this.prob = new Double(-1.0);
	}
	/**
	 *
	 */
	public TaggedWord() { }
	/**
	 * @return the prob
	 */
	public Double getProb() {
		return prob;
	}
	/**
	 * @param prob the prob to set
	 */
	public void setProb(Double prob) {
		this.prob = prob;
	}
	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}
	/**
	 * @param word the word to set
	 */
	public void setWord(String word) {
		this.word = word;
	}
	/**
	 * @return the pos
	 */
	public PartOfSpeech getPos() {
		return pos;
	}
	/**
	 * @param pos the pos to set
	 */
	public void setPos(PartOfSpeech pos) {
		this.pos = pos;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		DecimalFormat df = new DecimalFormat("0.00000000000000000");
		return "v[" + this.pos.getPartOfSpeech() + "][" + this.getWord() + "] = " + df.format(this.getProb().doubleValue());
	}
	/**
	 * @return the knownWord
	 */
	public boolean isKnownWord() {
		return knownWord;
	}
	/**
	 * @param knownWord the knownWord to set
	 */
	public void setKnownWord(boolean knownWord) {
		this.knownWord = knownWord;
	}
}
