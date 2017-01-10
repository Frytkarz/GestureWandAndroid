package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.library.listeners.TrainListener;
import pl.chipsoft.gesturewand.library.managers.GestureManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends DrawerFragment {
    public static final int INDEX = 2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView txtInfo;
    private Button btnRetrain, btnClearAll;

    private TrainListener trainListener = new TrainListener() {
        @Override
        public void onStepProgress(int epoch, double error) {
            getActivity().runOnUiThread(() ->
                    txtInfo.setText(getString(R.string.training_progress_message, epoch, error)));
        }

        @Override
        public void onMessage(String message) {
            getActivity().runOnUiThread(() ->
                    txtInfo.setText(message));
        }
    };

    private View.OnClickListener onBtnRetrainClick = v -> {
        Log.d("Click","Hello");
        new Thread(() -> {
            boolean result = GestureManager.getInstance().train(trainListener);
            getActivity().runOnUiThread(() -> {
                if(result)
                    txtInfo.setText(getString(R.string.done));
                else
                    txtInfo.setText(getString(R.string.unexpected_error));
            });

        }).start();
    };

    private View.OnClickListener onBtnClearAllClick = v -> {
        GestureManager.getInstance().clearAll();
        Intent i = getActivity().getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    };

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        txtInfo = (TextView) view.findViewById(R.id.fsTxtInfo);
        btnRetrain = (Button) view.findViewById(R.id.fsBtnReTrain);
        btnClearAll = (Button) view.findViewById(R.id.fsBtnClearAll);

        btnRetrain.setOnClickListener(onBtnRetrainClick);
        btnClearAll.setOnClickListener(onBtnClearAllClick);

        return view;
    }

    @Override
    public String getTitle(Context context) {
        return context.getString(R.string.settings);
    }

    @Override
    public int getIndex() {
        return 2;
    }
}