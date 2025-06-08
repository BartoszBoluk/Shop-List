package com.example.shoplist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<SuggestionAdapter.ViewHolder> {

    private final List<String> suggestions;
    private final OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(String product);
    }

    public SuggestionAdapter(List<String> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button buttonSuggestion;

        public ViewHolder(View view) {
            super(view);
            buttonSuggestion = view.findViewById(R.id.buttonSuggestion);
        }
    }

    @NonNull
    @Override
    public SuggestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionAdapter.ViewHolder holder, int position) {
        String product = suggestions.get(position);
        holder.buttonSuggestion.setText(product);
        holder.buttonSuggestion.setOnClickListener(v -> listener.onSuggestionClick(product));
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }
}
