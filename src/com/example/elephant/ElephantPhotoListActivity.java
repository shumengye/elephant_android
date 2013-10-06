package com.example.elephant;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class ElephantPhotoListActivity extends ListActivity {
	
	private ParseQueryAdapter<ElephantPhoto> mainAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getListView().setBackgroundColor(Color.BLUE);
		
		 ParseObject.registerSubclass(ElephantPhoto.class);
		 Parse.initialize(this, "squsUjhTdehGpFPumjW0KjxP7SPrsKsuYnRclVxI", "cSbjuBchn4m1DnjKfqHW2HeRNDoe4TGJJG1IDP4Q"); 
		 ParseUser.enableAutomaticUser();
		 ParseACL defaultACL = new ParseACL();
		 defaultACL.setPublicReadAccess(true);
		 ParseACL.setDefaultACL(defaultACL, true);
		
		 
		 mainAdapter = new ParseQueryAdapter<ElephantPhoto>(this, ElephantPhoto.class);
		 mainAdapter.setTextKey("question");
		 mainAdapter.setImageKey("imageThumb");

		setListAdapter(mainAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
