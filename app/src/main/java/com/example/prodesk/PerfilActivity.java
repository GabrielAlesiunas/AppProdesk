package com.example.prodesk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.*;

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

        // 🔒 PROTEÇÃO
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 🔥 VIEWS
        imgPerfil = findViewById(R.id.imgPerfil);
        btnTrocarFoto = findViewById(R.id.btnTrocarFoto);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnLogout = findViewById(R.id.btnLogout);
        edtNome = findViewById(R.id.edtNome);
        txtEmail = findViewById(R.id.txtEmail);

        itemCartoes = findViewById(R.id.itemCartoes);
        itemSeguranca = findViewById(R.id.itemSeguranca);

        // 🔥 CLIQUES
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
            Toast.makeText(this, "Logout realizado", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        carregarDadosUsuario();

        // 🔥 MENU INFERIOR
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

            if (id == R.id.nav_perfil) {
                return true;
            }

            return false;
        });
    }

    // 🔥 SALVAR (Firebase + Local)
    private void salvarTudo() {

        String nome = edtNome.getText().toString().trim();

        if (nome.isEmpty()) {
            edtNome.setError("Digite um nome");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        // 🔹 Salva nome no Firebase
        db.collection("usuarios")
                .document(userId)
                .update("nome", nome)
                .addOnSuccessListener(aVoid -> {

                    // 🔹 Se tiver imagem → salva local
                    if (imagemSelecionada != null) {

                        String caminho = salvarImagemLocal(imagemSelecionada);

                        if (caminho != null) {
                            salvarImagemLocalPrefs(caminho);

                            Glide.with(this)
                                    .load(caminho)
                                    .into(imgPerfil);
                        }

                        imagemSelecionada = null;
                    }

                    Toast.makeText(this, "Perfil atualizado!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar nome", Toast.LENGTH_LONG).show()
                );
    }

    // 🔥 SALVAR IMAGEM LOCAL
    private String salvarImagemLocal(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            String nomeArquivo = "perfil_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), nomeArquivo);

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao salvar imagem", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // 🔥 SALVAR CAMINHO DA IMAGEM
    private void salvarImagemLocalPrefs(String caminho) {

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        prefs.edit().putString("foto", caminho).apply();
    }

    // 🔥 CARREGAR DADOS DO FIREBASE
    private void carregarDadosUsuario() {

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc != null && doc.exists()) {

                        String nome = doc.getString("nome");
                        String email = doc.getString("email");

                        if (nome != null)
                            edtNome.setText(nome);

                        if (email != null)
                            txtEmail.setText(email);

                        // 🔥 CARREGA IMAGEM LOCAL
                        carregarImagemLocal();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_LONG).show()
                );
    }

    // 🔥 CARREGAR IMAGEM LOCAL
    private void carregarImagemLocal() {

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String foto = prefs.getString("foto", null);

        if (foto != null) {
            Glide.with(this)
                    .load(foto)
                    .into(imgPerfil);
        }
    }

    // 📸 ABRIR GALERIA
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    // 📸 RESULTADO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {

            imagemSelecionada = data.getData();

            // preview imediato
            imgPerfil.setImageURI(imagemSelecionada);
        }
    }
}