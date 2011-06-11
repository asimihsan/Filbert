package com.articheck.android;

import com.articheck.android.DatabaseManager.ConditionReport;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private View mContentView;
    private ConditionReport mConditionReport = null;
    
    public ConditionReportDetailFragment()
    {
        super();        
    }

    public ConditionReportDetailFragment(ConditionReport condition_report)
    {
        super();
        this.mConditionReport = condition_report;        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        final String TAG = getClass().getName() + "::onActivityCreated";
        Log.d(TAG, "Entry");        
        super.onActivityCreated(savedInstanceState);
        
        // If title is not null we were passed in some content to use
        // on start up.
        if (mConditionReport != null)
        {
            Log.d(TAG, "Update condition_report using: " + mConditionReport);
            try {
                updateContent(mConditionReport);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "exception updating content", e);
            }
        }
    } // public void onActivityCreated(Bundle savedInstanceState)
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final String TAG = getClass().getName() + "::onCreateView";
        Log.d(TAG, "Entry");        
        mContentView = inflater.inflate(R.layout.fragment_condition_report_detail, null);
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
                ft.commit();               
     
                return true;
            }
        });
        
        Log.d(TAG, "Returning: " + mContentView);
        return mContentView;
    } // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    
    
    /**
     * @param title Title of the condition report.
     * @throws JSONException 
     */
    public void updateContent(ConditionReport condition_report) throws JSONException
    {
        // Get and update the title.
        final String TAG = getClass().getName() + "::updateContent";
        Log.d(TAG, "entry.  condition_report: " + condition_report);
        
        View v = getView();
        Log.d(TAG, "getView() result: " + v);        
        TextView t = (TextView)v.findViewById(R.id.condition_report_title);
        Log.d(TAG, "TextView t: " + t);
        
        JSONObject json_object = new JSONObject(condition_report.contents);
        String title = json_object.getString("title");
        t.setText(title);        
    }
        
} // public class ConditionReportsFragment extends ListFragment