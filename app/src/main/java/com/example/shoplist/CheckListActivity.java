package com.example.shoplist;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CheckListActivity extends AppCompatActivity {

    private Button mButtonGetList;
    private ListView mListView;
    private FirebaseFirestore db;
    ArrayList<String> shopList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        db = FirebaseFirestore.getInstance();
        mButtonGetList = findViewById(R.id.buttonGetList);
        mListView = findViewById(R.id.listView);

        // Ustawienie adaptera do ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shopList);


        mButtonGetList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getShowList();
                mListView.setAdapter(adapter);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                String item = shopList.get(position);
                Toast.makeText(CheckListActivity.this, "Kliknąłeś: " + item, Toast.LENGTH_SHORT).show();
            }
        });

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void getShowList() {
        db.collection("listy_zakupow").document("moja_lista")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> pobranaLista = (ArrayList<String>) documentSnapshot.get("produkty");
                        if (pobranaLista != null) {
                            shopList.clear();
                            shopList.addAll(pobranaLista);
                            Log.d("Firestore", "Pobrano listę: " + shopList.toString());
                        }
                    }
                    String tekst2 = String.join(", ", shopList);
                    Toast.makeText(this, "sda" + tekst2, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Błąd pobierania", e));
    }
}