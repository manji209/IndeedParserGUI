package com.hotech.indeed;


import java.util.ArrayList;
import java.util.List;

public class UserInfo extends Document {

	private String firstName;
	private String lastName;
	private String fullName;
	private String location;
	private String responsibilities;
	private List<String> locationList;
	private String resume;
	private List<String> responsibilitiesList;
	private List<String> workHistory;
	private List<String> workRecent;

	public UserInfo(String text) {
		super(text);
		firstName = "";
		lastName = "";
		responsibilities = null;
		resume = null;
		location = "";
		workHistory = new ArrayList<String>();
		workRecent = new ArrayList<String>();
		locationList = new ArrayList<String>();
		responsibilitiesList = new ArrayList<String>();
	}

	

	public String getResume() {

		return resume;

	}

	public void setResume(String url) {

		resume = "http://www.indeed.com" + url + "/pdf";
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName() {
		firstName = getToken("(?<=(/r/|/me/)).*?(?=(-|_))");
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName() {
		lastName = getToken("(?<=(-|_)).*?(?=/)");
	}

	public void setFullName() {
		fullName = getToken("(?<=(/r/|/me/)).*?(?=/)");
	}

	public String getFullName() {
		return fullName;
	}

	public String getLocation() {
		return location;
	}

	public void setlocation(String loc) {
		location = loc;
	}

	public List<String> findLocation() {

		locationList = getTokens(
				"(?<=<p id=\"headline_location\" class=\"locality\" itemprop='addressLocality'>).*?(?=</p></div>)");

		return locationList;

	}

	
	public String getResponsibilities() {

		return responsibilities;

	}

	public void findResponsibilities() {
		responsibilities = getToken("(?<=<p class=\"work_description\">).*?(?=</p></div>)");
	}
	
	public void setResponsibilitiesList(String list) {
		responsibilitiesList = getTokens("[^?]+", list);
	}
	
	public List<String> getResponsibilitiesList() {

		return responsibilitiesList;

	}

	public List<String> getWorkHistory() {
		return workHistory;
	}

	// Extract each work history as a string
	public void setWorkHistory() {
		workHistory = getTokens("(?<=<p class=\"work_title title\">).*?(?=<p class=\"work_description\">)");
	}

	public List<String> getWorkRecent() {
		return workRecent;
	}

	// Extract each work history as a string
	public void setWorkRecent(String work) {
		workHistory = getTokens("[^-]+", work);
		// A space may occupy an item in workHistory so we test length > 1
		for (String tok : workHistory) {
			if (tok.length() > 1) {
				workRecent.add(tok);
			}
		}
	}

}
