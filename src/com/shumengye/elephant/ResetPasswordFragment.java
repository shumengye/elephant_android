package com.shumengye.elephant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ResetPasswordFragment extends Fragment {
	private View view;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_reset_password, container, false); 
	    
        // Submit form for resetting password
	    final Button loginButton = (Button) view.findViewById(R.id.resetPasswordButton);
	    final String email = ((TextView) view.findViewById(R.id.email)).getText().toString();
	    loginButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		new SendResetPasswordEmail().execute(email);
	        }
	    });
	    
	    return view;    
    }	
	

	private class SendResetPasswordEmail extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... userEmail) {	
			// POST request to Parse
			String URL = "https://api.parse.com/1/requestPasswordReset";
			
			HttpClient client = new DefaultHttpClient();
		    HttpPost post = new HttpPost(URL);
		    post.setHeader("X-Parse-Application-Id", "squsUjhTdehGpFPumjW0KjxP7SPrsKsuYnRclVxI");
		    post.addHeader("X-Parse-REST-API-Key", "v2kWry3fforIs7GeSb6IJXrc5zyQbZvelz5KwIAf");
		    post.addHeader("Content-Type", "application/json");
		    
		    try {
		    	StringEntity input = new StringEntity("{\"email\":\"" + userEmail + "\"}");
				input.setContentType("application/json");
				post.setEntity(input);
		 
			    HttpResponse response = client.execute(post);
			    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			    String line = rd.readLine();
			    // On success Parse response will be {}
			    if (line != null && line.equalsIgnoreCase("{}")) 
			    	return true;
		      
		      
		    } catch (IOException e) {
		      ((TextView) view.findViewById(R.id.msg)).setText(e.getMessage());
		    }
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
	        if (result == true) {
	        	// Indicate sending reset link was ok
	        	((TextView) view.findViewById(R.id.msg)).setText(getString(R.string.message_reset_password_sent));
	        	// Reset email text field
	        	((TextView) view.findViewById(R.id.email)).setText("");
	        }
	    }
		
	}
}
