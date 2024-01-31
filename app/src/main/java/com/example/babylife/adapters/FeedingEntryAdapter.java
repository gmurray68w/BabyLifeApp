package com.example.babylife.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.babylife.R;
import com.example.babylife.contracts.FeedingLogContract;

public class FeedingEntryAdapter extends RecyclerView.Adapter<FeedingEntryAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public FeedingEntryAdapter(Context context){this.context = context;}

    public FeedingEntryAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_add_afeeding_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if(!cursor.moveToPosition(position)) return;


        int feedingNameColumnIndex = cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_NAME);
        int feedingDateColumnIndex = cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_DATE);
        int feedingTimeColumnIndex = cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_TIME);
        int feedingTypeColumnIndex = cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_TYPE);
        int feedingDurationColumnIndex = cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_DURATION);
        int feedingAmountColumnIndex = cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_AMOUNT);
        int feedingNotesColumnIndex = cursor.getColumnIndex(FeedingLogContract.FeedingLogEntry.COLUMN_NOTES);


        String name = cursor.getString(feedingNameColumnIndex);
        String date = cursor.getString(feedingDateColumnIndex);
        String time = cursor.getString(feedingTimeColumnIndex);
        String type = cursor.getString(feedingTypeColumnIndex);
        String duration = cursor.getString(feedingDurationColumnIndex);
        String amount = cursor.getString(feedingAmountColumnIndex);
        String notes = cursor.getString(feedingNotesColumnIndex);

        holder.tvEntryName.setText(name);
        holder.tvDateEntry.setText(date);
        holder.tvTimeEntry.setText(time);
        holder.tvTypeEntry.setText(type);
        holder.tvDuration.setText(duration);
        holder.tvAmount.setText(amount);
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

        TextView tvEntryName, tvDateEntry, tvTimeEntry, tvTypeEntry, tvDuration, tvAmount, tvAdditionalNotesEntry;
        ImageView ivLogTypeImageEntry;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEntryName = itemView.findViewById(R.id.tv_feeding_entry_name_F);
            tvDateEntry = itemView.findViewById(R.id.tv_feeding_date_entry_F);
            tvTimeEntry = itemView.findViewById(R.id.tv_feeding_time_entry_F);
            tvTypeEntry = itemView.findViewById(R.id.tv_feeding_type_entry_F);
            tvDuration = itemView.findViewById(R.id.tv_feeding_duration_entry_F);
            tvAmount = itemView.findViewById(R.id.tv_feeding_amount_entry_F);
            tvAdditionalNotesEntry = itemView.findViewById(R.id.tv_feeding_additional_notes_F);
            ivLogTypeImageEntry = itemView.findViewById(R.id.iv_log_type_feeding_image_entry);
        }
    }
}