package edu.umbc.nlp.postagger;

/**
 * creating this as class instead of
 * a straight 'double' so it's easy to represent the
 * probability in other forms (e.g. log(prob) etc.)
 * @author dave
 *
 */
public class Probability {
	private double probability = 0.0f;
	public Probability(double prob) {
		this.probability = prob;
	}
	/**
	 * @return the probability
	 */
	public double doubleValue() {
		if(TaggerGlobals.USE_LOG_FOR_PROBABILITY_CALC)
			return Math.log(this.probability);
		return this.probability;
	}

	/**
	 * @return
	 */
	public double logValue() {
		return Math.log(this.probability);
	}
	/**
	 * @param probability the probability to set
	 */
	public void setProbability(double probability) {
		this.probability = probability;
	}
}
