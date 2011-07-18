package com.articheck.android.utilities;

import java.util.Comparator;

import com.articheck.android.objects.ConditionReport;

/**
 * Comparator to sort condition reports by their title.
 * 
 * @author ai
 *
 */
public class ConditionReportTitleComparator implements Comparator<ConditionReport>
{

    public int compare(ConditionReport o1, ConditionReport o2)
    {        
        return o1.getTitle().compareTo(o2.getTitle());
    } // public int compare(ConditionReport o1, ConditionReport o2)

}
