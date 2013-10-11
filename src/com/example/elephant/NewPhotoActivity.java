package com.example.elephant;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewPhotoActivity extends Activity {
	
	private Bitmap bitmap;
	
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
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.simple, menu);
		return true;
	}
	
	private void newElephantPhoto() {
		// Show progress loader
		final ProgressDialog loader = new ProgressDialog(this);
		loader.setMessage("Uploading new photo...");
		loader.setCancelable(false);
		loader.setIndeterminate(true);
		loader.show();
		
		Photo photo = new Photo();
		
		final String question = ((EditText) findViewById(R.id.photoQuestion)).getText().toString();
        photo.setQuestion(question);
        
        ParseUser currentUser = ParseUser.getCurrentUser();
        photo.setSender(currentUser);
        photo.setSenderName(currentUser.getUsername());
        
        ParseFile imageFile= new ParseFile("image.jpg", bitmapToData(bitmap, Bitmap.CompressFormat.JPEG, 50));
        photo.setImageFile(imageFile);
        
        Bitmap thumbBitmap = createThumbnail(bitmap);
        
		ParseFile imageThumb = new ParseFile("thumb.jpg", bitmapToData(thumbBitmap, Bitmap.CompressFormat.PNG, 80));
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
		int d = (int) (w / 16);
		if (d < 100)
			d = 100;
		
		int startX = (origBmp.getWidth() / 2) - (d / 2);
		int startY = (origBmp.getHeight() / 2) - (d / 2);		
				
		Bitmap thumb = Bitmap.createBitmap(origBmp, startX, startY, d, d);
		thumb = getRoundedCornerBitmap(thumb, 30);
		
		return thumb;
	}
	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
	            .getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = pixels;

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);

	    return output;
	}

}
