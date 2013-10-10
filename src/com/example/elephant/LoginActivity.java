package com.example.elephant;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;

public class LoginActivity extends FragmentActivity implements LoginFragment.LoginViewListener, SignupFragment.SignupViewListener  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ParseObject.registerSubclass(Photo.class);
		ParseObject.registerSubclass(PhotoComment.class);
		Parse.initialize(this, "squsUjhTdehGpFPumjW0KjxP7SPrsKsuYnRclVxI", "cSbjuBchn4m1DnjKfqHW2HeRNDoe4TGJJG1IDP4Q"); 
		ParseACL defaultACL = new ParseACL();
		defaultACL.setPublicReadAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);
		
		// Skip login view if user is already logged in
		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			onLogin();
		} 
		
		setContentView(R.layout.activity_login);
		
		LoginFragment loginFragment = new LoginFragment();		   

		FragmentManager fragManager = getFragmentManager();		
		FragmentTransaction transaction = fragManager.beginTransaction();		

		transaction.add(R.id.fragmentLayout, loginFragment);	
		transaction.commit();  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login,
                         menu);
		return true;
	}

	@Override
	public void onShowSignUp() {
		SignupFragment signupFragment = new SignupFragment();
		
		FragmentManager fragManager = getFragmentManager();		
		FragmentTransaction transaction = fragManager.beginTransaction();	
		
		transaction.replace(R.id.fragmentLayout, signupFragment);
		transaction.addToBackStack(null);
		transaction.commit();	
	}

	@Override
	public void onShowLogin() {
		LoginFragment loginFragment = new LoginFragment();
		
		FragmentManager fragManager = getFragmentManager();		
		FragmentTransaction transaction = fragManager.beginTransaction();	
		
		transaction.replace(R.id.fragmentLayout, loginFragment);
		transaction.addToBackStack(null);
		transaction.commit();	
		
	}

	@Override
	public void onLogin() {
		Intent intent = new Intent(this, PhotoListActivity.class);
		startActivity(intent);	
		finish();
	}


}
