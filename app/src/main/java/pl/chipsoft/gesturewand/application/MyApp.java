package pl.chipsoft.gesturewand.application;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import pl.chipsoft.gesturewand.background.BackgroundService;

/**
 * Created by Maciej Frydrychowicz on 12.12.2016.
 */

public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public void onResume(){
        //stopService(new Intent(this, BackgroundService.class));
        //Log.d(this.getClass().getSimpleName(), "Background service stopped!");
    }

    public void onPause(){
        //startService(new Intent(this, BackgroundService.class));
        //Log.d(this.getClass().getSimpleName(), "Background service started!");
    }
}
