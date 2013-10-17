package com.shumengye.elephant;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shumengye.elephant.R;

public class NewPhotoActivity extends Activity {
	private Bitmap bitmap;
	private ImageView maskView;
	private float mLastTouchX; // helper coordinates for dragging mask view
	private float mLastTouchY;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_photo);
		// Show "up" navigation back to parent activity
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
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
		
		// Show toast with instructions for moving mask
		Toast toast = Toast.makeText(this, R.string.toast_mask_preview, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP|Gravity.CENTER, 0, 250);
		toast.show();
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
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = MotionEventCompat.getActionMasked(ev); 
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int height = displaymetrics.heightPixels;
		int width = displaymetrics.widthPixels;
		
		// Boundaries for mask movement based on screen size
		int minX = -1163;
		int minY = -1843;
		int maxX = -1200 + width -43;
		int maxY = -1900 + height -250;
		
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
	
	private void newElephantPhoto() {
		// Show progress loader
		final ProgressDialog loader = new ProgressDialog(this);
		loader.setMessage(getString(R.string.message_uploadingphoto));
		loader.setCancelable(false);
		loader.setIndeterminate(true);
		loader.show();
		
		Photo photo = new Photo();
		
		String question = ((EditText) findViewById(R.id.photoQuestion)).getText().toString();
        photo.setQuestion(question);
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        photo.setSender(currentUser);
        photo.setSenderName(currentUser.getUsername());
        
        ParseFile imageFile= new ParseFile("image.jpg", bitmapToData(bitmap, Bitmap.CompressFormat.JPEG, 50));
        photo.setImageFile(imageFile);
        
        Bitmap thumbBitmap = createThumbnail(bitmap);
        
		ParseFile imageThumb = new ParseFile("thumb.jpg", bitmapToData(thumbBitmap, Bitmap.CompressFormat.PNG, 90));
		photo.setImageThumb(imageThumb);
		 
        photo.saveInBackground(new SaveCallback() {
			
        	@Override
			public void done(ParseException e) {
        		loader.dismiss();
        		
				if (e == null) {
					finish();
				}
				else
					Log.d("Photo upload error", e.toString());
			}
        });
	}
	
	private byte[] bitmapToData(Bitmap origBmp, CompressFormat f, int quality) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        origBmp.compress(f, quality, stream);
		return stream.toByteArray();
	}
	 
	private Bitmap createThumbnail(Bitmap origBmp) {
		int w = origBmp.getWidth();
		
		// Dimension for thumbnail
		int d = (int) (w / 10);
		if (d < 240)
			d = 240;
		
		int startX = (origBmp.getWidth() / 2) - (d / 2);
		int startY = (origBmp.getHeight() / 2) - (d / 2);		
				
		Bitmap thumb = Bitmap.createBitmap(origBmp, startX, startY, d, d);
		thumb = getRoundedCornerBitmap(thumb);
		
		return thumb;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	            bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);

	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
	            bitmap.getWidth() / 2, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	   
	    return output;
	}

}
