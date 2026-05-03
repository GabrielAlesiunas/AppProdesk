package com.example.prodesk;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class AvaliacaoActivity extends AppCompatActivity {

    TextView txtNome;
    RatingBar ratingBar;
    EditText edtComentario;
    MaterialButton btnEnviar;

    FirebaseFirestore db;

    String nomeEspaco;
    String espacoId; // 🔥 NOVO

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacao);

        db = FirebaseFirestore.getInstance();

        txtNome = findViewById(R.id.txtNomeEspaco);
        ratingBar = findViewById(R.id.ratingBar);
        edtComentario = findViewById(R.id.edtComentario);
        btnEnviar = findViewById(R.id.btnEnviar);

        // 🔥 RECEBE OS DADOS
        nomeEspaco = getIntent().getStringExtra("nome");
        espacoId = getIntent().getStringExtra("id");

        txtNome.setText(nomeEspaco);

        btnEnviar.setOnClickListener(v -> salvar());
    }

    private void salvar() {

        float nota = ratingBar.getRating();
        String comentario = edtComentario.getText().toString();

        if (nota == 0) {
            Toast.makeText(this, "Dê uma nota", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> avaliacao = new HashMap<>();

        avaliacao.put("espacoId", espacoId); // 🔥 AGORA FUNCIONA
        avaliacao.put("nomeEspaco", nomeEspaco);
        avaliacao.put("nota", nota);
        avaliacao.put("comentario", comentario);
        avaliacao.put("data", System.currentTimeMillis());

        db.collection("avaliacoes")
                .add(avaliacao) // 🔥 CORRETO
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Avaliação enviada!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}