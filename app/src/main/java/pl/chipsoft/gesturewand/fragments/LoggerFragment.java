package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.helpers.AccelerometerHelper;
import pl.chipsoft.gesturewand.logic.filters.LowPassFilter;
import pl.chipsoft.gesturewand.logic.filters.SmoothLowPassFilter;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;
import pl.chipsoft.gesturewand.logic.model.database.Configuration;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;
import pl.chipsoft.gesturewand.logic.model.database.Record;

import static android.content.Context.SENSOR_SERVICE;

/**
 *
 */
public class LoggerFragment extends DrawerFragment {

    private XYPlot plot;
    private CheckBox chBTestMode;

    private boolean volume_up, volume_down, pressed;

    private GestureManager gestureManager = GestureManager.getInstance();
    private Dao<Gesture, Integer> gestureDao;
    private List<Gesture> gestures;
    private List<String> gestureNames;

    private AccelerometerHelper accelerometer;

    public LoggerFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logger, container, false);

        chBTestMode = (CheckBox) view.findViewById(R.id.flChBTestMode);
        plot = (XYPlot) view.findViewById(R.id.flPlot);
        initPlot();
        accelerometer = new AccelerometerHelper(getContext());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        gestureDao = gestureManager.getDatabase().getDaoGen(Gesture.class);
        try {
            gestures = gestureDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        gestureNames = Stream.of(gestures).map(Gesture::getName).
                collect(Collectors.toList());
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
            List<Position> positions = GestureLearn.interpolate(accelerometer.stop(),
                    gestureManager.getDatabase().getConfiguration().getSamplesCount());
            pressed = false;
            if(positions.size() >= 10){
                if(chBTestMode.isChecked())
                    test(positions);
                showPlot(positions);
            }else{
                Toast.makeText(getContext(), R.string.too_fast, Toast.LENGTH_SHORT).show();
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

    private void initPlot(){
        int count = gestureManager.getDatabase().getConfiguration().getSamplesCount();
        List<Position> positions = new ArrayList<>(count);
        for (int i = 0; i < count; i++){
            positions.add(new Position(-1, 0, 1, false));
        }
        showPlot(positions);
    }

    private void showPlot(List<Position> positions){
        List<Number> xList = new ArrayList<>(positions.size());
        List<Number> yList = new ArrayList<>(positions.size());
        List<Number> zList = new ArrayList<>(positions.size());
        final int domains[] = new int[positions.size()];

        for (int i = 0; i < positions.size(); i++){
            Position p = positions.get(i);

            xList.add(p.getX());
            yList.add(p.getY());
            zList.add(p.getZ());
            domains[i] = i + 1;
        }

        XYSeries xSeries  = new SimpleXYSeries(xList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "x");
        XYSeries ySeries  = new SimpleXYSeries(yList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "y");
        XYSeries zSeries  = new SimpleXYSeries(zList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "z");

        LineAndPointFormatter xLine = new LineAndPointFormatter(
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.red),
                Color.TRANSPARENT,
                null);

        LineAndPointFormatter yLine = new LineAndPointFormatter(
                getResources().getColor(R.color.green),
                getResources().getColor(R.color.green),
                Color.TRANSPARENT,
                null);

        LineAndPointFormatter zLine = new LineAndPointFormatter(
                getResources().getColor(R.color.blue),
                getResources().getColor(R.color.blue),
                Color.TRANSPARENT,
                null);

        plot.clear();

        plot.addSeries(xSeries, xLine);
        plot.addSeries(ySeries, yLine);
        plot.addSeries(zSeries, zLine);

        plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(domains[i]);
            }
            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });

        plot.redraw();
    }

    private void test(List<Position> positions){
        Gesture gesture = gestureManager.getGesture(gestureManager.compute(positions,
                Configuration.MAX_ACCELERATION/*accelerometer.getAccelerometer().getMaximumRange()*/));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.test_gesture_message);
        builder.setMessage(gesture.getName());

        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            gesture.incGood();
            try {
                gestureDao.update(gesture);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {
            dialogInterface.dismiss();

            AlertDialog.Builder builderNo = new AlertDialog.Builder(getContext());
            builderNo.setTitle(R.string.wrong_gesture_message);
            List<String> filteredNames = Stream.of(gestureNames).
                    filter(n -> !n.equals(gesture.getName())).collect(Collectors.toList());
            builderNo.setItems(filteredNames.toArray(new String[filteredNames.size()]),
            (dialogInterfaceNo, iNo) -> {
                String name = filteredNames.get(iNo);
                Gesture rightGesture = Stream.of(gestures).
                    filter(n -> n.getName().equals(name)).findFirst().get();
                    dialogInterface.dismiss();
                rightGesture.incWrong();
                    try {
                        gestureDao.update(rightGesture);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                dialogInterfaceNo.dismiss();
                });

            AlertDialog dialogNo = builderNo.create();
            dialogNo.show();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.logger);
    }

    @Override
    public int getIndex() {
        return 3;
    }
}
