package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.library.managers.GestureManager;
import pl.chipsoft.gesturewand.library.model.GestureLearn;
import pl.chipsoft.gesturewand.library.model.Position;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends DrawerFragment {
    public static final int INDEX = 0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView txtInfo;

    private boolean volume_up, volume_down, pressed;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private GestureManager gestureManager = GestureManager.getInstance();

    private List<Position> records;

    private SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
                records.add(new Position(event.values[0], event.values[1], event.values[2]));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public SummaryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SummaryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1, String param2) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        txtInfo = (TextView) view.findViewById(R.id.fsTxtInfo);

        sensorManager = (SensorManager) getContext().getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.summary);
    }

    @Override
    public int getIndex() {
        return HOME_INDEX;
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
            records = new ArrayList<>(100);
            sensorManager.registerListener(sensorListener, accelerometer,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(volume_up && volume_down){
            Log.d(getClass().getName(), "Gesture record finished!");
            if(records.size() >= 10){
                pressed = false;
                sensorManager.unregisterListener(sensorListener);
                txtInfo.setText("Your gesture is " +
                        gestureManager.getGesture(gestureManager.compute(
                                GestureLearn.interpolate(records,
                                        gestureManager.getDatabase().getConfiguration().
                                                getSamplesCount()),
                                accelerometer.getMaximumRange())).getName());

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
}
