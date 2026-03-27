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

        // REFERÊNCIAS DOS COMPONENTES
        nome = findViewById(R.id.nomeEspaco);
        descricao = findViewById(R.id.descricao);
        preco = findViewById(R.id.preco);
        avaliacao = findViewById(R.id.avaliacao);
        dono = findViewById(R.id.dono);
        opinioes = findViewById(R.id.opinioes);
        reservar = findViewById(R.id.btnReservar);

        // RECEBE DADO DA TELA ANTERIOR
        String recebido = getIntent().getStringExtra("nome");

        final String nomeEspaco;

        if (recebido == null) {
            nomeEspaco = "Espaço não informado";
        } else {
            nomeEspaco = recebido;
        }

        // DADOS MOCKADOS (por enquanto)
        nome.setText(nomeEspaco);
        descricao.setText("Espaço moderno com ótima estrutura e ambiente confortável.");
        preco.setText("R$ 50,00 / hora");
        avaliacao.setText("⭐ 4.5 / 5");
        dono.setText("admin");
        opinioes.setText("Muito bom espaço! Recomendo.");

        // BOTÃO RESERVAR
        reservar.setOnClickListener(v -> {
            Intent intent = new Intent(DetalheActivity.this, ReservasActivity.class);
            intent.putExtra("reserva", nomeEspaco);
            startActivity(intent);
        });
    }
}