package pl.chipsoft.gesturewand.logic.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.chipsoft.gesturewand.logic.model.database.Gesture;

/**
 * Created by Maciej Frydrychowicz on 03.01.2017.
 */

public class GestureLearn {
    private Gesture gesture;
    private List<List<Position>> records;

    public GestureLearn() {
        records = new ArrayList<>(100);
        gesture = new Gesture(SimpleDateFormat.
                getDateTimeInstance().format(Calendar.getInstance().getTime()));
    }

    public Gesture getGesture() {
        return gesture;
    }

    public void setGesture(Gesture gesture) {
        this.gesture = gesture;
    }

    public List<List<Position>> getRecords() {
        return records;
    }

    public void setRecords(List<List<Position>> records) {
        this.records = records;
    }


    public static List<Position> interpolate(List<Position> inputPoints, int numOfElements) {
        if(inputPoints.size() < 3) {
            Position position = inputPoints.get(0);
            inputPoints = new ArrayList<Position>();
            inputPoints.add(position);
            inputPoints.add(position);
            inputPoints.add(position);
        }

        List<Position> outputPoints = inputPoints;

        while(outputPoints.size() < numOfElements) {
            outputPoints = new ArrayList<Position>();

            for(int i = 0; i < inputPoints.size() - 1; i++) {
                outputPoints.add(inputPoints.get(i));
                Position interpolatedPoint = new Position(
                inputPoints.get(i).getX() + (inputPoints.get(i+1).getX() - inputPoints.get(i).getX()) * 0.5f,
                inputPoints.get(i).getY() + (inputPoints.get(i+1).getY()  - inputPoints.get(i).getY()) * 0.5f,
                inputPoints.get(i).getZ()  + (inputPoints.get(i+1).getZ()  - inputPoints.get(i).getZ()) * 0.5f,
                        false);
                outputPoints.add(interpolatedPoint);
            }

            inputPoints = outputPoints;
        }

        if(outputPoints.size() > numOfElements) {
            float offset = (float) outputPoints.size() / (float) (outputPoints.size() - numOfElements);
            float index = offset;

            Position elementToDelete = new Position(999.0f, 999.0f, 999.0f, false);

            while(index < outputPoints.size()) {
                outputPoints.remove((int) index);
                outputPoints.add((int) index, elementToDelete);
                index += offset;
            }

            for (int i = outputPoints.size() - 1; i >= 0; i--){
                if(outputPoints.get(i).equals(elementToDelete))
                    outputPoints.remove(i);
            }

            //outputPoints.RemoveAll(x => x.Equals(elementToDelete));

            while(outputPoints.size() > numOfElements) {
                outputPoints.remove(outputPoints.size() - 1);
            }
        }

        return outputPoints;
    }
}
