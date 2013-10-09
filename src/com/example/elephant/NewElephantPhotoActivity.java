package com.example.elephant;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewElephantPhotoActivity extends Activity {
	
	private Bitmap bitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_photo);
				
		Uri photoPath = (Uri) getIntent().getExtras().get("photoPath");
		if (photoPath != null) {		
			
			try { 
				// Read bitmap data from file
	            InputStream stream = getContentResolver().openInputStream(photoPath);       
	            bitmap = BitmapFactory.decodeStream(stream);
	            stream.close();
				
	            final ImageView imagePreview = (ImageView) findViewById(R.id.imagePreview);
			    imagePreview.setImageBitmap(bitmap);
	            
	        }catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        } 
		}
		
		final Button newPhotoButton = (Button) findViewById(R.id.newPhotoButton);
		newPhotoButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		newElephantPhoto();
	        }
	    });
	}
	
	private void newElephantPhoto() {
		
		ElephantPhoto photo = new ElephantPhoto();
		
		final String question = ((EditText) findViewById(R.id.photoQuestion)).getText().toString();
        photo.setQuestion(question);
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        photo.setSender(currentUser);
        photo.setSenderName(currentUser.getUsername());
        
        ParseFile imageFile= new ParseFile("image.jpg", bitmapToData(bitmap, 50));
        photo.setImageFile(imageFile);
        
        Bitmap thumbBitmap = createThumbnail(bitmap);
        
		ParseFile imageThumb = new ParseFile("thumb.jpg", bitmapToData(thumbBitmap, 50));
		photo.setImageThumb(imageThumb);
		 
        photo.saveInBackground(new SaveCallback() {
			
        	@Override
			public void done(ParseException e) {
				if (e == null) {
					finish();
				}
				else
					Log.d("Photo upload error", e.toString());
			}
        });
	}
	
	private byte[] bitmapToData(Bitmap origBmp, int quality) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        origBmp.compress(Bitmap.CompressFormat.JPEG, quality, stream);
		return stream.toByteArray();
	}
	 
	private Bitmap createThumbnail(Bitmap origBmp) {
		int w = origBmp.getWidth();
		
		// Dimension for thumbnail
		int d = (int) (w / 16);
		if (d < 100)
			d = 100;
		
		int startX = (origBmp.getWidth() / 2) - (d / 2);
		int startY = (origBmp.getHeight() / 2) - (d / 2);		
				
		Bitmap thumb = Bitmap.createBitmap(origBmp, startX, startY, d, d);
		return thumb;
	}
}
