package com.articheck.android;

import com.articheck.android.DatabaseManager.ConditionReport;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/** Display the details of the condition report selected in
 * ConditionReportFragments. 
 * 
 * @author Asim Ihsan
 *
 */
public class ConditionReportDetailFragment extends Fragment
{
    final static String FRAGMENT_TAG = "fragment_condition_report_detail";
    private View  mContentView;
    private TextView mText;
    private ConditionReport mConditionReport = null;
    
    public ConditionReportDetailFragment()
    {
        super();
    }

    public ConditionReportDetailFragment(ConditionReport condition_report)
    {
        super();
        final String TAG = getClass().getName() + "Constructor with ConditionReport";
        Log.d(TAG, "Entry.  condition_report: " + condition_report);        
        this.mConditionReport = condition_report;        
    }
    
    @Override
    public void onResume()
    {
        final String TAG = getClass().getName() + "::onResume";
        Log.d(TAG, "Entry");        
        super.onResume();
        Log.d(TAG, "Update condition_report using: " + mConditionReport);
        if (mConditionReport != null) 
        {
            try {
                updateContent(mConditionReport);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "exception updating content", e);
            } // try/catch            
        } // if (mConditionReport != null)
    } // public void onResume()

    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final String TAG = getClass().getName() + "::onCreateView";
        Log.d(TAG, "Entry");
        
        //mContentView = inflater.inflate(R.layout.fragment_condition_report_detail, null);
        JSONObject json_object_template;
        if (mConditionReport != null)
        {
            try {
                json_object_template = new JSONObject(mConditionReport.template_contents);
            } catch (JSONException e) {
                Log.e(TAG, "Exception while decoding template contents", e);
                return null;
            }
        } // if (mConditionReport != null)        
        
        Activity activity = getActivity();
        Resources resources = activity.getResources();        

        mContentView = new ScrollView(getActivity());
        ((ScrollView) mContentView).setFillViewport(true);
        mContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                      resources.getDimensionPixelSize(R.dimen.thickbar_height)));
        mContentView.setPadding(resources.getDimensionPixelSize(R.dimen.body_padding_large),
                                resources.getDimensionPixelSize(R.dimen.body_padding_medium),
                                resources.getDimensionPixelSize(R.dimen.body_padding_large),
                                resources.getDimensionPixelSize(R.dimen.body_padding_medium));
        
        LinearLayout linear_layout = new LinearLayout(getActivity());
        linear_layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
                                                        LayoutParams.WRAP_CONTENT));        
        ((ViewGroup) mContentView).addView(linear_layout);
        
        mText = new TextView(getActivity());
        mText.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
                                               LayoutParams.WRAP_CONTENT));        
        mText.setTextAppearance(activity, R.style.TextHeader);
        linear_layout.addView(mText);        
        
        mContentView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                final String TAG = getClass().getName() + "::onCreateView::onLongClick";
                Log.d(TAG, "Entry");
                
                final FragmentManager fm = getFragmentManager();
                final ConditionReportsFragment f = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                
                View v = getActivity().findViewById(R.id.first_pane);
                Log.d(TAG, "View v: " + v);
                Log.d(TAG, "ConditionReportsFragment f: " + f);               
                if (f.isVisible())
                {                    
                    ft.hide(f);                    
                    v.setVisibility(View.GONE);                               
                } 
                else   
                {
                    ft.show(f);
                    v.setVisibility(View.VISIBLE);
                } // if (c.isVisible())
                
                // TODO this doesn't work, can't back out.
                ft.addToBackStack(null)
                  .commit();
                return true;
            }
        });
        
        Log.d(TAG, "Returning: " + mContentView);
        return mContentView;
    } // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    
    
    /**
     * @param condition_report 
     * @throws JSONException 
     */
    public void updateContent(ConditionReport condition_report) throws JSONException
    {
        // Get and update the title.
        final String TAG = getClass().getName() + "::updateContent";
        Log.d(TAG, "entry.  condition_report: " + condition_report);
        
        View v = getView();
        Log.d(TAG, "getView() result: " + v);        
        
        //TextView t = (TextView)v.findViewById(R.id.condition_report_title);
        TextView t = mText;
        Log.d(TAG, "TextView t: " + t);
        
        JSONObject json_object = new JSONObject(condition_report.contents);
        String title = json_object.getString("title");
        t.setText(title);                
    }
        
} // public class ConditionReportsFragment extends ListFragment