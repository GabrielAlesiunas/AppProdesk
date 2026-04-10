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
        setContentView(R.layout.activity_cartao);

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

        // BOTÃO SALVAR
        btnSalvarCartao.setOnClickListener(v -> salvarCartao());
    }

    private void salvarCartao() {
        String numero = edtNumeroCartao.getText().toString().trim();
        String nome = edtNomeCartao.getText().toString().trim();

        if (numero.isEmpty() || nome.isEmpty()) {
            toast("Preencha os dados do cartão");
            return;
        }

        if (numero.length() < 8) {
            toast("Número de cartão inválido");
            return;
        }

        // ADICIONA NA LISTA
        listaCartoes.add(new Cartao(numero, nome));
        adapter.notifyDataSetChanged();

        // LIMPAR CAMPOS
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