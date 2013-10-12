package com.example.elephant;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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
		// Show progress loader
		final ProgressDialog loader = new ProgressDialog(this.getActivity());
		loader.setMessage(getString(R.string.message_login));
		loader.setCancelable(false);
		loader.setIndeterminate(true);
		loader.show();
		
		final String username = ((EditText) view.findViewById(R.id.loginUsername)).getText().toString();
		final String password = ((EditText) view.findViewById(R.id.loginPassword)).getText().toString();
		
		ParseUser.logInInBackground(username, password, new LogInCallback() {
			public void done(ParseUser user, ParseException e) {
				loader.dismiss();
				
			    if (user != null) {
			    	activityCallback.onLogin();
			    } else 
			    	Log.e("Login error", "Login failed " + e.toString());
			}
		});
		
		
	}
	
}
