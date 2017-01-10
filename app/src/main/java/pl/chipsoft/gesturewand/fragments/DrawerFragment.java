package pl.chipsoft.gesturewand.fragments;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by Maciej Frydrychowicz on 22.10.2016.
 */
public abstract class DrawerFragment extends Fragment {

    public static final int HOME_INDEX = 0;

    private OnFragmentInteractionListener interactionListener;

//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (interactionListener != null) {
//            interactionListener.onFragmentInteraction(uri);
//        }
//    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            interactionListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        interactionListener = null;
    }

    /**
     * Zwraca tutuł fragmentu
     * @return
     */
    public abstract String getTitle(Context context);

    /**
     * Zwraca index fragmentu
     * @return
     */
    public abstract int getIndex();

    public boolean onKeyDown(int keyCode, KeyEvent event){
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event){
        return false;
    }

    /**
     * Listener interkacji frgamntu z innymi fragmentami lub aktywnością
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
