package pl.chipsoft.gesturewand.library.model.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Maciej Frydrychowicz on 07.01.2017.
 */

@DatabaseTable(tableName = "history")
public class History {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int gestureId;

    @DatabaseField
    private String date;

    @DatabaseField
    private String mode;

    public History() {
    }

    public History(int gestureId, String date, String mode) {
        this.gestureId = gestureId;
        this.date = date;
        this.mode = mode;
    }

    public int getId() {
        return id;
    }

    public int getGestureId() {
        return gestureId;
    }

    public String getDate() {
        return date;
    }

    public String getMode() {
        return mode;
    }
}
