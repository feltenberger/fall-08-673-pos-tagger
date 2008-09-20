package edu.umbc.nlp.postagger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dave
 *
 */
public class PartOfSpeech {
	private String partOfSpeech;
	private long frequency;
	private double unigramProbability;
	/**
	 * The b matrix contains the probability of a word, given <code>this</code> <type>PartOfSpeech</type>.
	 */
	private Map<String, Probability> bMatrix = new HashMap<String, Probability>();

	public PartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	/**
	 * @return the partOfSpeech
	 */
	public String getPartOfSpeech() {
		return partOfSpeech;
	}
	/**
	 * @param partOfSpeech the partOfSpeech to set
	 */
	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}
	/**
	 * @return the frequency
	 */
	public long getFrequency() {
		return frequency;
	}
	/**
	 * @param frequency the frequency to set
	 */
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}
	/**
	 * @return the unigramProbability
	 */
	public double getUnigramProbability() {
		return unigramProbability;
	}
	/**
	 * @param unigramProbability the unigramProbability to set
	 */
	public void setUnigramProbability(double unigramProbability) {
		this.unigramProbability = unigramProbability;
	}

	/**
	 * Adds a word and its probability to this PartOfSpeech's b-matrix.
	 * For example, if this POS is "NN", we add:
	 * P(boat | NN).  This PartOfSpeech object will contain each word and
	 * its probability given this POS.
	 * Note: does not handle duplicate words.
	 * @param word
	 * @param probabilityOfWordGivenThisPOS
	 */
	public void addWordAndProbabilityToBMatrix(String word, Probability probabilityOfWordGivenThisPOS) {
		if(this.bMatrix.containsKey(word))
			throw new IllegalArgumentException("The bMatrix for " + this.partOfSpeech + " already has the word " + word + "!");
		this.bMatrix.put(word, probabilityOfWordGivenThisPOS);
	}

	/**
	 * Gets the probability of the word given this PartOfSpeech from bMatrix.
	 * E.g. returns P(word | this.partOfSpeech)
	 * @param word
	 * @return
	 */
	public Probability getProbabilityOfWord(String word) {
		return this.bMatrix.get(word);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == this) return true;
		if(obj instanceof PartOfSpeech)
			return this.partOfSpeech.equals(((PartOfSpeech)obj).getPartOfSpeech());
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.partOfSpeech.hashCode();
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.partOfSpeech.toString();
	}
}
