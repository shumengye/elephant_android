package com.shumengye.elephant;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;
import com.shumengye.elephant.R;

public class CommentListAdapter extends ParseQueryAdapter<PhotoComment> {

	
	public CommentListAdapter(Context context, QueryFactory<PhotoComment> q) {
		super(context, q);
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
