package pl.chipsoft.gesturewand.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.library.model.Gesture;

/**
 * Created by Maciej Frydrychowicz on 27.12.2016.
 */

public class GestureAdapter extends ArrayAdapter<Gesture> {
    public GestureAdapter(Context context, List<Gesture> gestures) {
        super(context, 0, gestures);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gesture, parent, false);
        }
        TextView txtName = (TextView) convertView.findViewById(R.id.igName);
        TextView txtApp = (TextView) convertView.findViewById(R.id.igApp);

        Gesture gesture = getItem(position);
        txtName.setText(gesture.getName());
        txtApp.setText(gesture.getAction());

        return convertView;
    }
}
