package com.example.prodesk;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ReservaActivity extends AppCompatActivity {

    TextView txtNome, txtPreco, txtDescricao;
    TextView txtDataInicio, txtDataFim, txtHoraInicio, txtResumo;
    TextView btnConfirmar;

    Calendar dataInicioCal = Calendar.getInstance();
    Calendar dataFimCal = Calendar.getInstance();

    double precoHora = 50.0;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        db = FirebaseFirestore.getInstance();

        txtNome = findViewById(R.id.txtNome);
        txtPreco = findViewById(R.id.txtPreco);
        txtDescricao = findViewById(R.id.txtDescricao);

        txtDataInicio = findViewById(R.id.txtDataInicio);
        txtDataFim = findViewById(R.id.txtDataFim);
        txtHoraInicio = findViewById(R.id.txtHoraInicio);
        txtResumo = findViewById(R.id.txtResumo);

        btnConfirmar = findViewById(R.id.btnConfirmar);

        String nome = getIntent().getStringExtra("nome");
        String preco = getIntent().getStringExtra("preco");
        String descricao = getIntent().getStringExtra("descricao");

        txtNome.setText(nome);
        txtPreco.setText(preco);
        txtDescricao.setText(descricao);

        try {
            precoHora = Double.parseDouble(preco.replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}

        // 🔥 CLIQUES NOS CARDS (TextView agora funciona como botão)
        txtDataInicio.setOnClickListener(v -> selecionarData(true));
        txtDataFim.setOnClickListener(v -> selecionarData(false));
        txtHoraInicio.setOnClickListener(v -> selecionarHora(true));

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

    private void selecionarData(boolean inicio) {

        Calendar c = Calendar.getInstance();

        new DatePickerDialog(this, (view, y, m, d) -> {

            if (inicio) {
                dataInicioCal.set(y, m, d);
                txtDataInicio.setText(d + "/" + (m + 1) + "/" + y);
            } else {
                dataFimCal.set(y, m, d);
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
                txtHoraInicio.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
            }

            calcular();

        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private double calcularHoras() {

        long diff = dataFimCal.getTimeInMillis() - dataInicioCal.getTimeInMillis();

        if (diff <= 0) return 0;

        return diff / (1000.0 * 60 * 60);
    }

    private void calcular() {

        double horas = calcularHoras();

        if (horas <= 0) {
            txtResumo.setText("Datas inválidas");
            return;
        }

        double total = horas * precoHora;

        txtResumo.setText(String.format(Locale.getDefault(),
                "Horas: %.1f\nTotal: R$ %.2f", horas, total));
    }

    private void confirmarReserva() {

        long inicio = dataInicioCal.getTimeInMillis();
        long fim = dataFimCal.getTimeInMillis();

        if (fim <= inicio) {
            Toast.makeText(this, "Datas inválidas", Toast.LENGTH_SHORT).show();
            return;
        }

        verificarDisponibilidade(inicio, fim, ok -> {

            if (!ok) {
                Toast.makeText(this, "Horário indisponível", Toast.LENGTH_SHORT).show();
                return;
            }

            salvarReserva(inicio, fim);
        });
    }

    private void verificarDisponibilidade(long inicio, long fim, OnCheck callback) {

        String espacoId = getIntent().getStringExtra("id");

        db.collection("reservas")
                .whereEqualTo("espacoId", espacoId)
                .get()
                .addOnSuccessListener(query -> {

                    for (var doc : query) {

                        Long i = doc.getLong("dataInicio");
                        Long f = doc.getLong("dataFim");

                        if (i == null || f == null) continue;

                        boolean conflito = (inicio < f && fim > i);

                        if (conflito) {
                            callback.result(false);
                            return;
                        }
                    }

                    callback.result(true);
                });
    }

    private void salvarReserva(long inicio, long fim) {

        double horas = calcularHoras();
        double total = horas * precoHora;

        HashMap<String, Object> reserva = new HashMap<>();
        reserva.put("espacoId", getIntent().getStringExtra("id"));
        reserva.put("nomeEspaco", txtNome.getText().toString());
        reserva.put("dataInicio", inicio);
        reserva.put("dataFim", fim);
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

    interface OnCheck {
        void result(boolean ok);
    }
}