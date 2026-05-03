package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.net.Uri;

public class MainActivity extends AppCompatActivity {

    RecyclerView recycler;
    ArrayList<Espaco> lista = new ArrayList<>();
    MapView map;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().load(
                getApplicationContext(),
                getSharedPreferences("osmdroid", MODE_PRIVATE)
        );

        setContentView(R.layout.activity_main);

        FloatingActionButton btnChatbot = findViewById(R.id.btnChatbot);

        btnChatbot.setOnClickListener(v -> {
            abrirChatbot();
        });

        db = FirebaseFirestore.getInstance();

        recycler = findViewById(R.id.recyclerEspacos);
        map = findViewById(R.id.map);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        map.setMultiTouchControls(true);

        carregarEspacos();

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

    private void carregarEspacos() {

        db.collection("espacos")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) return;

                    lista.clear();
                    map.getOverlays().clear();

                    GeoPoint centro = new GeoPoint(-23.55, -46.63);

                    for (var doc : value.getDocuments()) {

                        Espaco e = doc.toObject(Espaco.class);
                        if (e == null) continue;

                        e.setId(doc.getId()); // 🔥 ESSENCIAL (ERA ISSO QUE FALTAVA)

                        lista.add(e);

                        if (e.getLatitude() != 0 && e.getLongitude() != 0) {

                            GeoPoint point = new GeoPoint(e.getLatitude(), e.getLongitude());

                            Marker marker = new Marker(map);
                            marker.setPosition(point);
                            marker.setTitle(e.getNome());
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

                            map.getOverlays().add(marker);
                        }
                    }

                    recycler.setAdapter(new EspacoAdapter(lista, this));

                    map.getController().setCenter(centro);
                    map.getController().setZoom(13.5);

                    map.invalidate();
                });
    }

    private void abrirChatbot() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://cdn.botpress.cloud/webchat/v3.6/shareable.html?configUrl=https://files.bpcontent.cloud/2026/05/03/12/20260503121853-61DFXM6I.json"));
        startActivity(intent);
    }
}