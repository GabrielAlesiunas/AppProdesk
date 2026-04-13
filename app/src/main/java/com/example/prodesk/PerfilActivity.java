package com.example.prodesk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilActivity extends AppCompatActivity {

    ImageView imgPerfil;
    Button btnTrocarFoto, btnCartoes, btnSeguranca;
    TextView txtNome, txtEmail;

    private static final int PICK_IMAGE = 1;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        prefs = getSharedPreferences("perfil", MODE_PRIVATE);

        imgPerfil = findViewById(R.id.imgPerfil);
        btnTrocarFoto = findViewById(R.id.btnTrocarFoto);
        btnCartoes = findViewById(R.id.btnCartoes);
        btnSeguranca = findViewById(R.id.btnSeguranca);
        txtNome = findViewById(R.id.txtNome);
        txtEmail = findViewById(R.id.txtEmail);

        // carregar foto salva
        String fotoSalva = prefs.getString("foto", null);
        if (fotoSalva != null) {
            imgPerfil.setImageURI(Uri.parse(fotoSalva));
        }

        // trocar foto
        btnTrocarFoto.setOnClickListener(v -> abrirGaleria());

        // navegar para cartões
        btnCartoes.setOnClickListener(v ->
                startActivity(new Intent(this, CartaoActivity.class))
        );

        // navegar para segurança
        btnSeguranca.setOnClickListener(v ->
                startActivity(new Intent(this, SenhaActivity.class))
        );

        // MENU INFERIOR
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_perfil);

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

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri imagem = data.getData();
            imgPerfil.setImageURI(imagem);

            prefs.edit().putString("foto", imagem.toString()).apply();
        }
    }
}