package com.example.shoplist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mButtonStart, mButtonExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonStart = findViewById(R.id.buttonStart);
        mButtonExit = findViewById(R.id.buttonExit);

        mButtonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(v, MainMenu.class);
            }
        });

        mButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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