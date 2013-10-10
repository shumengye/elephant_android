package com.example.elephant;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginFragment extends Fragment {
	
	private View view;
	private ProgressBar loader;
	LoginViewListener activityCallback;

	public interface LoginViewListener {
		public void onShowSignUp();
		public void onLogin();
	}
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);

	    try {
	       activityCallback = (LoginViewListener) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString()
	                + " must implement LoginViewListener");
	    }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_login, container, false);
        
        // Progress indicator
       loader = (ProgressBar) view.findViewById(R.id.loader);
       loader.setVisibility(View.GONE);
		
        // Link for switching to signup view
		final TextView goToSignUp = (TextView) view.findViewById(R.id.goToSignup);
		goToSignUp.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		activityCallback.onShowSignUp();
	        }
	    });
	    
		// Submit button for login form
	    final Button loginButton = (Button) view.findViewById(R.id.loginButton);
	    loginButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		loginUser();
	        }
	    });
	    
	    return view;    
    }
	
	
	private void loginUser() {	
		loader.setVisibility(View.VISIBLE);
		final String username = ((EditText) view.findViewById(R.id.loginUsername)).getText().toString();
		final String password = ((EditText) view.findViewById(R.id.loginPassword)).getText().toString();
		
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
			    if (user != null) {
			    	// Notify activity of successful login
			    	loader.setVisibility(View.GONE);
			    	activityCallback.onLogin();
			    } else 
			    	Log.e("Login error", "Login failed " + e.toString());
			}
		});
		
		
	}
	
}
