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

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ElephantPhotoListActivity extends ListActivity {
	
	private ParseQueryAdapter<ElephantPhoto> mainAdapter;
	private static final int USER_LOGIN_CODE = 101;
	private static final int TAKE_PHOTO_CODE = 102;
	private Uri photoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Drawable bg = getResources().getDrawable(R.drawable.blur);
		getListView().setBackground(bg);

		 
		mainAdapter = new ParseQueryAdapter<ElephantPhoto>(this, ElephantPhoto.class);
		mainAdapter.setTextKey("question");
		mainAdapter.setImageKey("imageThumb");
		setListAdapter(mainAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	// Taking a new photo
	        case R.id.new_photo:
	        	
	        	Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "tmp.jpg");
	    	    photoPath = Uri.fromFile(f);
	    	    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoPath);
	    	    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
	    	   
	            return true;
	        case R.id.logout:
	        	ParseUser.logOut();
	        	Intent loginIntent = new Intent(this, LoginActivity.class);
	    		startActivity(loginIntent);	
	    		finish();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// Returning from taking new photo
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
		
		// Returning from user login
		if (requestCode == USER_LOGIN_CODE && resultCode == RESULT_OK) {
			refreshElephantPhotoList();
		}
	 }
	
	private void refreshElephantPhotoList() {
		mainAdapter.loadObjects();
		setListAdapter(mainAdapter);
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
