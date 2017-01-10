package pl.chipsoft.gesturewand.library.model;

import com.google.gson.Gson;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Maciej Frydrychowicz on 08.01.2017.
 */
@DatabaseTable(tableName = "records")
public class Record {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int gestureId;

    @DatabaseField
    private String json;

    private double[][] records;

    public Record() {
    }

    public Record(int gestureId, double[][] records) {
        this.gestureId = gestureId;
        this.records = records;
        setJson();
    }

    public void setJson(){
        Gson gson = new Gson();
        json = gson.toJson(records);
    }

    public void loadRecords(){
        Gson gson = new Gson();
        records = gson.fromJson(json, double[][].class);
    }

    public int getId() {
        return id;
    }

    public int getGestureId() {
        return gestureId;
    }

    public String getJson() {
        return json;
    }

    public double[][] getRecords() {
        return records;
    }

    public void setRecords(double[][] records) {
        this.records = records;
    }
}
