package pl.chipsoft.gesturewand.background;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.helpers.AccelerometerHelper;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;
import pl.chipsoft.gesturewand.logic.model.database.Configuration;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;

/**
 * Created by macie on 23.01.2017.
 */

public class BackgroundService extends Service implements View.OnTouchListener {
    private static final int DOUBLE_CLICK_TIME = 300000000;

    private WindowManager windowManager;
    private View view;
    private ImageButton button;

    private float xOff;
    private float yOff;
    private int xPos;
    private int yPos;

    private boolean isMoving, isRecording;
    private long lastClickTime;

    private GestureManager gestureManager = GestureManager.getInstance();
    private AccelerometerHelper accelerometer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        accelerometer = new AccelerometerHelper(this);
        accelerometer.setSpeed(SensorManager.SENSOR_DELAY_FASTEST);
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        button = new ImageButton(this);
        button.setBackgroundColor(Color.TRANSPARENT);
        button.setImageResource(R.mipmap.ic_launcher);
        button.setMinimumHeight(200);
        button.setMinimumWidth(200);
        button.setOnTouchListener(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 0;
        windowManager.addView(button, params);

        view = new View(this);
        WindowManager.LayoutParams topLeftParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        topLeftParams.gravity = Gravity.START | Gravity.TOP;
        topLeftParams.x = 0;
        topLeftParams.y = 0;
        topLeftParams.width = 0;
        topLeftParams.height = 0;
        windowManager.addView(view, topLeftParams);

        Log.d(this.getClass().getSimpleName(), "Background service created!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (button != null) {
            windowManager.removeView(button);
            windowManager.removeView(view);
            button = null;
            view = null;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            long clickTime = System.nanoTime();
            if(clickTime - lastClickTime > DOUBLE_CLICK_TIME){
                accelerometer.start();
            }else if(accelerometer.isWorking()){
                accelerometer.stop();
            }
            lastClickTime = clickTime;

            float x = event.getRawX();
            float y = event.getRawY();

            isMoving = false;

            int[] location = new int[2];
            button.getLocationOnScreen(location);

            xPos = location[0];
            yPos = location[1];

            xOff = xPos - x;
            yOff = yPos - y;

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int[] topLeftLocationOnScreen = new int[2];
            this.view.getLocationOnScreen(topLeftLocationOnScreen);

            float x = event.getRawX();
            float y = event.getRawY();

            WindowManager.LayoutParams params = (WindowManager.LayoutParams)
                    button.getLayoutParams();

            int newX = (int) (xOff + x);
            int newY = (int) (yOff + y);

            if (Math.abs(newX - xPos) < 1 && Math.abs(newY - yPos) < 1 && !isMoving) {
                return false;
            }

            params.x = newX - (topLeftLocationOnScreen[0]);
            params.y = newY - (topLeftLocationOnScreen[1]);

            windowManager.updateViewLayout(button, params);
            isMoving = true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if(accelerometer.isWorking()){
                if(System.nanoTime() - lastClickTime > DOUBLE_CLICK_TIME){
                    List<Position> positions = accelerometer.stop();
                     positions = GestureLearn.interpolate(positions,
                            gestureManager.getDatabase().getConfiguration().getSamplesCount());
                    Gesture gesture = gestureManager.getGesture(gestureManager.compute(positions,
                            Configuration.MAX_ACCELERATION));
                    Toast.makeText(this, gesture.getName(), Toast.LENGTH_SHORT).show();
                    gestureManager.processGestureAction(this, gesture);
                }else{
                    accelerometer.stop();
                }
            }

            if (isMoving) {
                return true;
            }
        }

        return false;
    }
}
