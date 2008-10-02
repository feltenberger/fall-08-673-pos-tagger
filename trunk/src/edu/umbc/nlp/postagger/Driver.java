package edu.umbc.nlp.postagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class Driver {

	private static final Logger log = Logger.getLogger(Driver.class);

	//private String aMatrixFilename;
	//private String bMatrixFilename;
	//private String outFile;
	//private static String startMatrixFilename = "/start_tags_prob.txt";

	//private String testSetFilename;
	//private static String trainingSetFilename = "/wsj/training.pos";
	//private static String corpusFilename = "/wsj/combined.pos";
	//private File bMatrixFile = null;
	//private File aMatrixFile = null;


	/**
	 * @param args
	 */
	private static void print_control(Sentence new_s)
	{
		System.out.println("\n------------------------------------------");
		System.out.println("Control:");
		for (int i = 0; i < new_s.getSentence().size(); i++)
		{
			System.out.print("" + new_s.getSentence().get(i)
					+ "/" + new_s.getTags().get(i) + " ");
		}
		System.out.println();
	}


	private static void print_stats(TaggedSentence new_s, String info, long timeInMillis)
	{
		System.out.println(info + " Tagger Output: " + "(" + timeInMillis + "ms)");
		for (int i = 0; i < new_s.getSentence().size(); i++)
		{
			System.out.print(new_s.getSentence().get(i)
					+ "/" + new_s.getHypothesisedTags().get(i) + " ");
		}
		System.out.println();
	}

	private static List<String> print_results(String title, Evaluator eval, Boolean printing)
	{
		List<String> output = new ArrayList<String>();
		if (printing)
		{
		System.out.println("------------------------------------------");
		System.out.println(title + ":");
		System.out.println("Word Error Rate = " + eval.getWordErrorRate());
		System.out.println("Known Word WER = " + eval.getKnownWord_WordErrorRate());
		System.out.println("Unknown Word WER = " + eval.getUnknownWord_WordErrorRate());
		}
		output.add("------------------------------------------");
		output.add(title + ":");
		output.add("Word Error Rate = " + eval.getWordErrorRate());
		output.add("Known Word WER = " + eval.getKnownWord_WordErrorRate());
		output.add("Unknown Word WER = " + eval.getUnknownWord_WordErrorRate());
		return output;
	}

	public static void main(String[] args) throws Exception
	{
		//THIS IS TO SPLIT THE ORIGINAL WSJ CORPUS into Training and Test sets
		/*TaggerHelper helper = new TaggerHelper();
		System.out.println("Analyzing WSJ Corpus.");
		List<Sentence> sentences = helper.parseCorpus(corpusFilename);
		for (int i = 1; i <= 10; i ++)
		{
			System.out.println("Processing: " + i + " of 10" );
			helper.splitCorpus("80training" + i+".pos", "80evaluation" + i+".pos", sentences, 0.8);
		}*/

		for (int i = 1; i <=10; i++)
		{

		//should be modified
		String aMatrixFilename = "/90prev_tag_prob"+ i + ".dat";
		//should be modified
		String bMatrixFilename = "/90tag_word_prob"+ i +".dat";
		//should be modified
		//String testSetFilename =  "/home/niels/workspace2/corpus/90evaluation" + i + ".pos";
		String testSetFilename = "/resources/90evaluation" + i + ".pos";
		//should be modified
		//String outFile = "/home/niels/workspace2/corpus/output/90results"+ i +".csv";
		String outFile = "./dist/output/90results"+ i +".csv";

		Boolean printing = true; //print screen output

		File oFile = new File(outFile);

		//create the taggers
		BaselineTagger myBaselineTagger = new BaselineTagger(bMatrixFilename);
		HMMTagger hmmTagger = new HMMTagger(bMatrixFilename, aMatrixFilename);
		File bMatrixFile = new File(Driver.class.getResource(bMatrixFilename).getFile());
		File aMatrixFile = new File(Driver.class.getResource(aMatrixFilename).getFile());
		Tagger tagger = TaggerHelper.readMatrices(aMatrixFile, bMatrixFile);

		//The arrays that will contain the tagged sentences for each tagger
		List<TaggedSentence> baselineTaggedSentences = new ArrayList<TaggedSentence>();
		List<TaggedSentence> baselineImprovedTaggedSentences = new ArrayList<TaggedSentence>();
		List<TaggedSentence> hmmTaggedSentences = new ArrayList<TaggedSentence>();
		List<TaggedSentence> hmmDaveTaggedSentences = new ArrayList<TaggedSentence>();

		//read the test set and convert it to a list of sentences
		TaggerHelper helper = new TaggerHelper();
		List<Sentence> sentences = helper.parseEvalCorpus(testSetFilename);

		List<String> output = new ArrayList<String>();

		long start;
		long end;
		int numIterations = 0;
		int total = sentences.size();
		for (Sentence s : sentences)
		{
			System.out.println("#############################################");
			System.out.println("Processing: " + numIterations +"/" + total);
			if (printing) print_control(s);

			//The plain baseline
			start = System.currentTimeMillis();
			TaggedSentence baselineTS = myBaselineTagger.tagSentence(s);
			end = System.currentTimeMillis();
			baselineTaggedSentences.add( baselineTS );
			print_stats(baselineTS, "BASELINE", (end-start));

			//The improved baseline
			start = System.currentTimeMillis();
			TaggedSentence baselineImprovedTS = myBaselineTagger.tagSentenceImproved(s);
			baselineImprovedTaggedSentences.add( baselineImprovedTS );
			end = System.currentTimeMillis();
			if (printing) print_stats(baselineImprovedTS, "BASELINE IMPROVED", (end-start));

			//Niels' Bi-gram HMM Tagger
			start = System.currentTimeMillis();
			TaggedSentence hmmTS = hmmTagger.tagSentence(s);
			end = System.currentTimeMillis();
			hmmTaggedSentences.add( hmmTS );
			if (printing) print_stats(hmmTS, "NIELS", (end-start));

			//Dave's Bi-gram HMM Tagger
			start = System.currentTimeMillis();
			List<TaggedWord> tw = tagger.tagPartsOfSpeech(s.getSentence());
			end = System.currentTimeMillis();
			List<String> tags  = new ArrayList<String>();
			List<Boolean> known_word  = new ArrayList<Boolean>();
			for (TaggedWord word : tw)
			{
				PartOfSpeech pos = word.getPos();
				tags.add(pos.toString());
				known_word.add(word.isKnownWord());
			}
			//currently getting an incorrect known_word list (i.e. from the baseline)
			TaggedSentence taggerTS = new TaggedSentence(s.getSentence(), s.getTags(), tags, known_word);
			hmmDaveTaggedSentences.add(taggerTS);
			if (printing) print_stats(taggerTS, "DAVE", (end-start));

			numIterations++;
			//if(numIterations == 1000) break;
		}

		Evaluator myBaselineEvaluator = new Evaluator(baselineTaggedSentences);
		output.addAll(print_results("Baseline", myBaselineEvaluator, printing));
		output.addAll(myBaselineEvaluator.printConfusionMatrix());

		Evaluator myBaselineImprovedEvaluator = new Evaluator(baselineImprovedTaggedSentences);
		//myBaselineTagger.printUnknownWordList();
		output.addAll(print_results("Baseline Improved", myBaselineImprovedEvaluator, printing));
		output.addAll(myBaselineImprovedEvaluator.printConfusionMatrix());

		Evaluator myHMMEvaluator = new Evaluator(hmmTaggedSentences);
		output.addAll(print_results("Niels' Bi-gram Tagger", myHMMEvaluator, printing));
		output.addAll(myHMMEvaluator.printConfusionMatrix());

		Evaluator myTaggerEvaluator = new Evaluator(hmmDaveTaggedSentences);
		output.addAll(print_results("Dave's Bi-gram Tagger", myTaggerEvaluator, printing));
		output.addAll(myTaggerEvaluator.printConfusionMatrix());

		FileUtils.writeLines(oFile, output, "\n");
		}

	}

}
