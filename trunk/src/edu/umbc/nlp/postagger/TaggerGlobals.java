package edu.umbc.nlp.postagger;

/**
 * @author dave
 *
 */
public class TaggerGlobals {
	public static final int N_GRAM_SIZE = 2; // 2 = bigram, 3 = trigram, etc.
	public static boolean MAKE_ALL_WORDS_LOWER_CASE = false;
	/**
	 * If duplicate words/frequencies are encountered, sum their values.
	 * If false, throw an exception on duplicates.  Otherwise compute sum. 
	 */
	public static boolean SUM_DUPLICATE_WORDS = false;
	public static boolean USE_ZERO_DEFAULT_PROBABILITY_WHEN_ONE_DOES_NOT_EXIST = true;
	public static String FILE_PARSING_FIELD_DELIMETER = "	";
}
