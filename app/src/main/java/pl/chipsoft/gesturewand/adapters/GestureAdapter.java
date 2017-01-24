package pl.chipsoft.gesturewand.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.gson.Gson;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import pl.chipsoft.gesturewand.R;
import pl.chipsoft.gesturewand.activities.NewGestureActivity;
import pl.chipsoft.gesturewand.logic.managers.DatabaseHelper;
import pl.chipsoft.gesturewand.logic.managers.GestureManager;
import pl.chipsoft.gesturewand.logic.model.database.Gesture;
import pl.chipsoft.gesturewand.logic.model.database.Record;

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
            holder.txtRecognizability = (TextView) convertView.findViewById(R.id.igTxtRecognizability);
            holder.txtAction = (TextView) convertView.findViewById(R.id.igTxtAction);
            holder.txtActionParam = (TextView) convertView.findViewById(R.id.igTxtActionParam);
            holder.btnMenu = (ImageButton) convertView.findViewById(R.id.igBtnMenu);
            convertView.setTag(holder);
        }else{
            holder = (GestureHolder) convertView.getTag();
        }

        //teksty
        Gesture gesture = getItem(position);
        holder.txtName.setText(gesture.getName());
        int count = gesture.getGood() + gesture.getWrong();
        float percent = count == 0 ? 0 : (1.0f * gesture.getGood() / count);
        holder.txtRecognizability.setText(getContext().getString(R.string.recognizability_message,
                gesture.getGood(), count, (int) (percent * 100)));
        holder.txtRecognizability.setTextColor(
                Color.rgb((int) (255 * (1 - percent)),(int) (255 * percent), 0));
        holder.txtAction.setText(gesture.getAction());
        holder.txtActionParam.setText(gesture.getActionParam());

        //menu
        holder.btnMenu.setOnClickListener(view -> {
            PopupMenu menu = new PopupMenu(getContext(), view);
            menu.getMenuInflater().inflate(R.menu.menu_item_gesture, menu.getMenu());
            menu.show();
            menu.setOnMenuItemClickListener(menuItem -> {
                int id = menuItem.getItemId();
                if(id == R.id.igOptEdit){
                    Intent intent = new Intent(getContext(), NewGestureActivity.class);
                    intent.putExtra(NewGestureActivity.GESTURE, new Gson().toJson(gesture));
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

    private class GestureHolder{
        private TextView txtName;
        private TextView txtRecognizability;
        private TextView txtAction;
        private TextView txtActionParam;
        private ImageButton btnMenu;
    }
}
