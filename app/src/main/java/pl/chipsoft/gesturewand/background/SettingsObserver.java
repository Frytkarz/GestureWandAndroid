package pl.chipsoft.gesturewand.background;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;

import java.util.List;

import pl.chipsoft.gesturewand.helpers.AccelerometerHelper;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.Position;
import pl.chipsoft.gesturewand.logic.model.database.Configuration;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;

/**
 * Created by macie on 23.01.2017.
 */

public class SettingsObserver extends ContentObserver {
    private static final long MAX_DELTA_TIME = 1000000000;

    private long lastTime;
    private int previousVolume;

    private Context context;
    private AccelerometerHelper accelerometer;
    private GestureManager gestureManager = GestureManager.getInstance();

    public SettingsObserver(Context c, Handler handler) {
        super(handler);
        context=c;

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
        accelerometer = new AccelerometerHelper(context);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);

        int delta=previousVolume-currentVolume;

        if(delta>0)
        {
            Log.d(this.getClass().getSimpleName(), "Volume decreased");

            previousVolume=currentVolume;

            if(accelerometer.isWorking()){
                processGesture(accelerometer.stop());
            }else{
                lastTime = System.nanoTime();
            }
        }
        else if(delta<0)
        {
            Log.d(this.getClass().getSimpleName(), "Volume increased");

            previousVolume=currentVolume;

            if(System.nanoTime() - lastTime <= MAX_DELTA_TIME){
                Log.d(this.getClass().getSimpleName(), "Gesture detector started!");
                ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

                accelerometer.start();
            }
        }
    }

    private void processGesture(List<Position> positions){
        Gesture gesture = gestureManager.getGesture(gestureManager.compute(positions,
                Configuration.MAX_ACCELERATION));
        Log.d(this.getClass().getSimpleName(), "Found gesture: " + gesture.getName());
    }
}
