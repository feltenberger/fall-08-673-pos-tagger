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
		return probability;
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
