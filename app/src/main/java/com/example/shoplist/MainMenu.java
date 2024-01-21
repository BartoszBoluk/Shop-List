package com.example.shoplist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    private Button mButtonAddToList, mButtonCheckList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mButtonAddToList = findViewById(R.id.buttonAdd);
        mButtonCheckList = findViewById(R.id.buttonCheckList);

        mButtonAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(v, AddToListActivity.class);
            }
        });

    }

    /*
     * Funkcja uruchamia nowe Activity(Nowy ekran)
     */
    private void startActivity(View v, Class directed_class) {
        Intent intent = new Intent(this, directed_class);
        startActivity(intent);
    }
}