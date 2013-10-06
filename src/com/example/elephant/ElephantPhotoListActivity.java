package com.example.elephant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ElephantPhotoListActivity extends ListActivity {
	
	private ParseQueryAdapter<ElephantPhoto> mainAdapter;
	
	private static final int TAKE_PHOTO_CODE = 102;
	private Uri photoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//getListView().setBackgroundColor(Color.BLUE);
		Drawable bg = getResources().getDrawable(R.drawable.blur);
		getListView().setBackground(bg);
		
		 ParseObject.registerSubclass(ElephantPhoto.class);
		 Parse.initialize(this, "squsUjhTdehGpFPumjW0KjxP7SPrsKsuYnRclVxI", "cSbjuBchn4m1DnjKfqHW2HeRNDoe4TGJJG1IDP4Q"); 
		 ParseUser.enableAutomaticUser();
		 ParseACL defaultACL = new ParseACL();
		 defaultACL.setPublicReadAccess(true);
		 ParseACL.setDefaultACL(defaultACL, true);
		
		 
		 mainAdapter = new ParseQueryAdapter<ElephantPhoto>(this, ElephantPhoto.class);
		 mainAdapter.setTextKey("question");
		 mainAdapter.setImageKey("imageThumb");

		setListAdapter(mainAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
	    switch (item.getItemId()) {
	        case R.id.new_photo:
	        	
	        	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tmp.jpg");
	    	    photoPath = Uri.fromFile(f);
	    	    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
	    	    startActivityForResult(intent, TAKE_PHOTO_CODE);
	    	    
	        	
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {	        
	        
	        try { 
	            InputStream stream = getContentResolver().openInputStream(photoPath);       
	            Bitmap bitmap = BitmapFactory.decodeStream(stream);
	            stream.close();
     
	            newElephantPhoto(bitmap);
	            
	        }catch (FileNotFoundException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }   
	        
	    }		
	 }
	
	private void newElephantPhoto(Bitmap bitmap) {
		ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream1);
		byte[] imageBytes = stream1.toByteArray();
		
		ElephantPhoto photo = new ElephantPhoto();
        photo.setQuestion("What is this?");
        photo.setSenderName("test user");
        ParseFile imageFile= new ParseFile("test.jpg", imageBytes);
        photo.setImageFile(imageFile);
        photo.setImageThumb(imageFile);
        
        photo.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub  
			}
        });
	}
}
