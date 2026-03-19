package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ListView lista;

    String[] espacos = {
            "Coworking Centro - R$20/h",
            "Sala Tech - R$30/h",
            "Espaço Premium - R$50/h"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lista = findViewById(R.id.listView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                espacos
        );

        lista.setAdapter(adapter);

        // Clique no item
        lista.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(MainActivity.this, DetalheActivity.class);
            intent.putExtra("nome", espacos[position]);
            startActivity(intent);
        });
    }
}