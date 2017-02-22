package pl.chipsoft.gesturewand.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import pl.chipsoft.gesturewand.logic.filters.LowPassFilter;
import pl.chipsoft.gesturewand.logic.filters.SmoothLowPassFilter;
import pl.chipsoft.gesturewand.logic.model.Position;

/**
 * Created by macie on 22.01.2017.
 */

public class AccelerometerHelper {
    private SensorManager sensorManager;
    private Sensor accelerometer;

    private List<Position> positions;
    private LowPassFilter lpf;

    private int speed = SensorManager.SENSOR_DELAY_FASTEST;
    private boolean isWorking;

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                positions.add(new Position(lpf.process(event.values), false));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public AccelerometerHelper(Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lpf = new SmoothLowPassFilter();
    }

    public void start(){
        isWorking = true;
        positions = new ArrayList<>(500);
        lpf = new SmoothLowPassFilter();
        sensorManager.registerListener(sensorListener, accelerometer, speed);
    }

    public List<Position> stop(){
        sensorManager.unregisterListener(sensorListener);
        isWorking = false;
        Log.d(getClass().getSimpleName(), "Recorded " + positions.size() + " samples.");
        return positions;
    }

    public Sensor getAccelerometer() {
        return accelerometer;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
