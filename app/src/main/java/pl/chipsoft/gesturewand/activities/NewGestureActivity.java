package pl.chipsoft.gesturewand.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.application.MyApp;
import pl.chipsoft.gesturewand.helpers.AccelerometerHelper;
import pl.chipsoft.gesturewand.logic.listeners.TrainListener;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;
import pl.chipsoft.gesturewand.logic.model.database.Configuration;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;

public class NewGestureActivity extends Activity {
    public static final String GESTURE = "gesture";
    public static final String TITLE = "NEW GESTURE";

    private Button btnSave;
    private EditText eTxtName, eTxtParam;
    private Spinner spnAction, spnParam;
    private TextView txtInfo;

    private boolean volume_up, volume_down, pressed, editMode;

    private ArrayAdapter<String> actionAdapter, paramAdapter;

    private GestureLearn gestureLearn;
    private GestureManager gestureManager = GestureManager.getInstance();
    private AccelerometerHelper accelerometer;

    private View.OnClickListener onSaveClick = v -> {
        setEnabled(false);

        //zapis gestu
        gestureLearn.getGesture().setName(eTxtName.getText().toString());

        if(gestureLearn.getGesture().getAction().equals(Gesture.ACTION_NONE)){
            gestureLearn.getGesture().setActionParam("");
        }else if(gestureLearn.getGesture().getAction().equals(Gesture.ACTION_APP)){
            gestureLearn.getGesture().setActionParam(
                    paramAdapter.getItem(spnParam.getSelectedItemPosition()));
        }else if(gestureLearn.getGesture().getAction().equals(Gesture.ACTION_CALL)){
            gestureLearn.getGesture().setActionParam(eTxtParam.getText().toString());
        }

        //tryb edycji to zapisz i wyjdź
        if(editMode){
            Dao<Gesture, Integer> gestureDao = gestureManager.getDatabase().getDaoGen(Gesture.class);
            try {
                gestureDao.update(gestureLearn.getGesture());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            finish();
            return;
        }

        //progress
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle(R.string.learning);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(true);
        dialog.setMax(1000);
        dialog.setProgress(0);
        dialog.show();

        //zapisz i ucz
        new Thread(() -> {
            boolean result = gestureManager.train(gestureLearn, new TrainListener() {
                        @Override
                        public void onStepProgress(int epoch, double error, double progress) {
                            dialog.setMessage(
                                    getString(R.string.training_progress_message, epoch, error));
                            dialog.setProgress((int) (progress * dialog.getMax()));
                        }

                        @Override
                        public void onMessage(String message) {
                            runOnUiThread(() -> Toast.makeText(NewGestureActivity.this,
                                    message, Toast.LENGTH_LONG).show());
                        }
                    },
                    Configuration.MAX_ACCELERATION);
            runOnUiThread(() -> {
                if(result)
                    finish();
                else
                    Toast.makeText(this, R.string.unexpected_error, Toast.LENGTH_LONG);
            });

        }).start();
    };

    private AdapterView.OnItemSelectedListener spnActionListener =
            new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            gestureLearn.getGesture().setAction(Gesture.ACTIONS[i]);
            if(gestureLearn.getGesture().getAction().equals(Gesture.ACTION_NONE)){
                eTxtParam.setEnabled(false);
                spnParam.setEnabled(false);
            }else if(gestureLearn.getGesture().getAction().equals(Gesture.ACTION_APP)){
                eTxtParam.setEnabled(false);
                spnParam.setEnabled(true);
            }else if(gestureLearn.getGesture().getAction().equals(Gesture.ACTION_CALL)){
                eTxtParam.setEnabled(true);
                spnParam.setEnabled(false);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_gesture);

        btnSave = (Button) findViewById(R.id.angBtnSave);
        spnAction = (Spinner) findViewById(R.id.angSpnAction);
        spnParam = (Spinner) findViewById(R.id.angSpnParam);
        eTxtName = (EditText) findViewById(R.id.angETxtName);
        eTxtParam = (EditText) findViewById(R.id.angETxtParam);
        txtInfo = (TextView) findViewById(R.id.angTxtInfo);

        btnSave.setOnClickListener(onSaveClick);

        gestureLearn = new GestureLearn();
        accelerometer = new AccelerometerHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApp.getInstance().onResume();

        //spinner akcji
        actionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Gesture.ACTIONS);
        spnAction.setAdapter(actionAdapter);
        spnAction.setOnItemSelectedListener(spnActionListener);

        //spinner aplikacji
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        List<String> appNames = Stream.of(packages).
                map(p -> p.applicationInfo.loadLabel(getPackageManager()).toString()).
                collect(Collectors.toList());

        paramAdapter = new ArrayAdapter<>(NewGestureActivity.this,
                android.R.layout.simple_spinner_item, appNames);
        spnParam.setAdapter(paramAdapter);

        //tryb edytowania
        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            return;

        editMode = true;
        gestureLearn.setGesture(new Gson().fromJson(bundle.getString(GESTURE), Gesture.class));
        eTxtName.setText(gestureLearn.getGesture().getName());
        btnSave.setEnabled(true);
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

        if(volume_up && volume_down && !pressed && !editMode){
            Log.d(getClass().getName(), "Gesture record started!");
            pressed = true;
            accelerometer.start();
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(volume_up && volume_down && !editMode){
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
        eTxtParam.setEnabled(enabled);
        eTxtName.setEnabled(enabled);
        spnParam.setEnabled(enabled);
        spnAction.setEnabled(enabled);
        btnSave.setEnabled(enabled);
    }
}
