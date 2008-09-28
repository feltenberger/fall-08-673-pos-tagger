package edu.umbc.nlp.postagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
	 * @param observationsFile
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Tagger readMatrices(File nGramsFile, File observationsFile) throws IOException {
		log.debug("Reading ngrams file " + nGramsFile.getAbsolutePath());
		log.debug("Reading observations file " + observationsFile.getAbsolutePath());
		Tagger tagger = new Tagger();
		// b-matrix
		parseObservationProbabilities(tagger, observationsFile);
		// a-matrix
		parseNGrams(tagger, nGramsFile);
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
	public static void parseNGrams(Tagger tagger, File aMatrix) throws IOException {
		List<String>lines = FileUtils.readLines(aMatrix);
		int i = 0;
		for(String line : lines) {
			if(i == 0) {
				String lp[] = line.split(" ");
				TaggerGlobals.N_GRAM_SIZE = Integer.parseInt(lp[lp.length-1]);
			}
			else {
				String[] lineParts = line.split(TaggerGlobals.FILE_PARSING_FIELD_DELIMETER);
				addNGram(tagger, lineParts);
			}
			i++; //if(i > 100) break;
		}
	}

	/**
	 * @param tagger
	 * @param lineParts
	 */
	private static void addNGram(Tagger tagger, String[] lineParts) {
		String prev_tag = lineParts[0];
		String tag = lineParts[1];
		String[] nGramStrings = new String[] { prev_tag, tag };
		String probabilityString = lineParts[lineParts.length-1];
		double probability = Math.log(Double.parseDouble(probabilityString));

		NGram nGram = tagger.getNGram(nGramStrings);
		if(nGram == null) {
			tagger.addNGram(nGramStrings, probability);
			nGram = tagger.getNGram(nGramStrings);
		}
	}

	/**
	 * @param tagger
	 * @param lineParts
	 */
	private static void addObservationProb(Tagger tagger, String[] lineParts) {
		String tag = lineParts[0];
		String word = lineParts[1];
		String probabilityString = lineParts[2];

		PartOfSpeech partOfSpeech = tagger.getPartOfSpeech(tag);
		if(partOfSpeech == null)
			partOfSpeech = new PartOfSpeech(tag);

		Double probability = Math.log(Double.parseDouble(probabilityString));
		//Double probability = Double.parseDouble(probabilityString);
		partOfSpeech.addWordAndProbabilityToBMatrix(word, probability);
		tagger.addPartOfSpeech(partOfSpeech);
	}

	/**
	 * @param tagger
	 * @param theFile
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public static void parseObservationProbabilities(Tagger tagger, File theFile) throws IOException {
		List<String>lines = FileUtils.readLines(theFile);
		for(String line : lines) {
			String[] lineParts = parseWordProbabilityLineFromFile(line);
			addObservationProb(tagger, lineParts);
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

	/**
	 * Parses a file in WSJ standard notation and converts it to a Sentence structure.	 *
	 * @param String filename - the name of the WSJ format file	 *
	 * @return List<Sentence> - the sentences of the corpus in a list.
	 */
	public List<Sentence> parseCorpus(String corpusFile)throws IOException
	{
		List<Sentence> sentences = new ArrayList<Sentence>();

		File file = new File(TaggerHelper.class.getResource(corpusFile).getFile());
		List<String> lines = FileUtils.readLines(file);

		Sentence s = new Sentence();

		for(String line : lines)
		{
			String test = line.trim();
			if (line.startsWith("="))
			{
				if (s.getSentence().size() > 0) //if we actually have a sentence
				{
					sentences.add(s); // add the current sentence
				}

				s = new Sentence(); //create a new sentence
			}
			else
			{
				s = corpus_line_parse(s, line);
			}
		}

		return sentences;
	}

	private Sentence corpus_line_parse(Sentence sentence, String line)
	{
		String[] splitline = line.split(" ");
		for (int i = 0; i < splitline.length; i++)
		{
			String[] wordtag = corpus_item_split(splitline[i]);
			if (wordtag.length == 2)
			{
				String word = wordtag[0].trim();
				String tag = wordtag[1].trim();
				sentence.addWordAndTag(word, tag);
			}
		}
		return sentence;
	}

	private String[] corpus_item_split(String item)
	{
		if (item.contains("\\/"))
		{
			//  3\/4/CD
			String[] return_item = new String[2];
			return_item[0] = item.substring(0, item.lastIndexOf('/'));
			return_item[1] = item.substring(item.lastIndexOf('/')+1);

			return return_item;

		}
		return item.split("/");
	}

	public void splitCorpus(String training_filename, String testing_filename, List<Sentence> sentences, double percentage) throws IOException
	{
		File training = new File("/home/niels/workspace2/corpus/"+training_filename);
		//File testing = new File(TaggerHelper.class.getResource(testing_filename).getFile());
		File testing = new File("/home/niels/workspace2/corpus/"+testing_filename);
		Random generator = new Random();

		List<String> train = new ArrayList<String>();
		List<String> test = new ArrayList<String>();

		for (int i = 0; i < sentences.size(); i++)
		{
			List<String> words = sentences.get(i).getSentence();
			List<String> tags = sentences.get(i).getTags();
			String to_write = "";
			for (int j = 0; j < words.size(); j++)
			{
				to_write += words.get(j) + "/" + tags.get(j) + " ";
			}
			to_write = to_write.trim();
			double r = generator.nextDouble();

			if (r < percentage)
			{
				train.add(to_write);
			}
			else
			{
				test.add(to_write);
			}
		}
		FileUtils.writeLines(training, train, "\n");
		FileUtils.writeLines(testing, test, "\n");
	}

	private Sentence trainingCorpus_line_parse(String line)
	{
		Sentence sentence = new Sentence();
		String[] splitline = line.split(" ");
		for (int i = 0; i < splitline.length; i++)
		{
			String[] wordtag = corpus_item_split(splitline[i]);
			if (wordtag.length == 2)
			{
				String word = wordtag[0].trim();
				String tag = wordtag[1].trim();
				sentence.addWordAndTag(word, tag);
			}
		}
		return sentence;
	}

	/**
	 * Parses a file in containing converted WSJ notation and converts it to a Sentence structure. *
	 * @param String filename - the name of the file	 *
	 * @return List<Sentence> - the sentences of the corpus in a list.
	 */
	public List<Sentence> parseEvalCorpus(String evaluationFile)throws IOException
	{
		List<Sentence> sentences = new ArrayList<Sentence>();

		File file = new File(TaggerHelper.class.getResource(evaluationFile).getFile());
		List<String> lines = FileUtils.readLines(file);

		for(String line : lines)
		{
			Sentence s = trainingCorpus_line_parse(line);
			if (s.getSentence().size() > 0) //if we actually have a sentence
			{
				sentences.add(s); // add the current sentence
			}
		}
		return sentences;
	}

}
