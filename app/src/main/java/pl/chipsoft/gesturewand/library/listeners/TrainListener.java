package pl.chipsoft.gesturewand.library.listeners;

/**
 * Created by Maciej Frydrychowicz on 08.01.2017.
 */

public interface TrainListener {
    void onStepProgress(int epoch, double error);
    void onMessage(String message);
}
