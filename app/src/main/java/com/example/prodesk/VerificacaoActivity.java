package com.example.prodesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.*;

import java.util.concurrent.TimeUnit;

public class VerificacaoActivity extends AppCompatActivity {

    EditText c1, c2, c3, c4, c5, c6;
    Button btnSms, btnConfirmar;

    FirebaseAuth mAuth;

    String verificationId;
    String telefone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacao);

        c1 = findViewById(R.id.code1);
        c2 = findViewById(R.id.code2);
        c3 = findViewById(R.id.code3);
        c4 = findViewById(R.id.code4);
        c5 = findViewById(R.id.code5);
        c6 = findViewById(R.id.code6);

        btnSms = findViewById(R.id.btnSms);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        mAuth = FirebaseAuth.getInstance();
        telefone = getIntent().getStringExtra("telefone");

        mover(c1, c2);
        mover(c2, c3);
        mover(c3, c4);
        mover(c4, c5);
        mover(c5, c6);

        btnSms.setOnClickListener(v -> enviarSms());

        btnConfirmar.setOnClickListener(v -> {

            String codigo = getCodigo();

            if (codigo.length() != 6) {
                Toast.makeText(this, "Digite o código completo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (verificationId == null) {
                Toast.makeText(this, "Clique em enviar código primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            verificarCodigo(codigo);
        });
    }

    private void enviarSms() {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+5515997671792")
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(callbacks)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    signIn(credential);
                }

                public void onVerificationFailed(FirebaseException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                public void onCodeSent(String id, PhoneAuthProvider.ForceResendingToken token) {
                    verificationId = id;
                    Toast.makeText(getApplicationContext(), "Código enviado", Toast.LENGTH_SHORT).show();
                }
            };

    private void verificarCodigo(String codigo) {

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(verificationId, codigo);

        signIn(credential);
    }

    private void signIn(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
                        prefs.edit().putBoolean("2fa_ok", true).apply();

                        Toast.makeText(this, "Verificado!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, "Erro: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void mover(EditText atual, EditText prox) {
        atual.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) prox.requestFocus();
            }
            public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            public void onTextChanged(CharSequence s,int a,int b,int c){}
        });
    }

    private String getCodigo() {
        return c1.getText().toString() +
                c2.getText().toString() +
                c3.getText().toString() +
                c4.getText().toString() +
                c5.getText().toString() +
                c6.getText().toString();
    }
}