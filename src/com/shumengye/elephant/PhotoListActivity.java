package com.shumengye.elephant;

import java.io.File;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.shumengye.elephant.R;

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
		getListView().setBackgroundResource(R.drawable.blur2);
		
		mainAdapter = new PhotoListAdapter(this);
		mainAdapter.setTextKey("question");
		
		loadElephantPhotoList();
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
	        // load list 
	        case R.id.refresh:
	        	loadElephantPhotoList(); 
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
			loadElephantPhotoList();
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
			loadElephantPhotoList();
		}	
	 }
	
	public boolean isInternetConnected() {
		// Determine if internet access
		ConnectivityManager cm =
		        (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		 
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		boolean isConnected = activeNetwork != null &&
		                      activeNetwork.isConnectedOrConnecting();
		return isConnected;
	}
	
	public void loadElephantPhotoList() {
		// Check for network connection
		if (this.isInternetConnected() == false) {
			Toast toast = Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		else {
			// Re-load adapter
			mainAdapter.loadObjects();
			setListAdapter(mainAdapter);
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Check for network connection
		if (this.isInternetConnected() == false) {
			Toast toast = Toast.makeText(this, R.string.toast_no_internet, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}
		else {
			// Open detail view for selected photo
			Photo photo = (Photo) getListAdapter().getItem(position);
			Intent intent = new Intent(this, PhotoDetailActivity.class);
			intent.putExtra("photoId", photo.getObjectId());
			intent.putExtra("senderName", photo.getSenderName());
			intent.putExtra("question", photo.getQuestion());
			startActivity(intent);	 
		}
	}
}
