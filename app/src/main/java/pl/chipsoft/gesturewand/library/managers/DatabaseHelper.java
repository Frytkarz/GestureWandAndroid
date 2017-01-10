package pl.chipsoft.gesturewand.library.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import pl.chipsoft.gesturewand.library.model.Gesture;
import pl.chipsoft.gesturewand.library.model.History;
import pl.chipsoft.gesturewand.library.model.Record;

/**
 * Created by Maciej Frydrychowicz on 18.12.2016.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "gesture.db";
    // inkrementacja przy zmnianie obiektów
    private static final int DATABASE_VERSION = 12;


    private final Map<Class, Dao> daos = new HashMap<Class, Dao>(){{
       put(Gesture.class, null);
       put(History.class, null);
       put(Record.class, null);
    }};

    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.d(DatabaseHelper.class.getName(), "Creating database...");
            for (Class c : daos.keySet()) {
                TableUtils.createTable(connectionSource, c);
            }

        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            dropAll(connectionSource);

            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Dao<T, Integer> getDaoGen(Class<T> type){
        if(!daos.containsKey(type))
            return null;

        if(daos.get(type) == null){
            try {
                daos.put(type, getDao(type));
            }catch (SQLException e){
                Log.d(this.getClass().getSimpleName(), "Can't get dao!", e);
            }
        }


        return daos.get(type);
    }

    public void dropAll(ConnectionSource connectionSource) throws SQLException {
        for (Class c : daos.keySet()) {
            TableUtils.dropTable(connectionSource, c, true);
        }
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        for (Map.Entry e : daos.entrySet()) {
            e.setValue(null);
        }
        //gestures = null;
        //simpleRuntimeDao = null;
    }
}
