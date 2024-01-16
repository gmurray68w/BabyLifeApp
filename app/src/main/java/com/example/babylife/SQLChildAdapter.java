package com.example.babylife;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SQLChildAdapter extends RecyclerView.Adapter<SQLChildAdapter.ViewHolder> {
    //This binds my data from my Sqlite database into the view holder which currently has the layout for each item.

    private Context context;
    private Cursor cursor;

    public SQLChildAdapter(Context context) {
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


        int nameColumnIndex = cursor.getColumnIndex(ChildLogContract.DiaperLogEntry.COLUMN_NAME);
        int dateColumnIndex = cursor.getColumnIndex(ChildLogContract.DiaperLogEntry.COLUMN_DATE);
        int timeColumnIndex = cursor.getColumnIndex(ChildLogContract.DiaperLogEntry.COLUMN_TIME);
        int typeColumnIndex = cursor.getColumnIndex(ChildLogContract.DiaperLogEntry.COLUMN_TYPE);
        int notesColumnIndex = cursor.getColumnIndex(ChildLogContract.DiaperLogEntry.COLUMN_NOTES);

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
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEntryName, tvDateEntry, tvTimeEntry, tvTypeEntry, tvAdditionalNotesEntry;
        ImageView ivLogTypeImageEntry;
        ViewHolder(View itemView) {
            super(itemView);
            tvEntryName = itemView.findViewById(R.id.tv_entry_name);
            tvDateEntry = itemView.findViewById(R.id.tv_date_entry);
            tvTimeEntry = itemView.findViewById(R.id.tv_time_entry);
            tvTypeEntry = itemView.findViewById(R.id.tv_type_entry);
            tvAdditionalNotesEntry = itemView.findViewById(R.id.tv_additional_notes);
            ivLogTypeImageEntry = itemView.findViewById(R.id.iv_log_type_image_entry);
        }
    }
}