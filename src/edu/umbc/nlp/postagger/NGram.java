package edu.umbc.nlp.postagger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dave
 *
 */
public class NGram {
	private int size = TaggerGlobals.N_GRAM_SIZE;
	private List<PartOfSpeech> nGramPartsOfSpeech = new ArrayList<PartOfSpeech>();
	private Probability probability = null;
	/**
	 * @param probability
	 * @param partsOfSpeech
	 */
	public NGram(Probability probability, PartOfSpeech...partsOfSpeech) {
		if(partsOfSpeech == null) throw new IllegalArgumentException("partsOfSpeech can't be null.");
		if(partsOfSpeech.length != TaggerGlobals.N_GRAM_SIZE)
			throw new IllegalStateException("NGram is a different size than expected.  Got " + partsOfSpeech.length + " and expected " + TaggerGlobals.N_GRAM_SIZE);
		this.probability = probability;
		for(PartOfSpeech pos : partsOfSpeech) {
			this.nGramPartsOfSpeech.add(pos);
		}
	}

	/**
	 * Returns the "history" part of the NGram probability.
	 * For example, the trigram "my name is", the history will
	 * be "my name" because the probability of the trigram looks like:
	 * P(is | my name)
	 * @return
	 */
	public List<PartOfSpeech> getNGramHistory() {
		List<PartOfSpeech>history = new ArrayList<PartOfSpeech>();
		for(int i = 0; i < this.nGramPartsOfSpeech.size()-1; i++) {
			history.add(this.nGramPartsOfSpeech.get(i));
		}
		return history;
	}

	/**
	 * The opposite of <code>getNGramHistory</code>.  This returns the
	 * "hypothesis" of our probability.  Using the same trigram as in the
	 * history method, "my name is", the hypothesis would be "is" because
	 * the probability of this trigram is based on the probability of "is"
	 * given the rest of the prior words.  P(is | my name).
	 * (Note, of course, that we're using parts of speech and not words, however.)
	 * @return
	 */
	public PartOfSpeech getNGramHypothesis() {
		return this.nGramPartsOfSpeech.get(this.nGramPartsOfSpeech.size()-1);
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	/**
	 * @return the probability
	 */
	public Probability getProbability() {
		return probability;
	}
	/**
	 * @param probability the probability to set
	 */
	public void setProbability(Probability probability) {
		this.probability = probability;
	}
	/**
	 * @return the nGramPartsOfSpeech
	 */
	public List<PartOfSpeech> getNGramPartsOfSpeech() {
		return nGramPartsOfSpeech;
	}
	/**
	 * @param gramPartsOfSpeech the nGramPartsOfSpeech to set
	 */
	public void setNGramPartsOfSpeech(List<PartOfSpeech> gramPartsOfSpeech) {
		nGramPartsOfSpeech = gramPartsOfSpeech;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String tmp = "P(" + this.getNGramHypothesis().getPartOfSpeech() + "|";
		String hist = "";
		for(PartOfSpeech pos : this.getNGramHistory()) {
			if(hist.equals(""))
				hist += pos.getPartOfSpeech();
			else
				hist += "-" + pos.getPartOfSpeech();
		}
		return tmp + hist + ") = " + this.getProbability().doubleValue();
	}

	public String asDashedString() {
		String str = "";
		for(PartOfSpeech pos : this.nGramPartsOfSpeech) {
			if(str.equals(""))
				str += pos.getPartOfSpeech();
			else
				str += "-" + pos.getPartOfSpeech();
		}
		return str;
	}

	/**
	 * The string to use to hash ngrams into a Set or Map.
	 * @param partsOfSpeech
	 * @return
	 */
	public static String getHashCodeFromStringArray(String[] partsOfSpeech) {
		String hashString = "";
		for(int i = 0; i < partsOfSpeech.length; i++) {
			if(i == 0)
				hashString += partsOfSpeech[i];
			else
				hashString += ("-" + partsOfSpeech[i]);
		}
		return hashString;
	}
}
