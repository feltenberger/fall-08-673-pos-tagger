/**
 *
 */
package edu.umbc.nlp.postagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 * @author dave
 *
 */
public class TaggerTest extends TestCase {
	private static final Logger log = Logger.getLogger(TaggerTest.class);
	private String aMatrixFilename = "/bigram-dummy-data.txt";
	private String bMatrixFilename = "/tag-word-prob-dummy-data.txt";
	private File bFileMatrix = null;
	private File aFileMatrix = null;
	private List<String> linesOfFile = null;
	private String[][] splitLinesOfFile = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		super.setUp();
		bFileMatrix = new File(getClass().getResource(bMatrixFilename).getFile());
		aFileMatrix = new File(getClass().getResource(aMatrixFilename).getFile());
		linesOfFile = FileUtils.readLines(bFileMatrix);
		splitLinesOfFile = new String[linesOfFile.size()][];
		int i = 0;
		for(String line : linesOfFile) {
			splitLinesOfFile[i] = TaggerHelper.parseWordProbabilityLineFromFile(line);
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
		Tagger pos = TaggerHelper.readMatrices(aFileMatrix, bFileMatrix);
		String testWord = "to";
		String testPos = "TO";
		String testHypothesis = "TO";
		String[] testGiven = new String[] { "<s>" };
		double expected = findProbabilityFromFile(testWord, testPos);
		Probability actual = pos.getLikelihoodProbability(testWord, testPos);
		assertEquals("expected and actual equal each other", expected, actual.doubleValue());
		log.info("P(" + testWord + "|" + testPos + ") = " + actual.doubleValue());
		NGram ngram = pos.getNGram(new String[] { testGiven[0], testHypothesis });
		Probability ngramProb = pos.getPriorProbability(testHypothesis, testGiven);
		log.info("Probability of NGram " + ngram.asDashedString() + " is " + ngram.getProbability().doubleValue());
		assertEquals("NGram probability is correct", ngram.getProbability().doubleValue(), ngramProb.doubleValue());
	}

	public void testViterbi() throws Exception {
		Tagger pos = TaggerHelper.readMatrices(aFileMatrix, bFileMatrix);
		List<String> sentence = new ArrayList<String>();
		sentence.add("I");
		sentence.add("want");
		sentence.add("to");
		sentence.add("race");
		pos.viterbi(sentence);
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
