package com.example.elephant;

import android.app.Activity;
import android.content.ClipData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ElephantPhotoDetailActivity extends Activity {
	
	private ImageView maskView;
	private float mLastTouchX; // helper coordinates for dragging mask view
	private float mLastTouchY;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.activity_detail);
		maskView = (ImageView) findViewById(R.id.imageMask);
		
		// Show photo
		String objectId = (String) getIntent().getExtras().get("photoId");
		showPhoto(objectId);
		
		ImageView mask = (ImageView) findViewById(R.id.imageMask);
		mask.setOnTouchListener(
			new ImageView.OnTouchListener() {
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					//onTouchMaskEvent(arg1);
					onTouchEvent(arg1);
    			    return true;
				}
	        }
		);
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
	
	private void onTouchMaskEvent(MotionEvent ev) {
	    
	    ImageView view = (ImageView) findViewById(R.id.imageMask);
	   
	    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	    	ClipData clipData = ClipData.newPlainText("", "");
	 	    View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);

	 	    view.startDrag(clipData, dsb, view, 0);
	 	    view.setVisibility(View.INVISIBLE);
	    }
	}
}
