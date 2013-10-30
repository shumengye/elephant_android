package com.shumengye.elephant;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class PhotoListAdapter extends ParseQueryAdapter<Photo> {

	public PhotoListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Photo>() {
			public ParseQuery<Photo> create() {
				ParseQuery<Photo> query = new ParseQuery<Photo>("UserPhoto");
				query.orderByDescending("createdAt");
				return query;
			}
		});
		
	}

	@Override
	public View getItemView(Photo photo, View v, ViewGroup parent) {
		if (v == null) {
		    v = View.inflate(getContext(), R.layout.adapter_photo_item, null);
		}
    
		super.getItemView(photo, v, parent);
		
		// Thumbnail
		ParseImageView thumbImage = (ParseImageView) v.findViewById(R.id.icon);
		ParseFile thumbFile = photo.getImageThumb();
		final View theView = v;
		
		if (thumbFile != null) {
			thumbImage.setParseFile(thumbFile);
			thumbImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					
					// Create rounded thumbnails
					ParseImageView thumbImage2 = (ParseImageView) theView.findViewById(R.id.icon);
			        Bitmap bmp = ((BitmapDrawable)thumbImage2.getDrawable()).getBitmap();
			        thumbImage2.setImageBitmap(getRoundedCornerBitmap(bmp));
				}
			});
		}
		
		// Question
		TextView questionView = (TextView) v.findViewById(R.id.question);
		questionView.setText(photo.getQuestion());
		
		// Sender name
		TextView descriptionView = (TextView) v.findViewById(R.id.senderName);
		descriptionView.setText(photo.getSenderName());
		
		// Date, convert date to relative time e.g. "1 day ago"
		Calendar c = GregorianCalendar.getInstance();
        c.setTime(photo.getCreatedAt());
        TextView dateView = (TextView) v.findViewById(R.id.date);
		dateView.setText(RelativeDate.getRelativeDate(c));
		
		return v;
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
