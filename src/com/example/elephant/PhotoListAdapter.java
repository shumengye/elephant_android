package com.example.elephant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
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
		if (thumbFile != null) {
			thumbImage.setParseFile(thumbFile);
			thumbImage.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
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

}
