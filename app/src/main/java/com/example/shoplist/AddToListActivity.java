package com.example.shoplist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddToListActivity extends AppCompatActivity {

    private Button mTestButton;
    private TextInputEditText mTextInput;
    ArrayList<String> shopList = new ArrayList<>();
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_list);

        mTestButton = findViewById(R.id.buttonTest);
        mTextInput = findViewById(R.id.textInput);

        // Inicjalizacja Firestore
        db = FirebaseFirestore.getInstance();

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = String.valueOf(mTextInput.getText());
                shopList.add(text);
                saveShopList();
            }
        });
    }

    private void saveShopList() {
        Map<String, Object> lista = new HashMap<>();
        lista.put("produkty", shopList);

        db.collection("listy_zakupow") // Kolekcja w Firestore
                .document("moja_lista") // Dokument
                .set(lista)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Lista zakupów została zapisana!");
                    Toast.makeText(this, "Lista została zapisana!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Błąd zapisu", e);
                    Toast.makeText(this, "Błąd zapisu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}