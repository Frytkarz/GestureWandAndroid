package pl.chipsoft.gesturewand.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.application.MyApp;
import pl.chipsoft.gesturewand.helpers.AccelerometerHelper;
import pl.chipsoft.gesturewand.logic.listeners.TrainListener;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.database.Configuration;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;

public class NewGestureActivity extends Activity {

    public static final String TITLE = "NEW GESTURE";

    private Button btnSave;
    private EditText txtName;
    private Spinner spnApp;
    private TextView txtInfo;

    private boolean volume_up, volume_down, pressed;

    private ArrayAdapter<String> appAdapter;

    private GestureLearn gestureLearn;
    private GestureManager gestureManager = GestureManager.getInstance();
    private AccelerometerHelper accelerometer;

    private TrainListener trainListener = new TrainListener() {
        @Override
        public void onStepProgress(int epoch, double error) {
            runOnUiThread(() ->
                    txtInfo.setText(getString(R.string.training_progress_message, epoch, error)));
        }

        @Override
        public void onMessage(String message) {
            runOnUiThread(() ->
                    txtInfo.setText(message));
        }
    };

    private View.OnClickListener onSaveClick = v -> {
        setEnabled(false);
        gestureLearn.setGesture(new Gesture(txtName.getText().toString(),
                appAdapter.getItem(spnApp.getSelectedItemPosition())));

        new Thread(() -> {
            boolean result = gestureManager.train(gestureLearn, trainListener,
                    Configuration.MAX_ACCELERATION/*accelerometer.getAccelerometer().getMaximumRange()*/);
            runOnUiThread(() -> {
                if(result)
                    finish();
                else
                    txtInfo.setText(getString(R.string.unexpected_error));
            });

        }).start();


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gesture);

        btnSave = (Button) findViewById(R.id.angBtnSave);
        spnApp = (Spinner) findViewById(R.id.angSpnParam);
        txtName = (EditText) findViewById(R.id.angETxtName);
        txtInfo = (TextView) findViewById(R.id.angTxtInfo);

        btnSave.setOnClickListener(onSaveClick);

        gestureLearn = new GestureLearn();
        accelerometer = new AccelerometerHelper(this);

        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        List<String> appNames = new ArrayList<>(apps.size());
        for (ApplicationInfo a : apps)
            appNames.add(pm.getApplicationLabel(a).toString());

        appAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, appNames);
        spnApp.setAdapter(appAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        MyApp.getInstance().onPause();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            volume_up = true;
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            volume_down = true;
        else
            return super.onKeyDown(keyCode, event);

        if(volume_up && volume_down && !pressed){
            Log.d(getClass().getName(), "Gesture record started!");
            pressed = true;
            accelerometer.start();
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(volume_up && volume_down){
            Log.d(getClass().getName(), "Gesture record finished!");

            if(gestureLearn.getRecords().size() >= gestureManager.getDatabase().getConfiguration().
                    getRecordsCount() ){
                txtInfo.setText(getString(R.string.enough_gestures));
                return true;
            }

            List<Position> records = accelerometer.stop();
            pressed = false;
            if(records.size() >= 10){
                gestureLearn.getRecords().add(GestureLearn.interpolate(records,
                            gestureManager.getDatabase().getConfiguration().getSamplesCount()));

                if(gestureLearn.getRecords().size() < gestureManager.getDatabase().
                        getConfiguration().getRecordsCount()){
                    txtInfo.setText(getString(R.string.gesture_left,
                            (gestureManager.getDatabase().getConfiguration().getRecordsCount()
                                    - gestureLearn.getRecords().size())));
                }

                else if (gestureLearn.getRecords().size() == gestureManager.getDatabase().
                        getConfiguration().getRecordsCount()){
                    btnSave.setEnabled(true);
                    txtInfo.setText(getString(R.string.done));
                    ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
                }

                records = null;
            }else{
                txtInfo.setText(getString(R.string.too_fast));
            }

        }

        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            volume_up = false;
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            volume_down = false;
        else
            return super.onKeyUp(keyCode, event);

        return true;
    }

    private void setEnabled(boolean enabled){
        txtName.setEnabled(enabled);
        btnSave.setEnabled(enabled);
    }
}
