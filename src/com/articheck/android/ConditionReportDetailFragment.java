package com.articheck.android;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.articheck.android.ConditionReport;
import com.articheck.android.utilities.Json;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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

class Field
{
    private final String section_name;
    private final String field_name;
    private final String type;    
    private final View single_view;
    private final List<String> value_labels;
    private final List<View> value_views;
    private OnClickListener on_click_listener = null;
    private OnCheckedChangeListener on_checked_change_listener = null;
    private OnFocusChangeListener on_focus_change_listener = null;
    private TextWatcher text_watcher = null;    
    private BiMap<String, View> bi_lookup_label_to_view;
    
    private static final ImmutableSet<String> text_types =
        new ImmutableSet.Builder<String>()
            .add("text")
            .build();    
    
    private static final ImmutableSet<String> types_with_values =
        new ImmutableSet.Builder<String>()
            .add("check")
            .add("radio")
            .build();    
    
    public String getSectionName()
    {
        return section_name;
    }

    public String getFieldName()
    {
        return field_name;
    }    

    public String getType()
    {
        return type;
    }

    public List<String> getValueLabels()
    {
        return new ArrayList<String>(value_labels);       
    }
    
    public String getValueLabel(int index)
    {
        if ((index < 0) || (index >= value_labels.size()))
        {
            return null;
        } // if ((index < 0) || (index >= value_labels.size()))
        return value_labels.get(index);
    }
    
    public List<View> getValueViews()
    {
        return new ArrayList<View>(value_views);       
    }
    
    public View getValueView(int index)
    {
        if ((index < 0) || (index >= value_views.size()))
        {
            return null;
        } // if ((index < 0) || (index >= value_labels.size()))
        return value_views.get(index);
    }    
    
    public View getViewFromLabel(String label)
    {
        return bi_lookup_label_to_view.get(label);
    }
    
    public String getLabelFromView(View view)
    {
        return bi_lookup_label_to_view.inverse().get(view);
    }    
    
    public OnClickListener getOnClickListener()
    {
        return on_click_listener;
    } // public OnClickListener getOnClickListener()
    
    public OnCheckedChangeListener getOnCheckedChangeListener()
    {
        return on_checked_change_listener;
    }
    
    public OnFocusChangeListener getOnFocusChangeListener()
    {
        return on_focus_change_listener;
    }    
    
    public TextWatcher getTextWatcher()
    {
        return text_watcher;
    } // public TextWatcher getTextWatcher()
    
    public View getSingleView()
    {
        return single_view;
    }
    
    public static class Builder
    {
        // Required parameters.
        private String section_name = null;
        private String field_name = null;
        private String type = null;                
        
        // Optional parameters.
        private View single_view = null;
        private List<String> value_labels = null;
        private List<View> value_views = null;
        private TextWatcher text_watcher = null;
        private OnClickListener on_click_listener = null;
        private OnCheckedChangeListener on_checked_change_listener = null;
        private OnFocusChangeListener on_focus_change_listener = null;
        
        // Valid field types.
        private static final ImmutableSet<String> valid_types =
            new ImmutableSet.Builder<String>()
                .add("text")
                .add("check")
                .add("radio")
                .build();
        private static final ImmutableSet<String> types_with_values =
            new ImmutableSet.Builder<String>()
                .add("check")
                .add("radio")
                .build();
        private static final ImmutableSet<String> text_types =
            new ImmutableSet.Builder<String>()
                .add("text")
                .build();        
        
        public Builder sectionName(String val)
        {
            this.section_name = val;
            return this;
        } // public Builder section_name(String val)
        
        public Builder fieldName(String val)
        {
            this.field_name = val;
            return this;
        } // public Builder field_name(String val)
        
        public Builder type(String val)
        {
            this.type = val;
            return this;
        } // public Builder type(String val)        
        
        public Builder value_labels(List<String> val)
        {
            this.value_labels = new ArrayList<String>(val);
            return this;
        } // public Builder values(List<String> values)
        
        public Builder value_views(List<View> val)
        {
            this.value_views = new ArrayList<View>(val);
            return this;
        } // public Builder values(List<String> values)        
        
        public Builder onClickListener(OnClickListener val)
        {
            this.on_click_listener = val;
            return this;
        } // public Builder onClickListener(OnClickListener val)
        
        public Builder onCheckedChangeListener(OnCheckedChangeListener val)
        {
            this.on_checked_change_listener = val;
            return this;
        } // public Builder onClickListener(OnClickListener val)
        
        public Builder onFocusChangeListener(OnFocusChangeListener val)
        {
            this.on_focus_change_listener = val;
            return this;
        } // public Builder onClickListener(OnClickListener val)
        
        public Builder textWatcher(TextWatcher val)
        {
            this.text_watcher = val;
            return this;
        } // public Builder textWatcher(TextWatcher val)
        
        public Builder singleView(View val)
        {
            this.single_view = val;
            return this;
        } // public Builder singleView(View val)

