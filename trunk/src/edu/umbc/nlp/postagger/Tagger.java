package edu.umbc.nlp.postagger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author dave
 *
 */
public class Tagger {
	private static final Logger log = Logger.getLogger(Tagger.class);
	/**
	 * This map combined with the PartOfSpeech's word probabilities makes the bMatrix.
	 */
	private Map<String, PartOfSpeech> allPartsOfSpeech = new HashMap<String, PartOfSpeech>();
	private Set<String>allWords = new HashSet<String>();
	private PartOfSpeech startState = new PartOfSpeech(TaggerGlobals.START_TAG);
	//private PartOfSpeech endState = new PartOfSpeech("<e>");
	/**
	 * This is the aMatrix.
	 */
	private Map<String, NGram> allNGrams = new HashMap<String, NGram>();

	/**
	 * @param pos
	 * @return
	 */
	public PartOfSpeech getPartOfSpeech(String pos) {
		return this.allPartsOfSpeech.get(pos);
	}

	/**
	 * Given an array of parts of speech in historical order,
	 * i.e. the trigram "my name is" would be passed in as:
	 * { "my", "name", "is" }, return the NGram for that
	 * sequence.
	 * @param partsOfSpeech
	 * @return
	 */
	public NGram getNGram(String[] partsOfSpeech) {
		if(partsOfSpeech.length != TaggerGlobals.N_GRAM_SIZE)
			throw new IllegalArgumentException("Can't get an ngram of size " + partsOfSpeech.length + "; expecting " + TaggerGlobals.N_GRAM_SIZE);
		String hc = NGram.getHashCodeFromStringArray(partsOfSpeech);
		return this.allNGrams.get(hc);
	}

	/**
	 * @param nGramHashCode
	 * @return
	 */
	public NGram getNGram(String nGramHashCode) {
		return this.allNGrams.get(nGramHashCode);
	}

	/**
	 * @param hypothesis
	 * @param given
	 * @return
	 */
	public NGram getNGram(String hypothesis, String[] given) {
		return this.getNGram(this.getNGramArrayFromHypothesisAndGiven(hypothesis, given));
	}

	/**
	 * Adds an ngram to the list of all ngrams (i.e. adds to a-matrix).
	 * @param partsOfSpeech
	 * @param probability
	 */
	public void addNGram(String[] partsOfSpeech, Double probability) {
		String ng = NGram.getHashCodeFromStringArray(partsOfSpeech);
		if(this.allNGrams.get(ng) != null) {
			throw new IllegalArgumentException("Trying to add NGram " + ng + " but it already exists!");
		}
		else {
			PartOfSpeech[] pos = new PartOfSpeech[partsOfSpeech.length];
			for(int i = 0; i < pos.length; i++) {
				if(TaggerGlobals.DEFAULT_TAG.equals(partsOfSpeech[i])) {
					pos[i] = new PartOfSpeech(TaggerGlobals.DEFAULT_TAG);
				}
				else if(TaggerGlobals.START_TAG.equals(partsOfSpeech[i])) {
					pos[i] = new PartOfSpeech(TaggerGlobals.START_TAG);
				}
				else {
					pos[i] = this.getPartOfSpeech(partsOfSpeech[i]);
				}
			}
			NGram ngram = new NGram(probability, pos);
			this.allNGrams.put(ng, ngram);
		}
	}

	/**
	 * @param word
	 */
	public void addToKnownWords(String word) {
		this.allWords.add(word);
	}

	/**
	 * @param word
	 * @return
	 */
	public boolean isKnownWord(String word) {
		return this.allWords.contains(word);
	}

