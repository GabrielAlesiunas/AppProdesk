package com.example.prodesk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CadastroEspacoActivity extends AppCompatActivity {

    EditText edtNome, edtDescricao, edtPreco, edtEndereco;
    Button btnSalvar, btnImagem;
    ImageView imgEspaco;

    CheckBox checkWifi, checkAr, checkEstacionamento, checkCafe;
    RadioButton radioSim;

    FirebaseFirestore db;

    // 🖼️ MULTI IMAGENS
    ArrayList<Uri> imagensSelecionadas = new ArrayList<>();

    private static final int PICK_IMAGES = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_espaco);

        db = FirebaseFirestore.getInstance();

        edtNome = findViewById(R.id.edtNomeEspaco);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtPreco = findViewById(R.id.edtPreco);
        edtEndereco = findViewById(R.id.edtEndereco);

        checkWifi = findViewById(R.id.checkWifi);
        checkAr = findViewById(R.id.checkAr);
        checkEstacionamento = findViewById(R.id.checkEstacionamento);
        checkCafe = findViewById(R.id.checkCafe);

        radioSim = findViewById(R.id.radioSim);

        imgEspaco = findViewById(R.id.imgEspaco);
        btnImagem = findViewById(R.id.btnSelecionarImagem);
        btnSalvar = findViewById(R.id.btnSalvarEspaco);

        // 📷 abrir galeria (MULTI SELEÇÃO)
        btnImagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Selecionar imagens"), PICK_IMAGES);
        });

        // 💾 salvar
        btnSalvar.setOnClickListener(v -> salvarEspaco());

        // MENU
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_CadEspacos);

        bottomNav.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            }

            if (id == R.id.nav_reservas) {
                startActivity(new Intent(this, HistoricoReservasActivity.class));
                return true;
            }

            if (id == R.id.nav_perfil) {
                return true;
            }

            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGES && resultCode == Activity.RESULT_OK && data != null) {

            imagensSelecionadas.clear();

            // múltiplas imagens
            if (data.getClipData() != null) {

                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    imagensSelecionadas.add(uri);
                }

            } else if (data.getData() != null) {
                imagensSelecionadas.add(data.getData());
            }

            // mostra primeira imagem só no preview
            if (!imagensSelecionadas.isEmpty()) {
                imgEspaco.setImageURI(imagensSelecionadas.get(0));
            }

            Toast.makeText(this,
                    imagensSelecionadas.size() + " imagem(ns) selecionada(s)",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void salvarEspaco() {

        String nome = edtNome.getText().toString();
        String descricao = edtDescricao.getText().toString();
        String preco = edtPreco.getText().toString();
        String endereco = edtEndereco.getText().toString();

        if (nome.isEmpty() || descricao.isEmpty() || preco.isEmpty() || endereco.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> dados = new HashMap<>();

        dados.put("nome", nome);
        dados.put("descricao", descricao);
        dados.put("preco", preco);
        dados.put("endereco", endereco);

        // 🖼️ SALVAR LISTA DE IMAGENS (SEM STORAGE)
        List<String> imagens = new ArrayList<>();
        for (Uri uri : imagensSelecionadas) {
            imagens.add(uri.toString());
        }
        dados.put("imagens", imagens);

        // comodidades
        dados.put("comodidades",
                (checkWifi.isChecked() ? "Wi-Fi " : "") +
                        (checkAr.isChecked() ? "Ar " : "") +
                        (checkEstacionamento.isChecked() ? "Estacionamento " : "") +
                        (checkCafe.isChecked() ? "Café " : "")
        );

        dados.put("tipo", radioSim.isChecked() ? "Compartilhado" : "Privado");

        db.collection("espacos")
                .add(dados)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Espaço salvo!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro ao salvar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}