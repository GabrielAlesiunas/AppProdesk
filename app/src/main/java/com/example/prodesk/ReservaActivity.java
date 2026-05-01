package com.example.prodesk;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ReservaActivity extends AppCompatActivity {

    TextView txtNome, txtPreco, txtDescricao;
    TextView txtDataInicio, txtDataFim, txtHoraInicio, txtHoraFim, txtResumo;
    ImageView imgEspaco;
    MaterialButton btnConfirmar;

    Calendar dataInicioCal;
    Calendar dataFimCal;

    double precoHora = 50.0;

    FirebaseFirestore db;
    String espacoId;

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

        btnConfirmar.setOnClickListener(v -> confirmarReserva());

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setSelectedItemId(R.id.nav_home);

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

    // =========================
    // CARREGAR ESPAÇO + IMAGEM
    // =========================
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

    // =========================
    // SELEÇÃO DE DATA
    // =========================
    private void selecionarData(boolean inicio) {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (view, y, m, d) -> {

            if (inicio) {
                dataInicioCal.set(y, m, d, 0, 0, 0);
                txtDataInicio.setText(d + "/" + (m + 1) + "/" + y);
            } else {
                dataFimCal.set(y, m, d, 0, 0, 0);
                txtDataFim.setText(d + "/" + (m + 1) + "/" + y);
            }

            calcular();

        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    // =========================
    // SELEÇÃO DE HORA
    // =========================
    private void selecionarHora(boolean inicio) {

        Calendar c = Calendar.getInstance();

        new TimePickerDialog(this, (view, h, m) -> {

            if (inicio) {
                dataInicioCal.set(Calendar.HOUR_OF_DAY, h);
                dataInicioCal.set(Calendar.MINUTE, m);
                dataInicioCal.set(Calendar.SECOND, 0);
                dataInicioCal.set(Calendar.MILLISECOND, 0);

                txtHoraInicio.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));

            } else {
                dataFimCal.set(Calendar.HOUR_OF_DAY, h);
                dataFimCal.set(Calendar.MINUTE, m);
                dataFimCal.set(Calendar.SECOND, 0);
                dataFimCal.set(Calendar.MILLISECOND, 0);

                txtHoraFim.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
            }

            calcular();

        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    // =========================
    // CÁLCULO DE HORAS
    // =========================
    private double calcularHoras() {

        long inicio = dataInicioCal.getTimeInMillis();
        long fim = dataFimCal.getTimeInMillis();

        if (inicio <= 0 || fim <= 0) return 0;
        if (fim <= inicio) return 0;

        double horas = (fim - inicio) / 3600000.0;

        if (horas < 0.25) return 0;

        return horas;
    }

    // =========================
    // RESUMO
    // =========================
    private void calcular() {

        double horas = calcularHoras();

        if (horas <= 0) {
            txtResumo.setText("Selecione data e hora válidas");
            return;
        }

        double total = horas * precoHora;

        txtResumo.setText(
                String.format(Locale.getDefault(),
                        "Horas: %.2f\nTotal: R$ %.2f",
                        horas, total)
        );
    }

    // =========================
    // CONFIRMAR RESERVA
    // =========================
    private void confirmarReserva() {

        if (dataInicioCal.getTimeInMillis() <= 0 || dataFimCal.getTimeInMillis() <= 0) {
            Toast.makeText(this, "Selecione data e hora", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dataFimCal.getTimeInMillis() <= dataInicioCal.getTimeInMillis()) {
            Toast.makeText(this, "Saída deve ser depois da entrada", Toast.LENGTH_SHORT).show();
            return;
        }

        double horas = calcularHoras();

        if (horas <= 0) {
            Toast.makeText(this, "Intervalo inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = horas * precoHora;

        HashMap<String, Object> reserva = new HashMap<>();
        reserva.put("espacoId", espacoId);
        reserva.put("nomeEspaco", txtNome.getText().toString());
        reserva.put("dataInicio", dataInicioCal.getTimeInMillis());
        reserva.put("dataFim", dataFimCal.getTimeInMillis());
        reserva.put("horas", horas);
        reserva.put("valorTotal", total);

        db.collection("reservas")
                .add(reserva)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Reserva confirmada!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}