        public Field build()
        {
            // -----------------------------------------------------------------
            //  Validate required inputs are not null.
            // -----------------------------------------------------------------            
            if (section_name == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "section_name must not be null."));
            } // if (section_name == null)
            if (field_name == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "field_name must not be null."));
            } // if (field_name == null)
            if (type == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "type must not be null."));
            } // if (type == null)            
            // -----------------------------------------------------------------
            
            // -----------------------------------------------------------------
            //  Validate input values.
            // -----------------------------------------------------------------
            if (!valid_types.contains(type))
            {
                throw new IllegalStateException(String.format(Locale.US, "type '%s' is not valid.", type));
            } // if (!valid_types.contains(type))
            // -----------------------------------------------------------------           

            if (types_with_values.contains(type) && (value_labels == null))
            {
                // Checkbox or radio specified, but no corresponding values.
                throw new IllegalStateException(String.format(Locale.US, "type is '%s' but no value labels specified.", type));
            }
            if (types_with_values.contains(type) && (value_views == null))
            {
                // Checkbox or radio specified, but no corresponding values.
                throw new IllegalStateException(String.format(Locale.US, "type is '%s' but no value views specified.", type));
            }                        
            if (types_with_values.contains(type) && (on_checked_change_listener == null))
            {
                throw new IllegalStateException(String.format(Locale.US, "type is '%s' but no on_checked_change_listener specified.", type));
            }            
            
            // -----------------------------------------------------------------
            //  Text views require TextWatcher and OnFocusChangeListener
            //  instances.
            // -----------------------------------------------------------------            
            if (text_types.contains(type) && (text_watcher == null))
            {
                // Text fields need an instance that supports the TextWatcher interface.
                throw new IllegalStateException(String.format(Locale.US, "type is '%s' but no text_watcher.", type));
            }            
            if (text_types.contains(type) && (on_focus_change_listener == null))
            {
                // Text fields need an instance that supports the OnFocusChangeListener interface.
                throw new IllegalStateException(String.format(Locale.US, "type is '%s' but no on_focus_change_listener.", type));
            }            
            // -----------------------------------------------------------------            
            if (!types_with_values.contains(type))
            {
                value_labels = new ArrayList<String>(0);
                value_views = new ArrayList<View>(0);
                if (single_view == null)
                {
                    throw new IllegalStateException(String.format(Locale.US, "type is '%s' but no single_view specified.", type));
                }
            } // if (!type.equals("check") && (!type.equals("radio")))
            if (value_labels.size() != value_views.size())
            {
                throw new IllegalStateException(String.format(Locale.US, "value_labels size '%s' not equal to value_views size '%s'", value_labels.size(), value_views.size()));
            } // if (value_labels.size() != value_views.size())            

            return new Field(this);
        } // public Field build()        
    } // public static class Builder
    
    private Field(Builder builder)
    {
        section_name = builder.section_name;
        field_name = builder.field_name;
        type = builder.type;
        single_view = builder.single_view;
        value_labels = builder.value_labels;
        value_views = builder.value_views;
        on_click_listener = builder.on_click_listener;
        on_checked_change_listener = builder.on_checked_change_listener;
        on_focus_change_listener = builder.on_focus_change_listener;
        text_watcher = builder.text_watcher;
        initialize();                
    } // private Field(Builder builder)
    
    public Field(Field field)
    {
        section_name = field.getSectionName();
        field_name = field.getFieldName();
        type = field.getType();
        single_view = field.getSingleView();
        value_labels = field.getValueLabels();
        value_views = field.getValueViews();
        on_click_listener = field.getOnClickListener();
        on_checked_change_listener = field.getOnCheckedChangeListener();
        on_focus_change_listener = field.getOnFocusChangeListener();
        text_watcher = field.getTextWatcher();
        initialize();
    } // public Field(Field field)
    
    private void initialize()
    {
        int size = value_labels.size();
        bi_lookup_label_to_view = HashBiMap.create(size);        
        for (int i = 0; i < size; i++)
        {
            bi_lookup_label_to_view.put(value_labels.get(i), value_views.get(i));
        } // for (int i = 0; i < size; i++)
        
        if (isTextType())
        {
            assert(text_watcher != null);
            ((TextView)single_view).addTextChangedListener(text_watcher);
            ((TextView)single_view).setOnFocusChangeListener(on_focus_change_listener);
        }
        else if (type.equals("radio"))
        {
            assert(on_checked_change_listener != null);
            assert(value_views.size() > 0);
            RadioButton radio_button = (RadioButton) value_views.get(0);
            RadioGroup parent_radio_group = (RadioGroup) radio_button.getParent();
            parent_radio_group.setOnCheckedChangeListener((android.widget.RadioGroup.OnCheckedChangeListener) on_checked_change_listener);
        }
        else if (type.equals("check"))
        {
            assert(on_checked_change_listener != null);
            assert(value_views.size() > 0);
            for (View view : value_views)
            {
                ((CheckBox)view).setOnCheckedChangeListener(on_checked_change_listener);
            } // for (View view : value_views)
        }        
    } // private void initializeLookup()
    
    @Override 
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("section_name", section_name)
                       .add("field_name", field_name)
                       .add("type", type)
                       .add("single_view", single_view)
                       .add("value_labels", value_labels)
                       .add("value_views", value_views)
                       .add("on_click_listener", on_click_listener)
                       .add("on_checked_change_listener", on_checked_change_listener)
                       .add("on_focus_change_listener", on_focus_change_listener)
                       .add("text_watcher", text_watcher)
                       .toString();        
    } // public String toString()
    
    @Override public int hashCode()
    {
        return Objects.hashCode(section_name,
                                 field_name,
                                 type,
                                 single_view,
                                 value_labels,
                                 value_views,
                                 on_click_listener,
                                 on_checked_change_listener,
                                 on_focus_change_listener,
                                 text_watcher);
    } // @Override public int hashCode()
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        } // if (o == this)
        if (!(o instanceof Field))
        {
            return false;
        } // if (!(o instanceof Field))
        Field field = (Field)o;
        boolean result = (Objects.equal(section_name, field.section_name) &&
                           Objects.equal(field_name, field.field_name) &&
                           Objects.equal(type, field.type) &&
                           Objects.equal(on_click_listener, field.on_click_listener) &&
                           Objects.equal(on_checked_change_listener, field.on_checked_change_listener) &&
                           Objects.equal(on_focus_change_listener, field.on_focus_change_listener) &&
                           Objects.equal(text_watcher, field.text_watcher));
        return result;
    } // public boolean equals(Object o)
    
    public boolean isTextType()
    {
        return text_types.contains(type);
    } // public boolean isTextType()
    
    public boolean isTypeWithValues()
    {
        return types_with_values.contains(type);
    } // public boolean isTypeWithValues()
    
} // class Field

class Section
{
    private final String section_name;
    private final View detail_view;
    
    public String getSectionName()
    {
        return section_name;
    } // public String getSectionName()
    
    public View getDetailView()
    {
        return detail_view;
    } // public List<Field> getFields()
    
    public static class Builder
    {
        // Required fields.
        private String section_name = null;        
        private View detail_view = null;
        
