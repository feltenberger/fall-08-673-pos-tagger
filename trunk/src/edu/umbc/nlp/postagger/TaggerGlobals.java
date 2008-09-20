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
	public static double DEFAULT_PROBABILITY = 0.0;
	public static final String FILE_PARSING_FIELD_DELIMETER = "	";
}
