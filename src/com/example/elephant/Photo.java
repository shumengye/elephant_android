package com.example.elephant;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("UserPhoto")
public class Photo extends ParseObject {
	
	public Photo() {
		
	}
	
	public ParseUser getSender() {
		return getParseUser("sender");
	}

	public void setSender(ParseUser user) {
		put("sender", user);
	}
	
	public String getSenderName() {
		return getString("senderName");
	}
	
	public void setSenderName(String n) {
		put("senderName", n);
	}
	
	public String getQuestion() {
		return getString("question");
	}
	
	public void setQuestion(String q) {
		put("question", q);
	}
	
	public ParseFile getImageThumb() {
		return getParseFile("imageThumb");
	}
	
	public void setImageThumb(ParseFile f) {
		put("imageThumb", f);
	}
	
	public ParseFile getImageFile() {
		return getParseFile("imageFile");
	}
	
	public void setImageFile(ParseFile f) {
		put("imageFile", f);
	}

}
