package com.example.prodesk;

import android.content.Intent;
import android.os.Bundle;
import android.text.*;
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

        // 🔥 VIEWS
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

        // 🔥 MÁSCARAS
        aplicarMascaraCartao();
        aplicarMascaraValidade();

        // 🔥 BOTÃO
        btnSalvarCartao.setOnClickListener(v -> {
            animarBotao();
            salvarCartao();
        });

        carregarCartoes();
        ativarSwipe();

        configurarMenu();
    }

    // =============================
    // 🔥 SALVAR CARTÃO
    // =============================
    private void salvarCartao() {

        String numero = edtNumeroCartao.getText().toString().replaceAll(" ", "");
        String nome = edtNomeCartao.getText().toString().trim();
        String validade = edtValidade.getText().toString().trim();
        String cvv = edtCVV.getText().toString().trim();

        // 🔥 VALIDAÇÕES
        if (numero.isEmpty()) {
            edtNumeroCartao.setError("Digite o número");
            return;
        }

        if (numero.length() < 13 || numero.length() > 19) {
            edtNumeroCartao.setError("Número inválido");
            return;
        }

        if (nome.isEmpty()) {
            edtNomeCartao.setError("Digite o nome");
            return;
        }

        if (!validade.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            edtValidade.setError("Ex: 05/28");
            return;
        }

        if (cvv.length() < 3 || cvv.length() > 4) {
            edtCVV.setError("CVV inválido");
            return;
        }

        if (mAuth.getCurrentUser() == null) {
            toast("Usuário não logado");
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

                    recyclerCartoes.smoothScrollToPosition(listaCartoes.size() - 1);

                    toast("Cartão salvo!");

                    limparCampos();
                })
                .addOnFailureListener(e ->
                        toast("Erro: " + e.getMessage())
                );
    }

    // =============================
    // 🔥 LIMPAR CAMPOS
    // =============================
    private void limparCampos() {
        edtNumeroCartao.setText("");
        edtNomeCartao.setText("");
        edtValidade.setText("");
        edtCVV.setText("");
    }

    // =============================
    // 🔥 CARREGAR CARTÕES
    // =============================
    private void carregarCartoes() {

        if (mAuth.getCurrentUser() == null) return;

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
                })
                .addOnFailureListener(e ->
                        toast("Erro ao carregar cartões")
                );
    }

    // =============================
    // 🔥 SWIPE PARA DELETAR
    // =============================
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

    // =============================
    // 🔥 MÁSCARA CARTÃO
    // =============================
    private void aplicarMascaraCartao() {

        edtNumeroCartao.addTextChangedListener(new TextWatcher() {

            private boolean isUpdating;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (isUpdating) return;

                isUpdating = true;

                String str = s.toString().replaceAll(" ", "");

                StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < str.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ");
                    formatted.append(str.charAt(i));
                }

                edtNumeroCartao.setText(formatted.toString());
                edtNumeroCartao.setSelection(formatted.length());

                isUpdating = false;
            }
        });
    }

    // =============================
    // 🔥 MÁSCARA VALIDADE
    // =============================
    private void aplicarMascaraValidade() {

        edtValidade.addTextChangedListener(new TextWatcher() {

            private boolean isUpdating;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                if (isUpdating) return;

                isUpdating = true;

                String str = s.toString().replaceAll("/", "");

                if (str.length() >= 2) {
                    str = str.substring(0, 2) + "/" + str.substring(2);
                }

                edtValidade.setText(str);
                edtValidade.setSelection(str.length());

                isUpdating = false;
            }
        });
    }

    // =============================
    // 🔥 ANIMAÇÃO BOTÃO
    // =============================
    private void animarBotao() {

        btnSalvarCartao.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() ->
                        btnSalvarCartao.animate().scaleX(1).scaleY(1).setDuration(100)
                );
    }

    // =============================
    // 🔥 MENU
    // =============================
    private void configurarMenu() {

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

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}