	/**
	 * Adds the part of speech if it doesn't exist.  Will throw an error
	 * if a different instance of the same part of speech is added.
	 * @param pos
	 */
	public void addPartOfSpeech(PartOfSpeech pos) {
		PartOfSpeech existing = this.allPartsOfSpeech.get(pos.getPartOfSpeech());
		if(existing == null)
			this.allPartsOfSpeech.put(pos.getPartOfSpeech(), pos);
		else if(existing != pos)
			throw new IllegalArgumentException(existing.getPartOfSpeech() +
					" is already present; can't insert a different instance of the same object!");
	}

	/**
	 * @return the allPartsOfSpeech
	 */
	public Map<String, PartOfSpeech> getAllPartsOfSpeech() {
		return allPartsOfSpeech;
	}

	/**
	 * @param allPartsOfSpeech the allPartsOfSpeech to set
	 */
	public void setAllPartsOfSpeech(Map<String, PartOfSpeech> allPartsOfSpeech) {
		this.allPartsOfSpeech = allPartsOfSpeech;
	}

	/**
	 * Returns the probability of the word given its part of speech from the bMatrix.
	 * That is, it returns P(word|partOfSpeech).
	 * @param word
	 * @param partOfSpeech
	 * @return
	 */
	public Double getLikelihoodProbability(String word, String partOfSpeech) {
		PartOfSpeech pos = this.allPartsOfSpeech.get(partOfSpeech);
		if(pos == null)
			throw new IllegalArgumentException("The part of speech " + partOfSpeech + " does not exist in this corpus!");
			//return new Probability(TaggerGlobals.DEFAULT_PROBABILITY);

		Double probability = pos.getProbabilityOfWord(word);
		if(probability == null && TaggerGlobals.USE_DEFAULT_PROBABILITIES) {
			probability = pos.getProbabilityOfWord(TaggerGlobals.DEFAULT_TAG);
		}

		if(probability == null)
			throw new IllegalArgumentException("The likelihood probability was impossible to find: P(" +
					word + "|" + partOfSpeech + "). (no default either!)");

		return probability;
	}

	/**
	 * initialize first column of probabilities, i.e. probability of each state given <s> state
	 * prior (aMatrix)			likelihood (bMatrix)
	 * P([each-state] | <s>) * 	P(firstWord | [each-state])
	 * start at position 1 instead of 0 b/c <s> is at position 0, which we're transitioning from.
	 * @param sortedPartsOfSpeech
	 * @param viterbi
	 * @param backpointer
	 */
	private void viterbiStartTransition(String firstWord, List<String>sortedPartsOfSpeech, TaggedWord[][]viterbi, int[][]backpointer) {
		for(int s = 0; s < sortedPartsOfSpeech.size(); s++) {
			String partOfSpeechStr = sortedPartsOfSpeech.get(s);
			TaggedWord tw = new TaggedWord();
			tw.setKnownWord(this.isKnownWord(firstWord));
			Double likelihood = this.getLikelihoodProbability(firstWord, partOfSpeechStr);
			Double prior = this.getPriorProbability(partOfSpeechStr, new String[] {startState.getPartOfSpeech()});
			tw.setProb(likelihood + prior);
			tw.setPos(this.getPartOfSpeech(partOfSpeechStr));
			tw.setWord(firstWord);
			viterbi[s][0] = tw;
			backpointer[s][0] = -999; // should never reference this, but it indicates a pointer to <s>
		}
	}

