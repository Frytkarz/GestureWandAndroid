package pl.chipsoft.gesturewand.logic.model.database;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Maciej Frydrychowicz on 30.09.2016.
 */
@DatabaseTable(tableName = "gestures")
public class Gesture {

    public static final String ACTION_NONE = "None";
    public static final String ACTION_APP = "Open app";
    public static final String ACTION_CALL = "Call number";

    public static final String[] ACTIONS = new String[]{ACTION_NONE, ACTION_APP, ACTION_CALL};

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

    @DatabaseField
    private int good;

    @DatabaseField
    private int wrong;

    public Gesture() {
    }

    public Gesture(String createDate) {
        this.createDate = createDate;
    }

    public void resetRecognizability(){
        good = wrong = 0;
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

    public int getGood() {
        return good;
    }

    public void incGood() {
        good++;
    }

    public int getWrong() {
        return wrong;
    }

    public void incWrong() {
        wrong++;
    }

    public String getActionParam() {
        return actionParam;
    }

    public void setActionParam(String actionParam) {
        this.actionParam = actionParam;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setName(String name) {
        this.name = name;
    }
}
