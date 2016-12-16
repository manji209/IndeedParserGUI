package com.hotech.indeed;


import java.util.List;

/** 
 * A class that represents a text document
 * 
 */
public class ProcessLinks extends Document {
	
	

	
	public ProcessLinks(String text)
	{
		super(text);
		processURL();
	}
	
	
	
    /** Passes through the text one time to extract individual user page url 
     * 
     * @return 
     */
	public List<String> processURL()
	{
		
		List<String> tokens = getTokens("(?<=href=\")(/r/|/me/).*?(?=\\?sp=0)");
		
		
		return tokens;
		
	}
	
	
	

}
