package com.example.shoplist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class JoinListActivity extends AppCompatActivity {

    private EditText mEitTextListId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_list);

        mEitTextListId = findViewById(R.id.editTextListId);
        Button joinButton = findViewById(R.id.buttonJoinList);

        joinButton.setOnClickListener(v -> {
            String listId = mEitTextListId.getText().toString().trim();

            if (listId.isEmpty()) {
                Toast.makeText(this, "Wpisz ID listy", Toast.LENGTH_SHORT).show();
                return;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("lists").document(listId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Intent intent = new Intent(this, ViewListActivity.class);
                            intent.putExtra("LIST_ID", listId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Nie znaleziono listy o podanym ID", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
