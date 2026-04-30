package com.example.prodesk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CadastroEspacoActivity extends AppCompatActivity {

    EditText edtNome, edtDescricao, edtPreco, edtEndereco;
    Button btnSalvar, btnImagem;
    ImageView imgEspaco;

    CheckBox checkWifi, checkAr, checkEstacionamento, checkCafe;
    RadioButton radioSim;

    Uri imagemSelecionada;
    FirebaseFirestore db;

    private static final int PICK_IMAGE = 100;

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

        btnImagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        btnSalvar.setOnClickListener(v -> salvarEspaco());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_CadEspacos);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imagemSelecionada = data.getData();
            imgEspaco.setImageURI(imagemSelecionada);
        }
    }

    private String converterImagemBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

            byte[] bytes = baos.toByteArray();
            return Base64.encodeToString(bytes, Base64.DEFAULT);

        } catch (Exception e) {
            return null;
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

        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> lista = geocoder.getFromLocationName(endereco, 1);

            if (lista == null || lista.isEmpty()) {
                Toast.makeText(this, "Endereço não encontrado", Toast.LENGTH_SHORT).show();
                return;
            }

            Address a = lista.get(0);

            double lat = a.getLatitude();
            double lng = a.getLongitude();

            String imagemBase64 = imagemSelecionada != null
                    ? converterImagemBase64(imagemSelecionada)
                    : "";

            Map<String, Object> dados = new HashMap<>();
            dados.put("nome", nome);
            dados.put("descricao", descricao);
            dados.put("preco", preco);
            dados.put("endereco", endereco);
            dados.put("latitude", lat);
            dados.put("longitude", lng);
            dados.put("imagem", imagemBase64);

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
                        Toast.makeText(this, "Salvo com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                    );

        } catch (Exception e) {
            Toast.makeText(this, "Erro ao processar endereço", Toast.LENGTH_SHORT).show();
        }
    }
}