	/**
	 * @param sentenceOfWords
	 * @param sortedPartsOfSpeech
	 * @param viterbi
	 * @param backpointer
	 */
	private void viterbiSentenceTransitions(List<String>sentenceOfWords, List<String>sortedPartsOfSpeech, TaggedWord[][]viterbi, int[][]backpointer) {
		// for each word after the first one (since we calculated likelihood probs for first word above)
		for(int t = 1; t < sentenceOfWords.size(); t++) {
			String word = sentenceOfWords.get(t);
			//backpointer[0][t] = -1;
			// skip start state <s> by starting at index 1 - we don't need any transitions to/from it.
			for(int s = 0; s < sortedPartsOfSpeech.size(); s++) {
				String state = sortedPartsOfSpeech.get(s);
				Double likelihood = this.getLikelihoodProbability(word, state);
				Double maxPriorProb = (-1)*Double.MAX_VALUE;
				int priorCol = t-1;
				int backpointerRow = -1;
				// find the max value from the prior column transitioning to this column.
				// i.e. max (viterbi[s'][t-1] * P(s|s'))
				for(int sp = 1; sp < viterbi.length; sp++) {
					TaggedWord priorColState = viterbi[sp][priorCol];

					Double transToThisColProb = this.getPriorProbability(state, new String[] { priorColState.getPos().getPartOfSpeech() });
					transToThisColProb = transToThisColProb + priorColState.getProb();
					if(transToThisColProb > maxPriorProb) {
						maxPriorProb = transToThisColProb;
						backpointerRow = sp;
					}
				}
				double maxProbability = maxPriorProb + likelihood;
				viterbi[s][t] = new TaggedWord(maxProbability, word, this.getPartOfSpeech(state), this.isKnownWord(word));
				backpointer[s][t] = backpointerRow;
			}
		}
	}

	/**
	 * @param sortedPartsOfSpeech
	 * @param sentenceOfWords
	 * @param viterbi
	 * @param backpointer
	 * @return
	 */
	private int viterbiFinalTransition(List<String>sentenceOfWords, List<String>sortedPartsOfSpeech, TaggedWord[][]viterbi, int[][]backpointer) {
		Double maxEndStateProb = (-1)*Double.MAX_VALUE;
		int bpRowFromFinalState = -1;
		// transition from last state to "end" state
		for(int s = 0; s < sortedPartsOfSpeech.size(); s++) {
			TaggedWord tmpVit = viterbi[s][sentenceOfWords.size()-1];
			double tmp = tmpVit.getProb();
			if(tmp > maxEndStateProb.doubleValue()) {
				maxEndStateProb = tmp;
				bpRowFromFinalState = s;
			}
		}
		return bpRowFromFinalState;
	}

	/**
	 * Traces the backpointer from the given startRow to get the "correct" tagged word sequence.
	 * @param startRow
	 * @param viterbi
	 * @param backpointer
	 * @return
	 */
	private List<TaggedWord> traceBackpointerForMaxProbSentence(int startRow, TaggedWord[][]viterbi, int[][]backpointer) {
		List<TaggedWord> tagged = new ArrayList<TaggedWord>();
		int currBpRow = startRow;
		//solution.add(viterbi[currBpRow][viterbi[0].length-1]);
		for(int col = backpointer[0].length-1; col > 0; col--) {
			tagged.add(viterbi[currBpRow][col]);
			currBpRow = backpointer[currBpRow][col];
		}
		tagged.add(viterbi[currBpRow][0]);
		Collections.reverse(tagged);
		return tagged;
	}

	/**
	 * Implementation of Viterbi algorithm. Equivalent to the pseudo code on page 147 of the book.
	 * @param sentenceOfWords
	 * @return
	 */
	public List<TaggedWord> tagPartsOfSpeech(List<String> sentenceOfWords) {
		List<String>sortedPartsOfSpeech = sortSet(this.allPartsOfSpeech.keySet());

		TaggedWord[][] viterbi = new TaggedWord[this.allPartsOfSpeech.size()][sentenceOfWords.size()];
		int[][] backpointer = new int[this.allPartsOfSpeech.size()][sentenceOfWords.size()];
		for(int i = 0; i < backpointer.length; i++)
			for(int j = 0; j < backpointer[i].length; j++)
				backpointer[i][j] = -1;

		// initialization step
		viterbiStartTransition(sentenceOfWords.get(0), sortedPartsOfSpeech, viterbi, backpointer);

		// recursion step
		viterbiSentenceTransitions(sentenceOfWords, sortedPartsOfSpeech, viterbi, backpointer);

		// termination step
		int bpRowFromFinalState = viterbiFinalTransition(sentenceOfWords, sortedPartsOfSpeech, viterbi, backpointer);

		if(log.isInfoEnabled()) {
			dumpViterbiMatrix(sortedPartsOfSpeech, viterbi);
			dumpBackpointers(backpointer);
		}

		return traceBackpointerForMaxProbSentence(bpRowFromFinalState, viterbi, backpointer);
	}

