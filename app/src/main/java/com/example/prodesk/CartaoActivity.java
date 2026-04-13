package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CartaoActivity extends AppCompatActivity {

    EditText edtNumeroCartao, edtNomeCartao, edtValidade, edtCVV;
    Button btnSalvarCartao;

    RecyclerView recyclerCartoes;
    CartaoAdapter adapter;
    List<Cartao> listaCartoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao);

        // BOTTOM NAV
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
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

        // INPUTS
        edtNumeroCartao = findViewById(R.id.edtNumeroCartao);
        edtNomeCartao = findViewById(R.id.edtNomeCartao);
        edtValidade = findViewById(R.id.edtValidade);
        edtCVV = findViewById(R.id.edtCVV);
        btnSalvarCartao = findViewById(R.id.btnSalvarCartao);

        // LISTA
        recyclerCartoes = findViewById(R.id.recyclerCartoes);
        listaCartoes = new ArrayList<>();

        adapter = new CartaoAdapter(listaCartoes);
        recyclerCartoes.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartoes.setAdapter(adapter);

        btnSalvarCartao.setOnClickListener(v -> salvarCartao());
    }

    private void salvarCartao() {
        String numero = edtNumeroCartao.getText().toString().trim();
        String nome = edtNomeCartao.getText().toString().trim();

        if (numero.length() < 8 || nome.isEmpty()) {
            toast("Dados inválidos");
            return;
        }

        listaCartoes.add(new Cartao(numero, nome));
        adapter.notifyDataSetChanged();

        edtNumeroCartao.setText("");
        edtNomeCartao.setText("");
        edtValidade.setText("");
        edtCVV.setText("");

        toast("Cartão adicionado!");
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}