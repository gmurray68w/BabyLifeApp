package com.example.babylife.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.babylife.R;
import com.example.babylife.contracts.DiaperChangeContract;

public class SQLDiaperChangeAdapter extends RecyclerView.Adapter<SQLDiaperChangeAdapter.ViewHolder> {
    //This binds my data from my Sqlite database into the view holder which currently has the layout for each item.

    private Context context;
    private Cursor cursor;

    public SQLDiaperChangeAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.entry_log_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) return;


        int nameColumnIndex = cursor.getColumnIndex(DiaperChangeContract.DiaperLogEntry.COLUMN_NAME);
        int dateColumnIndex = cursor.getColumnIndex(DiaperChangeContract.DiaperLogEntry.COLUMN_DATE);
        int timeColumnIndex = cursor.getColumnIndex(DiaperChangeContract.DiaperLogEntry.COLUMN_TIME);
        int typeColumnIndex = cursor.getColumnIndex(DiaperChangeContract.DiaperLogEntry.COLUMN_TYPE);
        int notesColumnIndex = cursor.getColumnIndex(DiaperChangeContract.DiaperLogEntry.COLUMN_NOTES);

        String name = cursor.getString(nameColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        String time = cursor.getString(timeColumnIndex);
        String type = cursor.getString(typeColumnIndex);
        String notes = cursor.getString(notesColumnIndex);

        holder.tvEntryName.setText(name);
        holder.tvDateEntry.setText(date);
        holder.tvTimeEntry.setText(time);
        holder.tvTypeEntry.setText(type);
        holder.tvAdditionalNotesEntry.setText(notes);


    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEntryName, tvDateEntry, tvTimeEntry, tvTypeEntry, tvAdditionalNotesEntry;
        ImageView ivLogTypeImageEntry;
        ViewHolder(View itemView) {
            super(itemView);
            tvEntryName = itemView.findViewById(R.id.tv_entry_name);
            tvDateEntry = itemView.findViewById(R.id.tv_date_entry_diaper);
            tvTimeEntry = itemView.findViewById(R.id.tv_time_entry_diaper);
            tvTypeEntry = itemView.findViewById(R.id.tv_type_entry_diaper);
            tvAdditionalNotesEntry = itemView.findViewById(R.id.tv_additional_notes_diaper);
            ivLogTypeImageEntry = itemView.findViewById(R.id.iv_log_type_image_entry);
        }
    }
}