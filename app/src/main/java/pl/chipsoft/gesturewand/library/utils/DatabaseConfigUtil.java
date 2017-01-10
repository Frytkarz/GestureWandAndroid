package pl.chipsoft.gesturewand.library.utils;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

import pl.chipsoft.gesturewand.library.model.Gesture;
import pl.chipsoft.gesturewand.library.model.History;
import pl.chipsoft.gesturewand.library.model.Record;

/**
 * Created by Maciej Frydrychowicz on 18.12.2016.
 */

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {History.class, Gesture.class, Record.class};

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
