package com.example.shoplist;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class ViewStatisticsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StatsAdapter statsAdapter;
    private List<StatItem> statList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_statistics);

        recyclerView = findViewById(R.id.recyclerViewStats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        statsAdapter = new StatsAdapter(statList);
        recyclerView.setAdapter(statsAdapter);

        loadStats();
    }

    private void loadStats() {
        db.collection("stats")
                .orderBy("count", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    statList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("name");
                        Long count = doc.getLong("count");
                        Long lastAdded = doc.getLong("lastAdded");

                        if (name != null && count != null) {
                            statList.add(new StatItem(name, count, lastAdded));
                        }
                    }
                    statsAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Błąd ładowania statystyk", Toast.LENGTH_SHORT).show();
                });
    }
}
