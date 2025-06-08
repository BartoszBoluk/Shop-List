package com.example.shoplist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.StatViewHolder> {

    private final List<StatItem> statList;

    public StatsAdapter(List<StatItem> statList) {
        this.statList = statList;
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_stat, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        StatItem item = statList.get(position);
        holder.textName.setText(item.getName());
        holder.textCount.setText("Dodane: " + item.getCount());
        if (item.getLastAdded() > 0) {
            String dateStr = DateFormat.getDateTimeInstance().format(new Date(item.getLastAdded()));
            holder.textLastAdded.setText("Ostatnio: " + dateStr);
        } else {
            holder.textLastAdded.setText("Brak daty");
        }
    }

    @Override
    public int getItemCount() {
        return statList.size();
    }

    static class StatViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textCount, textLastAdded;

        public StatViewHolder(@NonNull View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.textStatName);
            textCount = itemView.findViewById(R.id.textStatCount);
            textLastAdded = itemView.findViewById(R.id.textStatLastAdded);
        }
    }
}
