package pl.chipsoft.gesturewand.background;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by macie on 23.01.2017.
 */

public class ShakeDetector implements SensorEventListener {
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;

    private OnShakeListener onShakeListener;
    private long shakeTimestamp;

    public interface OnShakeListener {
        void onShake();
    }

    public ShakeDetector(OnShakeListener onShakeListener) {
        this.onShakeListener = onShakeListener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (onShakeListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                //zbyt blisko siebie
                if (shakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                shakeTimestamp = now;

                onShakeListener.onShake();
            }
        }
    }
}
