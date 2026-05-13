package com.example.homeworkhelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HomeworkEntry> entries;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HomeworkEntry entry);
    }

    public HistoryAdapter(List<HomeworkEntry> entries, OnItemClickListener listener) {
        this.entries = entries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeworkEntry entry = entries.get(position);

        holder.tvSubject.setText("📚 " + entry.getSubject());
        holder.tvAnswer.setText(entry.getAnswer());

        // פורמט תאריך
        if (entry.getTimestamp() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvDate.setText(sdf.format(entry.getTimestamp().toDate()));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(entry));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubject, tvAnswer, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvAnswer  = itemView.findViewById(R.id.tvAnswer);
            tvDate    = itemView.findViewById(R.id.tvDate);
        }
    }
}
