package com.example.prodesk;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    EditText nome, email, senha;
    Button btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = findViewById(R.id.nome);
        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        btnCadastrar.setOnClickListener(v -> {
            Toast.makeText(this, "Cadastro realizado (fake)", Toast.LENGTH_SHORT).show();
            finish(); // volta pro login
        });
    }
}