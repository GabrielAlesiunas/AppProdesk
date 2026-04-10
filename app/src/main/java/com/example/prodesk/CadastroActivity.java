package com.example.prodesk;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class CadastroActivity extends AppCompatActivity {

    EditText nome, email, telefone, senha, confirmarSenha;
    Button btnCadastrar;
    CheckBox checkTermos;
    TextView btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // REFERÊNCIAS
        nome = findViewById(R.id.nome);
        email = findViewById(R.id.email);
        telefone = findViewById(R.id.telefone);
        senha = findViewById(R.id.senha);
        confirmarSenha = findViewById(R.id.confirmarSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        checkTermos = findViewById(R.id.checkTermos);
        btnVoltar = findViewById(R.id.btnVoltar);

        // BOTÃO CADASTRAR
        btnCadastrar.setOnClickListener(v -> {

            String nomeStr = nome.getText().toString().trim();
            String emailStr = email.getText().toString().trim();
            String telefoneStr = telefone.getText().toString().trim();
            String senhaStr = senha.getText().toString();
            String confirmarStr = confirmarSenha.getText().toString();

            if (nomeStr.isEmpty()) {
                nome.setError("Digite seu nome");
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                email.setError("Email inválido");
                return;
            }

            if (telefoneStr.length() < 10) {
                telefone.setError("Telefone inválido");
                return;
            }

            if (senhaStr.length() < 6) {
                senha.setError("Mínimo 6 caracteres");
                return;
            }

            if (!senhaStr.equals(confirmarStr)) {
                confirmarSenha.setError("Senhas não coincidem");
                return;
            }

            if (!checkTermos.isChecked()) {
                Toast.makeText(this, "Aceite os termos", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Cadastro realizado!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // BOTÃO VOLTAR
        btnVoltar.setOnClickListener(v -> finish());
    }
}