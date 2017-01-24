package pl.chipsoft.gesturewand.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;

import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;

/**
 * Created by macie on 24.01.2017.
 */

public class VolumeButtonsHelper {
//
//    public interface EventListener{
//        void onDown();
//        void onUp();
//        void onCallSuperDown(int keyCode, KeyEvent event);
//        void onCallSuperUp(int keyCode, KeyEvent event);
//    }
//
//    private boolean volume_up, volume_down, pressed;
//
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//
//        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
//            volume_up = true;
//        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
//            volume_down = true;
//        else
//            return activity.onKeyDown(keyCode, event);
//
//        if(volume_up && volume_down && !pressed){
//            pressed = true;
//        }
//
//        return true;
//    }
//
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//        if(volume_up && volume_down){
//            pressed = false;
//        }
//
//        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP)
//            volume_up = false;
//        else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
//            volume_down = false;
//        else
//            return activity.onKeyUp(keyCode, event);
//
//        return true;
//    }
}
