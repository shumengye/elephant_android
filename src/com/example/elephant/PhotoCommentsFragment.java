package com.example.elephant;

import java.util.List;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class PhotoCommentsFragment extends ListFragment {
	private View view;
	private String photoId;
	private CommentListAdapter mainAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_comments, container, false);
        
        // Photo info summary
        showPhotoInfo();
        
        mainAdapter = new CommentListAdapter(this.getActivity(), new ParseQueryAdapter.QueryFactory<PhotoComment>() {
			public ParseQuery<PhotoComment> create() {
				ParseQuery<PhotoComment> query = new ParseQuery<PhotoComment>("PhotoComment");
				query.orderByDescending("createdAt");
				Photo photo = ParseObject.createWithoutData(Photo.class, photoId);
				query.whereEqualTo("parent", photo);
				return query;
			}
		});
        
        
		mainAdapter.setTextKey("comment");
		setListAdapter(mainAdapter);
		
		// Submit button for new comment
	    final ImageView commentButton = (ImageView) view.findViewById(R.id.newCommentButton);
	    commentButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		newComment();
	        }
	    });
	    
	    //Sliding drawer
	    SlidingDrawer s = (SlidingDrawer) view.findViewById(R.id.slidingDrawer1);
	    s.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
				drawerOpened();				
			}
		});
	    
	   s.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
		@Override
		public void onDrawerClosed() {
			drawerClosed();	
			
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
	
	private void showPhotoInfo() {
		final TextView questionField = (TextView) view.findViewById(R.id.photoinfo_question);
		final TextView senderField = (TextView) view.findViewById(R.id.photoinfo_user);
		
		// Show photo question and user name
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhoto");
		query.whereEqualTo("objectId", photoId);
		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if (e == null) {
					questionField.setText(capitalize(object.getString("question")));
					senderField.setText(capitalize(object.getString("senderName")));			
				}
				else
					Log.d("Photo load error", e.toString());
			}	  
		});
		
		showNumberOfComments();
	}
	
	private void showNumberOfComments() {

		final TextView commentsNum = (TextView) view.findViewById(R.id.photoinfo_comments_num);
		ParseQuery<ParseObject> query = ParseQuery.getQuery("PhotoComment");
		Photo photo = ParseObject.createWithoutData(Photo.class, photoId);
		query.whereEqualTo("parent", photo);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null)
				commentsNum.setText(String.valueOf(objects.size()));	
			}		
		});	
	}
	
	private String capitalize(String line)
	{
	  return Character.toUpperCase(line.charAt(0)) + line.substring(1);
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
					showNumberOfComments();
				}
				else
					Log.d("Comment upload error", e.toString());
			}
        	
        });
	}
	
	private void drawerOpened() {
		final ImageView icon = (ImageView) view.findViewById(R.id.photoinfo_arrow);
		icon.setImageResource(R.drawable.ic_action_cancel);
	}
	
	private void drawerClosed() {
		final ImageView icon = (ImageView) view.findViewById(R.id.photoinfo_arrow);
		icon.setImageResource(R.drawable.arrow_down);
	}
	
}
