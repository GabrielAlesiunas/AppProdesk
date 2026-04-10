package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class ReservasActivity extends AppCompatActivity {

    ListView lista;
    static ArrayList<String> reservas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // MENU INFERIOR
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                return true;
            }

            if (id == R.id.nav_CadEspacos) {
                return true;
            }

            if (id == R.id.nav_reservas) {
                startActivity(new Intent(this, ReservasActivity.class));
                return true;
            }

            if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });

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