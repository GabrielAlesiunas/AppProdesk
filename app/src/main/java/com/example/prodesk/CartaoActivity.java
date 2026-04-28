package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class CartaoActivity extends AppCompatActivity {

    EditText edtNumeroCartao, edtNomeCartao, edtValidade, edtCVV;
    Button btnSalvarCartao;

    RecyclerView recyclerCartoes;
    CartaoAdapter adapter;
    List<Cartao> listaCartoes;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartao);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtNumeroCartao = findViewById(R.id.edtNumeroCartao);
        edtNomeCartao = findViewById(R.id.edtNomeCartao);
        edtValidade = findViewById(R.id.edtValidade);
        edtCVV = findViewById(R.id.edtCVV);
        btnSalvarCartao = findViewById(R.id.btnSalvarCartao);

        recyclerCartoes = findViewById(R.id.recyclerCartoes);
        listaCartoes = new ArrayList<>();

        adapter = new CartaoAdapter(listaCartoes);
        recyclerCartoes.setLayoutManager(new LinearLayoutManager(this));
        recyclerCartoes.setAdapter(adapter);

        btnSalvarCartao.setOnClickListener(v -> salvarCartao());

        carregarCartoes();
        ativarSwipe();

        // MENU
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
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }

    // 🔥 SALVAR
    private void salvarCartao() {

        String numero = edtNumeroCartao.getText().toString().trim();
        String nome = edtNomeCartao.getText().toString().trim();
        String validade = edtValidade.getText().toString().trim();

        if (numero.length() < 8 || nome.isEmpty()) {
            toast("Dados inválidos");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        String numeroMascarado = "**** **** **** " + numero.substring(numero.length() - 4);

        Cartao cartao = new Cartao(null, numeroMascarado, nome, validade);

        db.collection("usuarios")
                .document(userId)
                .collection("cartoes")
                .add(cartao)
                .addOnSuccessListener(doc -> {

                    cartao.id = doc.getId();
                    listaCartoes.add(cartao);
                    adapter.notifyItemInserted(listaCartoes.size() - 1);

                    toast("Cartão salvo!");

                    edtNumeroCartao.setText("");
                    edtNomeCartao.setText("");
                    edtValidade.setText("");
                    edtCVV.setText("");
                });
    }

    // 🔥 CARREGAR
    private void carregarCartoes() {

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("usuarios")
                .document(userId)
                .collection("cartoes")
                .get()
                .addOnSuccessListener(query -> {

                    listaCartoes.clear();

                    for (var doc : query.getDocuments()) {

                        Cartao c = doc.toObject(Cartao.class);
                        c.id = doc.getId();

                        listaCartoes.add(c);
                    }

                    adapter.notifyDataSetChanged();
                });
    }

    // 🔥 SWIPE
    private void ativarSwipe() {

        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(RecyclerView rv, RecyclerView.ViewHolder vh, RecyclerView.ViewHolder t) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder vh, int direction) {

                        int pos = vh.getAdapterPosition();
                        Cartao cartao = listaCartoes.get(pos);

                        String userId = mAuth.getCurrentUser().getUid();

                        db.collection("usuarios")
                                .document(userId)
                                .collection("cartoes")
                                .document(cartao.id)
                                .delete();

                        listaCartoes.remove(pos);
                        adapter.notifyItemRemoved(pos);

                        toast("Cartão removido");
                    }
                });

        helper.attachToRecyclerView(recyclerCartoes);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}