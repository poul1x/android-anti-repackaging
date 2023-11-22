package com.example.antrp;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
}
