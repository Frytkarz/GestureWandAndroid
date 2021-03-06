package pl.chipsoft.gesturewand.logic.managers;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.Dao;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationLOG;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.arrayutil.NormalizeArray;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import pl.chipsoft.gesturewand.application.MyApp;
import pl.chipsoft.gesturewand.logic.listeners.TrainListener;
import pl.chipsoft.gesturewand.logic.model.GestureLearn;
import pl.chipsoft.gesturewand.logic.model.Position;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;
import pl.chipsoft.gesturewand.logic.model.database.Record;
import pl.chipsoft.gesturewand.logic.utils.AppUtils;
import pl.chipsoft.gesturewand.logic.utils.MathUtils;

/**
 * Created by Maciej Frydrychowicz on 05.11.2016.
 */
public class GestureManager {

    private static volatile GestureManager instance;

    public static GestureManager getInstance(){
        if(instance == null){
            synchronized (GestureManager.class){
                if(instance == null)
                    instance = new GestureManager();
            }
        }

        return instance;
    }

    private static final String NETWORK_FILE_NAME = "gesture_network";
    private static final int HIGH = 1;
    private static final int LOW = -1;

    private BasicNetwork network;
    private final DatabaseHelper database;

    private GestureManager(){
        //baza danych
        database = new DatabaseHelper(MyApp.getInstance());

        initNetwork(false);
    }

    private void initNetwork(boolean forceReset){
        //sieć neuronowa
        File file = new File(MyApp.getInstance().getFilesDir() +
                File.pathSeparator + NETWORK_FILE_NAME);

        if(!forceReset && file.exists()){
            Log.d(this.getClass().getSimpleName(), "Loading neural network from file!");
            network = (BasicNetwork) EncogDirectoryPersistence.loadObject(file);
        }
        else{
            Log.d(this.getClass().getSimpleName(), "Could not find neural network file! Creating new one!");
            network = new BasicNetwork();
            network.addLayer(new BasicLayer(null, true, database.getConfiguration().getSamplesCount() * 3));
            network.addLayer(new BasicLayer(new ActivationLOG(), true, (int) (Math.sqrt(2) * database.getConfiguration().getSamplesCount() * 3)));
            network.addLayer(new BasicLayer(new ActivationLOG(), false, 1));
            network.getStructure().finalizeStructure();
            network.reset();

            save();
        }
    }

    public boolean train(GestureLearn gestureLearn, TrainListener listener, float maxValue){
        Log.d(this.getClass().getSimpleName(), "Starting learning for max value: " + maxValue);

        //zapis gestu do bazy
        Dao<Gesture, Integer> gestureDao = database.getDaoGen(Gesture.class);
        Dao<Record, Integer> recordDao = database.getDaoGen(Record.class);

        try {
            gestureDao.create(gestureLearn.getGesture());
        }catch (SQLException e){
            Log.e(this.getClass().getSimpleName(), "Can't create gesture row!", e);
            return false;
        }

        //normalizacja wyników

        //przepisanie z listy list do tablicy tablic i nromalizacja
        int recordsSize = gestureLearn.getRecords().size();
        int positionSize = gestureLearn.getRecords().get(0).size();
        double[][] recordInputs = new double[recordsSize][];
        for (int l = 0; l < recordsSize; l++){
            double[] positions = new double[positionSize * 3];
            int index = 0;
            for (int p = 0; p < positionSize; p++){
                Position position = gestureLearn.getRecords().get(l).get(p);
                positions[index++] = position.getX() / maxValue;
                positions[index++] = position.getY() / maxValue;
                positions[index++] = position.getZ() / maxValue;
            }

            recordInputs[l] = positions;
        }

        //zapisz record
        Record record = new Record(gestureLearn.getGesture().getId(), recordInputs);
        try {
            recordDao.create(record);
        }catch (SQLException e){
            Log.e(this.getClass().getSimpleName(), "Can't create record row!", e);
            return false;
        }

        return train(listener);
    }

    public boolean train(TrainListener listener){
        //sprawdz czy mamy juz wystarczajaco duzo gestow
        Dao<Record, Integer> recordDao = database.getDaoGen(Record.class);
        try {
            long count = recordDao.countOf();
            if (count < database.getConfiguration().getMinGestureCount()){
                listener.onMessage("You need " +
                        (database.getConfiguration().getMinGestureCount() - count) + " more gestures!");
                return true;
            }
        }catch (SQLException e){
            Log.e(this.getClass().getSimpleName(), "Can't get record dao count!", e);
            return false;
        }

        //wydobądź wszystkie recordy do uczenia
        List<Record> records;
        List<Gesture> gestures;
        Dao<Gesture, Integer> gestureDao = database.getDaoGen(Gesture.class);
        try {
            records = recordDao.queryForAll();
            gestures = gestureDao.queryForAll();
        }catch (SQLException e){
            Log.e(this.getClass().getSimpleName(), "Can't get record dao", e);
            return false;
        }

        //wyznaczenie wartości ideal
        double div = 2.0 / gestures.size();
        double div2 = div / 2;

        for (int i = 0; i < gestures.size(); i++){
            gestures.get(i).setIdeal(i * div + div2 - 1.0);
            try{
                gestureDao.createOrUpdate(gestures.get(i));
            }catch (SQLException e){
                Log.e(this.getClass().getSimpleName(), "Can't update gesture dao!", e);
                return false;
            }
        }

        //wyznaczenie tablic do nauki
        double[][] inputs = new double[records.size() * database.getConfiguration().getRecordsCount()]
                [database.getConfiguration().getSamplesCount() * 3];
        double[][] ideals = new double[records.size() * database.getConfiguration().getRecordsCount()][1];

        int index = 0;
        for (Record r : records) {
            r.loadRecords();
            double ideal;
            try {
                ideal = gestureDao.queryForId(r.getGestureId()).getIdeal();
            }catch (SQLException e){
                    Log.e(this.getClass().getSimpleName(), "Can't find gesture!", e);
                    return false;
                }
            for (double[] d: r.getRecords()) {
                inputs[index] = d;
                ideals[index][0] = ideal;
                index++;
            }
        }

        //uczenie
        network.reset();
        MLDataSet trainingSet = new BasicMLDataSet(inputs, ideals);
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;
        double error, maxError = database.getConfiguration().getMaxError();
        do{
            train.iteration();
            error = train.getError();
            listener.onStepProgress(epoch++, error, maxError / error);
        }while (error > maxError);

        train.finishTraining();
        save();
        Log.d(this.getClass().getSimpleName(), "Finished learning with error: "
                + network.calculateError(trainingSet));

        for (Gesture g : gestures) {
            Log.d(this.getClass().getSimpleName(), "Gesture: " + g.getName() + ", ideal: " + g.getIdeal());
        }

        //test
        for (MLDataPair pair : trainingSet){
            final MLData output = network.compute(pair.getInput());
            Log.d(this.getClass().getSimpleName(),
                    "Result: actual= " + output.getData(0) + ", ideal= " + pair.getIdeal().getData(0));
        }

        Encog.getInstance().shutdown();

        return true;
    }

