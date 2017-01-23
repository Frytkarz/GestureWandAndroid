package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.adapters.GestureAdapter;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;

/**
 *
 */
public class GesturesFragment extends DrawerFragment {
    public static final int INDEX = 1;

    private ListView lstGestures;

    private GestureAdapter gestureAdapter;

    public GesturesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gestures, container, false);

        lstGestures = (ListView) view.findViewById(R.id.fgLstGestures);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Gesture> gestures;

        try {
            gestures = GestureManager.getInstance().getDatabase().getDaoGen(Gesture.class).queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
            gestures = new ArrayList<>(0);
        }

        gestureAdapter = new GestureAdapter(getContext(), gestures);
        lstGestures.setAdapter(gestureAdapter);
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.gestures);
    }

    @Override
    public int getIndex() {
        return 1;
    }
}
