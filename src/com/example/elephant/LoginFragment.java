package com.example.elephant;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginFragment extends Fragment {
	private View view;
	LoginViewListener activityCallback;

	public interface LoginViewListener {
		public void onShowSignUp();
		public void onSuccessfulLogin();
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
		
		final Button goToSignUpButton = (Button) view.findViewById(R.id.goToSignUpButton);
		goToSignUpButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		activityCallback.onShowSignUp();
	        }
	    });
	    
	    final Button loginButton = (Button) view.findViewById(R.id.loginButton);
	    loginButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		loginUser();
	        }
	    });
	    
	    return view;    
    }
	
	
	private void loginUser() {
		
		final EditText username = (EditText) view.findViewById(R.id.loginUsername);
		final EditText password = (EditText) view.findViewById(R.id.loginPassword);
		
		ParseUser.logInInBackground("shumeng", "19ninja", new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
			    if (user != null) {
			    	System.out.println("login ok");
			    	activityCallback.onSuccessfulLogin();
			    } else {
			    	System.out.println("Login failed " + e.toString());
			    }
			}
		});
		
		
	}
	
}
