package com.shumengye.elephant;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;

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

		transaction.add(R.id.fragmentLayout, loginFragment, "LOGIN");	
		transaction.commit();  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);	
		// By default hide the login option since login fragment is shown
		MenuItem item = menu.findItem(R.id.goToLogin);
		item.setVisible(false);
		return true;
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Toggle signup and login screens
	    switch (item.getItemId()) {
	        case R.id.goToSignup:
	        	invalidateOptionsMenu(); 
	        	onShowSignUp();
	            return true;
	        case R.id.goToLogin:
	        	invalidateOptionsMenu(); 	
	        	onShowLogin();	        	
	        	return true; 
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Depending on active frament, show either login or signup menu option or both
		MenuItem loginOption = menu.findItem(R.id.goToLogin);
		MenuItem signupOption = menu.findItem(R.id.goToSignup);

		LoginFragment loginFragment = (LoginFragment)getFragmentManager().findFragmentByTag("LOGIN");
		if (loginFragment != null && loginFragment.isVisible()) {
			loginOption.setVisible(false);			
			signupOption.setVisible(true);
		}
			
		SignupFragment signupFragment = (SignupFragment)getFragmentManager().findFragmentByTag("SIGNUP");
		if (signupFragment != null && signupFragment.isVisible()) {
			loginOption.setVisible(true);			
			signupOption.setVisible(false);
		}
		
		// Show both login and signup menu options on password reset screen
		ResetPasswordFragment passwordFragment = (ResetPasswordFragment)getFragmentManager().findFragmentByTag("PASSWORD");
		if (passwordFragment != null && passwordFragment.isVisible()) {
			loginOption.setVisible(true);			
			signupOption.setVisible(true);
		}
		
		return true;
	}

	public void onShowSignUp() {
		SignupFragment signupFragment = new SignupFragment();
		
		FragmentManager fragManager = getFragmentManager();		
		FragmentTransaction transaction = fragManager.beginTransaction();	
		
		transaction.replace(R.id.fragmentLayout, signupFragment, "SIGNUP");
		transaction.addToBackStack(null);
		transaction.commit();	
	}

	public void onShowLogin() {
		LoginFragment loginFragment = new LoginFragment();
		
		FragmentManager fragManager = getFragmentManager();		
		FragmentTransaction transaction = fragManager.beginTransaction();	
		
		transaction.replace(R.id.fragmentLayout, loginFragment, "LOGIN");
		transaction.addToBackStack(null);
		transaction.commit();	
		
	}

	public void onLogin() {
		Intent intent = new Intent(this, PhotoListActivity.class);
		startActivity(intent);	
		finish();
	}

	public void onShowForgotPassword(View v) {
		ResetPasswordFragment passwordFragment = new ResetPasswordFragment();
		
		FragmentManager fragManager = getFragmentManager();		
		FragmentTransaction transaction = fragManager.beginTransaction();	
		
		transaction.replace(R.id.fragmentLayout, passwordFragment, "PASSWORD");
		transaction.addToBackStack(null);
		transaction.commit();	
		
		// Update menu
		invalidateOptionsMenu();
	}


}
