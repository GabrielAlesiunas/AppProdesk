package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<Espaco> lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RECYCLER VIEW
        recycler = findViewById(R.id.recyclerEspacos);

        // LISTA
        lista = new ArrayList<>();

        // DADOS MOCKADOS
        lista.add(new Espaco("Coworking Central", "Espaço moderno com salas privativas", "R$ 250/hora"));
        lista.add(new Espaco("Hub Criativo", "Ambiente colaborativo para startups", "R$ 22/hora"));
        lista.add(new Espaco("Espaço Verde", "Área ao ar livre sustentável", "R$ 28/hora"));

        // ADAPTER
        EspacoAdapter adapter = new EspacoAdapter(lista, this);

        // CONFIGURA RECYCLER
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // MENU INFERIOR
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }

            if (id == R.id.nav_reservas) {
                startActivity(new Intent(this, HistoricoReservasActivity.class));
                return true;
            }

            if (id == R.id.nav_CadEspacos) {
                startActivity(new Intent(this, CadastroEspacoActivity.class));
                return true;
            }

            if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }
}