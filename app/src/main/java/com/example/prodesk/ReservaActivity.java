package com.example.prodesk;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ReservaActivity extends AppCompatActivity {

    TextView txtNome, txtPreco, txtDescricao, txtData, txtHora;
    Button btnSelecionarData, btnSelecionarHora, btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        txtNome = findViewById(R.id.txtNome);
        txtPreco = findViewById(R.id.txtPreco);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtData = findViewById(R.id.txtData);
        txtHora = findViewById(R.id.txtHora);

        btnSelecionarData = findViewById(R.id.btnData);
        btnSelecionarHora = findViewById(R.id.btnHora);
        btnConfirmar = findViewById(R.id.btnConfirmar);

        // RECEBER DADOS
        String nome = getIntent().getStringExtra("nome");
        String preco = getIntent().getStringExtra("preco");
        String descricao = getIntent().getStringExtra("descricao");

        txtNome.setText(nome);
        txtPreco.setText(preco);
        txtDescricao.setText(descricao);

        // DATA
        btnSelecionarData.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
                txtData.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // HORA
        btnSelecionarHora.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();

            new TimePickerDialog(this, (view, hour, minute) -> {
                txtHora.setText(hour + ":" + minute);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        // CONFIRMAR
        btnConfirmar.setOnClickListener(v -> {

            if (txtData.getText().toString().isEmpty() ||
                    txtHora.getText().toString().isEmpty()) {

                Toast.makeText(this, "Escolha data e horário", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Reserva confirmada! (fake)", Toast.LENGTH_LONG).show();
            finish();
        });
    }
}