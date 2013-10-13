package com.example.elephant;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        // Logout
	        case R.id.logout:
	        	ParseUser.logOut();
	        	Intent loginIntent = new Intent(this, LoginActivity.class);
	        	loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(loginIntent);	
	    		finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void showPhoto(String objectId) {
		// Show progress loader
		final ProgressDialog loader = new ProgressDialog(this);
		loader.setMessage(getString(R.string.message_loadingphoto));
		loader.setCancelable(false);
		loader.setIndeterminate(true);
		loader.show();
		
		final Context context = getApplicationContext();
				
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
							loader.dismiss(); 
							
							if (e == null) {
								// Show photo in image view
								final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
								Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);
								imageView.setImageBitmap(bmp);
								
								// Show toast with instructions for moving mask
								Toast toast = Toast.makeText(context, R.string.toast_mask, Toast.LENGTH_LONG);
								toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 250);
								toast.show();
							}
							else
								Log.d("Image file load error", e.toString());
						}
						  
					});
				}
				else
					loader.dismiss(); 
		
			}
		  
		});
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev); 
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		//System.out.println("Screen "+width + ", " + height);
		//System.out.println("New coordinates "+maskView.getX()+ ", " + maskView.getY());
		// Boundaries for mask movement based on screen size
		int minX = -1163;
		int minY = -1843;
		int maxX = -1200 + width -43;
		int maxY = -1900 + height -380;
		
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
		        
		        // Check boundaries
		        float newX = maskView.getX() + dx;
		        float newY = maskView.getY() + dy;
		        
		        
		        if (newX < minX)
		        	newX = minX;
		        if (newX > maxX)
		        	newX = maxX;

		        
		        if (newY < minY)
		        	newY = minY;
		        if (newY > maxY)
		        	newY = maxY;
		   
		        // Update mask position
		        maskView.setX(newX);
		        maskView.setY(newY);
		        
		        // Remember current touch position
		        mLastTouchX = x;
		        mLastTouchY = y;
		        break;
	    	}
			
		}
		return false;
	}
}
