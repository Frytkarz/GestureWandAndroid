package pl.chipsoft.gesturewand.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by macie on 23.01.2017.
 */

public class BackgroundService extends Service {

    private SensorManager sensorManager;
    private Sensor light;
    private SettingsObserver settingsObserver;

    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Log.d(this.getClass().getSimpleName(), "Light: " + sensorEvent.values[0]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        settingsObserver = new SettingsObserver(this, new Handler());
        getApplicationContext().getContentResolver().
                registerContentObserver(Settings.System.CONTENT_URI, true, settingsObserver);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, light,
                SensorManager.SENSOR_DELAY_NORMAL);
        Log.d(this.getClass().getSimpleName(), "Background service created!");
    }

    @Override
    public void onDestroy() {
        //sensorManager.unregisterListener(sensorEventListener);
        getApplicationContext().getContentResolver().unregisterContentObserver(settingsObserver);
        super.onDestroy();
    }
}
