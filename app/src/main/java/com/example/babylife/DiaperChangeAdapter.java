package com.example.babylife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiaperChangeAdapter extends RecyclerView.Adapter<DiaperChangeAdapter.ViewHolder> {
    private List<DiaperChangeLogList> diaperLogList;

    public DiaperChangeAdapter(List<DiaperChangeLogList> diaperLogList) {
        this.diaperLogList = diaperLogList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entry_log_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DiaperChangeLogList logEntry = diaperLogList.get(position);

        holder.tvEntryName.setText(logEntry.getName());
        holder.tvDateEntry.setText(logEntry.getDate());
        holder.tvTimeEntry.setText(logEntry.getTime());
        holder.tvTypeEntry.setText(logEntry.getType());
        holder.tvAdditionalNotesEntry.setText(logEntry.getNotes());
    }

    @Override
    public int getItemCount() {
        return diaperLogList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEntryName, tvDateEntry, tvTimeEntry, tvTypeEntry, tvAdditionalNotesEntry;
        ImageView ivLogTypeImageEntry;

        public ViewHolder(View itemView) {
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