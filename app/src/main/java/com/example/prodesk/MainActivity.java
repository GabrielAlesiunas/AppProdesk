package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.views.MapView;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    List<Espaco> lista;
    MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_main);

        // MAPA
        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        GeoPoint centro = new GeoPoint(-23.55, -46.63); // SP
        map.getController().setZoom(13.0);
        map.getController().setCenter(centro);

        // RECYCLER
        recycler = findViewById(R.id.recyclerEspacos);

        lista = new ArrayList<>();

        // DADOS MOCKADOS
        lista.add(new Espaco("Coworking Central", "Espaço moderno com salas privativas", "R$ 25/hora"));
        lista.add(new Espaco("Hub Criativo", "Ambiente colaborativo para startups", "R$ 22/hora"));
        lista.add(new Espaco("Espaço Verde", "Área ao ar livre sustentável", "R$ 28/hora"));

        EspacoAdapter adapter = new EspacoAdapter(lista, this);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        // MARCADORES NO MAPA
        for (Espaco e : lista) {

            // posição aleatória próxima (até você usar lat/lng real)
            GeoPoint ponto = new GeoPoint(
                    -23.55 + Math.random() / 100,
                    -46.63 + Math.random() / 100
            );

            Marker marker = new Marker(map);
            marker.setPosition(ponto);
            marker.setTitle(e.getNome());

            map.getOverlays().add(marker);
        }

        // MENU INFERIOR
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_home);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) return true;

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