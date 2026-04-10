package com.example.prodesk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilActivity extends AppCompatActivity {

    // FOTO
    ImageView imgPerfil;
    Button btnTrocarFoto;

    // MENU
    TextView menuPerfil, menuCartao, menuSenha;

    // EDITAR
    TextView btnEditarNome, btnEditarEmail, btnEditarTelefone, btnEditarEndereco;

    private static final int PICK_IMAGE = 1;


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
        setContentView(R.layout.activity_perfil);

        // ===== FOTO =====
        imgPerfil = findViewById(R.id.imgPerfil);
        btnTrocarFoto = findViewById(R.id.btnTrocarFoto);

        // ===== MENU =====
        menuPerfil = findViewById(R.id.menuPerfil);
        menuCartao = findViewById(R.id.menuCartao);
        menuSenha = findViewById(R.id.menuSenha);

        // ===== AÇÕES =====

        // FOTO
        btnTrocarFoto.setOnClickListener(v -> abrirGaleria());

        // EDITAR
//        btnEditarNome.setOnClickListener(v -> toast("Editar nome"));
//        btnEditarEmail.setOnClickListener(v -> toast("Editar email"));
//        btnEditarTelefone.setOnClickListener(v -> toast("Editar telefone"));
//        btnEditarEndereco.setOnClickListener(v -> toast("Editar endereço"));

        // ===== MENU NAVEGAÇÃO =====

        menuPerfil.setOnClickListener(v ->
                toast("Você já está em Dados Pessoais")
        );

        menuCartao.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartaoActivity.class);
            startActivity(intent);
        });

        menuSenha.setOnClickListener(v -> {
            Intent intent = new Intent(this, SenhaActivity.class);
            startActivity(intent);
        });
    }

    // ===== GALERIA =====
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imagemSelecionada = data.getData();
            imgPerfil.setImageURI(imagemSelecionada);
        }
    }

    // ===== TOAST =====
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}