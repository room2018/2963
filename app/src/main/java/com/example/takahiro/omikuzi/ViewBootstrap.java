package com.example.takahiro.omikuzi;

import android.app.Application;
import com.beardedhen.androidbootstrap.TypefaceProvider;

public class ViewBootstrap extends Application {
    @Override public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}