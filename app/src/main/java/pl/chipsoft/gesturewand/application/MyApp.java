package pl.chipsoft.gesturewand.application;

import android.app.Application;
import android.util.Log;

/**
 * Created by Maciej Frydrychowicz on 12.12.2016.
 */

public class MyApp extends Application {
    private static MyApp instance;

    public static MyApp getInstance() {
        return instance;
    }

//    private AudioManager audioManager;

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public void onResume(){
//        ComponentName receiver = new ComponentName(MediaReceiver.class.getPackage().getName(),
//                MediaReceiver.class.getName());
//        getAudioManager().unregisterMediaButtonEventReceiver(receiver);

//        Intent intent = new Intent( getApplicationContext(), BackgroundService.class );
//        intent.setAction( BackgroundService.ACTION_PLAY );
//        stopService(intent);
        Log.d(this.getClass().getSimpleName(), "Media receiver unregistered!");
    }

    public void onPause(){
//        ComponentName receiver = new ComponentName(getPackageName(),
//                MediaReceiver.class.getName());
//        getAudioManager().registerMediaButtonEventReceiver(receiver);

//        Intent intent = new Intent( getApplicationContext(), BackgroundService.class );
//        intent.setAction( BackgroundService.ACTION_PLAY );
//        startService( intent );
        Log.d(this.getClass().getSimpleName(), "Media receiver registered!");
    }

//    private AudioManager getAudioManager(){
//        if(audioManager == null)
//            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//
//        return audioManager;
//    }
}