        public Builder sectionName(String val)
        {
            section_name = val;
            return this;
        } // public Builder sectionName(String val)
        
        public Builder detailView(View val)
        {
            detail_view = val;
            return this;
        }
        
        public Section build()
        {
            if (section_name == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "section_name must not be null."));
            } // if (section_name == null)            
            if (detail_view == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "detail_view must not be null."));
            } // if (detail_view == null)          
            
            return new Section(this);
        } // public Section build()        
    } // public static class Builder
    
    private Section(Builder builder)
    {
        section_name = builder.section_name;
        detail_view = builder.detail_view;
    } // private Section(Builder builder)
    
    public Section(Section section)
    {
        section_name = section.getSectionName();
        detail_view = section.getDetailView();
    } // public Section(Section section)
    
    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                       .add("section_name", section_name)
                       .add("detail_view", detail_view)
                       .toString();        
    } // public String toString()
    
    @Override
    public int hashCode()
    {
        return Objects.hashCode(section_name, detail_view);
    } // public int hashCode()
    
    @Override
    public boolean equals(Object o)
    {
        if (o == this)
        {
            return true;
        } // if (o == this)
        if (!(o instanceof Section))
        {
            return false;
        } // if (!(o instanceof Section))
        Section section = (Section)o;
        boolean result = (Objects.equal(section_name, section.section_name) &&
                           Objects.equal(detail_view, section.detail_view));
        return result;        
    } // @Override boolean equals(Object o)    
} // class Section

class ConditionReportState
{
    // Required parameters.    
    private final ConditionReport condition_report;
    private OnClickListener on_click_listener = null;
    private OnCheckedChangeListener on_checked_change_listener = null;
    private OnFocusChangeListener on_focus_change_listener = null;
    private TextWatcher text_watcher = null;
    private DatabaseManager database_manager;
    
    // Internal variables.
    private Multimap<String, Field> lookup_section_name_to_fields = null;    
    private Map<View, Field> lookup_view_to_field = null;
    private Map<View, String> lookup_view_to_label = null;    

    private Map<View, String> lookup_button_view_to_section_name = null;
    private BiMap<Section, String> bi_lookup_section_to_section_name = null;    
    
    private Section currently_selected_section = null;
    private View currently_focused_view = null;    
    
    @Override
    public String toString() 
    {
        return Objects.toStringHelper(this)
                       .add("condition_report", condition_report)
                       .add("on_click_listener", on_click_listener)
                       .add("on_checked_change_listener", on_checked_change_listener)
                       .add("on_focus_change_listener", on_focus_change_listener)
                       .add("text_watcher", text_watcher)
                       .add("lookup_section_name_to_fields", lookup_section_name_to_fields)
                       .add("lookup_view_to_field", lookup_view_to_field)                       
                       .add("lookup_view_to_label", lookup_view_to_label)
                       .add("lookup_button_view_to_section_name", lookup_button_view_to_section_name)
                       .add("bi_lookup_section_to_section_name", bi_lookup_section_to_section_name)
                       .add("currently_selected_section", currently_selected_section)
                       .add("currently_focused_view", currently_focused_view)
                       .add("database_manager", database_manager)
                       .toString();        
    } // public String toString()
    
    public static class Builder
    {
        // Required parameters.
        private ConditionReport condition_report = null;
        private OnClickListener on_click_listener = null;
        private OnCheckedChangeListener on_checked_change_listener = null;
        private OnFocusChangeListener on_focus_change_listener = null;
        private TextWatcher text_watcher = null;
        private DatabaseManager database_manager = null;
        
        public Builder conditionReport(ConditionReport val)
        {
            condition_report = val;  
            return this;
        }
        
        public Builder onClickListener(OnClickListener val)
        {
            on_click_listener = val;  
            return this;
        }
        
        public Builder onCheckedChangeListener(OnCheckedChangeListener val)
        {
            on_checked_change_listener = val;  
            return this;
        }        
        
        public Builder onFocusChangeListener(OnFocusChangeListener val)
        {
            on_focus_change_listener = val;  
            return this;
        }        
        
        public Builder textWatcher(TextWatcher val)
        {
            text_watcher = val;  
            return this;
        }        
        
        public Builder databaseManager(DatabaseManager val)
        {
            database_manager = val;
            return this;
        } // public Builder databaseManager(DatabaseManager val)
        
