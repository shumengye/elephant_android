package com.example.elephant;

import java.io.File;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

public class PhotoListActivity extends ListActivity {
	
	private ParseQueryAdapter<Photo> mainAdapter;
	private static final int USER_LOGIN_CODE = 101;
	private static final int TAKE_PHOTO_CODE = 102;
	private static final int NEW_PHOTO_CODE = 102;
	private Uri photoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getListView().setDividerHeight(0);
		getListView().setDivider(null);
		
		mainAdapter = new PhotoListAdapter(this);
		mainAdapter.setTextKey("question");
		
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
	        // Refresh list 
	        case R.id.refresh:
	        	refreshElephantPhotoList(); 
	        	return true;
	        // Logout
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
		
		// Returning from user login
		if (requestCode == USER_LOGIN_CODE && resultCode == RESULT_OK) {
			refreshElephantPhotoList();
		}
		
		// Returning from taking photo with camera
		if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {    				
			 System.out.println("opening new photo view");
	            // Start activity to create new elephant photo with question / comment
				Intent newPhotoIntent = new Intent(this, NewPhotoActivity.class);
				newPhotoIntent.putExtra("photoPath", photoPath);
	    		startActivityForResult(newPhotoIntent, NEW_PHOTO_CODE);	 
	    }
		
		// Returning from creating new elephant photo
		if (requestCode == NEW_PHOTO_CODE && resultCode == RESULT_OK) {
			refreshElephantPhotoList();
		}	
	 }
	
	public void refreshElephantPhotoList() {
		mainAdapter.loadObjects();
		setListAdapter(mainAdapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Open detail view for selected photo
		Photo photo = (Photo) getListAdapter().getItem(position);
		Intent intent = new Intent(this, PhotoDetailActivity.class);
		intent.putExtra("photoId", photo.getObjectId());
		intent.putExtra("senderName", photo.getSenderName());
		intent.putExtra("question", photo.getQuestion());
		startActivity(intent);	 

	}
}
