package com.example.prodesk;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    Button btnTrocarFoto, btnSenha, btnCartao, btnSalvar, btnSair;
    Switch switch2FA;
    EditText edtEndereco;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        btnTrocarFoto = findViewById(R.id.btnTrocarFoto);
        btnSenha = findViewById(R.id.btnSenha);
        btnCartao = findViewById(R.id.btnCartao);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnSair = findViewById(R.id.btnSair);
        switch2FA = findViewById(R.id.switch2FA);
        edtEndereco = findViewById(R.id.edtEndereco);

        btnTrocarFoto.setOnClickListener(v ->
                Toast.makeText(this, "Selecionar foto", Toast.LENGTH_SHORT).show());

        btnSenha.setOnClickListener(v ->
                Toast.makeText(this, "Alterar senha", Toast.LENGTH_SHORT).show());

        btnCartao.setOnClickListener(v ->
                Toast.makeText(this, "Adicionar cartão", Toast.LENGTH_SHORT).show());

        btnSalvar.setOnClickListener(v ->
                Toast.makeText(this, "Dados salvos!", Toast.LENGTH_SHORT).show());

        btnSair.setOnClickListener(v ->
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show());

        switch2FA.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(this, "2FA ativado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "2FA desativado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}