package com.example.elephant;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class PhotoDetailActivity extends Activity {
	private String photoId;
	private ImageView maskView;
	private float mLastTouchX; // helper coordinates for dragging mask view
	private float mLastTouchY;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.activity_detail);
		
		// Show photo
		photoId = (String) getIntent().getExtras().get("photoId");
		showPhoto(photoId);
		
		// Photo info
		//String senderName = (String) getIntent().getExtras().get("senderName");
		//String question = (String) getIntent().getExtras().get("question");
		//TextView photoInfo = (TextView) findViewById(R.id.handle);
		//photoInfo.setText(senderName + ": " + question);
		
		// Move image mask on user touch event
		maskView = (ImageView) findViewById(R.id.imageMask);
		maskView.setOnTouchListener(
			new ImageView.OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					onTouchEvent(arg1);
    			    return true;
				}
	        }
		);
		
		// Comments fragment
		
		PhotoCommentsFragment commentsFragment = new PhotoCommentsFragment();		   
		commentsFragment.init(photoId);
		
		FragmentManager fragManager = getFragmentManager();		
		FragmentTransaction transaction = fragManager.beginTransaction();		

		transaction.add(R.id.fragmentLayout, commentsFragment);	
		transaction.commit();  
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.simple, menu);
		return true;
	} 
	
	private void showPhoto(String objectId) {
		// Get Parse photo object
		ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPhoto");
		query.whereEqualTo("objectId", objectId);
		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {
				if (object != null) {
					// Get image file data
					ParseFile file = (ParseFile) object.get("imageFile");
					file.getDataInBackground(new GetDataCallback() {

						@Override
						public void done(byte[] data, ParseException e) {
							if (e == null) {
								// Show photo in image view
								final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
								Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);
								imageView.setImageBitmap(bmp);
							}
							else
								Log.d("Image file load error", e.toString());
						}
						  
					});
				}
			}
		  
		});
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev); 
		
		switch (action) { 
		case MotionEvent.ACTION_DOWN: {
		        final float x = ev.getRawX();
		        final float y  = ev.getRawY();
		        
		        // Remember current touch position
		        mLastTouchX = x;
		        mLastTouchY = y;
		            
		        break;
		    }
		
			case MotionEvent.ACTION_MOVE: {
		        final float x = ev.getRawX();
		        final float y  = ev.getRawY();
		        
		        
		        // Calculate the distance moved
		        final float dx = x - mLastTouchX;
		        final float dy = y - mLastTouchY;
		        
		        // Update mask position
		        maskView.setX(maskView.getX() + dx);
		        maskView.setY(maskView.getY() + dy);
		        
		        // Remember current touch position
		        mLastTouchX = x;
		        mLastTouchY = y;
		        break;
	    	}
		}
		return false;
	}
}
