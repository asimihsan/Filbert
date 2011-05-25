package com.articheck.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    private String title = null;
    
    public ConditionReportDetailFragment()
    {
        super();
    }

    public ConditionReportDetailFragment(String title)
    {
        super();
        this.title = title;        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        final String TAG = getClass().getName() + "::onActivityCreated";
        Log.d(TAG, "Entry");        
        super.onActivityCreated(savedInstanceState);
        
        // If title is not null we were passed in some content to use
        // on start up.
        if (title != null)
        {
            Log.d(TAG, "Update title to: " + title);
            updateContent(title);
        }
    } // public void onActivityCreated(Bundle savedInstanceState)
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final String TAG = getClass().getName() + "::onCreateView";
        
        mContentView = inflater.inflate(R.layout.fragment_condition_report_detail, null);     
        

        mContentView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                final FragmentManager fm = getFragmentManager();
                final ConditionReportsFragment f = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
                View v = getActivity().findViewById(R.id.first_pane);
                FragmentTransaction ft = fm.beginTransaction();
                
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
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
     */
    public void updateContent(String title)
    {
        // Get and update the title.
        final String TAG = getClass().getName() + "::updateContent";        
        View v = getView();
        Log.d(TAG, "getView() result: " + v);        
        TextView t = (TextView)v.findViewById(R.id.condition_report_title);
        Log.d(TAG, "TextView t: " + t);
        
        t.setText(title);        
    }
        
} // public class ConditionReportsFragment extends ListFragment