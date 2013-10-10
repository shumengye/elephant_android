package com.example.elephant;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("PhotoComment")
public class PhotoComment extends ParseObject {
	
	public PhotoComment() {
		
	}
	
	public ParseUser getUser() {
		return getParseUser("user");
	}

	public void setUser(ParseUser user) {
		put("user", user);
	}
	
	public String getComment() {
		return getString("comment");
	}

	public void setComment(String comment) {
		put("comment", comment);
	}
	
	public String getUserName() {
		return getString("username");
	}

	public void setUserName(String name) {
		put("username", name);
	}
	
	public Photo getParent() {
		return (Photo) get("parent");
	}

	public void setParent(Photo parent) {
		put("parent", parent);
	}
	
}
