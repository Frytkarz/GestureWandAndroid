package pl.chipsoft.gesturewand.logic.filters;

/**
 * Created by macie on 21.01.2017.
 */

public class LowPassFilter
{
    protected float alpha = 0.9f;

    // Gravity and linear accelerations components for the
    // Wikipedia low-pass filter
    protected float[] output = new float[]
            { 0, 0, 0 };

    protected float[] gravity = new float[]
            { 0, 0, 0 };

    // Raw accelerometer data
    protected float[] input = new float[]
            { 0, 0, 0 };

    public LowPassFilter() {
    }

    public LowPassFilter(float[] acceleration) {
        process(acceleration);
    }

    /**
     * Add a sample.
     *
     * @param acceleration
     *            The acceleration data.
     * @return Returns the output of the filter.
     */
    public float[] process(float[] acceleration)
    {
        System.arraycopy(acceleration, 0, input, 0, acceleration.length);

        float alphaMinus = (1.0f - alpha);

        gravity[0] = alpha * gravity[0] + alphaMinus * input[0];
        gravity[1] = alpha * gravity[1] + alphaMinus * input[1];
        gravity[2] = alpha * gravity[2] + alphaMinus * input[2];

        // Determine the linear acceleration
        output[0] = input[0] - gravity[0];
        output[1] = input[1] - gravity[1];
        output[2] = input[2] - gravity[2];

        return output;
    }
}
