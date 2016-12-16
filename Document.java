package com.hotech.indeed;

/** 
 * A class that represents a text document
 * @author UC San Diego Intermediate Programming MOOC team
 */
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Document {

	private String text;
	private int numTokens;

	/**
	 * Create a new document from the given text. Because this class is
	 * abstract, this is used only from subclasses.
	 * 
	 * @param text
	 *            The text of the document.
	 */
	protected Document(String text) {
		this.text = text;
	}

	/**
	 * Returns the tokens that match the regex pattern from the document text
	 * string.
	 * 
	 * @param pattern
	 *            A regular expression string specifying the token pattern
	 *            desired
	 * @return A List of tokens from the document text that match the regex
	 *         pattern
	 */
	protected List<String> getTokens(String pattern) {
		ArrayList<String> tokens = new ArrayList<String>();
		Pattern tokSplitter = Pattern.compile(pattern);
		Matcher m = tokSplitter.matcher(text);

		while (m.find()) {
			tokens.add(m.group());
			numTokens++;
		}

		return tokens;
	}

	//Search through a file one line at a time
	protected List<String> getTokens(String pattern, String work) {
		ArrayList<String> tokens = new ArrayList<String>();
		Pattern tokSplitter = Pattern.compile(pattern);
		Matcher m = tokSplitter.matcher(work);

		while (m.find()) {
			tokens.add(m.group());
		}

		return tokens;
	}

	//Search through one line
	protected String getToken(String pattern) {
		String token = null;
		Pattern tokSplitter = Pattern.compile(pattern);
		Matcher m = tokSplitter.matcher(text);

		if (m.find()) {
			token = m.group();
			return token;
		}

		return token;
	}

	/** Return the entire text of this document */
	public String getText() {
		return this.text;
	}

	public int getNumTok() {
		return this.numTokens;
	}

}
