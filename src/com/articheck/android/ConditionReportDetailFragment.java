package com.articheck.android;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.articheck.android.ConditionReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TableRow;
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
public class ConditionReportDetailFragment extends Fragment implements OnClickListener
{
    final static String FRAGMENT_TAG = "fragment_condition_report_detail";
    private Map<String, View> lookup_text_to_view = null;
    private Map<String, View> lookup_check_to_view = null;
    private Map<String, View> lookup_radio_to_view = null;
    private Map<String, View> lookup_section_name_to_view = null;
    private Map<View, String> lookup_button_view_to_section_name = null;
    
    private ConditionReport mConditionReport = null;
    private ScrollView detail_scroll_view = null; 
    
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
            Log.d(TAG, "Resuming with a non-null ConditionReport.");
            try {
                updateContent();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "exception updating content", e);
            } // try/catch            
        } 
        else
        {
            Log.d(TAG, "Resuming with a null ConditionReport.");
            final FragmentManager fm = getFragmentManager();
            final ConditionReportsFragment f = (ConditionReportsFragment)fm.findFragmentByTag(ConditionReportsFragment.FRAGMENT_TAG);
            FragmentTransaction ft = fm.beginTransaction();            
            View v = getActivity().findViewById(R.id.first_pane);
            Log.d(TAG, "View v: " + v);
            Log.d(TAG, "ConditionReportsFragment f: " + f);            
            ft.show(f);
            v.setVisibility(View.VISIBLE);               
            ft.commit();
        } // if (mConditionReport != null)
    } // public void onResume()
    
    @Override
    public void onPause() {
        super.onPause();
        final String TAG = getClass().getName() + "::onPause()";
        Log.d(TAG, "Entry.");        
    }    

    private View getRenderedSection(JSONArray json_template, Activity activity) throws JSONException
    {
        final String TAG = getClass().getName() + "::getRenderedSection";
        Log.d(TAG, "Entry");
        
        assert(json_template != null);
        
        GsonBuilder gsonb = new GsonBuilder();
        Gson gson = gsonb.create();      
        Resources resources = activity.getResources();
        TableLayout table_layout = new TableLayout(activity);
        table_layout.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, 
                                                                  TableLayout.LayoutParams.WRAP_CONTENT));
        
        int template_size = json_template.length();
        Log.d(TAG, "template_size: " + template_size);                
        for (int i = 0; i < template_size; ++i)
        {
            JSONObject element = json_template.getJSONObject(i);
            String type = element.getString("type");                    
            Log.d(TAG, "Index: " + i + ", type is: " + type);
            
            // ---------------------------------------------------------
            //  Create the row that holds the label and edit fields.
            // ---------------------------------------------------------
            TableRow row_view = new TableRow(activity);
            row_view.setPadding(0, 20, 0, 20);
            // ---------------------------------------------------------                        
            
            // ---------------------------------------------------------
            //  Left-hand label describing the text field.
            // ---------------------------------------------------------
            TextView label_view = new TextView(activity);
            label_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                        LayoutParams.WRAP_CONTENT));
            String name = element.getString("name");
            label_view.setText(name);            
            TableRow.LayoutParams label_lp = new TableRow.LayoutParams();
            row_view.addView(label_view, label_lp);                    
            // ---------------------------------------------------------
            
            if (type.equals("text"))
            {
                Log.d(TAG, "Index: " + i + ", is a text field");
                
                // -----------------------------------------------------
                //  Right-hand editable text field.
                // -----------------------------------------------------                        
                EditText text_view = new EditText(activity);
                text_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                           LayoutParams.WRAP_CONTENT));
                text_view.setBackgroundDrawable(resources.getDrawable(R.drawable.textfield));
                text_view.setPadding(10, 10, 10, 10);
                
                // Set the minimum and maximum width to the same value, as we
                // don't want the text view to resize based on its contents.
                text_view.setMinWidth(300);
                // -----------------------------------------------------                        
                
                TableRow.LayoutParams text_lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 
                                                                          TableRow.LayoutParams.WRAP_CONTENT);
                row_view.addView(text_view, text_lp);                
                lookup_text_to_view.put(name, text_view);
            } 
            else if (type.equals("check"))
            {
                // -----------------------------------------------------
                //  Right-hand check boxes.
                // -----------------------------------------------------                        
                Log.d(TAG, "Index: " + i + ", are check boxes.");
                JSONArray values = element.getJSONArray("values");
                Log.d(TAG, "Check box values: " + values);
                Type collection_type = new TypeToken<Collection<String>>(){}.getType();
                Collection<String> decoded_values = gson.fromJson(values.toString(), collection_type);
                Log.d(TAG, "Decoded values: " + decoded_values);
                
                LinearLayout linear_layout = new LinearLayout(activity);
                linear_layout.setOrientation(Configuration.ORIENTATION_PORTRAIT);                                                
                TableRow.LayoutParams linear_layout_lp = new TableRow.LayoutParams();
                
                for (String value : decoded_values)
                {
                    CheckBox check_box = new CheckBox(activity);
                    LinearLayout.LayoutParams check_box_lp = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                                                                             LayoutParams.MATCH_PARENT));
                    check_box.setText(value);
                    linear_layout.addView(check_box, check_box_lp);
                } // for (String value : decoded_values)                        
                row_view.addView(linear_layout, linear_layout_lp);
                
                //lookup_check_to_view(internal_name, check_box);
            } 
            else if (type.equals("radio"))
            {
                // -----------------------------------------------------
                //  Right-hand radio group.
                // -----------------------------------------------------                        
                Log.d(TAG, "Index: " + i + ", is radio group.");                        
                JSONArray values = element.getJSONArray("values");                        
                Log.d(TAG, "Radio group values: " + values);
                Type collection_type = new TypeToken<Collection<String>>() {}.getType();
                Collection<String> decoded_values = gson.fromJson(values.toString(), collection_type);
                Log.d(TAG, "Decoded values: " + decoded_values);                        
                
                RadioGroup radio_group = new RadioGroup(activity);
                for (String value : decoded_values)
                {
                    RadioButton radio_button = new RadioButton(activity);
                    radio_button.setText(value);                            
                    radio_group.addView(radio_button);
                } // for (String value : decoded_values)                        
                TableRow.LayoutParams radio_group_lp = new TableRow.LayoutParams();
                row_view.addView(radio_group, radio_group_lp);                        
            } // if (type of template)                    
            
            TableLayout.LayoutParams row_lp = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, 
                                                                           TableLayout.LayoutParams.WRAP_CONTENT);
            table_layout.addView(row_view, row_lp);            
        } // for (int i = 0; i < template_size; ++i)
        
        table_layout.setLongClickable(true);
        table_layout.setOnLongClickListener(new View.OnLongClickListener() {
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
                    //ft.hide(f);                    
                    v.setVisibility(View.GONE);                               
                } 
                else   
                {
                    //ft.show(f);
                    v.setVisibility(View.VISIBLE);
                } // if (c.isVisible())
                
                // TODO this doesn't work, can't back out.
                ft.addToBackStack(null)
                  .commit();
                return true;
            }
        });        
        
        Log.d(TAG, String.format("Returning: %s", table_layout));
        return table_layout;
    } // private View getRenderedSection(JSONArray json_template, Activity activity)
    
    
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

        Activity activity = getActivity();
        Resources resources = activity.getResources();
        Configuration configuration = resources.getConfiguration();
        
        // ---------------------------------------------------------------------
        //  Set up the containing view.
        // ---------------------------------------------------------------------
        LinearLayout top_view = new LinearLayout(activity);
        top_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
                                                               LinearLayout.LayoutParams.MATCH_PARENT));        
        top_view.setPadding(resources.getDimensionPixelSize(R.dimen.body_padding_large),
                            resources.getDimensionPixelSize(R.dimen.body_padding_medium),
                            resources.getDimensionPixelSize(R.dimen.body_padding_large),
                            resources.getDimensionPixelSize(R.dimen.body_padding_medium));
        top_view.setOrientation(LinearLayout.VERTICAL);
        TextView top_view_title = new TextView(activity);
        top_view_title.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 
                                                        LayoutParams.WRAP_CONTENT));
        if (mConditionReport != null)
        {
            String top_view_title_contents = mConditionReport.getTitle(); 
            Log.d(TAG, String.format("Setting top_view title to '%s'.", top_view_title_contents));
            top_view_title.setText(top_view_title_contents);            
            top_view_title.setTextAppearance(activity, R.style.TextHeader);
        } // if (mConditionReport != null)        
        // ---------------------------------------------------------------------

        // ---------------------------------------------------------------------
        //  Create the horizontally-scrollable view of button for sections.
        // ---------------------------------------------------------------------
        HorizontalScrollView section_button_view = new HorizontalScrollView(activity);
        section_button_view.setFillViewport(true);
        section_button_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                             LayoutParams.WRAP_CONTENT));
        section_button_view.setPadding(0, 20, 0, 0);
        LinearLayout linear_section_button_view = new LinearLayout(activity);
        linear_section_button_view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 
                                                                                 LinearLayout.LayoutParams.MATCH_PARENT));
        linear_section_button_view.setOrientation(LinearLayout.HORIZONTAL);   
        if (mConditionReport != null)
        {
            Log.d(TAG, "Condition report is not null so populate section button bar");
            lookup_button_view_to_section_name = new LinkedHashMap<View, String>();
            for(String section_name : mConditionReport.getTemplateSectionNames())
            {
                Log.d(TAG, String.format("Adding section %s to section button bar.", section_name));
                Button button = new Button(activity);
                button.setText(section_name);
                button.setOnClickListener(this);
                linear_section_button_view.addView(button);
                lookup_button_view_to_section_name.put(button, section_name);
            } // for(String section_name : mConditionReport.getTemplateSectionNames())
        } // if (mConditionReport != null)        
        // ---------------------------------------------------------------------        
        
        // ---------------------------------------------------------------------
        //  This is the view that contains the content of the condition report.
        // ---------------------------------------------------------------------        
        detail_scroll_view = new ScrollView(activity);
        detail_scroll_view.setFillViewport(true);
        detail_scroll_view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                            LayoutParams.MATCH_PARENT));
        // ---------------------------------------------------------------------
       
        // ---------------------------------------------------------------------
        //  Instantiate the member variables that track views by their
        //  JSON field name.
        // ---------------------------------------------------------------------        
        lookup_text_to_view = new LinkedHashMap<String, View>();
        lookup_check_to_view = new LinkedHashMap<String, View>();
        lookup_radio_to_view = new LinkedHashMap<String, View>();        
        lookup_section_name_to_view = new LinkedHashMap<String, View>();
        // ---------------------------------------------------------------------
        
        
        if (mConditionReport != null)
        {
            Log.d(TAG, "Condition report is not null.");        
            for(String section_name : mConditionReport.getTemplateSectionNames())
            {
                Log.d(TAG, String.format("Add section name '%s'.", section_name));
                JSONArray json_template = mConditionReport.getTemplateSection(section_name);
                View view;
                try
                {
                    view = getRenderedSection(json_template, activity);
                    Log.d(TAG, String.format("lookup_section_to_view: put name '%s' as view '%s'", section_name, view));
                    lookup_section_name_to_view.put(section_name, view);
                }
                catch (JSONException e)
                {                    
                    Log.e(TAG, "Exception on decoding JSON template.", e);
                    return null;
                }
                
                if (section_name.equals("Basic info"))
                {
                    detail_scroll_view.addView(view);
                } // if (section_name.equals("Basic info"))
                
            } // for(String section_name : mConditionReport.getTemplateSectionNames())
        } // if (mConditionReport != null)
        
        // ---------------------------------------------------------------------
        //  Connect together all the views.
        // ---------------------------------------------------------------------        
        section_button_view.addView(linear_section_button_view);

        top_view.addView(top_view_title);
        top_view.addView(section_button_view);
        top_view.addView(detail_scroll_view);
        // ---------------------------------------------------------------------        
                
        Log.d(TAG, "Returning: " + top_view);        
        return top_view;
    } // public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)    
    
    /**
     * Handle clicks on the buttons that represent the sections for a condition
     * report.
     * 
     * Keep in mind that this will probably be extended to handle
     * clicks on other views for which this fragment is registered as the
     * click handler.
     */
    public void onClick(View v)
    {
        final String TAG = getClass().getName() + "::onClick";
        Log.d(TAG, String.format("Entry. View: '%s'", v));
        
        if (lookup_button_view_to_section_name.containsKey(v))
        {
            Log.d(TAG, "Identified the view as a section button.");
            String section_name = lookup_button_view_to_section_name.get(v);
            Log.d(TAG, String.format("Section name is '%s'", section_name));            
            if (lookup_section_name_to_view.containsKey(section_name))
            {
                View new_child_view = lookup_section_name_to_view.get(section_name);
                Log.d(TAG, String.format("Section's view is '%s'", new_child_view));                
                detail_scroll_view.removeAllViews();                
                detail_scroll_view.addView(new_child_view);
            } // if (lookup_section_name_to_view.containsKey(section_name))
        } // if (!lookup_section_name_to_view.containsKey(v))        
    } // public void onClick(View v)
    
    /**
     * 
     * Update the contents of the condition report detail.
     * 
     * @throws JSONException 
     */
    private void updateContent() throws JSONException
    {
        // Get and update the title.
        final String TAG = getClass().getName() + "::updateContent";
        Log.d(TAG, String.format("entry.  condition_report: '%s'", mConditionReport));
        
        View v = getView();
        Log.d(TAG, String.format("getView() result: '%s'", v));
        
        JSONObject json_object = mConditionReport.getDecodedContents();
        Log.d(TAG, "lookup_field_to_value size: " + lookup_text_to_view.size());
        for (Map.Entry<String, View> entry : lookup_text_to_view.entrySet())
        {
            String key = entry.getKey();
            EditText view = (EditText) entry.getValue();
            Log.d(TAG, "Updating View with label: " + key);
            if (!json_object.isNull(key))
            {
                Log.d(TAG, "Key: " + key + " exists in condition report JSON.");
                String value = json_object.getString(key);            
                Log.d(TAG, "Setting TextView " + view + " to value " + value);
                view.setText(value);
            } // if (!json_object.isNull(entry.getKey()))
        } // for (Map.Entry<String, View> entry : lookup_text_to_view.entrySet())        
    } // public void updateContent(ConditionReport condition_report) throws JSONException
        
} // public class ConditionReportsFragment extends ListFragment