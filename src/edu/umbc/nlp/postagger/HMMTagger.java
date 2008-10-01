package edu.umbc.nlp.postagger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import java.util.Random;

public class HMMTagger
{	
	private HashMap<String, Double> emission_probs = new HashMap<String, Double>();  //P(word|tag)
	private HashMap<String, Double> transition_probs = new HashMap<String, Double>(); //P(tag|prev_tag)
	
	private final double neg_infinity = -9999999999999999.9; 
	
	private HashMap<String, String> tag_list = new HashMap<String, String>();
	
	private static String DEFAULT = "<DEFAULT>";
	private static String STARTSYMBOL = "<s>";
	
	private Boolean is_word_known;
		
	public HMMTagger(String emission_filename, String transition_filename) throws IOException
	{		
		readEmissionFile(emission_filename);
		readTransitionFile(transition_filename);	
	}
	
	private double convert_to_ln(double n)
	{
		return Math.log(n);
	}
	
	private void readStartFile(String filename) throws IOException
	{
		File file = new File(TaggerHelper.class.getResource(filename).getFile());
		List<String> lines = FileUtils.readLines(file);
			
		for(String line : lines)
		{
			String[] elements = line.split("\t");
			if (elements.length == 3)
			{
				//String start_symbol = elements[0].trim();
				String tag = elements[1].trim();
				String probability = elements[2].trim();
				transition_probs.put(tag +"|" + STARTSYMBOL, convert_to_ln(Double.parseDouble(probability)));
			}
			else
			{
				System.out.println(line);
			}
		}
	}
	
	private void readEmissionFile(String filename) throws IOException
	{
		File file = new File(TaggerHelper.class.getResource(filename).getFile());
		List<String> lines = FileUtils.readLines(file);
			
		for(String line : lines)
		{
			String[] elements = line.split("\t");
			if (elements.length == 3)
			{
				String tag = elements[0].trim();
				String word = elements[1].trim();
				String probability = elements[2].trim();
				emission_probs.put(word + "|" + tag, convert_to_ln(Double.parseDouble(probability)));
			}
			else
			{
				System.out.println(line);
			}
		}
	}
	
	private void readTransitionFile(String filename) throws IOException
	{
		File file = new File(TaggerHelper.class.getResource(filename).getFile());
		List<String> lines = FileUtils.readLines(file);
			
		for(String line : lines)
		{
			String[] elements = line.split("\t");
			if (elements.length == 3)
			{				
				String prev_tag = elements[0].trim();
				String tag = elements[1].trim();
				String probability = elements[2].trim();
					
				
				transition_probs.put(tag + "|" + prev_tag, convert_to_ln(Double.parseDouble(probability)));
				
				if (!tag_list.containsKey(tag))
				{
					if (tag.compareToIgnoreCase(DEFAULT) != 0)
					{						
						tag_list.put(tag, tag);
					}
				}
			}
			else
			{
				System.out.println(line);
			}
		}
	}
	
	private Double max_viterbi(HashMap<String, Double> viterbi, int timestep, String current_state, String observation)
	{
		Collection<String> states = tag_list.values();
		Double max = neg_infinity;
		
		for (String state : states)
		{
			//check if the transition exists
			Double trans_prob;
			if (!transition_probs.containsKey(current_state + "|" + state))
				trans_prob = transition_probs.get(DEFAULT + "|" + state );
			else
				trans_prob = transition_probs.get(current_state + "|" + state);
			
			//check if the emission exists
			Double emiss_prob;
			if (!emission_probs.containsKey(observation + "|" + current_state))
			{
				emiss_prob = emission_probs.get(DEFAULT + "|" + current_state);
				is_word_known = false;
			}
			else
			{
				emiss_prob = emission_probs.get(observation + "|" + current_state);
				is_word_known = true;
			}
				
			//calculate
			Double tval = viterbi.get(state + "|" + (timestep - 1))
						  + trans_prob + emiss_prob;
			if (tval > max)
			{
				max = tval;
			}
		}
		
		return max;
	}
	
	private Double max_final_viterbi(HashMap<String, Double> viterbi, int T)
	{
		Collection<String> states = tag_list.values();
		Double max = neg_infinity;
		
		for (String state : states)
		{			
			//calculate
			Double tval = viterbi.get(state + "|" + (T - 1)); //ADD THE FINAL STATE HERE
						  
			if (tval > max)
			{
				max = tval;
			}
		}		
		return max;
	}
	
