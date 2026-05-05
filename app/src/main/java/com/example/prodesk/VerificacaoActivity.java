package com.example.prodesk;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class VerificacaoActivity extends AppCompatActivity {

    EditText c1, c2, c3, c4, c5, c6;
    Button btnConfirmar;

    // 🔐 Código fixo para teste
    private static final String CODIGO_FIXO = "123456";

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

        btnConfirmar = findViewById(R.id.btnConfirmar);

        // mover foco automático
        mover(c1, c2);
        mover(c2, c3);
        mover(c3, c4);
        mover(c4, c5);
        mover(c5, c6);

        btnConfirmar.setOnClickListener(v -> {

            String codigo = getCodigo();

            if (codigo.length() != 6) {
                Toast.makeText(this, "Digite o código completo", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 valida código fixo
            if (codigo.equals(CODIGO_FIXO)) {

                SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
                prefs.edit()
                        .putBoolean("2fa_ok", true)
                        .apply();

                Toast.makeText(this, "Verificado!", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, MainActivity.class));
                finish();

            } else {
                Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show();
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