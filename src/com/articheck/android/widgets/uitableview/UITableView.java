package com.articheck.android.widgets.uitableview;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.articheck.android.R;
import com.articheck.android.widgets.uitableview.BasicItem;
import com.articheck.android.widgets.uitableview.IListItem;
import com.articheck.android.widgets.uitableview.ViewItem;

public class UITableView extends LinearLayout {
	
    private Context mContext;
	private int mIndexController = 0;
	private LayoutInflater mInflater;
	private LinearLayout mMainContainer;
	private LinearLayout mListContainer;
	private List<IListItem> mItemList;
	private ClickListener mClickListener;
	private String mTitle;
	private Typeface mTitleTypeface;
	
    public UITableView(Context context)
    {
        this(context, null, 0);
    }
    
    public UITableView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);        
    }
    
    public UITableView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);        
        initialize(context);
    }	
    
    public void setTitle(String title)
    {
        this.mTitle = title;
    }
    
    public void setTitleTypeface(Typeface typeface)
    {
        this.mTitleTypeface = typeface;
    }
    
    private void initialize(Context context)
    {
        this.mContext = context;
        mItemList = new ArrayList<IListItem>();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMainContainer = (LinearLayout)  mInflater.inflate(R.layout.list_container, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        addView(mMainContainer, params);                
        mListContainer = (LinearLayout) mMainContainer.findViewById(R.id.buttonsContainer);    
    } // private void initialize(Context context)	

    /**
	 * 
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(String title, String summary)
	{
		mItemList.add(new BasicItem(title, summary));
	}
	
	/**
	 * 
	 * @param drawable
	 * @param title
	 * @param summary
	 */
	public void addBasicItem(int drawable, String title, String summary)
	{
		mItemList.add(new BasicItem(drawable, title, summary));
	}
	
	/**
	 * 
	 * @param item
	 */
	public void addBasicItem(BasicItem item) {
		mItemList.add(item);
	}
	
	/**
	 * 
	 * @param itemView
	 */
	public void addViewItem(ViewItem itemView) {
		mItemList.add(itemView);
	}
	
	public void commit() {
	    if (mTitle != null)
	    {
	        TextView text_view = (TextView) mInflater.inflate(R.layout.list_header, null);
	        text_view.setText(mTitle);
	        if (mTitleTypeface != null)
	        {
	            text_view.setTypeface(this.mTitleTypeface);    
	        } // if (mTitleTypeface != null)	                
	        mMainContainer.addView(text_view, 0);	        
	    } // if (mTitle != null)
	    
		mIndexController = 0;		
		if(mItemList.size() > 1) {
			//when the list has more than one item
			for(IListItem obj : mItemList) {
				View tempItemView;
				if(mIndexController == 0) {
					tempItemView = mInflater.inflate(R.layout.list_item_top, null);
				}
				else if(mIndexController == mItemList.size()-1) {
					tempItemView = mInflater.inflate(R.layout.list_item_bottom, null);
				}
				else {
					tempItemView = mInflater.inflate(R.layout.list_item_middle, null);
				}	
				setupItem(tempItemView, obj, mIndexController);
				tempItemView.setClickable(obj.isClickable());
				mListContainer.addView(tempItemView);
				mIndexController++;
			}
		}
		else if(mItemList.size() == 1) {
			//when the list has only one item
			View tempItemView = mInflater.inflate(R.layout.list_item_single, null);
			IListItem obj = mItemList.get(0);
			setupItem(tempItemView, obj, mIndexController);
			tempItemView.setClickable(obj.isClickable());
			mListContainer.addView(tempItemView);
		}
	}
	
	private void setupItem(View view, IListItem item, int index) {
		if(item instanceof BasicItem) {
			BasicItem tempItem = (BasicItem) item;
			setupBasicItem(view, tempItem, mIndexController);
		}
		else if(item instanceof ViewItem) {
			ViewItem tempItem = (ViewItem) item;
			setupViewItem(view, tempItem, mIndexController);
		}
	}
	
	/**
	 * 
	 * @param view
	 * @param item
	 * @param index
	 */
	private void setupBasicItem(View view, BasicItem item, int index) {
		if(item.getDrawable() > -1) {
			((ImageView) view.findViewById(R.id.image)).setBackgroundResource(item.getDrawable());
		}
		if(item.getSubtitle() != null) {
			((TextView) view.findViewById(R.id.subtitle)).setText(item.getSubtitle());
		}
		else {
			((TextView) view.findViewById(R.id.subtitle)).setVisibility(View.GONE);
		}		
		((TextView) view.findViewById(R.id.title)).setText(item.getTitle());
		view.setTag(index);
		if(item.isClickable()) {
			view.setOnClickListener( new View.OnClickListener() {
			    
				public void onClick(View view) {
					if(mClickListener != null)
						mClickListener.onClick((Integer) view.getTag());
				}
				
			});	
		}
		else {
			((ImageView) view.findViewById(R.id.chevron)).setVisibility(View.GONE);
		}
	}
	
	/**
	 * 
	 * @param view
	 * @param itemView
	 * @param index
	 */
	private void setupViewItem(View view, ViewItem itemView, int index) {
		if(itemView.getView() != null) {
			LinearLayout itemContainer = (LinearLayout) view.findViewById(R.id.itemContainer);
			itemContainer.removeAllViews();
			//itemContainer.removeAllViewsInLayout();
			itemContainer.addView(itemView.getView());
		}
	}
	
	public interface ClickListener {		
		void onClick(int index);		
	}
	
	/**
	 * 
	 * @return
	 */
	public int getCount() {
		return mItemList.size();
	}
	
	/**
	 * 
	 */
	public void clear() {
		mItemList.clear();
		mListContainer.removeAllViews();
	}
	
	/**
	 * 
	 * @param listener
	 */
	public void setClickListener(ClickListener listener) {
		this.mClickListener = listener;
	}
	
	/**
	 * 
	 */
	public void removeClickListener() {
		this.mClickListener = null;
	}

}
