package edu.umbc.nlp.postagger;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * @author dave
 *
 */
public class Tagger {
	private static final Logger log = Logger.getLogger(Tagger.class);

	/**
	 * Entry point to the program.
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String filename = "/tag-word-prob-dummy-data.txt";	
		if(args.length > 2)
			filename = args[1];
		log.info("Using file " + filename + " to get b-matrix observation probabilities.");
		File file = new File(Tagger.class.getResource(filename).getFile());
		PartsOfSpeechHelper partsOfSpeech = TaggerHelper.readProbabilityOfWordGivenTag(file);
		
	}

}
