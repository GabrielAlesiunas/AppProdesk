package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, senha;
    Button btnLogin;
    TextView btnCadastro, btnEsqueciSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCadastro = findViewById(R.id.btnCadastro);
        btnEsqueciSenha = findViewById(R.id.btnEsqueciSenha);

        // LOGIN
        btnLogin.setOnClickListener(v -> {

            String emailTxt = email.getText().toString().trim();
            String senhaTxt = senha.getText().toString().trim();

            if (emailTxt.isEmpty()) {
                email.setError("Digite o email");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailTxt).matches()) {
                email.setError("Email inválido");
                return;
            }

            if (senhaTxt.length() < 4) {
                senha.setError("Senha muito curta");
                return;
            }

            Toast.makeText(this, "Login realizado", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // ESQUECI SENHA
        btnEsqueciSenha.setOnClickListener(v -> {
            startActivity(new Intent(this, RecuperarSenhaActivity.class));
        });

        // CADASTRO
        btnCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });
    }
}