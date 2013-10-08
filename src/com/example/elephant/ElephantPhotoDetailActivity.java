package com.example.elephant;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ElephantPhotoDetailActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
 
		setContentView(R.layout.activity_detail);
		
		// Show photo
		String objectId = (String) getIntent().getExtras().get("photoId");
		showPhoto(objectId);
		
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
}
