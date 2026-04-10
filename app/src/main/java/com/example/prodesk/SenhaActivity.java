package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SenhaActivity extends AppCompatActivity {

    // SENHA
    EditText edtSenhaAtual, edtNovaSenha, edtConfirmarSenha;
    Button btnSalvarSenha;

    // 2FA
    Switch switch2FA;
    EditText edtCodigo2FA;
    Button btnValidar2FA;

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
        setContentView(R.layout.activity_senha);

        // ===== SENHA =====
        edtSenhaAtual = findViewById(R.id.edtSenhaAtual);
        edtNovaSenha = findViewById(R.id.edtNovaSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        btnSalvarSenha = findViewById(R.id.btnSalvarSenha);

        // ===== 2FA =====
        switch2FA = findViewById(R.id.switch2FA);
        edtCodigo2FA = findViewById(R.id.edtCodigo2FA);
        btnValidar2FA = findViewById(R.id.btnValidar2FA);

        // ===== AÇÃO TROCAR SENHA =====
        btnSalvarSenha.setOnClickListener(v -> {
            String atual = edtSenhaAtual.getText().toString();
            String nova = edtNovaSenha.getText().toString();
            String confirmar = edtConfirmarSenha.getText().toString();

            if (atual.isEmpty() || nova.isEmpty() || confirmar.isEmpty()) {
                toast("Preencha todos os campos");
            } else if (!nova.equals(confirmar)) {
                toast("As senhas não coincidem");
            } else if (nova.length() < 6) {
                toast("Senha deve ter pelo menos 6 caracteres");
            } else {
                toast("Senha alterada com sucesso!");
            }
        });

        // ===== 2FA =====
        switch2FA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtCodigo2FA.setVisibility(View.VISIBLE);
                btnValidar2FA.setVisibility(View.VISIBLE);
                toast("2FA ativado");
            } else {
                edtCodigo2FA.setVisibility(View.GONE);
                btnValidar2FA.setVisibility(View.GONE);
                toast("2FA desativado");
            }
        });

        btnValidar2FA.setOnClickListener(v -> {
            String codigo = edtCodigo2FA.getText().toString();

            if (codigo.isEmpty()) {
                toast("Digite o código");
            } else if (codigo.length() < 4) {
                toast("Código inválido");
            } else {
                toast("Código validado!");
            }
        });
    }

    // ===== TOAST =====
    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}