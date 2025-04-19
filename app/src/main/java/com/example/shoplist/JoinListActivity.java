package com.example.shoplist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class JoinListActivity extends AppCompatActivity {

    private EditText mEitTextListId;
    private ArrayList<String> mListNames = new ArrayList<>();
    private HashMap<String, String> mNameToIdMap = new HashMap<>();

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
                            String listName = documentSnapshot.getString("name");
                            saveJoinedListLocally(listName, listId);
                            Intent intent = new Intent(this, ViewListActivity.class);
                            intent.putExtra("LIST_ID", listId);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Nie znaleziono listy o podanym ID", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Błąd: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        ListView listView = findViewById(R.id.ListViewJoinedLists);
        loadJoinedLists();

        // Niestandardowy adapter do wyświetlania listy z przyciskiem "X"
        CustomListAdapter adapter = new CustomListAdapter();
        listView.setAdapter(adapter);
    }

    private void saveJoinedListLocally(String listName, String listId) {
        String data = listName + "|" + listId + "\n";
        try {
            FileOutputStream fos = openFileOutput("joined_lists.txt", MODE_APPEND);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadJoinedLists() {
        try {
            FileInputStream fis = openFileInput("joined_lists.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            HashMap<String, String> uniqueLists = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2) {
                    String name = parts[0];
                    String id = parts[1];
                    uniqueLists.putIfAbsent(id, name);
                }
            }
            reader.close();
            saveUniqueListsLocally(uniqueLists);
            updateListView(uniqueLists);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUniqueListsLocally(HashMap<String, String> uniqueLists) {
        try {
            FileOutputStream fos = openFileOutput("joined_lists.txt", MODE_PRIVATE);
            StringBuilder data = new StringBuilder();

            for (String listId : uniqueLists.keySet()) {
                String listName = uniqueLists.get(listId);
                data.append(listName).append("|").append(listId).append("\n");
            }

            fos.write(data.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateListView(HashMap<String, String> uniqueLists) {
        mListNames.clear();
        mNameToIdMap.clear();

        for (String listId : uniqueLists.keySet()) {
            String listName = uniqueLists.get(listId);
            mListNames.add(listName);
            mNameToIdMap.put(listName, listId);
        }

        ListView listView = findViewById(R.id.ListViewJoinedLists);
        CustomListAdapter adapter = new CustomListAdapter();
        listView.setAdapter(adapter);
    }

    private class CustomListAdapter extends ArrayAdapter<String> {

        public CustomListAdapter() {
            super(JoinListActivity.this, R.layout.list_item, mListNames);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(JoinListActivity.this);
                view = inflater.inflate(R.layout.list_item, parent, false);
            }

            String listName = getItem(position);
            String listId = mNameToIdMap.get(listName);

            TextView textViewListName = view.findViewById(R.id.textViewListName);
            textViewListName.setText(listName);

            Button deleteButton = view.findViewById(R.id.buttonDeleteList);
            deleteButton.setOnClickListener(v -> removeList(listName, listId));

            // Nasłuchiwanie kliknięcia na całą listę (poza X)
            view.setOnClickListener(v -> {
                Intent intent = new Intent(JoinListActivity.this, ViewListActivity.class);
                intent.putExtra("LIST_ID", listId);
                startActivity(intent);
            });

            return view;
        }
    }

    private void removeList(String listName, String listId) {
        mListNames.remove(listName);
        mNameToIdMap.remove(listName);
        removeListFromFile(listId);
        updateListView(mNameToIdMap);
        Toast.makeText(this, "Lista usunięta!", Toast.LENGTH_SHORT).show();
    }

    private void removeListFromFile(String listId) {
        try {
            FileInputStream fis = openFileInput("joined_lists.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            StringBuilder newData = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 2 && !parts[1].equals(listId)) {
                    newData.append(line).append("\n");
                }
            }

            reader.close();

            FileOutputStream fos = openFileOutput("joined_lists.txt", MODE_PRIVATE);
            fos.write(newData.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
