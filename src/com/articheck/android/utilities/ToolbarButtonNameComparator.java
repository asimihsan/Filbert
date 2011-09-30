package com.articheck.android.utilities;

import java.util.Comparator;

import com.articheck.android.ToolbarButton;

public class ToolbarButtonNameComparator implements Comparator<ToolbarButton>
{
    public int compare(ToolbarButton o1, ToolbarButton o2)
    {
        return o1.getName().compareTo(o2.getName());
    }
}

