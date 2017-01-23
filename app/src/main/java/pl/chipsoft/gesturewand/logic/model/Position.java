package pl.chipsoft.gesturewand.logic.model;

/**
 * Created by Maciej Frydrychowicz on 03.01.2017.
 */

public class Position {
    private static final float ALPHA = 0.2f;

    private float x;
    private float y;
    private float z;

    public Position(float x, float y, float z, boolean filter) {
        setValues(x, y, z, filter);
    }

    public Position(float[] values, boolean filter){
        setValues(values[0], values[1], values[2], filter);
    }

    public void setValues(float[] values, boolean filter) {
        setValues(values[0], values[1], values[2], filter);
    }

    public void setValues(float x, float y, float z, boolean filter) {
        if(filter){
            this.x = x - (ALPHA * x + (1 - ALPHA) * x);
            this.y = y - (ALPHA * y + (1 - ALPHA) * y);
            this.z = z - (ALPHA * z + (1 - ALPHA) * z);
        }else{
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        Position p = (Position) o;
        return x == p.x && y == p.y && p.z == z;
    }
}
