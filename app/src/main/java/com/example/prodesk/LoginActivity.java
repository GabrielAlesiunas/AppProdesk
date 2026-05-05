package com.example.prodesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText email, senha;
    Button btnLogin;
    TextView btnCadastro, btnEsqueciSenha;

    FirebaseAuth mAuth;

    // ⏱️ 30 minutos
    private static final long TEMPO_LIMITE = 30 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        senha = findViewById(R.id.senha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCadastro = findViewById(R.id.btnCadastro);
        btnEsqueciSenha = findViewById(R.id.btnEsqueciSenha);

        mAuth = FirebaseAuth.getInstance();

        // 🔥 VERIFICA SESSÃO AO ABRIR
        if (mAuth.getCurrentUser() != null) {

            if (sessaoExpirada()) {

                // sessão venceu → desloga
                mAuth.signOut();
                limpar2FA();

                Toast.makeText(this,
                        "Sessão expirada, faça login novamente",
                        Toast.LENGTH_LONG).show();

            } else {

                SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
                boolean twoFaOk = prefs.getBoolean("2fa_ok", false);

                if (twoFaOk) {
                    irParaHome();
                } else {
                    irParaVerificacao();
                }
            }
        }

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

            if (senhaTxt.length() < 6) {
                senha.setError("Mínimo 6 caracteres");
                return;
            }

            mAuth.signInWithEmailAndPassword(emailTxt, senhaTxt)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            // 🔥 SALVA HORÁRIO DO LOGIN
                            salvarHorarioLogin();

                            // 🔒 FORÇA NOVO 2FA
                            limpar2FA();

                            Toast.makeText(this, "Login realizado!", Toast.LENGTH_SHORT).show();

                            // 👉 vai pra verificação
                            irParaVerificacao();

                        } else {
                            Toast.makeText(this,
                                    "Erro: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        });

        btnCadastro.setOnClickListener(v -> {
            startActivity(new Intent(this, CadastroActivity.class));
        });

        btnEsqueciSenha.setOnClickListener(v -> {
            startActivity(new Intent(this, RecuperarSenhaActivity.class));
        });
    }

    // 🔐 SALVA HORÁRIO
    private void salvarHorarioLogin() {
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        prefs.edit()
                .putLong("login_time", System.currentTimeMillis())
                .apply();
    }

    // ⏱️ VERIFICA SE EXPIROU
    private boolean sessaoExpirada() {

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);

        long loginTime = prefs.getLong("login_time", 0);
        long agora = System.currentTimeMillis();

        return (agora - loginTime) > TEMPO_LIMITE;
    }

    // 🔥 LIMPA 2FA (IMPORTANTE)
    private void limpar2FA() {
        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        prefs.edit()
                .putBoolean("2fa_ok", false)
                .apply();
    }

    // 🚀 IR PRA HOME
    private void irParaHome() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // 🔐 IR PRA VERIFICAÇÃO
    private void irParaVerificacao() {
        startActivity(new Intent(this, VerificacaoActivity.class));
        finish();
    }
}