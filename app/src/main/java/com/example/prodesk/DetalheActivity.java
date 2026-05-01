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

import com.google.firebase.firestore.DocumentSnapshot;
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
                .addOnSuccessListener(this::processarDocumento)
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void processarDocumento(DocumentSnapshot doc) {

        if (!doc.exists()) return;

        String nomeStr = doc.getString("nome");
        String descricaoStr = doc.getString("descricao");
        String precoStr = doc.getString("preco");
        String imagemBase64 = doc.getString("imagem");

        nome.setText(nomeStr);
        descricao.setText(descricaoStr);
        preco.setText("R$ " + precoStr + " /H");
        avaliacao.setText("⭐ 4.8 / 5");
        dono.setText("admin");
        opinioes.setText("Sem avaliações ainda");

        // 🔥 IMAGEM BASE64 CORRIGIDA
        if (imagemBase64 != null && !imagemBase64.isEmpty()) {
            try {

                // 🔥 remove quebras de linha e espaços (MUITO IMPORTANTE)
                String cleanBase64 = imagemBase64
                        .replace("\n", "")
                        .replace("\r", "")
                        .replace(" ", "");

                byte[] bytes = Base64.decode(cleanBase64, Base64.DEFAULT);

                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if (bmp != null) {
                    img.setImageBitmap(bmp);
                } else {
                    img.setImageResource(android.R.color.darker_gray);
                }

            } catch (Exception e) {
                img.setImageResource(android.R.color.darker_gray);
            }
        } else {
            img.setImageResource(android.R.color.darker_gray);
        }

        reservar.setOnClickListener(v -> {

            Intent intent = new Intent(DetalheActivity.this, ReservaActivity.class);

            intent.putExtra("id", doc.getId()); // 🔥 IMPORTANTE
            intent.putExtra("nome", nomeStr);
            intent.putExtra("preco", precoStr);
            intent.putExtra("descricao", descricaoStr);

            startActivity(intent);
        });
    }
}