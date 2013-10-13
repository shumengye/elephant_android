package com.example.elephant;

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

public class CommentListAdapter extends ParseQueryAdapter<PhotoComment> {
	
	public CommentListAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<PhotoComment>() {
			public ParseQuery<PhotoComment> create() {
				ParseQuery<PhotoComment> query = new ParseQuery<PhotoComment>("PhotoComment");
				query.orderByDescending("createdAt");
				return query;
			}
		});
	}
	
	@Override
	public View getItemView(PhotoComment comment, View v, ViewGroup parent) {
		if (v == null) {
		    v = View.inflate(getContext(), R.layout.adapter_comment_item, null);
		}
    
		super.getItemView(comment, v, parent);
		
		// Comment
		TextView questionView = (TextView) v.findViewById(R.id.comment);
		questionView.setText(comment.getComment());
		
		// Sender name
		TextView descriptionView = (TextView) v.findViewById(R.id.userName);
		descriptionView.setText(comment.getUserName());
		
		return v;
	}
}
