package com.example.shoplist;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CheckListActivity extends AppCompatActivity {

    private Button mButtonGetList;
    private ListView mListView;
    private FirebaseFirestore db;
    private ArrayList<String> shopList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        db = FirebaseFirestore.getInstance();
        mButtonGetList = findViewById(R.id.buttonGetList);
        mListView = findViewById(R.id.listView);

        // Inicjalizacja listy i adaptera
        shopList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shopList);
        mListView.setAdapter(adapter); // Ustawienie adaptera na start

        // Pobranie listy po kliknięciu przycisku
        mButtonGetList.setOnClickListener(view -> getShowList());

        // Obsługa kliknięcia elementu listy
        mListView.setOnItemClickListener((parentView, view, position, id) -> {
            String item = shopList.get(position);
            Toast.makeText(CheckListActivity.this, "Kliknąłeś: " + item, Toast.LENGTH_SHORT).show();
        });
    }

    /*
     * Pobiera dane z Firestore i aktualizuje ListView.
     */
    private void getShowList() {
        db.collection("listy_zakupow").document("moja_lista")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> pobranaLista = (ArrayList<String>) documentSnapshot.get("produkty");
                        if (pobranaLista != null && !pobranaLista.isEmpty()) {
                            shopList.clear();
                            shopList.addAll(pobranaLista);
                            adapter.notifyDataSetChanged(); // Odświeżenie ListView
                            Log.d("Firestore", "Pobrano listę: " + shopList);
                        } else {
                            Toast.makeText(this, "Lista jest pusta", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Dokument nie istnieje", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Błąd pobierania", e);
                    Toast.makeText(this, "Błąd pobierania listy", Toast.LENGTH_SHORT).show();
                });
    }
}
