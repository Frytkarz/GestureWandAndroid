package pl.chipsoft.gesturewand.logic.model.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by macie on 17.01.2017.
 */
@DatabaseTable(tableName = "configuration")
public class Configuration {

    public static final int MIN_GESTURE_COUNT_MIN = 3;
    public static final int MIN_GESTURE_COUNT_MAX = 10;
    public static final int RECORDS_COUNT_MIN = 10;
    public static final int RECORDS_COUNT_MAX = 40;
    public static final int SAMPLES_COUNT_MIN = 10;
    public static final int SAMPLES_COUNT_MAX = 100;
    public static final int MAX_ERROR_POW_MIN = 2;
    public static final int MAX_ERROR_POW_MAX = 7;
    public static final double MAX_ERROR_MIN = 0.01;
    public static final double MAX_ERROR_MAX = 0.0000001;

    public static final int MAX_ACCELERATION = 50;

    @DatabaseField(generatedId = true)
    private int id = 1;

    @DatabaseField
    private int minGestureCount = 4;

    @DatabaseField
    private int recordsCount = 20;

    @DatabaseField
    private int samplesCount = 30;

    @DatabaseField
    private double maxError = 0.000001;

    @DatabaseField
    private boolean isCalibrated = false;

    @DatabaseField
    private double calX;

    @DatabaseField
    private double calY;

    @DatabaseField
    private double calZ;

    public Configuration() {
    }

    public int getMinGestureCount() {
        return minGestureCount;
    }

    public void setMinGestureCount(int minGestureCount) {
        this.minGestureCount = minGestureCount;
    }

    public int getRecordsCount() {
        return recordsCount;
    }

    public void setRecordsCount(int recordsCount) {
        this.recordsCount = recordsCount;
    }

    public int getSamplesCount() {
        return samplesCount;
    }

    public void setSamplesCount(int samplesCount) {
        this.samplesCount = samplesCount;
    }

    public double getMaxError() {
        return maxError;
    }

    public void setMaxError(double maxError) {
        this.maxError = maxError;
    }

    public boolean isCalibrated() {
        return isCalibrated;
    }

    public void setCalibrated(boolean calibrated) {
        isCalibrated = calibrated;
    }

    public double getCalX() {
        return calX;
    }

    public void setCalX(double calX) {
        this.calX = calX;
    }

    public double getCalY() {
        return calY;
    }

    public void setCalY(double calY) {
        this.calY = calY;
    }

    public double getCalZ() {
        return calZ;
    }

    public void setCalZ(double calZ) {
        this.calZ = calZ;
    }
}
