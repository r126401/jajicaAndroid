package net.jajica.myhomeiot;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import net.jajica.libiot.IotSitesDevices;

import java.util.ArrayList;

public class ListHomesAdapter extends RecyclerView.Adapter<ListHomesAdapter.ListHomesAdapterViewHolder> {


    private final String TAG = "ListHomesAdapter";
    ArrayList<IotSitesDevices> listSites;
    String currentSite;
    Context context;
    public interface OnRowSelectedData {
        void onRowSelectedData(String siteName, int position);
        void onDeleteData(String siteName, int position);
        void onRowEditData(String siteName, int position);
    }
    private OnRowSelectedData onRowSelectedData;

    public void setOnRowSelectedData(OnRowSelectedData onRowSelectedData) {
        this.onRowSelectedData = onRowSelectedData;
    }

    public ListHomesAdapter(String currentSite, ArrayList<IotSitesDevices> listSites, Context context) {

        this.listSites = listSites;
        this.context = context;
        this.currentSite = currentSite;
    }

    @Override
    public void onViewRecycled(@NonNull ListHomesAdapterViewHolder holder) {
        super.onViewRecycled(holder);
        Log.i(TAG, "h");
    }

    @NonNull
    @Override
    public ListHomesAdapter.ListHomesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        return new ListHomesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListHomesAdapter.ListHomesAdapterViewHolder holder, int position) {

        if (listSites.size() == 1) {
            holder.imageDelete.setVisibility(View.INVISIBLE);
        } else {
            holder.imageDelete.setVisibility(View.VISIBLE);
        }
        holder.imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = holder.editText.getText().toString();
                onRowSelectedData.onRowEditData(data, position);
                Log.i(TAG, "hh");
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
        holder.editText.setText(listSites.get(position).getSiteName());
        if (holder.editText.getText().toString().equals(currentSite)) {
            holder.editText.setTypeface(null, Typeface.BOLD);
        }

    }

    @Override
    public int getItemCount() {

        return listSites.size();
    }

    public class ListHomesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        AppCompatTextView editText;
        AppCompatImageView imageDelete;
        AppCompatImageView imageEdit;
        @SuppressLint("ClickableViewAccessibility")
        public ListHomesAdapterViewHolder(@NonNull View itemView) {
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

            int position = -1;
            String siteName;
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
                    if (position >= 0) onRowSelectedData.onRowEditData(siteName, position);
                    break;

                default:
                Log.i(TAG, "kk");
            }
        }
    }



}
