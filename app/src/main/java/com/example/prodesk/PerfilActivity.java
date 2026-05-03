package com.example.prodesk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PerfilActivity extends AppCompatActivity {

    ImageView imgPerfil, btnLogout;
    Button btnSalvar;
    TextView btnTrocarFoto, txtEmail;
    EditText edtNome;

    LinearLayout itemCartoes, itemSeguranca;

    private static final int PICK_IMAGE = 1;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    Uri imagemSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        imgPerfil = findViewById(R.id.imgPerfil);
        btnTrocarFoto = findViewById(R.id.btnTrocarFoto);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnLogout = findViewById(R.id.btnLogout);
        edtNome = findViewById(R.id.edtNome);
        txtEmail = findViewById(R.id.txtEmail);

        itemCartoes = findViewById(R.id.itemCartoes);
        itemSeguranca = findViewById(R.id.itemSeguranca);

        itemCartoes.setOnClickListener(v ->
                startActivity(new Intent(this, CartaoActivity.class))
        );

        itemSeguranca.setOnClickListener(v ->
                startActivity(new Intent(this, SenhaActivity.class))
        );

        btnTrocarFoto.setOnClickListener(v -> abrirGaleria());

        btnSalvar.setOnClickListener(v -> salvarTudo());

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        carregarDadosUsuario();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_perfil);

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

            if (id == R.id.nav_CadEspacos) {
                startActivity(new Intent(this, CadastroEspacoActivity.class));
                return true;
            }

            return id == R.id.nav_perfil;
        });
    }

    // =========================
    // 🔥 SALVAR DADOS
    // =========================
    private void salvarTudo() {

        String nome = edtNome.getText().toString().trim();

        if (nome.isEmpty()) {
            edtNome.setError("Digite um nome");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .update("nome", nome);

        if (imagemSelecionada != null) {

            String base64 = converterImagemBase64(imagemSelecionada);

            db.collection("usuarios")
                    .document(userId)
                    .update("foto", base64);

            carregarImagemBase64(base64);

            imagemSelecionada = null;
        }

        Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
    }

    // =========================
    // 🔥 CONVERTER IMAGEM
    // =========================
    private String converterImagemBase64(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);

            byte[] bytes = baos.toByteArray();

            return Base64.encodeToString(bytes, Base64.NO_WRAP);

        } catch (Exception e) {
            return "";
        }
    }

    // =========================
    // 🔥 CARREGAR USUÁRIO
    // =========================
    private void carregarDadosUsuario() {

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String nome = doc.getString("nome");
                    String email = doc.getString("email");
                    String foto = doc.getString("foto");

                    if (nome != null) edtNome.setText(nome);
                    if (email != null) txtEmail.setText(email);

                    if (foto != null && !foto.isEmpty()) {
                        carregarImagemBase64(foto);
                    }
                });
    }

    // =========================
    // 🔥 CARREGAR FOTO BASE64
    // =========================
    private void carregarImagemBase64(String base64) {

        try {
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            if (bmp != null) {
                imgPerfil.setImageBitmap(bmp);
            }

        } catch (Exception e) {
            imgPerfil.setImageResource(android.R.color.darker_gray);
        }
    }

    // =========================
    // 📸 GALERIA
    // =========================
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {

            imagemSelecionada = data.getData();
            imgPerfil.setImageURI(imagemSelecionada);
        }
    }
}