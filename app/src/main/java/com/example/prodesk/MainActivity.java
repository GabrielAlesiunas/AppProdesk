package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.ActionBarDrawerToggle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<Espaco> lista;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer); // 🔥 IMPORTANTE

        // MENU
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // CLIQUES DO MENU
        navigationView.setNavigationItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {

                startActivity(new Intent(this, MainActivity.class));

            } else if (id == R.id.nav_espacos) {

                startActivity(new Intent(this, MainActivity.class)); // mesma tela por enquanto

            } else if (id == R.id.nav_reservas) {

                startActivity(new Intent(this, ReservasActivity.class));

            } else if (id == R.id.nav_perfil) {

                startActivity(new Intent(this, PerfilActivity.class));

            }

            drawerLayout.closeDrawers();
            return true;
        });

        // 🔥 INFLA SUA TELA DENTRO DO DRAWER
        getLayoutInflater().inflate(R.layout.activity_main, findViewById(R.id.conteudo));

        // AGORA SIM pega o RecyclerView
        RecyclerView recycler = findViewById(R.id.conteudo)
                .findViewById(R.id.recyclerEspacos);

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
    }
}