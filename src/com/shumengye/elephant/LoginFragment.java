package com.shumengye.elephant;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

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
	    
	    // Submit if user clicks "done" button
	    final EditText p = ((EditText) view.findViewById(R.id.loginPassword));
	    p.setOnEditorActionListener(new OnEditorActionListener() {
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE))
	            	loginUser();
	            return true;
	        }
	    });
	    
	    // Fix for password field font issue
	    p.setTypeface(Typeface.SANS_SERIF);
	    
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
			    } else {
			    	// Show error
			    	String msg = capitalize(e.getMessage() + ".");
			    	((TextView) view.findViewById(R.id.error)).setText(msg);		    	
			    }
			}
		});		
	}
	
	private String capitalize(String line)
	{
	  return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
	
}
