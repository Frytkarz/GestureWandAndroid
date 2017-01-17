package pl.chipsoft.gesturewand.library.utils;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

import pl.chipsoft.gesturewand.library.model.database.Configuration;
import pl.chipsoft.gesturewand.library.model.database.Gesture;
import pl.chipsoft.gesturewand.library.model.database.History;
import pl.chipsoft.gesturewand.library.model.database.Record;

/**
 * Created by Maciej Frydrychowicz on 18.12.2016.
 */

public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    public static final Class<?>[] classes =
            new Class[] {History.class, Gesture.class, Record.class, Configuration.class};

    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile("ormlite_config.txt", classes);
    }
}