        public ConditionReportState build()
        {
            if (condition_report == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "condition_report must not be null."));
            } // if (condition_report == null)            
            if (on_click_listener == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "on_click_listener must not be null."));
            } // if (on_click_listener == null)
            if (on_checked_change_listener == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "on_checked_change_listener must not be null."));
            } // if (on_click_listener == null)            
            if (on_focus_change_listener == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "on_focus_change_listener must not be null."));
            } // if (on_click_listener == null)            
            if (text_watcher == null)
            {
                throw new IllegalStateException(String.format(Locale.US, "text_watcher must not be null."));
            } // if (on_click_listener == null)
            
            return new ConditionReportState(this);
        } // public ConditionReportState build()
    } // public static class Builder
    
    private ConditionReportState(Builder builder)
    {
        this.condition_report = builder.condition_report;
        this.on_click_listener = builder.on_click_listener;
        this.on_checked_change_listener = builder.on_checked_change_listener;
        this.text_watcher = builder.text_watcher;
        this.on_focus_change_listener = builder.on_focus_change_listener;
        this.database_manager = builder.database_manager;
        initialize();
    } // private ConditionReportState(Builder builder)
    
    /**
     * Wipe out the internal lookup structures used in condition report state.
     * This starts it off from scratch.
     */
    public void initialize()
    {        
        lookup_section_name_to_fields = ArrayListMultimap.create();
        lookup_view_to_label = Maps.newHashMap();
        lookup_button_view_to_section_name = Maps.newHashMap();
        bi_lookup_section_to_section_name = HashBiMap.create();        
        lookup_view_to_field = Maps.newHashMap();
        currently_selected_section = null;
        currently_focused_view = null;
    } // private void initialize()
    
    public void setDatabaseManager(DatabaseManager val)
    {
        database_manager = val;
    } // public void setDatabaseManager(DatabaseManager val)
    
    public String getTitle()
    {
        return condition_report.getTitle();
    } // public String getTitle()

    public List<String> getTemplateSectionNames()
    {
        return new ArrayList<String>(condition_report.getTemplateSectionNames());
    } // public List<String> getTemplateSectionNames()
    
    public void addEditText(String section_name, String name, View text_view)
    {
        final String TAG = getClass().getName() + "::addEditText";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', name: '%s', text_view: '%s'", section_name, name, text_view));        
        Field field = new Field.Builder()
                               .sectionName(section_name)
                               .fieldName(name)
                               .type("text")
                               .singleView(text_view)
                               .textWatcher(text_watcher)
                               .onFocusChangeListener(on_focus_change_listener)
                               .build();
        lookup_section_name_to_fields.put(section_name, field);
        lookup_view_to_field.put(text_view, field);
    } // public void addEditText(String section_name, String name, View text_view)
    
    public void addCheck(String section_name, String name, List<View> check_box_views, List<String> check_box_labels)
    {
        final String TAG = getClass().getName() + "::addCheck";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s'," +
            		                        "name: '%s', " +
            		                        "check_box_views: '%s', " +
            		                        "check_box_labels: '%s'",
            		                        section_name,
            		                        name,
            		                        check_box_views,
            		                        check_box_labels));
        
        // ---------------------------------------------------------------------
        //  Validate inputs.
        // ---------------------------------------------------------------------
        if (check_box_views.size() != check_box_labels.size())
        {
            throw new IllegalArgumentException(String.format(Locale.US, "Size of check_box_views '%s' not equal to check_box_labels '%s'", check_box_views.size(), check_box_labels.size()));
        } // if (check_box_views.size() != check_box_labels.size())
        // ---------------------------------------------------------------------
        
        Field field = new Field.Builder()
                               .sectionName(section_name)
                               .fieldName(name)
                               .type("check")
                               .value_labels(check_box_labels)
                               .value_views(check_box_views)
                               .onCheckedChangeListener(on_checked_change_listener)
                               .build();
        lookup_section_name_to_fields.put(section_name, field);
        for (View view : check_box_views)
        {
            lookup_view_to_field.put(view, field);
        } // for (View view : check_box_views)
    } // public void addCheck(String section_name, String name, List<View> check_box_views, List<String> check_box_labels)
    
    public void addRadio(String section_name, String name, List<View> radio_button_views, List<String> radio_button_labels)
    {
        final String TAG = getClass().getName() + "::addRadio";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s'," +
                                            "name: '%s', " +
                                            "check_box_views: '%s', " +
                                            "check_box_labels: '%s'",
                                            section_name,
                                            name,
                                            radio_button_views,
                                            radio_button_labels));
        
        // ---------------------------------------------------------------------
        //  Validate inputs.
        // ---------------------------------------------------------------------
        if (radio_button_views.size() != radio_button_labels.size())
        {
            throw new IllegalArgumentException(String.format(Locale.US, "Size of radio_button_views '%s' not equal to radio_button_labels '%s'", radio_button_views.size(), radio_button_labels.size()));
        } // if (check_box_views.size() != check_box_labels.size())
        // ---------------------------------------------------------------------
        
        Field field = new Field.Builder()
                               .sectionName(section_name)
                               .fieldName(name)
                               .type("radio")
                               .value_labels(radio_button_labels)
                               .value_views(radio_button_views)
                               .onCheckedChangeListener(on_checked_change_listener)
                               .build();
        lookup_section_name_to_fields.put(section_name, field);
        for (View view : radio_button_views)
        {
            lookup_view_to_field.put(view, field);
        } // for (View view : check_box_views)        
    } // public void addRadio(String section_name, String name, List<View> radio_button_views, List<String> radio_button_labels)    

    public void addButton(String section_name, Button button)
    {
        final String TAG = getClass().getName() + "::addButton";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', button: '%s'", section_name, button));
        lookup_button_view_to_section_name.put(button, section_name);        
    } // public void addButton(String section_name, Button button)

    public JSONArray getTemplateSection(String section_name)
    {
        final String TAG = getClass().getName() + "::getTemplateSection";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s'", section_name));
        JSONArray return_value = condition_report.getTemplateSection(section_name);
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public JSONArray getTemplateSection(String section_name)

    public void addSection(String section_name, View view)
    {
        final String TAG = getClass().getName() + "::addSection";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', view: '%s'", section_name, view));
        Section section = new Section.Builder()
                                     .sectionName(section_name)
                                     .detailView(view)
                                     .build();
        bi_lookup_section_to_section_name.put(section, section_name);
    } // public void addSection(String section_name, View view)
    
    public boolean isButtonView(View view)
    {
        final String TAG = getClass().getName() + "::isButtonView";
        Log.d(TAG, String.format(Locale.US, "Entry. view: '%s'", view));
        
        boolean return_value = lookup_button_view_to_section_name.containsKey(view);
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public boolean isButtonView(View view)    
    
    public Section getSectionFromButtonView(View view)
    {
        final String TAG = getClass().getName() + "::getSectionFromButtonView";
        Log.d(TAG, String.format(Locale.US, "Entry. view: '%s'", view));      
        
        // ---------------------------------------------------------------------
        //  Validate inputs.
        // ---------------------------------------------------------------------        
        if (!isButtonView(view))
        {
            throw new IllegalArgumentException(String.format(Locale.US, "View '%s' is not a button view!", view));
        } // if (!isButtonView(view))
        // ---------------------------------------------------------------------
        
        String section_name = lookup_button_view_to_section_name.get(view);
        Section section = bi_lookup_section_to_section_name.inverse().get(section_name);
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", section));
        return section;        
    } // public boolean getSectionFromButtonView(View view)

    public JSONObject getDecodedContents() throws JSONException
    {
        final String TAG = getClass().getName() + "::getDecodedContents";
        Log.d(TAG, "Entry");
        
        JSONObject return_value = condition_report.getDecodedContents();
        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public JSONObject getDecodedContents()

    /**
     * Set the currently selected section using a section name.
     * 
     * @param section_name Name of the section.
     * @param detail_scroll_view The parent view to which the contents of the
     * section will be added to.
     */
    public void setSelectedSectionFromSectionName(String section_name, ScrollView detail_scroll_view)
    {
        final String TAG = getClass().getName() + "::setSelectedSection";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', detail_scroll_view: '%s'", section_name, detail_scroll_view));
        Section section = bi_lookup_section_to_section_name.inverse().get(section_name);
        currently_selected_section = section;
        View new_child_view = section.getDetailView();
        detail_scroll_view.removeAllViews();
        detail_scroll_view.addView(new_child_view);
    } // public void setSelectedSection(String section_name, ScrollView detail_scroll_view)
    
    /**
     * Set the currently selected section using a Section instance.
     * 
     * @param section Section instance.
     * @param detail_scroll_view The parent view to which the contents of the
     * section will be added to.
     */
    public void setSelectedSectionFromSection(Section section, ScrollView detail_scroll_view)
    {
        final String TAG = getClass().getName() + "::setSelectedSection";
        Log.d(TAG, String.format(Locale.US, "Entry. section: '%s', detail_scroll_view: '%s'", section, detail_scroll_view));
        currently_selected_section = section;
        View new_child_view = section.getDetailView();
        detail_scroll_view.removeAllViews();
        detail_scroll_view.addView(new_child_view);
    } // public void setSelectedSectionFromSectionName(Section section, ScrollView detail_scroll_view)
    
    /**
     * Get the currently selected section.
     * @return Section instance corresponding to the currently selected
     * section.
     */
    public Section getCurrentlySelectedSection()
    {
        return currently_selected_section;
    } // public Section getCurrentlySelectedSection()
    
    public List<Field> getFieldsFromSectionName(String section_name)
    {
        final String TAG = getClass().getName() + "::getFieldsFromSectionName";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s'", section_name));
        
        ArrayList<Field> return_value = new ArrayList<Field>(lookup_section_name_to_fields.get(section_name));
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));
        return return_value;
    } // public List<Field> getFieldsFromSectionName(String section_name)
    
    public boolean isSingleValueInContents(String section_name, String field_name)
    {
        final String TAG = getClass().getName() + "::isSingleValueInContents";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));        
        return false;
    } // public boolean isSingleValueInContents(String section_name, String field_name)
    
    public boolean isMulipleValuesInContents(String section_name, String field_name)
    {
        final String TAG = getClass().getName() + "::isMulipleValuesInContents";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));        
        return false;
    } // public boolean isMulipleValuesInContents(String section_name, String field_name)
    
    /**
     * Get the value of a section name / field name pair within the current
     * contents of the condition report.  This assumes that the field in
     * question has a string value, as opposed to a List&ltString&gt;; the
     * caller must determine this before calling this function.
     * 
     * @param section_name Name of the section.
     * @param field_name Name of the field within the section.
     * @return String corresponding to the value within the condition report.
     */
    public String getValueFromSectionNameAndFieldName(String section_name, String field_name)
    {
        final String TAG = getClass().getName() + "::getValueFromSectionNameAndFieldName";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));
        
        String return_value = condition_report.getValueFromSectionNameAndFieldName(section_name, field_name);
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));        
        return return_value;
    }
    
    /**
     * Get the list of values of a section name / field name pair within the
     * current contents of the condition report.  This assumes that the field
     * in question has a string array of values, as opposed to a single String;
     * the caller must determine this before calling this function.
     * 
     * @param section_name Name of the section.
     * @param field_name Name of the field within the section.
     * @return List&lt;String&gt; corresponding to the values within the
     * condition report.
     */
    public List<String> getValuesFromSectionNameAndFieldName(String section_name, String field_name)
    {
        final String TAG = getClass().getName() + "::getValuesFromSectionNameAndFieldName";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s'", section_name, field_name));
        
        List<String> return_value = condition_report.getValuesFromSectionNameAndFieldName(section_name, field_name);
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_value));        
        return return_value;        
    }    
    
    public boolean setValue(String section_name, String field_name, String value)
    {
        final String TAG = getClass().getName() + "::setValue";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s', value: '%s'", section_name, field_name, value));
        boolean return_code = condition_report.setValue(section_name, field_name, value);
        if (return_code)
        {
            saveConditionReportToDatabase();
        } // if (return_code)        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_code));
        return return_code;
    } // public void setValue(String section_name, String field_name, String value)
    
    public boolean setValues(String section_name, String field_name, List<String> values)
    {
        final String TAG = getClass().getName() + "::setValues";
        Log.d(TAG, String.format(Locale.US, "Entry. section_name: '%s', field_name: '%s', values: '%s'", section_name, field_name, values));
        boolean return_code = condition_report.setValue(section_name, field_name, values);
        if (return_code)
        {
            saveConditionReportToDatabase();
        } // if (return_code)        
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", return_code));
        return return_code;        
    } // public void setValue(String section_name, String field_name, String value)    
    
    public View getCurrentlyFocusedView()
    {
        return currently_focused_view;
    } // public View getCurrentlyFocusedView()
    
    public void setCurrentlyFocusedView(View v)
    {
        currently_focused_view = v;
    } // public void setCurrentlyFocusedView(View v)
    
    public Field getFieldFromView(View v)
    {
        final String TAG = getClass().getName() + "::getFieldFromView";
        Log.d(TAG, String.format(Locale.US, "Entry. View: '%s'", v));
        if (!lookup_view_to_field.containsKey(v))
        {
            Log.d(TAG, "View not inside lookup_view_to_field");
            return null;
        } // if (!lookup_view_to_field.containsKey(v))
        Field f = lookup_view_to_field.get(v);
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", f));
        return f;
    }
    
    public String getSectionNameFromView(View v)
    {
        final String TAG = getClass().getName() + "::getSectionNameFromView";
        Log.d(TAG, String.format(Locale.US, "Entry. View: '%s'", v));        
        Field f = getFieldFromView(v);
        String section_name = f.getSectionName();
        Log.d(TAG, String.format(Locale.US, "Returning: '%s'", section_name));
        return section_name;        
    }
    
    public void saveConditionReportToDatabase()
    {
        final String TAG = getClass().getName() + "::saveContentsToDatabase";
        Log.d(TAG, "Entry.");
        Preconditions.checkNotNull(database_manager);
        
        database_manager.saveConditionReportToDatabase(condition_report);        
    } // public void saveContentsToDatabase()
    
} // class ConditionReportState

