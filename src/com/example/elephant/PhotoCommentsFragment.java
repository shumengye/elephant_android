package com.example.elephant;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PhotoCommentsFragment extends ListFragment {
	private View view;
	private String photoId;
	private ParseQueryAdapter<PhotoComment> mainAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_comments, container, false);
        
        // Photo info summary
        final TextView handle = (TextView) view.findViewById(R.id.photoinfo);
        handle.setText(this.photoId);
        
        // Comments list, fetch comments from Parse
        mainAdapter = new ParseQueryAdapter<PhotoComment>(this.getActivity(), new ParseQueryAdapter.QueryFactory<PhotoComment>() {
            public ParseQuery<PhotoComment> create() {
                // Reference to parent photo
            	Photo parent = new Photo();
            	parent.setObjectId(photoId);
            	
                ParseQuery<PhotoComment> query = new ParseQuery<PhotoComment>("PhotoComment");
                query.whereEqualTo("parent", parent);
                query.orderByDescending("createdAt");
                return query;
              }
            });
        
		mainAdapter.setTextKey("comment");
		setListAdapter(mainAdapter);
		
		// Submit button for new comment
	    final Button commentButton = (Button) view.findViewById(R.id.newCommentButton);
	    commentButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		newComment();
	        }
	    });
        
	    return view;    
    }
	
	public void init(String id) {
		this.photoId = id;
	}
	
	public void refreshCommentList() {
		mainAdapter.loadObjects();
		setListAdapter(mainAdapter);
	}
	
	private void newComment() {
		// Show progress loader
		final ProgressDialog loader = new ProgressDialog(this.getActivity());
		loader.setMessage("Adding comment...");
		loader.setCancelable(false);
		loader.setIndeterminate(true);
		loader.show();
		
		// Create new comment
        PhotoComment comment = new PhotoComment();
        ParseUser currentUser = ParseUser.getCurrentUser();
        comment.setUser(currentUser);
        comment.setUserName(currentUser.getUsername());
        
        final EditText commentTextField = ((EditText) view.findViewById(R.id.newComment));
        final String commentText= commentTextField.getText().toString();
        comment.setComment(commentText);
        
        Photo parent = new Photo();
    	parent.setObjectId(photoId);
    	comment.setParent(parent); 
    	 
        comment.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				loader.dismiss();
				
				if (e == null) {
					commentTextField.setText("");
					refreshCommentList();
				}
				else
					Log.d("Comment upload error", e.toString());
			}
        	
        });
	}
	
}
