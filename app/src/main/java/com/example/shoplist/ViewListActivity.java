package com.example.shoplist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewListActivity extends AppCompatActivity {

    private String mListId;
    private TextView mTextViewList;
    private EditText mEditTextItem;
    private RecyclerView recyclerView;
    private ItemAdapter itemAdapter;
    private ArrayList<DocumentSnapshot> items = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemsRef;
    private Button mButtonCopyID, mAddButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        mTextViewList = findViewById(R.id.textViewListName);
        mEditTextItem = findViewById(R.id.editTextItemName);
        recyclerView = findViewById(R.id.recyclerViewItems);
        mAddButton = findViewById(R.id.buttonAddItem);
        mButtonCopyID = findViewById(R.id.buttonCopyID);

        mListId = getIntent().getStringExtra("LIST_ID"); // Klucz zmieniony na "LIST_ID"
        if (mListId == null || mListId.isEmpty()) {
            Toast.makeText(this, "Brak ID listy", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        itemsRef = db.collection("lists").document(mListId).collection("items");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(items, mListId);
        recyclerView.setAdapter(itemAdapter);

        loadListInfo();
        loadItems();

        mAddButton.setOnClickListener(v -> addItem());

        mButtonCopyID.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("List ID", mListId);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "ID listy skopiowane do schowka", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadListInfo() {
        db.collection("lists").document(mListId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String name = doc.getString("name");
                        mTextViewList.setText("Lista: " + name);
                    }
                });
    }

    private void addItem() {
        String itemName = mEditTextItem.getText().toString().trim();
        if (itemName.isEmpty()) {
            Toast.makeText(this, "Wpisz nazwę zakupu", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> item = new HashMap<>();
        item.put("name", itemName);
        item.put("timestamp", System.currentTimeMillis());

        itemsRef.add(item)
                .addOnSuccessListener(ref -> {
                    mEditTextItem.setText("");
                    Toast.makeText(this, "Dodano!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadItems() {
        itemsRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;

                    items.clear();
                    items.addAll(snapshots.getDocuments());
                    itemAdapter.notifyDataSetChanged();
                });
    }
}
