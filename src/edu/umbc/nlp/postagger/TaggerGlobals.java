package edu.umbc.nlp.postagger;

/**
 * @author dave
 *
 */
public class TaggerGlobals {
	public static int N_GRAM_SIZE = 2; // 2 = bigram, 3 = trigram, etc.
	public static final boolean MAKE_ALL_WORDS_LOWER_CASE = false;
	/**
	 * If duplicate words/frequencies are encountered, sum their values.
	 * If false, throw an exception on duplicates.  Otherwise compute sum.
	 */
	public static final boolean SUM_DUPLICATE_WORDS = false;
	//public static double DEFAULT_PROBABILITY = 0.0;
	public static final String DEFAULT_TAG = "<DEFAULT>";
	public static final String FILE_PARSING_FIELD_DELIMETER = "\t";
	public static final boolean USE_DEFAULT_PROBABILITIES = true;
	public static final boolean USE_LOG_FOR_PROBABILITY_CALC = false;
	public static final String START_TAG = "<s>";
}
