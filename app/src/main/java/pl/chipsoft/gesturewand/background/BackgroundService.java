package pl.chipsoft.gesturewand.background;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.media.VolumeProviderCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

/**
 * Created by Maciej Frydrychowicz on 06.01.2017.
 */

public class BackgroundService extends Service implements MediaPlayer.OnPreparedListener {

    public static final String ACTION_PLAY = "PLAY";

    private final IBinder iBinder = new LocalBinder();


    private MediaPlayer mediaPlayer = null;

    private MediaSessionCompat mediaSession;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(this.getClass().getSimpleName(), "onStartCommand");
        if (intent.getAction().equals(ACTION_PLAY)) {
//            mediaPlayer = new MediaPlayer();
//            mediaPlayer.setOnPreparedListener(this);
//            mediaPlayer.prepareAsync();
            mediaSession = new MediaSessionCompat(this, "YourPlayerName", null, null);
            mediaSession = new MediaSessionCompat(getApplicationContext(), "AudioPlayer");
            mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS);
            mediaSession.setActive(true);
            mediaSession.setPlaybackToRemote(myVolumeProvider);
            Log.e(this.getClass().getSimpleName(), "here");
        }

        return super.onStartCommand(intent, flags, startId);
    }
    private VolumeProviderCompat myVolumeProvider =
            new VolumeProviderCompat(VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, 0, 0) {
        @Override
        public void onAdjustVolume(int direction) {
            Log.e(this.getClass().getSimpleName(), "onAdjustVolume");
            // <0 volume down
            // >0 volume up

        }
    };


    @Override
    public void onDestroy() {
        Log.e(this.getClass().getSimpleName(), "onDestroy");
        //mediaPlayer.release();
        mediaSession.setActive(false);
        mediaSession.release();
        super.onDestroy();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.e(this.getClass().getSimpleName(), "onPrepared");
        //mediaPlayer.start();
    }

    public class LocalBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }
}
