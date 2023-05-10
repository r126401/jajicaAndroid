package net.jajica.myhomeiot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import net.jajica.libiot.IotRoomsDevices;

import java.util.ArrayList;

public class ListRoomsAdapter extends RecyclerView.Adapter<ListRoomsAdapter.ListRoomsAdapterViewHolder> {

    private final String TAG = "ListRoomsAdapter";
    private ArrayList<IotRoomsDevices> roomsList;
    private String currentRoom;
    Context context;
    

    public ListRoomsAdapter(ArrayList<IotRoomsDevices> roomsList, String currentRoom, Context context) {
        this.roomsList = roomsList;
        this.context = context;

    }

    public interface OnRowSelectedData {
        void onRowSelectedData(String roomName, int position);
        void onDeleteData(String rootName, int position);
        Boolean onRowEditData(String oldNameRoom, String newNameRoom,int position);
    }
    private ListRoomsAdapter.OnRowSelectedData onRowSelectedData;

    public void setOnRowSelectedData(ListRoomsAdapter.OnRowSelectedData onRowSelectedData) {
        this.onRowSelectedData = onRowSelectedData;
    }


    public ArrayList<IotRoomsDevices> getRoomsList() {
        return roomsList;
    }

    public void setRoomsList(ArrayList<IotRoomsDevices> roomsList) {
        this.roomsList = roomsList;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ListRoomsAdapter.ListRoomsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        return new ListRoomsAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListRoomsAdapter.ListRoomsAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.imageEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                DialogName window;
                String oldRoom;
                oldRoom = holder.editText.getText().toString();
                window = new DialogName(context);
                window.setParameterDialog(R.drawable.ic_home_admin, R.string.modify_name_room, R.string.modify_name_room_text);

                window.show(fragmentManager, "dasfadshadskfdsf");
                window.alertDialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newRoom;

                        newRoom = window.getTextName();

                        if (onRowSelectedData.onRowEditData(oldRoom, newRoom, position)) {
                            window.mbinding.textIn.setText(newRoom);
                            notifyItemChanged(position);

                        } else {
                            Log.e(TAG, "Error al cambiar el nombre de la room");

                        }

                    }
                });
                window.alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

            }
        });
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = holder.editText.getText().toString();
                onRowSelectedData.onDeleteData(data, position);
                Log.i(TAG, "hh");

            }
        });
        holder.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = holder.editText.getText().toString();
                onRowSelectedData.onRowSelectedData(data, position);
                Log.i(TAG, "hh");

            }
        });
        holder.editText.setText(roomsList.get(position).getNameRoom());
        if (holder.editText.getText().toString().equals(currentRoom)) {
            holder.editText.setTypeface(null, Typeface.BOLD);
        }

    }

    @Override
    public int getItemCount() {
        return roomsList.size();
    }

    public class ListRoomsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        AppCompatTextView editText;
        AppCompatImageView imageDelete;
        AppCompatImageView imageEdit;


        public ListRoomsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = (AppCompatTextView) itemView.findViewById(R.id.editTextAdminItem);
            imageDelete = (AppCompatImageView) itemView.findViewById(R.id.imageDeleteItem);
            imageEdit = (AppCompatImageView) itemView.findViewById(R.id.imageEditItem);
            imageDelete.setOnClickListener(this);
            editText.setOnClickListener(this);
            imageEdit.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            /*
            int position = -1;
            String siteName;
            if (onRowSelectedData != null) {
                position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    //onRowSelectedData.onRowSelectedData(v, position);
                }
            }
            if (onRowSelectedData != null) {
                position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    //onRowSelectedData.onRowSelectedData(v, position);
                }
            }
            switch (v.getId()) {
                case (R.id.editTextAdminItem):
                    siteName = editText.getText().toString();
                    if (position >= 0) onRowSelectedData.onRowSelectedData(siteName, position);
                    editText.setTypeface(null, Typeface.BOLD);
                    break;
                case (R.id.imageDeleteItem):
                    siteName = editText.getText().toString();
                    if (position >= 0) onRowSelectedData.onDeleteData(siteName, position);
                    break;
                case (R.id.imageEditItem):
                    siteName = editText.getText().toString();
                    //if (position >= 0) onRowSelectedData.onRowEditData(siteName, "pp", position);
                    break;

                default:
                    Log.i(TAG, "kk");
            }



             */

        }




    }
}
