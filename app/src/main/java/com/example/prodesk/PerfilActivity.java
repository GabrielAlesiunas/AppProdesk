package com.example.prodesk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilActivity extends AppCompatActivity {

    ImageView imgPerfil, btnLogout;
    Button btnTrocarFoto, btnSalvar;
    EditText edtNome;
    TextView txtEmail;

    LinearLayout itemCartoes, itemSeguranca;

    private static final int PICK_IMAGE = 1;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    FirebaseStorage storage;

    Uri imagemSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // 🔥 PROTEÇÃO
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

        btnSalvar.setOnClickListener(v -> {
            salvarNome();

            if (imagemSelecionada != null) {
                uploadImagem();
            } else {
                Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show();
            }
        });

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

    // 🔥 CARREGAR DADOS
    private void carregarDadosUsuario() {

        if (mAuth.getCurrentUser() == null) return;

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc != null && doc.exists()) {

                        String nome = doc.getString("nome");
                        String email = doc.getString("email");
                        String fotoUrl = doc.getString("foto");

                        if (nome != null)
                            edtNome.setText(nome);

                        if (email != null)
                            txtEmail.setText("Email: " + email);

                        if (fotoUrl != null && !fotoUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(fotoUrl)
                                    .into(imgPerfil);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao carregar dados", Toast.LENGTH_LONG).show()
                );
    }

    // 🔥 SALVAR NOME
    private void salvarNome() {

        String nome = edtNome.getText().toString().trim();

        if (nome.isEmpty()) {
            edtNome.setError("Digite um nome");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .update("nome", nome)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Nome atualizado!", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao salvar nome", Toast.LENGTH_LONG).show()
                );
    }

    // 📸 GALERIA
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    // 📸 RESULTADO
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imagemSelecionada = data.getData();
            imgPerfil.setImageURI(imagemSelecionada);
        }
    }

    // 🔥 UPLOAD IMAGEM
    private void uploadImagem() {

        String userId = mAuth.getCurrentUser().getUid();

        StorageReference ref = storage.getReference()
                .child("fotos")
                .child(userId + ".jpg");

        ref.putFile(imagemSelecionada)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {

                            String url = uri.toString();

                            db.collection("usuarios")
                                    .document(userId)
                                    .update("foto", url);

                            Toast.makeText(this, "Foto atualizada!", Toast.LENGTH_SHORT).show();
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao enviar imagem", Toast.LENGTH_LONG).show()
                );
    }
}