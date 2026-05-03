package com.example.prodesk;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ReservaActivity extends AppCompatActivity {

    TextView txtNome, txtPreco, txtDescricao;
    TextView txtDataInicio, txtDataFim, txtHoraInicio, txtHoraFim, txtResumo, txtCartaoSelecionado;
    ImageView imgEspaco;
    MaterialButton btnConfirmar;

    Calendar dataInicioCal;
    Calendar dataFimCal;

    double precoHora = 50.0;

    FirebaseFirestore db;
    String espacoId;

    String imagemBase64 = "";

    RadioGroup radioPagamento;
    String formaPagamento = "";
    String cartaoSelecionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        db = FirebaseFirestore.getInstance();

        dataInicioCal = Calendar.getInstance();
        dataFimCal = Calendar.getInstance();

        txtNome = findViewById(R.id.txtNome);
        txtPreco = findViewById(R.id.txtPreco);
        txtDescricao = findViewById(R.id.txtDescricao);

        txtDataInicio = findViewById(R.id.txtDataInicio);
        txtDataFim = findViewById(R.id.txtDataFim);
        txtHoraInicio = findViewById(R.id.txtHoraInicio);
        txtHoraFim = findViewById(R.id.txtHoraFim);
        txtResumo = findViewById(R.id.txtResumo);

        imgEspaco = findViewById(R.id.imgPrincipal);
        radioPagamento = findViewById(R.id.radioPagamento);
        txtCartaoSelecionado = findViewById(R.id.txtCartaoSelecionado);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        espacoId = getIntent().getStringExtra("id");

        if (espacoId == null) {
            Toast.makeText(this, "Erro ao carregar espaço", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        carregarEspaco(espacoId);

        txtDataInicio.setOnClickListener(v -> selecionarData(true));
        txtDataFim.setOnClickListener(v -> selecionarData(false));

        txtHoraInicio.setOnClickListener(v -> selecionarHora(true));
        txtHoraFim.setOnClickListener(v -> selecionarHora(false));

        // 🔥 ESCUTA PAGAMENTO
        radioPagamento.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.radioPix) {
                formaPagamento = "PIX";
                mostrarQRPix();
            }

            if (checkedId == R.id.radioCartao) {
                formaPagamento = "Cartão";
                mostrarCartoes();
            }
        });

        btnConfirmar.setOnClickListener(v -> confirmarReserva());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

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

    private void carregarEspaco(String id) {

        db.collection("espacos").document(id)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    String nome = doc.getString("nome");
                    String descricao = doc.getString("descricao");
                    String preco = doc.getString("preco");
                    String imagem = doc.getString("imagem");

                    txtNome.setText(nome);
                    txtDescricao.setText(descricao);
                    txtPreco.setText("R$ " + preco + " /h");

                    imagemBase64 = imagem;

                    try {
                        String clean = preco.replaceAll("[^0-9]", "");
                        if (!clean.isEmpty()) {
                            precoHora = Double.parseDouble(clean);
                        }
                    } catch (Exception ignored) {}

                    if (imagem != null && !imagem.isEmpty()) {
                        try {
                            byte[] bytes = Base64.decode(imagem, Base64.DEFAULT);
                            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            imgEspaco.setImageBitmap(bmp);
                        } catch (Exception e) {
                            imgEspaco.setImageResource(android.R.color.darker_gray);
                        }
                    }
                });
    }

    private void selecionarData(boolean inicio) {
        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (view, y, m, d) -> {

            if (inicio) {
                dataInicioCal.set(y, m, d, 0, 0);
                txtDataInicio.setText(d + "/" + (m + 1) + "/" + y);
            } else {
                dataFimCal.set(y, m, d, 0, 0);
                txtDataFim.setText(d + "/" + (m + 1) + "/" + y);
            }

            calcular();

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void selecionarHora(boolean inicio) {

        Calendar c = Calendar.getInstance();

        new TimePickerDialog(this, (view, h, m) -> {

            if (inicio) {
                dataInicioCal.set(Calendar.HOUR_OF_DAY, h);
                dataInicioCal.set(Calendar.MINUTE, m);
                txtHoraInicio.setText(String.format("%02d:%02d", h, m));
            } else {
                dataFimCal.set(Calendar.HOUR_OF_DAY, h);
                dataFimCal.set(Calendar.MINUTE, m);
                txtHoraFim.setText(String.format("%02d:%02d", h, m));
            }

            calcular();

        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private double calcularHoras() {
        long inicio = dataInicioCal.getTimeInMillis();
        long fim = dataFimCal.getTimeInMillis();

        if (fim <= inicio) return 0;

        double horas = (fim - inicio) / 3600000.0;

        // 🔥 arredonda para 2 casas
        return Math.round(horas * 100.0) / 100.0;
    }

    private void calcular() {

        double horas = calcularHoras();

        if (horas <= 0) {
            txtResumo.setText("Datas inválidas");
            return;
        }

        double total = horas * precoHora;

        txtResumo.setText(
                "Horas: " + String.format("%.2f", horas) +
                        "\nTotal: R$ " + String.format("%.2f", total)
        );
    }

    // 🔥 LISTAR CARTÕES
    private void mostrarCartoes() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Faça login primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("usuarios")
                .document(userId)
                .collection("cartoes")
                .get()
                .addOnSuccessListener(query -> {

                    if (query.isEmpty()) {
                        Toast.makeText(this, "Nenhum cartão cadastrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<String> lista = new ArrayList<>();

                    for (DocumentSnapshot doc : query.getDocuments()) {

                        String nome = doc.getString("nome");
                        String numero = doc.getString("numero");

                        if (nome == null) nome = "Sem nome";
                        if (numero == null) numero = "Sem número";

                        lista.add(nome + " - " + numero);
                    }

                    String[] itens = lista.toArray(new String[0]);

                    new AlertDialog.Builder(this)
                            .setTitle("Selecione o cartão")
                            .setItems(itens, (dialog, which) -> {

                                cartaoSelecionado = itens[which];

                                // 🔥 MOSTRAR NA TELA
                                txtCartaoSelecionado.setText("Cartão: " + cartaoSelecionado);

                                Toast.makeText(this,
                                        "Selecionado: " + cartaoSelecionado,
                                        Toast.LENGTH_SHORT).show();
                            })
                            .show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // 🔥 QR PIX
    private void mostrarQRPix() {

        ImageView qr = new ImageView(this);
        qr.setImageResource(R.drawable.qrcode);

        new AlertDialog.Builder(this)
                .setTitle("Pague com PIX")
                .setMessage("Escaneie o QR Code abaixo")
                .setView(qr)
                .setPositiveButton("Já paguei", null)
                .show();
    }

    private void confirmarReserva() {

        long inicio = dataInicioCal.getTimeInMillis();
        long fim = dataFimCal.getTimeInMillis();

        if (fim <= inicio) {
            Toast.makeText(this, "Datas inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        if (formaPagamento.isEmpty()) {
            Toast.makeText(this, "Selecione forma de pagamento", Toast.LENGTH_SHORT).show();
            return;
        }

        if (formaPagamento.equals("Cartão") && cartaoSelecionado.isEmpty()) {
            Toast.makeText(this, "Selecione um cartão", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> reserva = new HashMap<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 🔥 NOVO

        reserva.put("userId", userId);
        reserva.put("espacoId", espacoId);
        reserva.put("nomeEspaco", txtNome.getText().toString());
        reserva.put("dataInicio", inicio);
        reserva.put("dataFim", fim);
        reserva.put("valorTotal", calcularHoras() * precoHora);
        reserva.put("imagem", imagemBase64);
        reserva.put("pagamento", formaPagamento);

        if (formaPagamento.equals("Cartão")) {
            reserva.put("cartao", cartaoSelecionado);
        }

        db.collection("reservas")
                .add(reserva)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Reserva confirmada!", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}