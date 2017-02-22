package pl.chipsoft.gesturewand.logic.filters;

/**
 * Created by macie on 21.01.2017.
 */

public class LowPassFilter
{
    protected float alpha = 0.9f;
    protected float[] output = new float[]{ 0, 0, 0 };
    protected float[] gravity = new float[]{ 0, 0, 0 };
    protected float[] input = new float[]{ 0, 0, 0 };

    public LowPassFilter() {
    }

    public LowPassFilter(float[] acceleration) {
        process(acceleration);
    }

    public float[] process(float[] acceleration)
    {
        System.arraycopy(acceleration, 0, input, 0, acceleration.length);

        float alphaMinus = (1.0f - alpha);

        gravity[0] = alpha * gravity[0] + alphaMinus * input[0];
        gravity[1] = alpha * gravity[1] + alphaMinus * input[1];
        gravity[2] = alpha * gravity[2] + alphaMinus * input[2];

        output[0] = input[0] - gravity[0];
        output[1] = input[1] - gravity[1];
        output[2] = input[2] - gravity[2];

        return output;
    }
}
