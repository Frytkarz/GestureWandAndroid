package pl.chipsoft.gesturewand.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.activities.NewGestureActivity;
import pl.chipsoft.gesturewand.library.managers.DatabaseHelper;
import pl.chipsoft.gesturewand.library.managers.GestureManager;
import pl.chipsoft.gesturewand.library.model.database.Gesture;
import pl.chipsoft.gesturewand.library.model.database.Record;

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
        GestureHolder holder;

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_gesture, parent, false);
            holder = new GestureHolder();
            holder.txtName = (TextView) convertView.findViewById(R.id.igTxtName);
            holder.txtApp = (TextView) convertView.findViewById(R.id.igTxtApp);
            holder.btnMenu = (ImageButton) convertView.findViewById(R.id.igBtnMenu);
            convertView.setTag(holder);
        }else{
            holder = (GestureHolder) convertView.getTag();
        }

        Gesture gesture = getItem(position);
        holder.txtName.setText(gesture.getName());
        holder.txtApp.setText(gesture.getAction());

        //menu
        holder.btnMenu.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(getContext(), view);
            menu.getMenuInflater().inflate(R.menu.menu_item_gesture, menu.getMenu());
            menu.show();
            menu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if(id == R.id.igOptEdit){
                    Intent intent = new Intent(getContext(), NewGestureActivity.class);
                    getContext().startActivity(intent);
                }else if (id == R.id.igOptDelete){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.delete_gesture);
                    builder.setMessage(R.string.delete_gesture_ask);
                    builder.setNegativeButton(R.string.cancel, (dialogInterface, i) ->
                    {dialogInterface.dismiss();});
                    builder.setPositiveButton(R.string.delete, (dialogInterface, i) -> {
                        DatabaseHelper db = GestureManager.getInstance().getDatabase();
                        Dao<Gesture, Integer> gestureDao = db.getDaoGen(Gesture.class);
                        Dao<Record, Integer> recordDao = db.getDaoGen(Record.class);
                        try {
                            recordDao.delete(
                                    recordDao.queryForEq(Record.FIELD_GESTURE_ID, gesture.getId()));
                            gestureDao.deleteById(gesture.getId());
                            remove(gesture);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        notifyDataSetChanged();
                        dialogInterface.dismiss();
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

                return true;
            });
        });

        return convertView;
    }

    static class GestureHolder{
        private TextView txtName;
        private TextView txtApp;
        private ImageButton btnMenu;
    }
}