    public double compute(List<Position> positions, float maxValue){
        Log.d(this.getClass().getSimpleName(), "Computing for max value: " + maxValue);

        double[] inputs = new double[positions.size() * 3];
        int index = 0;
        for(int i = 0; i < positions.size(); i++){
            Position position = positions.get(i);
            inputs[index++] = position.getX() / maxValue;
            inputs[index++] = position.getY() / maxValue;
            inputs[index++] = position.getZ() / maxValue;
        }

        MLData inputData = new BasicMLData(inputs);
        final MLData output = network.compute(inputData);
        Log.d(this.getClass().getSimpleName(),
                "Result: output= " + output.getData(0));

        return output.getData(0);
    }

    public Gesture getGesture(double output){
        Dao<Gesture, Integer> gestureDao = database.getDaoGen(Gesture.class);
        List<Gesture> gestures;
        try{
            gestures = gestureDao.queryForAll();
        }catch (SQLException e){
            Log.e(this.getClass().getSimpleName(), "Can't find gesture!", e);
            return null;
        }

        double[] ideals = new double[gestures.size()];
        for (int i = 0; i < gestures.size(); i++)
            ideals[i] = gestures.get(i).getIdeal();

        return gestures.get(MathUtils.getClosestIndex(output, ideals));
    }

    public String randomTest(){
        //sprawdz czy mamy juz wystarczajaco duzo gestow
        Dao<Record, Integer> recordDao = database.getDaoGen(Record.class);
        try {
            long count = recordDao.countOf();
            if (count < database.getConfiguration().getMinGestureCount()){
                return "You need " + (database.getConfiguration().getMinGestureCount() - count)
                        + " more gestures!";
            }
        }catch (SQLException e){
            Log.e(this.getClass().getSimpleName(), "Can't get record dao count!", e);
            return "Can't get record dao count!";
        }

        Record record;
        Gesture gesture;
        Dao<Gesture, Integer> gestureDao = database.getDaoGen(Gesture.class);
        try {
            List<Record> records = recordDao.queryForAll();
            record = records.get(new Random().nextInt(records.size()));
            record.loadRecords();
            gesture = gestureDao.queryForId(record.getGestureId());
        }catch (SQLException e){
            Log.e(this.getClass().getSimpleName(), "Can't get record dao", e);
            return "Can't get record dao";
        }

        MLData inputData = new BasicMLData(record.getRecords()
                [new Random().nextInt(record.getRecords().length)]);
        final MLData output = network.compute(inputData);

        return "Result: actual= " + output.getData(0) + ", ideal= " + gesture.getIdeal();
    }

    private void resetGesturesRecognizability(){
        Dao<Gesture, Integer> gestureDao = database.getDaoGen(Gesture.class);
        List<Gesture> gestures = null;
        try {
            gestures = gestureDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Gesture g : gestures) {
            g.resetRecognizability();
            try {
                gestureDao.update(g);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void resetNetwork(){
        resetGesturesRecognizability();
        initNetwork(true);
    }

    public void clearAll(){
        database.clearAll();
        initNetwork(true);
    }

    public void processGestureAction(Context context, Gesture gesture){
        if(gesture.getAction().equals(Gesture.ACTION_APP)){
            AppUtils.openApp(context, gesture.getActionParam());
        }else if (gesture.getAction().equals(Gesture.ACTION_CALL)){
            AppUtils.call(context, gesture.getActionParam());
        }
    }

    private void save(){
        Log.d(this.getClass().getSimpleName(), "Saving neural network to file!");
        EncogDirectoryPersistence.saveObject(new File(MyApp.getInstance().getFilesDir()
                + File.pathSeparator + NETWORK_FILE_NAME), network);
    }

    private NormalizeArray getNormalizeArray(){
        NormalizeArray normalizeArray = new NormalizeArray();
        normalizeArray.setNormalizedHigh(HIGH);
        normalizeArray.setNormalizedLow(LOW);
        return normalizeArray;
    }

    public DatabaseHelper getDatabase() {
        return database;
    }
}
