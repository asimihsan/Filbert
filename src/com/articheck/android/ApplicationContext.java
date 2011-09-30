package com.articheck.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.articheck.android.managers.DatabaseManager;
import com.articheck.android.managers.PhotographManager;
import com.articheck.android.messages.MessageObjectPool;

import android.app.Application;
import android.content.Context;
import android.util.Log;

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
    private MessageObjectPool message_object_pool;
    
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
    
    public MessageObjectPool getMessageObjectPool()
    {
        return this.message_object_pool;
    }

    public ApplicationContext()
    {
        instance = this;
        message_object_pool = new MessageObjectPool();
    }

    public static Context getContext()
    {
        return instance;
    }
    
} // public class ApplicationContext extends Application
