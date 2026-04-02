package kz.diplomka.startupmatch;

import android.app.Application;

import kz.diplomka.startupmatch.data.local.AppDatabase;

public class StartupMatchApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.init(this);
    }
}