/** Display the details of the condition report selected in
 * ConditionReportFragments. 
 * 
 * @author Asim Ihsan
 *
 */
public class ConditionReportDetailFragment
    extends Fragment
    implements OnClickListener, TextWatcher, OnCheckedChangeListener, OnFocusChangeListener, android.widget.RadioGroup.OnCheckedChangeListener 
{
    final static String FRAGMENT_TAG = "fragment_condition_report_detail";
    private ConditionReportState condition_report_state = null;
    private ScrollView detail_scroll_view = null; 
    private boolean is_updating_content = false;
    
    /**
     * 
     */
    public ConditionReportDetailFragment()
    {
        super();
    }

    /**
     * @param condition_report
     */
    public ConditionReportDetailFragment(ConditionReport condition_report)
    {
        super();
        final String TAG = getClass().getName() + "Constructor with ConditionReport";
        Log.d(TAG, "Entry.  condition_report: " + condition_report);
        if (condition_report != null)
        {
            Log.d(TAG, "condition_report is not null");
            condition_report_state = new ConditionReportState.Builder()
                                                             .conditionReport(condition_report)
                                                             .onClickListener(this)
                                                             .onCheckedChangeListener(this)
                                                             .textWatcher(this)  
                                                             .onFocusChangeListener(this)
                                                             .build();            
        } // if (condition_report != null)
    } // public ConditionReportDetailFragment(ConditionReport condition_report)
    
    @Override
    public void onResume()
    {
        final String TAG = getClass().getName() + "::onResume";
        Log.d(TAG, "Entry");        
        super.onResume();
        Log.d(TAG, String.format("Update condition_report. State: '%s'", condition_report_state));
        if (condition_report_state != null) 
        {
            Log.d(TAG, "Resuming with a non-null condition_report_state.");
            updateContent();            
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

    private View getRenderedSection(String section_name, JSONArray json_template, Activity activity) throws JSONException
    {
        final String TAG = getClass().getName() + "::getRenderedSection";
        Log.d(TAG, "Entry");
        
        assert(json_template != null);
        
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
                text_view.setMaxWidth(300);
                // -----------------------------------------------------                        
                
                TableRow.LayoutParams text_lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 
                                                                          TableRow.LayoutParams.WRAP_CONTENT);                
                row_view.addView(text_view, text_lp); 
                condition_report_state.addEditText(section_name, name, text_view);
            } 
            else if (type.equals("check"))
            {
                // -----------------------------------------------------
                //  Right-hand check boxes.
                // -----------------------------------------------------                        
                Log.d(TAG, "Index: " + i + ", are check boxes.");
                JSONArray values = element.getJSONArray("values");
                Log.d(TAG, "Check box values: " + values);
                List<String> decoded_values = Json.JsonArrayToList(values);
                Log.d(TAG, "Decoded values: " + decoded_values);
                
                LinearLayout linear_layout = new LinearLayout(activity);
                linear_layout.setOrientation(Configuration.ORIENTATION_PORTRAIT);                                                
                TableRow.LayoutParams linear_layout_lp = new TableRow.LayoutParams();
                
                List<View> check_boxes = new ArrayList<View>(decoded_values.size());                
                for (String value : decoded_values)
                {
                    CheckBox check_box = new CheckBox(activity);
                    LinearLayout.LayoutParams check_box_lp = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 
                                                                                                             LayoutParams.MATCH_PARENT));
                    check_box.setText(value);
                    linear_layout.addView(check_box, check_box_lp);
                    check_boxes.add(check_box);                    
                } // for (String value : decoded_values)                        
                row_view.addView(linear_layout, linear_layout_lp);
                condition_report_state.addCheck(section_name, name, check_boxes, decoded_values);
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
                List<String> decoded_values = Json.JsonArrayToList(values);
                Log.d(TAG, "Decoded values: " + decoded_values);                

                List<View> radio_buttons = new ArrayList<View>(decoded_values.size());
                RadioGroup radio_group = new RadioGroup(activity);
                int index = 1;
                for (String value : decoded_values)
                {
                    RadioButton radio_button = new RadioButton(activity);
                    radio_button.setText(value);
                    radio_button.setId(index);
                    radio_group.addView(radio_button);
                    radio_buttons.add(radio_button);
                    index += 1;
                } // for (String value : decoded_values)                        
                TableRow.LayoutParams radio_group_lp = new TableRow.LayoutParams();
                row_view.addView(radio_group, radio_group_lp);
                condition_report_state.addRadio(section_name, name, radio_buttons, decoded_values);
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
        
        Log.d(TAG, String.format(Locale.US, "Returning: %s", table_layout));
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
        //  Bit of a hack, but when you select one condition report, and then
        //  a second condition report, then when you move back you'll
        //  re-enter this function again but with a non-null
        //  condition_report_state.  We want to wipe it, as we're going to
        //  re-add all the views from scratch anyway.
        //
        //  Also use the same block to initialize the database manager
        //  reference.
        //
        //  TODO This would be an ideal place to reload the condition_report
        //  from the database.
        // ---------------------------------------------------------------------        
        if (condition_report_state != null)
        {
            Log.d(TAG, "Wiping condition report state.");
            condition_report_state.initialize();
            
            Preconditions.checkState(activity instanceof MainActivity);
            DatabaseManager database_manager = ((MainActivity)activity).getDatabaseManager();
            Preconditions.checkNotNull(database_manager);
            condition_report_state.setDatabaseManager(database_manager);            
        } // if (condition_report_state != null)
        // ---------------------------------------------------------------------        
        
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
        if (condition_report_state != null)
        {
            String top_view_title_contents = condition_report_state.getTitle(); 
            Log.d(TAG, String.format(Locale.US, "Setting top_view title to '%s'.", top_view_title_contents));
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
        if (condition_report_state != null)
        {
            Log.d(TAG, "Condition report is not null so populate section button bar");            
            for(String section_name : condition_report_state.getTemplateSectionNames())
            {
                Log.d(TAG, String.format(Locale.US, "Adding section %s to section button bar.", section_name));
                Button button = new Button(activity);
                button.setText(section_name);
                button.setOnClickListener(this);
                linear_section_button_view.addView(button);
                condition_report_state.addButton(section_name, button);
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
        detail_scroll_view.setScrollbarFadingEnabled(false);
        detail_scroll_view.setVerticalScrollBarEnabled(true);
        // ---------------------------------------------------------------------
       
        if (condition_report_state != null)
        {
            Log.d(TAG, "Condition report is not null.");        
            for(String section_name : condition_report_state.getTemplateSectionNames())
            {
                Log.d(TAG, String.format(Locale.US, "Add section name '%s'.", section_name));
                JSONArray json_template = condition_report_state.getTemplateSection(section_name);
                View view;
                try
                {
                    view = getRenderedSection(section_name, json_template, activity);
                    Log.d(TAG, String.format(Locale.US, "lookup_section_to_view: put name '%s' as view '%s'", section_name, view));
                    condition_report_state.addSection(section_name, view);
                }
                catch (JSONException e)
                {                    
                    Log.e(TAG, "Exception on decoding JSON template.", e);
                    return null;
                }                
                condition_report_state.setSelectedSectionFromSectionName("Basic info", detail_scroll_view);
                
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
        Log.d(TAG, String.format(Locale.US, "Entry. View: '%s'", v));
        
        if (condition_report_state.isButtonView(v))
        {
            Log.d(TAG, "Identified the view as a section button.");
            Section section = condition_report_state.getSectionFromButtonView(v);
            condition_report_state.setSelectedSectionFromSection(section, detail_scroll_view);
        } // if (condition_report_state.isButtonView(v))        
    } // public void onClick(View v)
    
    /**
     * Update the contents of the condition report detail.
     * 
     * @throws JSONException 
     */
    private void updateContent()
    {
        // Get and update the title.
        final String TAG = getClass().getName() + "::updateContent";
        Log.d(TAG, "Entry.");
        
        is_updating_content = true;        
        try
        {
            View v = getView();
            Log.d(TAG, String.format(Locale.US, "getView() result: '%s'", v));        
            
            List<String> section_names = condition_report_state.getTemplateSectionNames();
            for (String section_name : section_names)
            {
                Log.d(TAG, String.format(Locale.US, "Populating values for section: '%s'", section_name));
                List<Field> fields = condition_report_state.getFieldsFromSectionName(section_name);
                for (Field field : fields)
                {
                    Log.d(TAG, String.format(Locale.US, "Populating values for field: '%s'", field));
                    String field_name = field.getFieldName();
                    String type = field.getType();
                    
                    // -------------------------------------------------------------
                    // After getting the corresponding value we'll need to find
                    // the views and update their appearance to reflect the values.
                    // -------------------------------------------------------------                
                    if (type.equals("radio"))
                    {
                        String value = condition_report_state.getValueFromSectionNameAndFieldName(section_name, field_name);
                        Log.d(TAG, String.format(Locale.US, "Value is: '%s'", value));
                        if (value != null)                    
                        {
                            Log.d(TAG, "Since value is non-null populate the radio button.");
                            RadioButton radio_button = (RadioButton) field.getViewFromLabel(value);
                            Log.d(TAG, String.format(Locale.US, "Radio button view is: '%s'", radio_button));
                            radio_button.setChecked(true);
                        } // if (value != null)
                    }
                    else if (type.equals("check"))
                    {
                        List<String> values = condition_report_state.getValuesFromSectionNameAndFieldName(section_name, field_name);
                        Log.d(TAG, String.format(Locale.US, "Values are: '%s'", values));
                        if (values != null)                    
                        {   
                            Log.d(TAG, "Since values is non-null populate the check box.");
                            for (String value : values)
                            {                        
                                CheckBox check_box = (CheckBox) field.getViewFromLabel(value);
                                Log.d(TAG, String.format(Locale.US, "Selecting check_box: '%s'", check_box));
                                check_box.setChecked(true);                                                
                            } // for (String value : values)                        
                        } // if (values != null)
                    }
                    else if (type.equals("text"))
                    {
                        // ---------------------------------------------------------
                        //  Text fields are unique.  The Field instance that
                        //  tracks them knows that text fields only have a single
                        //  view responsible for the content, so you need to call
                        //  getSingleView() to get this view.
                        // ---------------------------------------------------------                    
                        String value = condition_report_state.getValueFromSectionNameAndFieldName(section_name, field_name);                    
                        Log.d(TAG, String.format(Locale.US, "Value is: '%s'", value));
                        if (value != null)                    
                        {
                            Log.d(TAG, "Since value is non-null populate the text field.");                    
                            TextView text_view = (TextView) field.getSingleView();
                            text_view.setText(value);
                        } // if (value != null)
                    } // if (type)                
                } // for (Field field : fields)
            } // for (String section_name : section_names)
        }
        finally
        {
            is_updating_content = false;
        }        
    } // public void updateContent(ConditionReport condition_report) throws JSONException

    public void afterTextChanged(Editable e)
    {
        final String TAG = getClass().getName() + "::afterTextChanged";
        Log.d(TAG, String.format(Locale.US, "Entry. Editable e: '%s'", e));
        
        if (is_updating_content)
        {
            Log.d(TAG, "Currently updating content, so ignore event.");
            return;
        }
        
        View focused_view = condition_report_state.getCurrentlyFocusedView();        
        if (focused_view == null)
        {
            Log.e(TAG, "Text change while there isn't a focused view!");
            Log.e(TAG, String.format(Locale.US, "condition_report_state: '%s'", condition_report_state));
            return;
        } // if (view == null)
        
        Section selected_section = condition_report_state.getCurrentlySelectedSection();
        String selected_section_name = selected_section.getSectionName();
        List<Field> fields = condition_report_state.getFieldsFromSectionName(selected_section_name);
        Field focused_field = null;
        for (Field field : fields)
        {
            if (field.isTextType())
            {
                View text_view = field.getSingleView();
                assert(text_view != null);
                if (text_view == focused_view)
                {
                    focused_field = field;
                    break;
                } // if (text_view == focused_view)                
            } // if (field.isTextType())
        } // for (Field field : fields)        
        Log.d(TAG, String.format(Locale.US, "Focused field: '%s'", focused_field));       
        if (focused_field == null)
        {
            Log.e(TAG, "Could not identify the focused field!");
            Log.e(TAG, String.format(Locale.US, "condition_report_state: '%s'", condition_report_state));
            return;            
        } // if (focused_field == null)
        String focused_field_name = focused_field.getFieldName();
        
        Log.d(TAG, "Updating contents.");
        String contents = ((EditText)focused_field.getSingleView()).getText().toString();
        boolean return_code = condition_report_state.setValue(selected_section_name,
                                                               focused_field_name,
                                                               contents);
        if (!return_code)
        {
            Log.e(TAG, "Failed to update contents.");
            Log.e(TAG, String.format(Locale.US, "condition_report_state: '%s'", condition_report_state));
            return;            
        }        
    } // public void afterTextChanged(Editable e)

    public void beforeTextChanged(CharSequence s, int start, int count, int after)
    {
        
    }

    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        
    }

    /**
     * When a check box is cliccked this callback is called.
     * 
     * @param buttonView View for the check box.
     * @param isChecked Whether the check box clicked is checked or not.
     */
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        final String TAG = getClass().getName() + "::onCheckedChanged";
        Log.d(TAG, String.format(Locale.US, "Entry. buttonView: '%s', isChecked: '%s'", buttonView, isChecked));
        
        if (is_updating_content)
        {
            Log.d(TAG, "Currently updating content, so ignore event.");
            return;
        }
        
        CheckBox event_check_box = (CheckBox) buttonView;
        Field field = condition_report_state.getFieldFromView(event_check_box);
        Log.d(TAG, String.format(Locale.US, "Field is '%s'", field));       
        if (field == null)
        {
            Log.e(TAG, String.format(Locale.US, "Could not find Field for check_box '%s'", event_check_box));
            Log.e(TAG, String.format(Locale.US, "condition_report_state: '%s'", condition_report_state));
            return;            
        } // if (field == null)
        List<View> views = field.getValueViews();
        List<String> labels = field.getValueLabels();
        int size = views.size();
        List<String> checked_labels = Lists.newArrayListWithCapacity(size);        
        for (int i = 0; i < size; i++)
        {
            View v = views.get(i);
            if (((CheckBox)v).isChecked())
            {
                String l = labels.get(i);
                checked_labels.add(l);
            } // if (((CheckBox)v).isChecked())            
        } // for (int i = 0; i <= size; i++)
        
        Log.d(TAG, "Updating contents.");
        boolean return_code = condition_report_state.setValues(field.getSectionName(),
                                                                field.getFieldName(),
                                                                checked_labels);
        if (!return_code)
        {
            Log.e(TAG, "Failed to update contents.");
            Log.e(TAG, String.format(Locale.US, "condition_report_state: '%s'", condition_report_state));
            return;            
        } // if (!return_code)        
    } // public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    
    /* 
     * When a radio button is clicked this callback is called.
     * 
     * Note that 
     * checkedId refers to the value assigned to the radio_button using
     * setId(), and hence is 1-based, whereas the field's internal
     * tracking structure used to track labels is in a 0-based array.
     * 
     * 
     * (non-Javadoc)
     * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
     */
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        final String TAG = getClass().getName() + "::onCheckedChanged";
        Log.d(TAG, String.format(Locale.US, "Entry. group: '%s', checkedId: '%s'", group, checkedId));
        
        if (is_updating_content)
        {
            Log.d(TAG, "Currently updating content, so ignore event.");
            return;
        }
        
        if (checkedId == 0)
        {
            Log.d(TAG, "No radio button is currently selected.");
            return;
        } // if (checkedId == 0)
        RadioButton radio_button = (RadioButton) group.findViewById(checkedId);
        Field field = condition_report_state.getFieldFromView(radio_button);
        Log.d(TAG, String.format(Locale.US, "Field is '%s'", field));       
        if (field == null)
        {
            Log.e(TAG, String.format(Locale.US, "Could not find Field for radio_button '%s'", radio_button));
            Log.e(TAG, String.format(Locale.US, "condition_report_state: '%s'", condition_report_state));
            return;            
        }
        Log.d(TAG, "Updating contents.");
        boolean return_code = condition_report_state.setValue(field.getSectionName(),
                                                               field.getFieldName(),
                                                               field.getValueLabel(checkedId-1));
        if (!return_code)
        {
            Log.e(TAG, "Failed to update contents.");
            Log.e(TAG, String.format(Locale.US, "condition_report_state: '%s'", condition_report_state));
            return;            
        }        
    } // public void onCheckedChanged(RadioGroup group, int checkedId)

    public void onFocusChange(View v, boolean hasFocus)
    {
        final String TAG = getClass().getName() + "::onFocusChange";
        Log.d(TAG, String.format(Locale.US, "Entry. v: '%s', hasFocus: '%s'", v, hasFocus));
        
        if (is_updating_content)
        {
            Log.d(TAG, "Currently updating content, so ignore event.");
            return;
        }
        
        if (hasFocus)
        {
            Log.d(TAG, "Setting currently focused view on condition_report_state.");
            condition_report_state.setCurrentlyFocusedView(v);
        }
        else
        {
            Log.d(TAG, "Setting currently focused view on condition_report_state to null.");
            condition_report_state.setCurrentlyFocusedView(null);
        } //  // if (hasFocus)
    } // public void onFocusChange(View v, boolean hasFocus)

} // public class ConditionReportsFragment extends ListFragment