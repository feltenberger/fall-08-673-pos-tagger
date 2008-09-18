/**
 *
 */
package edu.umbc.nlp.postagger;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author dave
 *
 */
public class TaggerHelperTest extends TestCase {
	private static final Logger log = Logger.getLogger(TaggerHelperTest.class);
	private String filename = "/tag-word-prob-dummy-data.txt";
	private File file = null;
	private List<String> linesOfFile = null;
	private String[][] splitLinesOfFile = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		super.setUp();
		file = new File(getClass().getResource(filename).getFile());
		linesOfFile = FileUtils.readLines(file);
		splitLinesOfFile = new String[linesOfFile.size()][];
		int i = 0;
		for(String line : linesOfFile) {
			//String[] lineParts = line.split(TaggerGlobals.FILE_PARSING_FIELD_DELIMETER);
			splitLinesOfFile[i] = TaggerHelper.parseWordProbabilityLineFromFile(line);
			//lineParts[2] = TaggerHelper.normalizeString(lineParts[2]);
			//splitLinesOfFile[i] = lineParts;
			i++;
		}
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Read the combined files.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testReadObservationProbabilityFromFile() throws Exception {
		PartsOfSpeechHelper pos = TaggerHelper.readProbabilityOfWordGivenTag(file);
		String testWord = "to";
		String testPos = "TO";
		double expected = findProbabilityFromFile(testWord, testPos);
		Probability actual = pos.getObservationProbability(testWord, testPos);
		assertEquals("expected and actual equal each other", expected, actual.getProbability());
		log.info("P(" + testWord + "|" + testPos + ") = " + actual.getProbability());
	}

	/**
	 * Will return the probability from the file (if it exists) for checking of parsing algorithm.
	 * @param word
	 * @param partOfSpeech
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private double findProbabilityFromFile(String word, String partOfSpeech) {
		for(String[] lineParts : splitLinesOfFile) {
			if(partOfSpeech.equals(lineParts[0]) && word.equals(lineParts[1])) {
				return Double.parseDouble(lineParts[2]);
			}
		}
		return 0.0;
	}
}
