package com.example.prodesk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class DetalheActivity extends AppCompatActivity {

    TextView nome, descricao, preco, avaliacao, dono, opinioes;
    ImageView img;
    Button reservar;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe);

        db = FirebaseFirestore.getInstance();

        nome = findViewById(R.id.nomeEspaco);
        descricao = findViewById(R.id.descricao);
        preco = findViewById(R.id.preco);
        avaliacao = findViewById(R.id.avaliacao);
        dono = findViewById(R.id.dono);
        opinioes = findViewById(R.id.opinioes);
        img = findViewById(R.id.imgPrincipal);
        reservar = findViewById(R.id.btnReservar);

        String id = getIntent().getStringExtra("id");

        if (id == null || id.trim().isEmpty()) {
            Toast.makeText(this, "ID inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarEspaco(id);
    }

    private void carregarEspaco(String id) {

        db.collection("espacos").document(id)
                .get()
                .addOnSuccessListener(doc -> {

                    Espaco e = doc.toObject(Espaco.class);
                    if (e == null) return;

                    nome.setText(e.getNome());
                    descricao.setText(e.getDescricao());

                    // 💰 formato correto
                    preco.setText("R$ " + e.getPreco() + " /H");

                    avaliacao.setText("⭐ 4.8 / 5");

                    dono.setText(e.getDonoNome() != null ? e.getDonoNome() : "admin");

                    opinioes.setText("Sem avaliações ainda");

                    if (e.getImagem() != null && !e.getImagem().isEmpty()) {
                        byte[] bytes = Base64.decode(e.getImagem(), Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        img.setImageBitmap(bmp);
                    }

                    // 🔥 BOTÃO RESERVAR
                    reservar.setOnClickListener(v -> {

                        Intent intent = new Intent(DetalheActivity.this, ReservaActivity.class);

                        intent.putExtra("nome", e.getNome());
                        intent.putExtra("preco", e.getPreco());
                        intent.putExtra("descricao", e.getDescricao());

                        startActivity(intent);
                    });

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}