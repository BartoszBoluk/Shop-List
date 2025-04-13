package com.example.shoplist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddToListActivity extends AppCompatActivity {

    private Button mTestButton;
    private TextInputEditText mTextInput;
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

        // Zamienia text z pola tekstowego na String i wrzuca do ArrayList
        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text;
                text = String.valueOf(mTextInput.getText());
                dodajProduktDoListy(text);
                mTextInput.setText("");
            }
        });
    }
    /*
     * Funkcja tworzy listę. Następnie stowrzy kolekcję i dokument na Firestore, gdzie wrzuci dane
     * z listy. Jeśli wszystko się powiedzie to dane będą dostępne na stronie Firestore.
     */
    private void dodajProduktDoListy(String nowyProdukt) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference listaRef = db.collection("listy_zakupow").document("moja_lista");

        listaRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Pobranie istniejącej listy produktów
                ArrayList<String> aktualnaLista = (ArrayList<String>) documentSnapshot.get("produkty");

                if (aktualnaLista == null) {
                    aktualnaLista = new ArrayList<>();
                }

                // Dodanie nowego produktu do listy
                aktualnaLista.add(nowyProdukt);

                // Aktualizacja Firestore
                listaRef.update("produkty", aktualnaLista)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Produkt dodany pomyślnie!");
                            Toast.makeText(this, "Dodano produkt: " + nowyProdukt, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Błąd podczas dodawania produktu", e);
                            Toast.makeText(this, "Błąd podczas dodawania produktu", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Jeśli dokument nie istnieje, tworzymy nową listę
                ArrayList<String> nowaLista = new ArrayList<>();
                nowaLista.add(nowyProdukt);

                listaRef.set(Collections.singletonMap("produkty", nowaLista))
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firestore", "Nowa lista utworzona i produkt dodany!");
                            Toast.makeText(this, "Dodano produkt: " + nowyProdukt, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firestore", "Błąd przy tworzeniu nowej listy", e);
                            Toast.makeText(this, "Błąd przy tworzeniu listy", Toast.LENGTH_SHORT).show();
                        });
            }
        }).addOnFailureListener(e -> {
            Log.e("Firestore", "Błąd pobierania listy", e);
            Toast.makeText(this, "Błąd pobierania listy", Toast.LENGTH_SHORT).show();
        });
    }

}