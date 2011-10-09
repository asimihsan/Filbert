package com.articheck.android.widgets.uitableview;

import android.view.View;

public class ViewItem implements IListItem {
	
	private boolean mClickable = true;
	private View mView;
	
	public ViewItem(View view, boolean clickable)
	{
		this.mView = view;
		this.mClickable = clickable;
	}
	
	public View getView() {
		return this.mView;
	}

	public boolean isClickable() {
		return mClickable;
	}

	public void setClickable(boolean clickable) {
		mClickable = clickable;		
	}
	
}
