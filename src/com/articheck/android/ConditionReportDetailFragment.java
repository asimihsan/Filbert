package com.articheck.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
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
    private View mContentView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    } // public void onActivityCreated(Bundle savedInstanceState)
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        mContentView = inflater.inflate(R.layout.fragment_condition_report_detail, null);
        
        mContentView.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {
                final FragmentManager fm = getFragmentManager();
                final ConditionReportsFragment f = (ConditionReportsFragment)fm.findFragmentById(R.id.fragment_condition_reports);
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);                
                if (f.isVisible())
                {                    
                    ft.hide(f);                    
                } 
                else   
                {
                    ft.show(f);
                } // if (c.isVisible())
                ft.commit();                
                return true;
            }
        });
        
        return mContentView;
    } // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    
    
    /**
     * @param title Title of the condition report.
     */
    public void updateContent(String title)
    {
        // Get and update the title.
        TextView t = (TextView)getView().findViewById(R.id.condition_report_title);
        t.setText(title);        
    }
        
} // public class ConditionReportsFragment extends ListFragment