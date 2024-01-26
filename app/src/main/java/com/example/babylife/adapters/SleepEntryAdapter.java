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
import com.example.babylife.contracts.SleepSessionContract;

public class SleepEntryAdapter extends RecyclerView.Adapter<SleepEntryAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public SleepEntryAdapter(Context context){this.context = context;}

    public SleepEntryAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
    }
    @NonNull
    @Override
    public SleepEntryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_add_asleeping_item, parent, false);
        return new SleepEntryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SleepEntryAdapter.ViewHolder holder, int position) {
        if(!cursor.moveToPosition(position)) return;


        int sleepingNameColumnIndex = cursor.getColumnIndex(SleepSessionContract.SleepLogEntry.COLUMN_NAME);
        int sleepingDateColumnIndex = cursor.getColumnIndex(SleepSessionContract.SleepLogEntry.COLUMN_DATE);
        int sleepingStartTimeColumnIndex = cursor.getColumnIndex(SleepSessionContract.SleepLogEntry.COLUMN_START_TIME);
        int sleepingEndTimeColumnIndex = cursor.getColumnIndex(SleepSessionContract.SleepLogEntry.COLUMN_END_TIME);
        int sleepingDurationColumnIndex = cursor.getColumnIndex(SleepSessionContract.SleepLogEntry.COLUMN_DURATION);
        int sleepingNotesColumnIndex = cursor.getColumnIndex(SleepSessionContract.SleepLogEntry.COLUMN_NOTES);


        String name = cursor.getString(sleepingNameColumnIndex);
        String date = cursor.getString(sleepingDateColumnIndex);
        String starttime = cursor.getString(sleepingStartTimeColumnIndex);
        String endtime = cursor.getString(sleepingEndTimeColumnIndex);
        String duration = cursor.getString(sleepingDurationColumnIndex);
        String notes = cursor.getString(sleepingNotesColumnIndex);

        holder.tvEntryName.setText(name);
        holder.tvDateEntry.setText(date);
        holder.tvStartTimeEntry.setText(starttime);
        holder.tvEndTimeEntry.setText(endtime);
        holder.tvDuration.setText(duration);
        holder.tvAdditionalNotesEntry.setText(notes);
    }

    @Override
    public int getItemCount() {
        return (cursor == null) ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEntryName, tvDateEntry, tvEndTimeEntry, tvStartTimeEntry, tvDuration, tvAdditionalNotesEntry;
        ImageView ivLogTypeImageEntry;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEntryName = itemView.findViewById(R.id.tv_sleeping_entry_name_S);
            tvDateEntry = itemView.findViewById(R.id.tv_sleeping_date_entry_S);
            tvStartTimeEntry = itemView.findViewById(R.id.tv_sleeping_start_time_S);
            tvEndTimeEntry = itemView.findViewById(R.id.tv_sleeping_end_time_S);
            tvDuration = itemView.findViewById(R.id.tv_sleeping_duration_entry_S);
            tvAdditionalNotesEntry = itemView.findViewById(R.id.tv_sleeping_additional_notes_S);
            ivLogTypeImageEntry = itemView.findViewById(R.id.iv_log_type_sleeping_image_entry);
        }
    }
}
