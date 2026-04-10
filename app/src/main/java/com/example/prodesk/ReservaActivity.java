package com.example.prodesk;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReservaActivity extends AppCompatActivity {

    TextView txtNome, txtPreco, txtDescricao;
    TextView txtDataInicio, txtDataFim, txtHoraInicio, txtHoraFim, txtResumo;

    Button btnDataInicio, btnDataFim, btnHoraInicio, btnHoraFim, btnConfirmar;

    Calendar dataInicioCal = Calendar.getInstance();
    Calendar dataFimCal = Calendar.getInstance();

    double precoHora = 50.0; // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        // refs
        txtNome = findViewById(R.id.txtNome);
        txtPreco = findViewById(R.id.txtPreco);
        txtDescricao = findViewById(R.id.txtDescricao);

        txtDataInicio = findViewById(R.id.txtDataInicio);
        txtDataFim = findViewById(R.id.txtDataFim);
        txtHoraInicio = findViewById(R.id.txtHoraInicio);
        txtHoraFim = findViewById(R.id.txtHoraFim);
        txtResumo = findViewById(R.id.txtResumo);

        btnDataInicio = findViewById(R.id.btnDataInicio);
        btnDataFim = findViewById(R.id.btnDataFim);
        btnHoraInicio = findViewById(R.id.btnHoraInicio);
        btnHoraFim = findViewById(R.id.btnHoraFim);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        // dados recebidos
        String nome = getIntent().getStringExtra("nome");
        String preco = getIntent().getStringExtra("preco");
        String descricao = getIntent().getStringExtra("descricao");

        txtNome.setText(nome);
        txtPreco.setText(preco);
        txtDescricao.setText(descricao);

        // pega preço numérico simples
        if (preco != null) {
            try {
                precoHora = Double.parseDouble(preco.replaceAll("[^0-9]", ""));
            } catch (Exception ignored) {}
        }

        // DATA INICIO
        btnDataInicio.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            new DatePickerDialog(this, (view, y, m, d) -> {
                dataInicioCal.set(y, m, d);
                txtDataInicio.setText(d + "/" + (m + 1) + "/" + y);
                calcular();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // DATA FIM
        btnDataFim.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            new DatePickerDialog(this, (view, y, m, d) -> {
                dataFimCal.set(y, m, d);
                txtDataFim.setText(d + "/" + (m + 1) + "/" + y);
                calcular();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // HORA INICIO
        btnHoraInicio.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            new TimePickerDialog(this, (view, h, min) -> {
                dataInicioCal.set(Calendar.HOUR_OF_DAY, h);
                dataInicioCal.set(Calendar.MINUTE, min);
                txtHoraInicio.setText(String.format("%02d:%02d", h, min));
                calcular();
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // HORA FIM
        btnHoraFim.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            new TimePickerDialog(this, (view, h, min) -> {
                dataFimCal.set(Calendar.HOUR_OF_DAY, h);
                dataFimCal.set(Calendar.MINUTE, min);
                txtHoraFim.setText(String.format("%02d:%02d", h, min));
                calcular();
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // confirmar
        btnConfirmar.setOnClickListener(v -> {
            Toast.makeText(this, "Reserva confirmada!", Toast.LENGTH_SHORT).show();
            finish();
        });

        // menu
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> true);
    }

    private void calcular() {

        long diffMs = dataFimCal.getTimeInMillis() - dataInicioCal.getTimeInMillis();

        if (diffMs <= 0) {
            txtResumo.setText("Datas inválidas");
            return;
        }

        double horas = diffMs / (1000.0 * 60 * 60);
        double total = horas * precoHora;

        txtResumo.setText(String.format(Locale.getDefault(),
                "Horas: %.2f | Total: R$ %.2f", horas, total));
    }
}