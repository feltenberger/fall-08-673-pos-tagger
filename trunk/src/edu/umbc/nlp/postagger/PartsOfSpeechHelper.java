package edu.umbc.nlp.postagger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dave
 *
 */
public class PartsOfSpeechHelper {
	private Map<String, PartOfSpeech> allPartsOfSpeech = new HashMap<String, PartOfSpeech>();

	/**
	 * @param pos
	 * @return
	 */
	public PartOfSpeech getPartOfSpeech(String pos) {
		return this.allPartsOfSpeech.get(pos);
	}

	/**
	 * Adds the part of speech if it doesn't exist.  Will throw an error
	 * if a different instance of the same part of speech is added.
	 * @param pos
	 */
	public void addPartOfSpeech(PartOfSpeech pos) {
		PartOfSpeech existing = this.allPartsOfSpeech.get(pos.getPartOfSpeech());
		if(existing == null)
			this.allPartsOfSpeech.put(pos.getPartOfSpeech(), pos);
		else if(existing != pos)
			throw new IllegalArgumentException(existing.getPartOfSpeech() +
					" is already present; can't insert a different instance of the same object!");
	}

	/**
	 * @return the allPartsOfSpeech
	 */
	public Map<String, PartOfSpeech> getAllPartsOfSpeech() {
		return allPartsOfSpeech;
	}

	/**
	 * @param allPartsOfSpeech the allPartsOfSpeech to set
	 */
	public void setAllPartsOfSpeech(Map<String, PartOfSpeech> allPartsOfSpeech) {
		this.allPartsOfSpeech = allPartsOfSpeech;
	}

	/**
	 * Returns the probability of the word given its part of speech from the bMatrix.
	 * That is, it returns P(word|partOfSpeech).
	 * @param word
	 * @param partOfSpeech
	 * @return
	 */
	public Probability getObservationProbability(String word, String partOfSpeech) {
		PartOfSpeech pos = this.allPartsOfSpeech.get(partOfSpeech);
		Probability probability = pos.getProbabilityOfWord(word);
		if(probability == null) {
			if(TaggerGlobals.USE_ZERO_DEFAULT_PROBABILITY_WHEN_ONE_DOES_NOT_EXIST)
				return new Probability(0.0);
		}
		return probability;
	}

}
