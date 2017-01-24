package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.helpers.AccelerometerHelper;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;
import pl.chipsoft.gesturewand.logic.model.database.Configuration;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;
import pl.chipsoft.gesturewand.logic.utils.AppUtils;

/**
 *
 */
public class SummaryFragment extends DrawerFragment {
    public static final int INDEX = 0;

    private TextView txtInfo;

    private AccelerometerHelper accelerometer;
    private GestureManager gestureManager = GestureManager.getInstance();

    private boolean volume_up, volume_down, pressed;

    public SummaryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        txtInfo = (TextView) view.findViewById(R.id.fsTxtInfo);

        accelerometer = new AccelerometerHelper(getContext());

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
            pressed = true;
            accelerometer.start();
        }

        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(volume_up && volume_down){
            pressed = false;
            process(GestureLearn.interpolate(accelerometer.stop(),
                    gestureManager.getDatabase().getConfiguration().getSamplesCount()));
        }

        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            volume_up = false;
        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            volume_down = false;
        else
            return super.onKeyUp(keyCode, event);

        return true;
    }

    private void process(List<Position> positions){
        Gesture gesture = gestureManager.getGesture(gestureManager.compute(positions,
                Configuration.MAX_ACCELERATION));

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.gesture);
        builder.setMessage(getContext().getString(R.string.gesture_message, gesture.getName(),
                gesture.getAction() + " " + gesture.getActionParam()));

        builder.setPositiveButton(R.string.yes, (dialogInterface, i) -> {
            dialogInterface.dismiss();
            if(gesture.getAction().equals(Gesture.ACTION_APP)){
                AppUtils.openApp(getContext(), gesture.getActionParam());
            }else if (gesture.getAction().equals(Gesture.ACTION_CALL)){
                AppUtils.call(getActivity(), gesture.getActionParam());
            }
        });

        builder.setNegativeButton(R.string.no, (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
