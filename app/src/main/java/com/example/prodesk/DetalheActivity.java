package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalheActivity extends AppCompatActivity {

    TextView nome, descricao, preco, avaliacao, dono, opinioes;
    Button reservar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        // REFERÊNCIAS
        nome = findViewById(R.id.nomeEspaco);
        descricao = findViewById(R.id.descricao);
        preco = findViewById(R.id.preco);
        avaliacao = findViewById(R.id.avaliacao);
        dono = findViewById(R.id.dono);
        opinioes = findViewById(R.id.opinioes);
        reservar = findViewById(R.id.btnReservar);

        // DADO RECEBIDO DA TELA ANTERIOR
        String recebido = getIntent().getStringExtra("nome");

        String nomeEspaco = (recebido == null || recebido.isEmpty())
                ? "Espaço não informado"
                : recebido;

        // DADOS MOCKADOS (depois você substitui por banco/API)
        nome.setText(nomeEspaco);
        descricao.setText("Espaço moderno com ótima estrutura e ambiente confortável.");
        preco.setText("R$ 50,00 / hora");
        avaliacao.setText("⭐ 4.5 / 5");
        dono.setText("admin");
        opinioes.setText("Muito bom espaço! Recomendo.");

        // BOTÃO RESERVAR
        reservar.setOnClickListener(v -> {
            Intent intent = new Intent(DetalheActivity.this, ReservaActivity.class);

            intent.putExtra("nome", nomeEspaco);
            intent.putExtra("preco", preco.getText().toString());
            intent.putExtra("descricao", descricao.getText().toString());

            startActivity(intent);
        });
    }
}