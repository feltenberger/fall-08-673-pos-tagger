package edu.umbc.nlp.postagger;

/**
 * A helper class - used as an object that can hold a PoS tag associated with a probability
 */
public class MostFrequentTag
{
	private String tag;
	private double count;
	
	/**
	 * A helper class - used as an object that can hold a PoS tag associated with a probability
	 */
	public MostFrequentTag(String tag, double count)
	{
		this.tag = tag;
		this.count = count;
	}
	/*
	 * The access functions for getting and setting the tag
	 */
	public String getTag(){ return tag; }
	public void setTag(String tag){ this.tag = tag; }
	
	/*
	 * The access functions for getting and setting the count
	 */
	public double getCount(){ return count; }
	public void setCount(double count){ this.count = count; }
	//allow integer
	public void setCount(int count){ this.count = (double)count; }
}
