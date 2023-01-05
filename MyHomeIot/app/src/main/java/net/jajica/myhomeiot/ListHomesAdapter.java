package net.jajica.myhomeiot;

import static android.view.MotionEvent.ACTION_DOWN;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

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
            editText = (AppCompatTextView) itemView.findViewById(R.id.editTextAdminHome);
            imageDelete = (AppCompatImageView) itemView.findViewById(R.id.imageDeleteHome);
            imageEdit = (AppCompatImageView) itemView.findViewById(R.id.imageEditHome);
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
                case (R.id.editTextAdminHome):
                    siteName = editText.getText().toString();
                    if (position >= 0) onRowSelectedData.onRowSelectedData(siteName, position);
                    editText.setTypeface(null, Typeface.BOLD);
                    break;
                case (R.id.imageDeleteHome):
                    siteName = editText.getText().toString();
                    if (position >= 0) onRowSelectedData.onDeleteData(siteName, position);
                    break;
                case (R.id.imageEditHome):
                    siteName = editText.getText().toString();
                    if (position >= 0) onRowSelectedData.onRowEditData(siteName, position);
                    break;

                default:
                Log.i(TAG, "kk");
            }
        }
    }



}
