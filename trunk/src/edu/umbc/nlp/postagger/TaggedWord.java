package edu.umbc.nlp.postagger;

import java.text.DecimalFormat;

/**
 * @author dave
 *
 */
public class TaggedWord {
	private Probability prob = null;
	private String word;
	private PartOfSpeech pos;
	/**
	 * @param prob
	 * @param word
	 * @param partOfSpeech
	 */
	public TaggedWord(Probability prob, String word, PartOfSpeech partOfSpeech) {
		this.prob = prob;
		this.word = word;
		this.pos = partOfSpeech;
	}
	public TaggedWord(String word, String partOfSpeech) {
		this.word = word;
		this.pos = new PartOfSpeech(partOfSpeech);
		this.prob = new Probability(-1.0);
	}
	/**
	 *
	 */
	public TaggedWord() { }
	/**
	 * @return the prob
	 */
	public Probability getProb() {
		return prob;
	}
	/**
	 * @param prob the prob to set
	 */
	public void setProb(Probability prob) {
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
}
