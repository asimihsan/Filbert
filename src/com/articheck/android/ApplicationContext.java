package com.articheck.android;

import com.articheck.android.managers.DatabaseManager;
import com.articheck.android.managers.PhotographManager;

import android.app.Application;
import android.content.Context;

/**
 * This class is created automatically when the app launches.
 * It is used to provide an application-level context for the SQLiteOpenHelper
 * 
 * http://stackoverflow.com/questions/987072/using-application-context-everywhere
 */
public class ApplicationContext extends Application
{
    private static ApplicationContext instance;
    
    private DatabaseManager database_manager;
    private PhotographManager photograph_manager;
    
    public DatabaseManager getDatabaseManager()
    {
        return database_manager;
    }

    public void setDatabaseManager(DatabaseManager database_manager)
    {
        this.database_manager = database_manager;
    }

    public PhotographManager getPhotographManager()
    {
        return photograph_manager;
    }

    public void setPhotographManager(PhotographManager photograph_manager)
    {
        this.photograph_manager = photograph_manager;
    }

    public ApplicationContext()
    {
        instance = this;
    }

    public static Context getContext()
    {
        return instance;
    }
} // public class ApplicationContext extends Application
