package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.*;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.*;
import androidx.appcompat.widget.SwitchCompat;

public class SenhaActivity extends AppCompatActivity {

    EditText edtSenhaAtual, edtNovaSenha, edtConfirmarSenha;
    CheckBox checkMostrarSenha;
    Button btnSalvarSenha;

    SwitchCompat switch2FA;
    EditText edtCodigo2FA;
    Button btnValidar2FA;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senha);

        mAuth = FirebaseAuth.getInstance();

        edtSenhaAtual = findViewById(R.id.edtSenhaAtual);
        edtNovaSenha = findViewById(R.id.edtNovaSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        checkMostrarSenha = findViewById(R.id.checkMostrarSenha);
        btnSalvarSenha = findViewById(R.id.btnSalvarSenha);

        switch2FA = findViewById(R.id.switch2FA); // agora bate com o XML
        edtCodigo2FA = findViewById(R.id.edtCodigo2FA);
        btnValidar2FA = findViewById(R.id.btnValidar2FA);

        // Mostrar senha
        checkMostrarSenha.setOnCheckedChangeListener((b, isChecked) -> {
            if (isChecked) {
                setVisible();
            } else {
                setHidden();
            }
        });

        // Salvar senha (🔥 REAL COM FIREBASE)
        btnSalvarSenha.setOnClickListener(v -> alterarSenhaFirebase());

        // 2FA
        switch2FA.setOnCheckedChangeListener((b, isChecked) -> {
            edtCodigo2FA.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            btnValidar2FA.setVisibility(isChecked ? View.VISIBLE : View.GONE);

            if (isChecked) {
                toast("Código enviado (simulado)");
            }
        });

        btnValidar2FA.setOnClickListener(v -> {
            if (edtCodigo2FA.getText().toString().length() < 4) {
                edtCodigo2FA.setError("Código inválido");
            } else {
                toast("2FA validado!");
            }
        });

        // MENU
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_perfil);

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
                return true;
            }

            return false;
        });
    }

    private void alterarSenhaFirebase() {

        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            toast("Usuário não autenticado");
            return;
        }

        String senhaAtual = edtSenhaAtual.getText().toString();
        String novaSenha = edtNovaSenha.getText().toString();
        String confirmar = edtConfirmarSenha.getText().toString();

        // Validação
        if (senhaAtual.isEmpty()) {
            edtSenhaAtual.setError("Digite a senha atual");
            return;
        }

        if (novaSenha.length() < 6) {
            edtNovaSenha.setError("Mínimo 6 caracteres");
            return;
        }

        if (!novaSenha.equals(confirmar)) {
            edtConfirmarSenha.setError("Senhas não coincidem");
            return;
        }

        // 🔥 REAUTENTICAÇÃO (OBRIGATÓRIO)
        AuthCredential credential = EmailAuthProvider.getCredential(
                user.getEmail(),
                senhaAtual
        );

        user.reauthenticate(credential)
                .addOnSuccessListener(aVoid -> {

                    // 🔥 ALTERAR SENHA
                    user.updatePassword(novaSenha)
                            .addOnSuccessListener(aVoid1 -> {
                                toast("Senha atualizada com sucesso!");
                                limparCampos();
                            })
                            .addOnFailureListener(e ->
                                    toast("Erro ao atualizar: " + e.getMessage())
                            );

                })
                .addOnFailureListener(e ->
                        toast("Senha atual incorreta")
                );
    }

    private void setVisible() {
        edtSenhaAtual.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        edtNovaSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        edtConfirmarSenha.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
    }

    private void setHidden() {
        edtSenhaAtual.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtNovaSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtConfirmarSenha.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void limparCampos() {
        edtSenhaAtual.setText("");
        edtNovaSenha.setText("");
        edtConfirmarSenha.setText("");
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}