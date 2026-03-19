package com.example.prodesk;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ReservasActivity extends AppCompatActivity {

    ListView lista;
    static ArrayList<String> reservas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservas);

        lista = findViewById(R.id.listReservas);

        String novaReserva = getIntent().getStringExtra("reserva");

        if (novaReserva != null) {
            reservas.add(novaReserva);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                reservas
        );

        lista.setAdapter(adapter);
    }
}