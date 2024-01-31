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
import com.example.babylife.contracts.PumpingSessionContract;

public class PumpingSessionAdapter extends RecyclerView.Adapter<PumpingSessionAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public PumpingSessionAdapter(Context context){this.context = context;}

    public PumpingSessionAdapter(Context context, Cursor cursor){
        this.context = context;
        this.cursor = cursor;
    }
    @NonNull
    @Override
    public PumpingSessionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.pumping_session_item, parent, false);
        return new PumpingSessionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!cursor.moveToPosition(position)) return;
// Get the column indices
        int logTypeColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_LOG_TYPE);
        int nameColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_NAME);
        int dateColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_DATE);
        int totalAmountColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_TOTAL_AMOUNT);
        int durationColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_DURATION);
        int amountColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_AMOUNT);
        int saveColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_SAVE);
        int sideColumnIndex = cursor.getColumnIndex(PumpingSessionContract.PumpingLogEntry.COLUMN_SIDE);

        // Extract the values from the cursor
        String logType = cursor.getString(logTypeColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        String date = cursor.getString(dateColumnIndex);
        int totalAmount = cursor.getInt(totalAmountColumnIndex);
        String duration = cursor.getString(durationColumnIndex);
        int amount = cursor.getInt(amountColumnIndex);
        int save = cursor.getInt(saveColumnIndex);
        String side = cursor.getString(sideColumnIndex);

        // Set the values to the ViewHolder
        holder.tvEntryName.setText(name);
        holder.tvDateEntry.setText(date);
        holder.tvTotalAmount.setText(String.valueOf(totalAmount));
        holder.tvDuration.setText(duration);
        holder.tvAmount.setText(String.valueOf(amount));
        holder.tvSave.setText(save == 1 ? "Saved" : "Not Saved");
        holder.tvSide.setText(side);
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

        TextView tvEntryName, tvDateEntry, tvTimeEntry, tvTotalAmount, tvDuration, tvAmount, tvAdditionalNotesEntry, tvSave, tvSide;
        ImageView ivLogTypeImageEntry;

        public ViewHolder(View itemView) {
            super(itemView);
            tvEntryName = itemView.findViewById(R.id.tv_pumping_entry_name_F);
            tvDateEntry = itemView.findViewById(R.id.tv_pumping_date_entry_P);
            tvTimeEntry = itemView.findViewById(R.id.tv_pumping_time_entry_P);
            tvDuration = itemView.findViewById(R.id.tv_pumping_duration_entry_F);
            tvAmount = itemView.findViewById(R.id.tv_pumping_amount_entry_P);
            tvTotalAmount= itemView.findViewById(R.id.tv_pumping_total_amount_P);
            tvSave = itemView.findViewById(R.id.tv_pumping_entry_saved_P);
            tvSide = itemView.findViewById(R.id.tv_pumping_entry_saved_P);
            ivLogTypeImageEntry = itemView.findViewById(R.id.iv_log_type_pumping_image_entry);
        }
    }
}
