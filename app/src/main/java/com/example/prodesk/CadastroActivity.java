package com.example.prodesk;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastroActivity extends AppCompatActivity {

    EditText nome, email, telefone, senha, confirmarSenha;
    Button btnCadastrar;
    CheckBox checkTermos;
    TextView btnVoltar;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = findViewById(R.id.nome);
        email = findViewById(R.id.email);
        telefone = findViewById(R.id.telefone);
        senha = findViewById(R.id.senha);
        confirmarSenha = findViewById(R.id.confirmarSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        checkTermos = findViewById(R.id.checkTermos);
        btnVoltar = findViewById(R.id.btnVoltar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnCadastrar.setOnClickListener(v -> cadastrarUsuario());

        btnVoltar.setOnClickListener(v -> finish());
    }

    private void cadastrarUsuario() {

        String nomeStr = nome.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String telefoneStr = telefone.getText().toString().trim();
        String senhaStr = senha.getText().toString();
        String confirmarStr = confirmarSenha.getText().toString();

        // 🔹 VALIDAÇÕES

        if (!nomeStr.matches("^[a-zA-ZÀ-ÿ\\s]{3,}$")) {
            nome.setError("Nome inválido");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
            email.setError("Email inválido");
            return;
        }

        if (!telefoneStr.matches("^\\d{10,11}$")) {
            telefone.setError("Telefone inválido");
            return;
        }

        if (senhaStr.length() < 6) {
            senha.setError("Mínimo 6 caracteres");
            return;
        }

        if (!senhaStr.matches(".*[A-Z].*")) {
            senha.setError("Precisa de letra maiúscula");
            return;
        }

        if (!senhaStr.matches(".*[0-9].*")) {
            senha.setError("Precisa de número");
            return;
        }

        if (!senhaStr.equals(confirmarStr)) {
            confirmarSenha.setError("Senhas não coincidem");
            return;
        }

        if (!checkTermos.isChecked()) {
            checkTermos.setError("Obrigatório aceitar");
            return;
        } else {
            checkTermos.setError(null);
        }

        // 🔥 BLOQUEIA BOTÃO (loading simples)
        btnCadastrar.setEnabled(false);
        btnCadastrar.setText("Cadastrando...");

        // 🔥 CRIAR USUÁRIO
        mAuth.createUserWithEmailAndPassword(emailStr, senhaStr)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String userId = mAuth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();
                        user.put("nome", nomeStr);
                        user.put("email", emailStr);
                        user.put("telefone", telefoneStr);

                        db.collection("usuarios")
                                .document(userId)
                                .set(user)
                                .addOnSuccessListener(aVoid -> {

                                    Toast.makeText(this, "✅ Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();

                                    finish(); // volta pro login
                                })
                                .addOnFailureListener(e -> {
                                    mostrarErro("Erro ao salvar dados");
                                    resetBotao();
                                });

                    } else {
                        tratarErroFirebase(task.getException().getMessage());
                        resetBotao();
                    }
                });
    }

    private void resetBotao() {
        btnCadastrar.setEnabled(true);
        btnCadastrar.setText("Cadastrar");
    }

    private void mostrarErro(String msg) {
        Toast.makeText(this, "❌ " + msg, Toast.LENGTH_LONG).show();
    }

    private void tratarErroFirebase(String erro) {

        if (erro.contains("email address is already in use")) {
            mostrarErro("Email já cadastrado");
        } else if (erro.contains("badly formatted")) {
            mostrarErro("Email inválido");
        } else if (erro.contains("Password should be at least")) {
            mostrarErro("Senha fraca");
        } else {
            mostrarErro("Erro: " + erro);
        }
    }
}