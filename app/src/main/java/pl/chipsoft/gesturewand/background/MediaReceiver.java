package pl.chipsoft.gesturewand.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Maciej Frydrychowicz on 06.01.2017.
 */

public class MediaReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 77627;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getSimpleName(), "MediaReceiver received call.");
        intent.setClass(context, BackgroundService.class);
        context.startService(intent);
    }
}
