package edu.umbc.nlp.postagger;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
	private PartOfSpeech endState = new PartOfSpeech("<e>");
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
	 * { "my", "name", "is" }.
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
	public void addNGram(String[] partsOfSpeech, double probability) {
		String ng = NGram.getHashCodeFromStringArray(partsOfSpeech);
		if(this.allNGrams.get(ng) != null) {
			throw new IllegalArgumentException("Trying to add NGram " + ng + " but it already exists!");
		}
		else {
			PartOfSpeech[] pos = new PartOfSpeech[partsOfSpeech.length];
			for(int i = 0; i < pos.length; i++) {
				pos[i] = this.getPartOfSpeech(partsOfSpeech[i]);
			}
			NGram ngram = new NGram(new Probability(probability), pos);
			this.allNGrams.put(ng, ngram);
		}
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
	public Probability getLikelihoodProbability(String word, String partOfSpeech) {
		PartOfSpeech pos = this.allPartsOfSpeech.get(partOfSpeech);
		if(pos == null)
			return new Probability(TaggerGlobals.DEFAULT_PROBABILITY);

		Probability probability = pos.getProbabilityOfWord(word);
		if(probability == null) {
			return new Probability(TaggerGlobals.DEFAULT_PROBABILITY);
		}
		return probability;
	}

	/**
	 * Implementation of Viterbi algorithm. Equivalent to the pseudo code on page 147 of the book.
	 * @param sentenceOfWords
	 * @return
	 */
	public List<TaggedWord> tagPartsOfSpeech(List<String> sentenceOfWords) {
		List<TaggedWord> tagged = new ArrayList<TaggedWord>();
		List<String>sortedPartsOfSpeech = sortSet(this.allPartsOfSpeech.keySet());

		TaggedWord[][] viterbi = new TaggedWord[this.allPartsOfSpeech.size()][sentenceOfWords.size()];
		int[][] backpointer = new int[this.allPartsOfSpeech.size()][sentenceOfWords.size()];
		for(int i = 0; i < backpointer.length; i++)
			for(int j = 0; j < backpointer[i].length; j++)
				backpointer[i][j] = -1;
		String firstWord = sentenceOfWords.get(0);
		String startStateStr = sortedPartsOfSpeech.get(0);

		// initialize first column of probabilities, i.e. probability of each state given <s> state
		// prior (aMatrix)			likelihood (bMatrix)
		// P([each-state] | <s>) * 	P(firstWord | [each-state])
		// start at position 1 instead of 0 b/c <s> is at position 0, which we're transitioning from.
		for(int s = 1; s < sortedPartsOfSpeech.size(); s++) {
			String partOfSpeechStr = sortedPartsOfSpeech.get(s);
			//log.info("Part of speech: " + partOfSpeechStr);
			TaggedWord tw = new TaggedWord();
			Probability likelihood = this.getLikelihoodProbability(firstWord, partOfSpeechStr);
			//log.info("Likelihood: P(" + firstWord + "|" + partOfSpeechStr + ") = " + likelihood.doubleValue());

			Probability prior = this.getPriorProbability(partOfSpeechStr, new String[] {startStateStr});
			//log.info("Prior: P(" + partOfSpeechStr + "|" + startStateStr + ") = " + prior.doubleValue());
			tw.setProb(new Probability(likelihood.doubleValue() * prior.doubleValue()));
			tw.setPos(this.getPartOfSpeech(partOfSpeechStr));
			tw.setWord(firstWord);
			viterbi[s][0] = tw;
			backpointer[s][0] = -999; // should never reference this, but it indicates a pointer to <s>
		}

		// for each word after the first one (since we calculated likelihood probs for first word above)
		for(int t = 1; t < sentenceOfWords.size(); t++) {
			String word = sentenceOfWords.get(t);
			//backpointer[0][t] = -1;
			// skip start state <s> by starting at index 1 - we don't need any transitions to/from it.
			for(int s = 1; s < sortedPartsOfSpeech.size(); s++) {
				String state = sortedPartsOfSpeech.get(s);
				Probability likelihood = this.getLikelihoodProbability(word, state);
				Probability maxPriorProb = new Probability(-1.0);
				int priorCol = t-1;
				int backpointerRow = -1;
				// find the max value from the prior column transitioning to this column.
				// i.e. max (viterbi[s'][t-1] * P(s|s'))
				for(int sp = 1; sp < viterbi.length; sp++) {
					TaggedWord tmpVit = viterbi[sp][priorCol];
					//log.info("Transitioning from " + tmpVit.getPos().getPartOfSpeech() + " to " + state);
					//log.info("viterbi[" + sp + "][" + priorCol + "]: " + tmpVit);
					NGram transToThisColNgram = this.getNGram(state, new String[] { tmpVit.getPos().getPartOfSpeech() } );
					//log.info(transToThisColNgram);
					Probability tmpProb = transToThisColNgram.getProbability();
					tmpProb = new Probability(tmpProb.doubleValue() * tmpVit.getProb().doubleValue());
					if(tmpProb.doubleValue() > maxPriorProb.doubleValue()) {
						maxPriorProb = tmpProb;
						backpointerRow = sp;
					}
				}
				Probability maxProbability = new Probability(maxPriorProb.doubleValue() * likelihood.doubleValue());
				viterbi[s][t] = new TaggedWord(maxProbability, word, this.getPartOfSpeech(state));
				backpointer[s][t] = backpointerRow;
				log.info("max1-N( viterbi[s'][" + s + "] * P(" + state + "|" + "s') ) * P(" + word + "|" + state + ") = " + viterbi[s][t].getProb().doubleValue());
				//log.info("Max Transition Probability P(s|s') == " + viterbi[s][t] + "\n\n");
			}
		}

		dumpViterbiMatrix(sortedPartsOfSpeech, viterbi);
		dumpBackpointers(backpointer);

		Probability maxEndStateProb = new Probability(-1.0);
		int bpRowFromFinalState = -1;
		// transition from last state to "end" state
		for(int s = 1; s < sortedPartsOfSpeech.size(); s++) {
			TaggedWord tmpVit = viterbi[s][sentenceOfWords.size()-1];
			Probability prior = this.getPriorProbability(this.endState.getPartOfSpeech(), new String[] { tmpVit.getPos().getPartOfSpeech() }, 0.0);
			double tmp = prior.doubleValue() * tmpVit.getProb().doubleValue();
			if(tmp > maxEndStateProb.doubleValue()) {
				maxEndStateProb = new Probability(tmp);
				bpRowFromFinalState = s;
			}
		}

		log.info("Backpointer to last row in viterbi: " + bpRowFromFinalState);
		int currBpRow = bpRowFromFinalState;
		//solution.add(viterbi[currBpRow][viterbi[0].length-1]);
		for(int col = backpointer[0].length-1; col > 0; col--) {
			tagged.add(viterbi[currBpRow][col]);
			currBpRow = backpointer[currBpRow][col];
		}
		tagged.add(viterbi[currBpRow][0]);
		Collections.reverse(tagged);
		log.info(tagged);
		return tagged;
	}

	/**
	 * @param backpointer
	 */
	private void dumpBackpointers(int backpointer[][]) {
		for(int row = 0; row < backpointer.length; row++) {
			String rs = "";
			for(int col = 0; col < backpointer[row].length; col++) {
				rs += "	" + "bp[" + row + "][" + col + "] = " + backpointer[row][col];
			}
			rs += "";
			log.info(rs);
		}
	}

	/**
	 * @param sortedPartsOfSpeech
	 * @param viterbi
	 */
	private void dumpViterbiMatrix(List<String> sortedPartsOfSpeech, TaggedWord[][] viterbi) {
		for(TaggedWord[] row : viterbi) {
			String rs = "";
			for(TaggedWord cell : row) {
				rs += "	" + cell;
			}
			rs += "";
			log.info(rs);
		}
	}

	/**
	 * Returns the prior probability of a hypthesis given some evidence.
	 * P(hypothesis | given), e.g. P(VB | TO) or P(VB | NN TO)
	 * @param hypothesis
	 * @param given
	 * @return
	 */
	public Probability getPriorProbability(String hypothesis, String[] given) {
		return this.getPriorProbability(hypothesis, given, false);
	}

	/**
	 * @param hypothesis
	 * @param given
	 * @param defaultProbability
	 * @return
	 */
	public Probability getPriorProbability(String hypothesis, String[] given, double defaultProbability) {
		try {
			return this.getPriorProbability(hypothesis, given, false);
		}
		catch(IllegalArgumentException e) {
			return new Probability(defaultProbability);
		}
	}

	/**
	 * @param hypothesis
	 * @param given
	 * @param useDefaultIfNoneExist
	 * @return
	 */
	public Probability getPriorProbability(String hypothesis, String[] given, boolean useDefaultIfNoneExist) {
		NGram ng = this.getNGram(this.getNGramArrayFromHypothesisAndGiven(hypothesis, given));
		if(ng == null) {
			if(useDefaultIfNoneExist)
				return new Probability(TaggerGlobals.DEFAULT_PROBABILITY);
			throw new IllegalArgumentException("The NGram " + ng + " doesn't exist!");
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
	 * Entry point to the program.
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String bMatrixFilename = "/tag-word-prob-dummy-data.txt";
		String aMatrixFilename = "/bigram-dummy-data.txt";
		log.info("Using file " + aMatrixFilename + " to get a-matrix ngram probabilities.");
		log.info("Using file " + bMatrixFilename + " to get b-matrix observation probabilities.");
		File aMatrixFile = new File(Tagger.class.getResource(aMatrixFilename).getFile());
		File bMatrixFile = new File(Tagger.class.getResource(bMatrixFilename).getFile());
		Tagger partsOfSpeech = TaggerHelper.readMatrices(aMatrixFile, bMatrixFile);
		String testWord = "to";
		String testPos = "TO";
		Probability prob = partsOfSpeech.getLikelihoodProbability(testWord, testPos);
		log.info("P(" + testWord + "|" + testPos + ") = " + prob.doubleValue());
	}
}