	private String argmax_viterbi(HashMap<String, Double> viterbi, int timestep, String current_state) throws Exception
	{
		Collection<String> states = tag_list.values();
		Double max = neg_infinity;
		String max_state = null;
		
		for (String state : states)
		{
			//check if the transition exists
			Double trans_prob;
			if (!transition_probs.containsKey(current_state + "|" + state))
				trans_prob = transition_probs.get(DEFAULT + "|" + state);
			else
				trans_prob = transition_probs.get(current_state + "|" + state);
			
			Double tval = viterbi.get(state + "|" + (timestep - 1)) + trans_prob; 		  
			
			if (tval > max)
			{
				max = tval;
				max_state = state;
			}
		}
		
		if (max_state == null)
		{
			throw new Exception("max_state is null: This should not happen.");
		}
		
		return max_state;
	}
	
	private String argmax_final_viterbi(HashMap<String, Double> viterbi, int T) throws Exception
	{
		Collection<String> states = tag_list.values();
		Double max = neg_infinity;
		String max_state = null;
		
		for (String state : states)
		{		
			Double tval = viterbi.get(state + "|" + (T - 1));  //FINAL STATE MULTIPLICATION 		  
			
			if (tval > max)
			{
				max = tval;
				max_state = state;
			}
		}
		
		if (max_state == null)
		{
			throw new Exception("max_state is null: This should not happen.");
		}
		
		return max_state;
	}
	
	public TaggedSentence tagSentence(Sentence s) throws Exception
    {
		String[] observations = s.getSentenceAsArray();
		List<Boolean> known_words = new ArrayList<Boolean>();
		
		HashMap<String, Double> viterbi = new HashMap<String, Double>();
		HashMap<String, String> backpointer = new HashMap<String, String>();
		
		
        Collection<String> states = tag_list.values();

        //initialization step
        for (String state : states)
        {        	
        	Double emission = null;
        	Double transition = null;
        	        	
        	if (!emission_probs.containsKey(observations[0] + "|" + state))
        	{
        		emission = emission_probs.get(DEFAULT + "|" + state);
        		known_words.add(false);
        	}
        	else
        	{
        		emission = emission_probs.get(observations[0] + "|" + state);
        		known_words.add(true);
        	}
        	
        	if(!transition_probs.containsKey(state + "|" + STARTSYMBOL))
        		transition = transition_probs.get(DEFAULT + "|" + STARTSYMBOL);
        	else
        		transition = transition_probs.get(state + "|" + STARTSYMBOL);
        	        	
        	String key = state + "|" + "0";
        	viterbi.put(key, emission + transition);
        	backpointer.put(key, "0");        	
        }
        //recursion step
        for (int i = 1; i < observations.length; i++)
        {
        	for (String state : states)
        	{
        		String key = state + "|" + i;        		
        		Double value = max_viterbi(viterbi, i, state, observations[i]);  		
        		viterbi.put(key, value);
        		String bPointer = argmax_viterbi(viterbi, i, state);
        		backpointer.put(key, bPointer);
        		known_words.add(get_known_word_value());
        	}
        }
        //termination step
        int T = observations.length;
        String key = "STOP" + "|" + T;
        //STOP PROBABILITIES NEED TO BE ADDED HERE
        Double value = max_final_viterbi(viterbi, T);
        viterbi.put(key, value);
        
        String bPointer = argmax_final_viterbi(viterbi, T);
        //backpointer.put(key, bPointer);
        
        // now backtrack        
        List<String> hypothesised_tags = new ArrayList<String>();
        
        for (int i = (observations.length - 1); i >= 0; i--)
        {        	
        	hypothesised_tags.add(bPointer);
        	bPointer = backpointer.get(bPointer + "|" + i);
        }
        //reverse the list
        List<String> reverse_hypothesised_tags = new ArrayList<String>();
        for (int i = (observations.length -1); i >= 0; i--)
        {
        	reverse_hypothesised_tags.add(hypothesised_tags.get(i));
        }
        TaggedSentence tagged = new TaggedSentence(s.getSentence(),
        										   s.getTags(),
        										   reverse_hypothesised_tags,
        										   known_words);
        
        return tagged;
     }
	
	private Boolean get_known_word_value()
	{
		return is_word_known;	
	}
	

}