	/**
	 * @param backpointer
	 */
	private void dumpBackpointers(int backpointer[][]) {
		String rs = "";
		for(int row = 0; row < backpointer.length; row++) {
			for(int col = 0; col < backpointer[row].length; col++) {
				if(col > 0) rs += "	";
				rs += "bp[" + row + "][" + col + "] = " + backpointer[row][col];
			}
			rs += "\n";
		}
		log.info("\n" + rs);
	}

	/**
	 * @param sortedPartsOfSpeech
	 * @param viterbi
	 */
	private void dumpViterbiMatrix(List<String> sortedPartsOfSpeech, TaggedWord[][] viterbi) {
		String rs = "";
		for(int row = 0; row < viterbi.length; row++) {
			for(int col = 0; col < viterbi[0].length; col++) {
				TaggedWord cell = viterbi[row][col];
				if(col > 0) rs += "	";
				rs += "(" + row + "," + col + ")-" + cell;
			}
			rs += "\n";
		}
		log.info("\n" + rs);
	}

	/**
	 * Returns the prior probability of a hypthesis given some evidence.
	 * P(hypothesis | given), e.g. P(VB | TO) or P(VB | NN TO)
	 * @param hypothesis
	 * @param given
	 * @return
	 */
	public Double getPriorProbability(String hypothesis, String[] given) {
		return this.getPriorProbability(hypothesis, given, TaggerGlobals.USE_DEFAULT_PROBABILITIES);
	}

	/**
	 * @param hypothesis
	 * @param given
	 * @param useDefaultIfNoneExist
	 * @return
	 */
	public Double getPriorProbability(String hypothesis, String[] given, boolean useDefaultIfNoneExist) {
		String nGramHashCode = NGram.getHashCodeFromStringArray(this.getNGramArrayFromHypothesisAndGiven(hypothesis, given));
		NGram ng = this.getNGram(nGramHashCode);
		//this.getNGram(new String[] { "#", "<DEFAULT>" });
		//this.getPartOfSpeech("$");
		if(ng == null) {
			if(useDefaultIfNoneExist) {
				ng = this.getNGram(this.getNGramArrayFromHypothesisAndGiven(TaggerGlobals.DEFAULT_TAG, given));
			}
			// if ngram is still null, throw an exception.
			if(ng == null)
				throw new IllegalArgumentException("The NGram " + nGramHashCode + " doesn't exist!");
		}
		return ng.getProbability();
	}

	/**
	 * @param hypothesis
	 * @param given
	 * @return
	 */
	private String[] getNGramArrayFromHypothesisAndGiven(String hypothesis, String[] given) {
		String[] nGram = new String[given.length+1];
		for(int i = 0; i < given.length; i++)
			nGram[i] = given[i];
		nGram[nGram.length-1] = hypothesis;
		return nGram;
	}

	public double priorProbability(int row, int col) {
		return 0.0;
	}

	/**
	 * @param toSort
	 * @return
	 */
	private List<String> sortSet(Set<String> toSort) {
		List<String>toSortList = new ArrayList<String>();
		for(String key : toSort) {
			toSortList.add(key);
		}
		Collections.sort(toSortList);
		return toSortList;
	}

	/**
	 * @return the startState
	 */
	public PartOfSpeech getStartState() {
		return startState;
	}

	/**
	 * @param startState the startState to set
	 */
	public void setStartState(PartOfSpeech startState) {
		this.startState = startState;
	}
}
