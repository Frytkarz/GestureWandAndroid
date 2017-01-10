package pl.chipsoft.gesturewand.library.model;

/**
 * Created by Maciej Frydrychowicz on 03.01.2017.
 */

public class Position {
    private float x;
    private float y;
    private float z;

    public Position(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
