package com.articheck.android;

import java.util.LinkedHashMap;
import java.util.Map;

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
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
    private LinkedHashMap<String, View> lookup_field_to_view = null;
    private ConditionReport mConditionReport = null;
    private JSONArray jsonTemplate;
    
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

    
    /* (non-Javadoc)
     * @see android.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     * 
     * References:
     * 
     * http://stackoverflow.com/questions/2305395/laying-out-views-in-relativelayout-programmatically
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final String TAG = getClass().getName() + "::onCreateView";
        Log.d(TAG, "Entry");
        
        //mContentView = inflater.inflate(R.layout.fragment_condition_report_detail, null);
        jsonTemplate = null;
        if (mConditionReport != null)
        {
            try {
                jsonTemplate = new JSONArray(mConditionReport.template_contents);
            } catch (JSONException e) {
                Log.e(TAG, "Exception while decoding template contents", e);
                return null;
            }
        } // if (mConditionReport != null)      
        Log.d(TAG, "jsonTemplate: " + jsonTemplate);
        
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
        
        RelativeLayout relative_layout = new RelativeLayout(getActivity());
        relative_layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, 
                                                                        RelativeLayout.LayoutParams.WRAP_CONTENT));        
        if (jsonTemplate != null)
        {
            Log.d(TAG, "Setting up view with JSON template.");
            try {
                lookup_field_to_view = new LinkedHashMap<String, View>();
                int template_size = jsonTemplate.length();
                Log.d(TAG, "template_size: " + template_size);
                for (int i = 0; i < template_size; ++i)
                {
                    JSONObject element = jsonTemplate.getJSONObject(i);
                    String type = element.getString("type");
                    Log.d(TAG, "Index: " + i + ", type is: " + type);
                    if (type.equals("text"))
                    {
                        Log.d(TAG, "Index: " + i + ", is a text field");
                        
                        TextView text_view = new TextView(getActivity());
                        text_view.setId(i+1);
                        text_view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
                                                                   LayoutParams.WRAP_CONTENT));
                        String internal_name = element.getString("internal_name");
                        if (internal_name.equals("title"))
                        {
                            Log.d(TAG, "Index " + i + " is the title.");
                            text_view.setTextAppearance(activity, R.style.TextHeader);
                        } // if (internal_name.equals("title"))
                        
                        Log.d(TAG, "Adding TextView " + text_view + " to RelativeLayout " + relative_layout);
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                                                                                         RelativeLayout.LayoutParams.WRAP_CONTENT);
                        if (i > 0)
                        {
                            lp.addRule(RelativeLayout.BELOW, i);    
                        } // if (i > 0)                        
                        relative_layout.addView(text_view, lp);                        
                        lookup_field_to_view.put(internal_name, text_view);                    
                    } // if (type == "text")
                    
                } // for (int i = 0; i < template_size; ++i)
            } catch (JSONException e) {
                Log.e(TAG, "Exception creating view from template.", e);
                return null;
            } // try/catch
        } // if (jsonTemplate != null)        
        
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
        ((ViewGroup) mContentView).addView(relative_layout);
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
        
        JSONObject json_object = new JSONObject(condition_report.contents);
        Log.d(TAG, "lookup_field_to_value size: " + lookup_field_to_view.size());
        for (Map.Entry<String, View> entry : lookup_field_to_view.entrySet())
        {
            String value = json_object.getString(entry.getKey());
            TextView view = (TextView) entry.getValue();
            Log.d(TAG, "Setting TextView " + view + " to value " + value);
            view.setText(value);
        } // for (Map.Entry<String, View> entry : lookup_field_to_view.entrySet())        
    }
        
} // public class ConditionReportsFragment extends ListFragment