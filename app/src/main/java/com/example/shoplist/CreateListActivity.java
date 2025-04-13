package com.example.shoplist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateListActivity extends AppCompatActivity {

    private EditText mEditTextListName;
    private Button mButtonCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);

        mEditTextListName = findViewById(R.id.editTextListName);
        mButtonCreate = findViewById(R.id.buttonCreateList);

        mButtonCreate.setOnClickListener(v -> createList());
    }

    private void createList() {
        String listName = mEditTextListName.getText().toString().trim();

        if (listName.isEmpty()) {
            Toast.makeText(this, "Wpisz nazwę listy", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> listData = new HashMap<>();
        listData.put("name", listName);
        listData.put("createdAt", System.currentTimeMillis());

        // Tworzymy dokument z automatycznym ID
        db.collection("lists")
                .add(listData)
                .addOnSuccessListener(documentReference -> {
                    String listId = documentReference.getId();
                    copyToClipboard(listId);
                    Toast.makeText(this, "Lista utworzona! ID skopiowano: " + listId, Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Błąd tworzenia listy: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("listId", text);
        clipboard.setPrimaryClip(clip);
    }
}
