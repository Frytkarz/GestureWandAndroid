package pl.chipsoft.gesturewand.library.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Maciej Frydrychowicz on 30.09.2016.
 */
@DatabaseTable(tableName = "gestures")
public class Gesture {
    public static final int ID_DIV = 100;
    public static final double ID_ROUND = 0.005;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(index = true)
    private String name;

    @DatabaseField
    private double ideal;

    @DatabaseField
    private String createDate;

    @DatabaseField
    private String action = null;

    @DatabaseField
    private String actionParam = null;

    public Gesture() {
    }

    public Gesture(String name) {
        this.name = name;
    }

    public Gesture(String name, String action) {
        this.name = name;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getAction() {
        return action;
    }

    public double getIdeal() {
        return ideal;
    }

    public void setIdeal(double ideal) {
        this.ideal = ideal;
    }

    public static class Action{
        public static final String APP = "Open app";
        public static final String CALL = "Call number";
        public static final String ANSWER_CALL = "Answer call";
        public static final String UNLOCK = "Unlock screen";

        public static final String[] actions = new String[]{APP, CALL, ANSWER_CALL, UNLOCK};
    }
}
