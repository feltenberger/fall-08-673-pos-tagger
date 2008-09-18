package edu.umbc.nlp.postagger;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author dave
 *
 */
public class TaggerHelper {
	private static final Logger log = Logger.getLogger(TaggerHelper.class);
	/**
	 * Reads a file of the format:
	 * partOfSpeech : word : probability
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static PartsOfSpeechHelper readProbabilityOfWordGivenTag(File file) throws IOException, NumberFormatException {
		log.info("Reading file " + file.getAbsolutePath());
		PartsOfSpeechHelper allPartsOfSpeech = new PartsOfSpeechHelper();
		List<String>lines = FileUtils.readLines(file);
		int i = 0;
		for(String line : lines) {
			String[] lineParts = parseWordProbabilityLineFromFile(line);
			String tag = lineParts[0];
			String word = lineParts[1];
			String probabilityString = lineParts[2];

			PartOfSpeech partOfSpeech = allPartsOfSpeech.getPartOfSpeech(tag);
			if(partOfSpeech == null)
				partOfSpeech = new PartOfSpeech(tag);

			Probability probability = new Probability(Double.parseDouble(probabilityString));
			try {
				partOfSpeech.addWordAndProbabilityToBMatrix(word, probability);
				allPartsOfSpeech.addPartOfSpeech(partOfSpeech);
			} catch (RuntimeException e) {
				log.error("Error parsing file on line " + i, e);
				throw e;
			}

			i++; //if(i > 100) break;
		}
		return allPartsOfSpeech;
	}

	/**
	 * Takes line of this form:
	 * NN , boat: 5.56414907468201e-05 
	 * PPSS	race	.0  (tab-delimited)
	 * And returns an array with:
	 * partOfSpeech, word, probability
	 * respectively.
	 * @param line
	 * @return
	 */
	public static String[] parseWordProbabilityLineFromFile(String line) {
		/*
		String[] returnValue = new String[3];
		String[] lineParts = line.split("	");

		returnValue[0] = lineParts[0].trim(); // tag/part of speech
		lineParts[2] = normalizeString(lineParts[2].trim());
		returnValue[1] = lineParts[2];
		returnValue[2] = lineParts[3].trim();

		return returnValue;
		*/
		String[] lineParts = line.split(TaggerGlobals.FILE_PARSING_FIELD_DELIMETER);
		for(int i = 0; i < lineParts.length; i++)
			lineParts[i] = lineParts[i].trim();
		return lineParts;
	}

	/**
	 * @param str
	 * @return
	 */
	public static String normalizeString(String str) {
		if(str == null) return null;
		str = str.trim();
		if(TaggerGlobals.MAKE_ALL_WORDS_LOWER_CASE)
			str = str.toLowerCase();
		if(str.length() > 1 && str.charAt(str.length()-1) == ':') {
			str = str.substring(0, str.length()-1);
		}
		return str;
	}
}
