package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.adapters.GestureAdapter;
import pl.chipsoft.gesturewand.library.managers.GestureManager;
import pl.chipsoft.gesturewand.library.model.Gesture;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GesturesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GesturesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GesturesFragment extends DrawerFragment {
    public static final int INDEX = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView lstGestures;

    private GestureAdapter gestureAdapter;

    public GesturesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GesturesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GesturesFragment newInstance(String param1, String param2) {
        GesturesFragment fragment = new GesturesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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
