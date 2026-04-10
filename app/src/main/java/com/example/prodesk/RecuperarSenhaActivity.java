package com.example.prodesk;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class RecuperarSenhaActivity extends AppCompatActivity {

    EditText email, novaSenha, confirmarSenha;
    Button btnAlterar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        email = findViewById(R.id.email);
        novaSenha = findViewById(R.id.novaSenha);
        confirmarSenha = findViewById(R.id.confirmarSenha);
        btnAlterar = findViewById(R.id.btnAlterar);

        btnAlterar.setOnClickListener(v -> {

            String emailStr = email.getText().toString().trim();
            String senhaStr = novaSenha.getText().toString();
            String confirmarStr = confirmarSenha.getText().toString();

            if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                email.setError("Email inválido");
                return;
            }

            if (senhaStr.length() < 4) {
                novaSenha.setError("Senha muito curta");
                return;
            }

            if (!senhaStr.equals(confirmarStr)) {
                confirmarSenha.setError("Senhas não coincidem");
                return;
            }

            Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();

            finish(); // volta pro login
        });
    }
}