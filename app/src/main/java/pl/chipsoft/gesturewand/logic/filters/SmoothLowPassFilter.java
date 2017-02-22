package pl.chipsoft.gesturewand.logic.filters;

/**
 * Created by macie on 22.01.2017.
 */

public class SmoothLowPassFilter extends LowPassFilter {
    private static final float TIME_CONSTANT = 0.3f;

    private float startTime = 0;
    private int count = 0;

    public static float getTimeConstant() {
        return TIME_CONSTANT;
    }

    @Override
    public float[] process(float[] acceleration)
    {
        if (startTime == 0)
            startTime = System.nanoTime();

        float timeStamp = System.nanoTime();
        System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);
        float dt = 1 / (count++ / ((timeStamp - startTime) / 1000000000.0f));
        alpha = getTimeConstant() / (getTimeConstant() + dt);

        if (count > 5)
        {
            output[0] = alpha * output[0] + (1 - alpha) * input[0];
            output[1] = alpha * output[1] + (1 - alpha) * input[1];
            output[2] = alpha * output[2] + (1 - alpha) * input[2];
        }

        float[] result = new float[3];
        System.arraycopy(output, 0, result, 0, output.length);
        return result;
    }
}
