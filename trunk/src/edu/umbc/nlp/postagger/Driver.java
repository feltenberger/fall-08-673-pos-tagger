package edu.umbc.nlp.postagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class Driver {

	private static final Logger log = Logger.getLogger(TaggerTest.class);

	private static String aMatrixFilename = "/prev_tag_prob.dat";
	private static String bMatrixFilename = "/tag_word_prob.dat";
	private static String startMatrixFilename = "/start_tags_prob.txt";

	private static String testSetFilename = "/wsj/evaluation.pos";
	private static String trainingSetFilename = "/wsj/training.pos";
	private static String corpusFilename = "/wsj/combined.pos";
	private static File bMatrixFile = null;
	private static File aMatrixFile = null;


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


	public static void main(String[] args) throws Exception
	{
		//create a new tagger
		BaselineTagger myBaselineTagger = new BaselineTagger(bMatrixFilename);
		HMMTagger hmmTagger = new HMMTagger(bMatrixFilename, aMatrixFilename, startMatrixFilename);

		TaggerHelper helper = new TaggerHelper();
		List<Sentence> sentences = helper.parseEvalCorpus(testSetFilename);
		//THIS IS TO SPLIT THE ORIGINAL WSJ CORPUS
		//List<Sentence> sentences = helper.parseCorpus(corpusFilename);
		//helper.splitCorpus(trainingFilename, testFilename, sentences);

		bMatrixFile = new File(Driver.class.getResource(bMatrixFilename).getFile());
		aMatrixFile = new File(Driver.class.getResource(aMatrixFilename).getFile());

		Tagger tagger = TaggerHelper.readMatrices(aMatrixFile, bMatrixFile);

		List<TaggedSentence> baselineTaggedSentences = new ArrayList<TaggedSentence>();
		List<TaggedSentence> hmmTaggedSentences = new ArrayList<TaggedSentence>();
		List<TaggedSentence> hmmDaveTaggedSentences = new ArrayList<TaggedSentence>();

		long start;
		long end;
		int numIterations = 0;
		for (Sentence s : sentences)
		{
			print_control(s);

			//THIS EVENTUALLY NEED TO BE THE tagSentenceImproved METHOD
			start = System.currentTimeMillis();
			TaggedSentence baselineTS = myBaselineTagger.tagSentence(s);
			end = System.currentTimeMillis();
			baselineTaggedSentences.add( baselineTS );
			print_stats(baselineTS, "BASELINE", (end-start));

			start = System.currentTimeMillis();
			TaggedSentence hmmTS = hmmTagger.tagSentence(s);
			end = System.currentTimeMillis();
			hmmTaggedSentences.add( hmmTS );
			print_stats(hmmTS, "NIELS", (end-start));

			start = System.currentTimeMillis();
			List<TaggedWord> tw = tagger.tagPartsOfSpeech(s.getSentence());
			end = System.currentTimeMillis();
			List<String> tags  = new ArrayList<String>();
			for (TaggedWord word : tw)
			{
				PartOfSpeech pos = word.getPos();
				tags.add(pos.toString());
			}
			TaggedSentence taggerTS = new TaggedSentence(s.getSentence(), s.getTags(), tags, hmmTS.getKnownWordFlag());
			hmmDaveTaggedSentences.add(taggerTS);
			print_stats(taggerTS, "DAVE", (end-start));

			numIterations++;
			if(numIterations == 1000) break;
		}

		Evaluator myBaselineEvaluator = new Evaluator(baselineTaggedSentences);
		System.out.println("------------------------------------------");
		System.out.println("BaseLine:");
		System.out.println("Word Error Rate = " + myBaselineEvaluator.getWordErrorRate());
		System.out.println("Known Word WER = " + myBaselineEvaluator.getKnownWord_WordErrorRate());
		System.out.println("Unknown Word WER = " + myBaselineEvaluator.getUnknownWord_WordErrorRate());

		Evaluator myHMMEvaluator = new Evaluator(hmmTaggedSentences);
		System.out.println("------------------------------------------");
		System.out.println("Niels:");
		System.out.println("Word Error Rate = " + myHMMEvaluator.getWordErrorRate());
		System.out.println("Known Word WER = " + myHMMEvaluator.getKnownWord_WordErrorRate());
		System.out.println("Unknown Word WER = " + myHMMEvaluator.getUnknownWord_WordErrorRate());

		Evaluator myTaggerEvaluator = new Evaluator(hmmDaveTaggedSentences);
		System.out.println("------------------------------------------");
		System.out.println("Dave:");
		System.out.println("Word Error Rate = " + myTaggerEvaluator.getWordErrorRate());
		System.out.println("Known Word WER = " + myTaggerEvaluator.getKnownWord_WordErrorRate());
		System.out.println("Unknown Word WER = " + myTaggerEvaluator.getUnknownWord_WordErrorRate());
	}

}
