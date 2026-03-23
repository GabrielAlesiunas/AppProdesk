package com.example.prodesk;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class VerificacaoActivity extends AppCompatActivity {

    EditText c1, c2, c3, c4;
    Button btnEmail, btnSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacao);

        c1 = findViewById(R.id.code1);
        c2 = findViewById(R.id.code2);
        c3 = findViewById(R.id.code3);
        c4 = findViewById(R.id.code4);

        btnEmail = findViewById(R.id.btnEmail);
        btnSms = findViewById(R.id.btnSms);

        moverAutomatico(c1, c2);
        moverAutomatico(c2, c3);
        moverAutomatico(c3, c4);

        btnEmail.setOnClickListener(v -> {
            // ação email
        });

        btnSms.setOnClickListener(v -> {
            // ação sms
        });
    }

    private void moverAutomatico(EditText atual, EditText proximo) {
        atual.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    proximo.requestFocus();
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private String getCodigo() {
        return c1.getText().toString() +
                c2.getText().toString() +
                c3.getText().toString() +
                c4.getText().toString();
    }
}