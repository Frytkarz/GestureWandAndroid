package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;

import static android.content.Context.SENSOR_SERVICE;

/**
 *
 */
public class SummaryFragment extends DrawerFragment {
    public static final int INDEX = 0;

    private TextView txtInfo;

    public SummaryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        txtInfo = (TextView) view.findViewById(R.id.fsTxtInfo);

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
}
