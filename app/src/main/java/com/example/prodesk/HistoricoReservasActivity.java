package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoricoReservasActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;

    List<ReservaModel> lista = new ArrayList<>();
    ReservaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_reservas);

        db = FirebaseFirestore.getInstance();

        recyclerView = findViewById(R.id.recyclerReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ReservaAdapter(this, lista);
        recyclerView.setAdapter(adapter);

        carregarReservas();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_reservas);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
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

            return id == R.id.nav_reservas;
        });
    }

    private void carregarReservas() {

        db.collection("reservas")
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        ReservaModel r = new ReservaModel();

                        r.espacoId = doc.getString("espacoId");
                        r.nomeEspaco = doc.getString("nomeEspaco");
                        r.dataInicio = doc.getLong("dataInicio");
                        r.dataFim = doc.getLong("dataFim");
                        r.valorTotal = doc.getDouble("valorTotal");
                        r.imagem = doc.getString("imagem");
                        r.pagamento = doc.getString("pagamento");

                        lista.add(r);
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}