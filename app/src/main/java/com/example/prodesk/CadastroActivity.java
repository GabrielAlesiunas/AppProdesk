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

            // VALIDAÇÕES
            if (nomeStr.isEmpty() || emailStr.isEmpty() || telefoneStr.isEmpty() || senhaStr.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (senhaStr.length() < 6) {
                Toast.makeText(this, "Senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!senhaStr.equals(confirmarStr)) {
                Toast.makeText(this, "Senhas não coincidem", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!checkTermos.isChecked()) {
                Toast.makeText(this, "Aceite os termos de uso", Toast.LENGTH_SHORT).show();
                return;
            }

            // SUCESSO (FAKE POR ENQUANTO)
            Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

            finish(); // volta para login
        });

        // BOTÃO VOLTAR
        btnVoltar.setOnClickListener(v -> finish());
    }
}