package pl.chipsoft.gesturewand.logic.utils;

/**
 * Created by Maciej Frydrychowicz on 08.01.2017.
 */

public class MathUtils {
    public static int getClosestIndex(double value, double[] values){
        if(values.length <= 1)
            return 0;

        double best = Math.abs(values[0] - value);
        int index = 0;
        for(int i = 1; i < values.length; i++){
            double actual = Math.abs(values[i] - value);
            if(actual < best){
                index = i;
                best = actual;
            }
        }
        return index;
    }
}
