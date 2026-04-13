package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SenhaActivity extends AppCompatActivity {

    EditText edtSenhaAtual, edtNovaSenha, edtConfirmarSenha;
    CheckBox checkMostrarSenha;
    Button btnSalvarSenha;

    Switch switch2FA;
    EditText edtCodigo2FA;
    Button btnValidar2FA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senha);

        // Bottom Navigation
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

        edtSenhaAtual = findViewById(R.id.edtSenhaAtual);
        edtNovaSenha = findViewById(R.id.edtNovaSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        checkMostrarSenha = findViewById(R.id.checkMostrarSenha);
        btnSalvarSenha = findViewById(R.id.btnSalvarSenha);

        switch2FA = findViewById(R.id.switch2FA);
        edtCodigo2FA = findViewById(R.id.edtCodigo2FA);
        btnValidar2FA = findViewById(R.id.btnValidar2FA);

        // Mostrar / esconder senha
        checkMostrarSenha.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                edtSenhaAtual.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edtNovaSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                edtConfirmarSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                edtSenhaAtual.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edtNovaSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
                edtConfirmarSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        // Salvar senha
        btnSalvarSenha.setOnClickListener(v -> {
            String atual = edtSenhaAtual.getText().toString();
            String nova = edtNovaSenha.getText().toString();
            String confirmar = edtConfirmarSenha.getText().toString();

            if (atual.isEmpty() || nova.isEmpty() || confirmar.isEmpty()) {
                toast("Preencha todos os campos");
            } else if (!nova.equals(confirmar)) {
                toast("As senhas não coincidem");
            } else if (nova.length() < 6) {
                toast("A senha deve ter pelo menos 6 caracteres");
            } else {
                toast("Senha alterada com sucesso!");
            }
        });

        // 2FA
        switch2FA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            edtCodigo2FA.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            btnValidar2FA.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        btnValidar2FA.setOnClickListener(v -> {
            String codigo = edtCodigo2FA.getText().toString();

            if (codigo.length() < 4) {
                toast("Código inválido");
            } else {
                toast("Código verificado!");
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}