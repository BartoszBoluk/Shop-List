package com.example.shoplist;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewListActivity extends AppCompatActivity {

    private String mListId;
    private TextView mTextViewList;
    private AutoCompleteTextView mEditTextItem;
    private RecyclerView recyclerViewItems, recyclerViewSuggestions;
    private ItemAdapter itemAdapter;
    private SuggestionAdapter suggestionAdapter;
    private ArrayList<DocumentSnapshot> items = new ArrayList<>();
    private ArrayList<String> suggestions = new ArrayList<>(Arrays.asList(
            "Chleb", "Mleko", "Masło", "Jajka", "Ser", "Pomidor", "Ogórek", "Kawa", "Herbata", "Czekolada"
    ));
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference itemsRef;
    private Button mButtonCopyID, mAddButton, mButtonAddFromPicture;
    private static final int REQUEST_IMAGE_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_list);

        mTextViewList = findViewById(R.id.textViewListName);
        mEditTextItem = findViewById(R.id.editTextItemName);
        recyclerViewItems = findViewById(R.id.recyclerViewItems);
        recyclerViewSuggestions = findViewById(R.id.recyclerViewSuggestions);
        mAddButton = findViewById(R.id.buttonAddItem);
        mButtonCopyID = findViewById(R.id.buttonCopyID);
        mButtonAddFromPicture = findViewById(R.id.buttonAddFromPicture);

        mListId = getIntent().getStringExtra("LIST_ID");
        if (mListId == null || mListId.isEmpty()) {
            Toast.makeText(this, "Brak ID listy", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        itemsRef = db.collection("lists").document(mListId).collection("items");

        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter(items, mListId);
        recyclerViewItems.setAdapter(itemAdapter);

        // Set up suggestion list
        suggestionAdapter = new SuggestionAdapter(suggestions, suggestion -> {
            mEditTextItem.setText(suggestion);
        });
        recyclerViewSuggestions.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewSuggestions.setAdapter(suggestionAdapter);

        loadListInfo();
        loadItems();

        mAddButton.setOnClickListener(v -> addItem());

        mButtonCopyID.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("List ID", mListId);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "ID listy skopiowane do schowka", Toast.LENGTH_SHORT).show();
        });

        mButtonAddFromPicture.setOnClickListener(v -> pickImageFromGallery());
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
                    updateProductStats(itemName);
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

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                recognizeTextFromImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Błąd ładowania obrazu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void recognizeTextFromImage(Bitmap bitmap) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(image)
                .addOnSuccessListener(result -> {
                    ArrayList<String> products = new ArrayList<>();

                    for (Text.TextBlock block : result.getTextBlocks()) {
                        for (Text.Line line : block.getLines()) {
                            String product = line.getText().trim();
                            if (!product.isEmpty() && !products.contains(product)) {
                                products.add(product);
                            }
                        }
                    }

                    if (products.isEmpty()) {
                        Toast.makeText(this, "Nie znaleziono produktów", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (String product : products) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("name", product);
                        item.put("timestamp", System.currentTimeMillis());

                        itemsRef.add(item);
                        updateProductStats(product);
                    }

                    Toast.makeText(this, "Dodano " + products.size() + " produktów", Toast.LENGTH_LONG).show();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Błąd rozpoznawania tekstu", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    private void updateProductStats(String productName) {
        String productKey = productName.toLowerCase();
        DocumentReference statRef = db.collection("stats").document(productKey);

        statRef.get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                statRef.update("count", FieldValue.increment(1), "lastAdded", System.currentTimeMillis())
                        .addOnSuccessListener(aVoid -> {
                            // log sukcesu
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Błąd aktualizacji statystyk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        });
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put("name", productName);
                data.put("count", 1);
                data.put("lastAdded", System.currentTimeMillis());
                statRef.set(data)
                        .addOnSuccessListener(aVoid -> {})
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Błąd zapisu statystyk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        });
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Błąd pobrania statystyk: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        });
    }
}
