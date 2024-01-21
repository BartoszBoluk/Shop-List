package com.example.shoplist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    private Button mButtonAddToList, mButtonCheckList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        mButtonAddToList = findViewById(R.id.buttonAdd);
        mButtonCheckList = findViewById(R.id.buttonCheckList);


    }
}