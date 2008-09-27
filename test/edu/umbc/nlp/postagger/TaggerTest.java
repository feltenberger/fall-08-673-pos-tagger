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
	// private String aMatrixFilename = "/bigram-dummy-data.txt";
	private String nGramsFilename = "/prev_tag_prob.dat";
	// private String bMatrixFilename = "/tag-word-prob-dummy-data.txt";
	private String observationsFilename = "/tag_word_prob.dat";
	private String testFilename = "/wsj/evaluation.pos";
	private File startMatrixFile = null;
	private File observationsFile = null;
	private File nGramsFile = null;
	private Tagger tagger = null;
	private String[][] splitLinesOfTagWordProbFile = null;
	private List<SimpleTaggedSentence> testSentences = null;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		super.setUp();
		observationsFile = new File(getClass().getResource(observationsFilename).getFile());
		nGramsFile = new File(getClass().getResource(nGramsFilename).getFile());

		List<String> linesOfFile = FileUtils.readLines(observationsFile);
		splitLinesOfTagWordProbFile = new String[linesOfFile.size()][];
		tagger = TaggerHelper.readMatrices(nGramsFile, observationsFile);

		int i = 0;
		for(String line : linesOfFile) {
			splitLinesOfTagWordProbFile[i] = TaggerHelper.parseWordProbabilityLineFromFile(line);
			i++;
		}

		addSomeTestSentences();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 *
	 */
	private void addSomeTestSentences() {
		testSentences = new ArrayList<SimpleTaggedSentence>();
		String ts = "I/PRP love/VB to/TO eat/VBP pizza/NN !/.";
		testSentences.add(new SimpleTaggedSentence(ts));
		ts = "I/PRP am/VBP stark/NNS raving/JJ mad/JJ !/.";
		testSentences.add(new SimpleTaggedSentence(ts));
		ts = "Please/RB take/VB out/RB the/DT trash/VB ./.";
		testSentences.add(new SimpleTaggedSentence(ts));
		ts = "Natural/NNP language/NN processing/VBG is/VBZ fun/NN ./.";
		testSentences.add(new SimpleTaggedSentence(ts));
	}

	/**
	 * Read the combined files.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testReadObservationProbabilityFromFile() throws Exception {
		Tagger pos = TaggerHelper.readMatrices(nGramsFile, observationsFile);
		String testWord = "to";
		String testPos = "TO";
		String testHypothesis = "TO";
		String[] testGiven = new String[] { "<s>" };
		double expected = findProbabilityFromFile(testWord, testPos);
		Double actual = pos.getLikelihoodProbability(testWord, testPos);
		assertEquals("expected and actual equal each other", expected, actual.doubleValue());
		log.info("P(" + testWord + "|" + testPos + ") = " + actual.doubleValue());
		NGram ngram = pos.getNGram(new String[] { testGiven[0], testHypothesis });
		Double ngramProb = pos.getPriorProbability(testHypothesis, testGiven);
		log.info("Probability of NGram " + ngram.asDashedString() + " is " + ngram.getProbability().doubleValue());
		assertEquals("NGram probability is correct", ngram.getProbability().doubleValue(), ngramProb.doubleValue());
	}

	/**
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testViterbiOnTestCorpus() throws Exception {
		List<String> linesOfTestFile = FileUtils.readLines(new File(getClass().getResource(testFilename).getFile()));
		for(String line : linesOfTestFile) {
			SimpleTaggedSentence sts = new SimpleTaggedSentence(line);
			doTestOfSentence(tagger, sts);
		}
	}

	/**
	 * Kickoff Niels' Driver from a unit test...
	 * @throws Exception
	 */
	public void testDriver() throws Exception {
		Driver.main(new String[] { });
	}

	/**
	 * @throws Exception
	 */
	public void testViterbi() throws Exception {
		for(int i = 0; i < this.testSentences.size(); i++) {
			SimpleTaggedSentence testSentence = this.testSentences.get(i);
			this.doTestOfSentence(tagger, testSentence);
		}
	}

	/**
	 * @param tagger
	 * @param sts
	 */
	private void doTestOfSentence(Tagger tagger, SimpleTaggedSentence sts) {
		List<TaggedWord>tagged = tagger.tagPartsOfSpeech(sts.getBaseSentenceList());
		List<TaggedWord>expected = sts.getListOfTaggedWords();
		for(int i = 0; i < expected.size(); i++) {
			TaggedWord actualTW = tagged.get(i);
			TaggedWord expectedTW = expected.get(i);
			PartOfSpeech actualTag = actualTW.getPos();
			PartOfSpeech expectedTag = expectedTW.getPos();
			assertEquals("Expected: " + expectedTag + ", actual: " + actualTag, expectedTag, actualTag);
		}
	}

	/**
	 * Will return the probability from the file (if it exists) for checking of parsing algorithm.
	 * @param word
	 * @param partOfSpeech
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private double findProbabilityFromFile(String word, String partOfSpeech) {
		for(String[] lineParts : splitLinesOfTagWordProbFile) {
			if(partOfSpeech.equals(lineParts[0]) && word.equals(lineParts[1])) {
				return Double.parseDouble(lineParts[2]);
			}
		}
		return 0.0;
	}
}
