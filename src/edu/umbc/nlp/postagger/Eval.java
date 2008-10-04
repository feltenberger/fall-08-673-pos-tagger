package edu.umbc.nlp.postagger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

public class Eval {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{	
		
		String oFileName = "/home/niels/workspace2/fall-08-673-pos-tagger/output/final/results50.csv";
		List<String> final_out = new ArrayList<String>();
		
		for (int index = 1; index <= 4; index++)
		{
			
		
		List<String> fhtags = new ArrayList<String>();
		List<String> fvtags = new ArrayList<String>();
		HashMap<String,Double> percent = new HashMap<String,Double>();
		Double WER = 0.0;
		Double knownWER = 0.0;
		Double unknownWER = 0.0;
		
		for (int k = 1; k <= 10; k++)
		{
		String inFile = "/home/niels/workspace2/fall-08-673-pos-tagger/output/50results"+k+".csv";
		File iFile = new File(inFile);
		List<String> lines = FileUtils.readLines(iFile);
		
		List<String> tags_horizontal = new ArrayList<String>();
		List<String> tags_vertical = new ArrayList<String>();
		
		
		
		
		int index_count = 0;
		for (String line : lines)
		{			
			if (line.startsWith("-") || line.startsWith("Word")||line.startsWith("Known")
				|| line.startsWith("Unknown") || line.startsWith("Baseline")
				|| line.startsWith("Niels") || line.startsWith("Dave"))
			{
				if (line.startsWith("-"))
				{
					index_count++;
				}
				//Word Error Rate = 9.915856496796943
				//Known Word WER = 8.439331141917965
				//Unknown Word WER = 83.56435643564356
				if (index_count == index)
				{
					if (line.startsWith("Word"))
					{
						String[] number = line.split(" = ");
						Double num = Double.parseDouble(number[1]);
						WER += num;					
					}
					if (line.startsWith("Known"))
					{
						String[] number = line.split(" = ");
						Double num = Double.parseDouble(number[1]);
						knownWER += num;					
					}
					if (line.startsWith("Unknown"))
					{
						String[] number = line.split(" = ");
						Double num = Double.parseDouble(number[1]);
						unknownWER += num;					
					}
				}
			}
			else
			{
			if (index == index_count)
			{
				String[] split = line.split("\t");
				if (split[0].equalsIgnoreCase(""))
				{					
					for (int i = 1; i < split.length; i++)
					{
						if (!fhtags.contains(split[i]))
						{
							fhtags.add(split[i]);							
						}
						tags_horizontal.add(split[i]);
					}
				}
				else
				{
					if (!fvtags.contains(split[0]))
					{
						fvtags.add(split[0]);							
					}
					String tag = split[0];
					tags_vertical.add(tag);
					for (int i = 1; i < split.length; i++)
					{
						String htag = tags_horizontal.get(i - 1);
						if (!(split[i].compareToIgnoreCase("-") == 0))
						{
							if (!percent.containsKey(tag + "#" + htag))
							{
								percent.put(tag + "#" + htag, Double.parseDouble(split[i]));
							}
							else
							{
								Double val = percent.get(tag + "#" + htag);
								percent.remove(tag + "#" + htag);
								percent.put(tag + "#" + htag, val + Double.parseDouble(split[i]));
							}
						}
					}
					
				}
			}				
			}
		}
		}
		List<String> output = new ArrayList<String>();
		output.add("Word Error Rate = " + WER /10);
		output.add("Known WER = " + knownWER /10);
		output.add("Unknown WER = " + unknownWER /10);
		String line = "";
		for (String incorrect : fhtags)
		{
			System.out.print("\t" + incorrect);
			line +="\t" + incorrect;
		}
		System.out.println();
		output.add(line);
		
		for (String correct : fvtags)
		{
			String nline = correct;
			System.out.print(correct);
			for (String incorrect : fhtags)
			{
				
				if (percent.containsKey(correct + "#" + incorrect))
				{					
					Double val = percent.get(correct + "#" + incorrect);				
					String result = Double.toString(val / 10);
					if (result.length() > 4)
					{
						result = result.substring(0, result.indexOf(".") + 3);
					}
									
					System.out.print("\t" + result);
					nline += "\t" + result;					
				}
				else
				{
					System.out.print("\t0");
					nline += "\t0";
				}
			}
			System.out.println();
			output.add(nline);
		}
		final_out.addAll(output);
		final_out.add("------------------------");
				
		
		}
		
		
		File oFile = new File(oFileName);
		FileUtils.writeLines(oFile, final_out);
		
		
		
	}

}
