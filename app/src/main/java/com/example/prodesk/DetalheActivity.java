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

        if (doc == null || !doc.exists()) return;

        String nomeStr = doc.getString("nome");
        String descricaoStr = doc.getString("descricao");
        String precoStr = doc.getString("preco");
        String imagemBase64 = doc.getString("imagem");
        String donoId = doc.getString("donoId");

        nome.setText(nomeStr != null ? nomeStr : "");
        descricao.setText(descricaoStr != null ? descricaoStr : "");
        preco.setText("R$ " + (precoStr != null ? precoStr : "0") + " /H");

        avaliacao.setText("⭐ 0.0 / 5");
        opinioes.setText("Carregando avaliações...");
        dono.setText("Carregando dono...");

        // =========================
        // 🔥 DONO DO ESPAÇO
        // =========================
        if (donoId != null && !donoId.isEmpty()) {

            db.collection("usuarios")
                    .document(donoId)
                    .get()
                    .addOnSuccessListener(userDoc -> {

                        if (userDoc != null && userDoc.exists()) {

                            String nomeDono = userDoc.getString("nome");

                            if (nomeDono != null && !nomeDono.trim().isEmpty()) {
                                dono.setText(nomeDono);
                            } else {
                                dono.setText("Usuário sem nome");
                            }

                        } else {
                            dono.setText("Usuário não encontrado");
                        }
                    })
                    .addOnFailureListener(e ->
                            dono.setText("Erro ao carregar dono")
                    );

        } else {
            dono.setText("Sem dono");
        }

        // =========================
        // 🔥 IMAGEM BASE64
        // =========================
        if (imagemBase64 != null && !imagemBase64.isEmpty()) {
            try {

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

        // =========================
        // 🔥 BOTÃO RESERVAR
        // =========================
        reservar.setOnClickListener(v -> {

            Intent intent = new Intent(DetalheActivity.this, ReservaActivity.class);

            intent.putExtra("id", doc.getId());
            intent.putExtra("nome", nomeStr);
            intent.putExtra("preco", precoStr);
            intent.putExtra("descricao", descricaoStr);

            startActivity(intent);
        });

        // =========================
        // 🔥 AVALIAÇÕES
        // =========================
        carregarAvaliacoes(doc.getId());
    }

    private void carregarAvaliacoes(String espacoId) {

        db.collection("avaliacoes")
                .whereEqualTo("espacoId", espacoId)
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        avaliacao.setText("⭐ 0.0 / 5");
                        opinioes.setText("Nenhuma avaliação ainda");
                        return;
                    }

                    double soma = 0;
                    int total = query.size();

                    StringBuilder comentarios = new StringBuilder();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        Double nota = doc.getDouble("nota");
                        String comentario = doc.getString("comentario");

                        if (nota != null) soma += nota;

                        if (comentario != null && !comentario.isEmpty()) {
                            comentarios.append("⭐ ")
                                    .append(nota != null ? nota : 0)
                                    .append(" - ")
                                    .append(comentario)
                                    .append("\n\n");
                        }
                    }

                    double media = soma / total;

                    avaliacao.setText(
                            gerarEstrelas(media) +
                                    " " + String.format("%.1f", media) + " / 5"
                    );

                    if (comentarios.length() > 0) {
                        opinioes.setText(comentarios.toString());
                    } else {
                        opinioes.setText("Sem comentários");
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar avaliações", Toast.LENGTH_SHORT).show()
                );
    }

    private String gerarEstrelas(double media) {

        int estrelasCheias = (int) media;
        boolean meia = (media - estrelasCheias) >= 0.5;

        StringBuilder estrelas = new StringBuilder();

        for (int i = 0; i < estrelasCheias; i++) {
            estrelas.append("⭐");
        }

        if (meia) {
            estrelas.append("✩");
        }

        while (estrelas.length() < 5) {
            estrelas.append("☆");
        }

        return estrelas.toString();
    }
}