package edu.umbc.nlp.postagger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	 * @param bMatrixFile
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Tagger readMatrices(File aMatrixFile, File bMatrixFile) throws IOException {
		log.info("Reading file " + aMatrixFile.getAbsolutePath());
		log.info("Reading file " + bMatrixFile.getAbsolutePath());
		Tagger tagger = new Tagger();
		parseObservationProbabilitiesForBMatrix(tagger, bMatrixFile);
		parseNGramsForAMatrix(tagger, aMatrixFile);
		return tagger;
	}

	/**
	 * Parses lines of format:
	 * <s>	VB	.019
	 * @param tagger
	 * @param aMatrix
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void parseNGramsForAMatrix(Tagger tagger, File aMatrix) throws IOException {
		List<String>lines = FileUtils.readLines(aMatrix);
		int i = 0;
		for(String line : lines) {
			if(i == 0) {
				String lp[] = line.split(" ");
				TaggerGlobals.N_GRAM_SIZE = Integer.parseInt(lp[lp.length-1]);
			}
			else {
				String[] lineParts = line.split(TaggerGlobals.FILE_PARSING_FIELD_DELIMETER);
				String[] nGramStrs = new String[lineParts.length-1];
				PartOfSpeech[] nGramPartsOfSpeech = new PartOfSpeech[lineParts.length-1];
				String probabilityString = lineParts[lineParts.length-1];
				double probability = Double.parseDouble(probabilityString);

				for(int j = 0; j < nGramStrs.length; j++) {
					nGramStrs[j] = lineParts[j];
					nGramPartsOfSpeech[j] = tagger.getPartOfSpeech(lineParts[j]);
				}
				NGram nGram = tagger.getNGram(nGramStrs);
				if(nGram == null) {
					tagger.addNGram(nGramStrs, probability);
					nGram = tagger.getNGram(nGramStrs);
				}
			}
			i++; //if(i > 100) break;
		}
	}

	/**
	 * @param tagger
	 * @param bMatrix
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void parseObservationProbabilitiesForBMatrix(Tagger tagger, File bMatrix) throws IOException {
		List<String>lines = FileUtils.readLines(bMatrix);
		int i = 0;
		Set<String>allWords = new HashSet<String>();
		for(String line : lines) {
			String[] lineParts = parseWordProbabilityLineFromFile(line);
			String tag = lineParts[0];
			String word = lineParts[1];
			allWords.add(word);
			String probabilityString = lineParts[2];

			PartOfSpeech partOfSpeech = tagger.getPartOfSpeech(tag);
			if(partOfSpeech == null)
				partOfSpeech = new PartOfSpeech(tag);

			Probability probability = new Probability(Double.parseDouble(probabilityString));
			try {
				partOfSpeech.addWordAndProbabilityToBMatrix(word, probability);
				tagger.addPartOfSpeech(partOfSpeech);
			} catch (RuntimeException e) {
				log.error("Error parsing file on line " + i, e);
				throw e;
			}

			i++; //if(i > 100) break;
		}
		String startState = "<s>";
		for(String word : allWords) {
			PartOfSpeech partOfSpeech = tagger.getPartOfSpeech(startState);
			if(partOfSpeech == null)
				partOfSpeech = new PartOfSpeech(startState);
			partOfSpeech.addWordAndProbabilityToBMatrix(word, new Probability(TaggerGlobals.DEFAULT_PROBABILITY));
			tagger.addPartOfSpeech(partOfSpeech);
		}
	}

	/**
	 * Takes line of this form:
	 * PPSS	race	.0  (tab-delimited)
	 * And returns an array with:
	 * partOfSpeech, word, probability
	 * respectively.
	 * @param line
	 * @return
	 */
	public static String[] parseWordProbabilityLineFromFile(String line) {
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
