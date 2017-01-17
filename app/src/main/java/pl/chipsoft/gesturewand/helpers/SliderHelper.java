package pl.chipsoft.gesturewand.helpers;

import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by macie on 17.01.2017.
 */

public class SliderHelper{

    public SliderHelper(SeekBar seekBar, TextView textView, int min, int max, int progress,
                        OnSliderChangeListener listener) {
        seekBar.setMax(max - min);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textView.setText(String.valueOf(i + min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                listener.onValueChanged(seekBar.getProgress() + min);
            }
        });

        seekBar.setProgress(progress - min);
    }

    public interface OnSliderChangeListener{
        void onValueChanged(int value);
    }
}
