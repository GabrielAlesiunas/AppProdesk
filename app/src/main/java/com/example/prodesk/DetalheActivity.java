package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class DetalheActivity extends AppCompatActivity {

    TextView nome;
    Button reservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        nome = findViewById(R.id.nomeEspaco);
        reservar = findViewById(R.id.btnReservar);

        String nomeEspaco = getIntent().getStringExtra("nome");
        nome.setText(nomeEspaco);

        reservar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservasActivity.class);
            intent.putExtra("reserva", nomeEspaco);
            startActivity(intent);
        });
    }
}