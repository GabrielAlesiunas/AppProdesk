package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, senha;
    Button btnLogin;
    TextView btnCadastro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCadastro = findViewById(R.id.btnCadastro);

        // Clique no botão Entrar
        btnLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Login clicado", Toast.LENGTH_SHORT).show();

            // só pra testar navegação
            startActivity(new Intent(this, MainActivity.class));
        });

        // Ir para cadastro
        btnCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });
    }
}