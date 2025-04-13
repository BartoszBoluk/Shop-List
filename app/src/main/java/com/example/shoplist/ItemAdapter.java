package com.example.shoplist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final ArrayList<DocumentSnapshot> items;
    private final String listId;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ItemAdapter(ArrayList<DocumentSnapshot> items, String listId) {
        this.items = items;
        this.listId = listId;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageButton deleteButton;

        ItemViewHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textViewItemName);
            deleteButton = itemView.findViewById(R.id.buttonDeleteItem);
        }
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        DocumentSnapshot doc = items.get(position);
        String name = doc.getString("name");
        String itemId = doc.getId();

        holder.text.setText(name);

        holder.deleteButton.setOnClickListener(v -> {
            db.collection("lists").document(listId)
                    .collection("items").document(itemId)
                    .delete();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
