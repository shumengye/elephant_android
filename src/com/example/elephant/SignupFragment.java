package com.example.elephant;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SignupFragment extends Fragment {
	
	SignupViewListener activityCallback;

	public interface SignupViewListener {
		public void onShowLogin();
	}
	
	@Override
	public void onAttach(Activity activity) {
	    super.onAttach(activity);

	    try {
	       activityCallback = (SignupViewListener) activity;
	    } catch (ClassCastException e) {
	        throw new ClassCastException(activity.toString()
	                + " must implement LoginViewListener");
	    }
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_signup, container, false);

		final Button goToLoginButton = (Button) view.findViewById(R.id.goToLoginButton);
		goToLoginButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		 activityCallback.onShowLogin();
	        }
	    });
	     
	    return view;    
    }
	
	
}
