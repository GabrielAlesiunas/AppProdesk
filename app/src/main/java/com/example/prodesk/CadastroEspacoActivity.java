package com.example.prodesk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CadastroEspacoActivity extends AppCompatActivity {

    EditText edtNome, edtDescricao, edtPreco, edtEndereco;
    ImageView imgEspaco;
    Button btnImagem, btnSalvar;
    CheckBox checkWifi, checkAr, checkEstacionamento, checkCafe;
    RadioGroup radioCompartilhado;
    RadioButton radioSim, radioNao;

    Uri imagemSelecionada;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_espaco);

        // COMPONENTES
        edtNome = findViewById(R.id.edtNomeEspaco);
        edtDescricao = findViewById(R.id.edtDescricao);
        edtPreco = findViewById(R.id.edtPreco);
        edtEndereco = findViewById(R.id.edtEndereco);
        checkWifi = findViewById(R.id.checkWifi);
        checkAr = findViewById(R.id.checkAr);
        checkEstacionamento = findViewById(R.id.checkEstacionamento);
        checkCafe = findViewById(R.id.checkCafe);

        radioCompartilhado = findViewById(R.id.radioCompartilhado);
        radioSim = findViewById(R.id.radioSim);
        radioNao = findViewById(R.id.radioNao);

        imgEspaco = findViewById(R.id.imgEspaco);
        btnImagem = findViewById(R.id.btnSelecionarImagem);
        btnSalvar = findViewById(R.id.btnSalvarEspaco);

        // IMAGEM
        btnImagem.setOnClickListener(v -> abrirGaleria());

        // SALVAR
        btnSalvar.setOnClickListener(v -> salvarEspaco());

        // MENU
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_CadEspacos);

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

            if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }

            return false;
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imagemSelecionada = data.getData();
            imgEspaco.setImageURI(imagemSelecionada);
        }
    }

    private void salvarEspaco() {

        String nome = edtNome.getText().toString();
        String descricao = edtDescricao.getText().toString();
        String preco = edtPreco.getText().toString();
        String endereco = edtEndereco.getText().toString();

        if (nome.isEmpty() || descricao.isEmpty() || preco.isEmpty() || endereco.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Comodidades
        StringBuilder comodidades = new StringBuilder();

        if (checkWifi.isChecked()) comodidades.append("Wi-Fi ");
        if (checkAr.isChecked()) comodidades.append("Ar-condicionado ");
        if (checkEstacionamento.isChecked()) comodidades.append("Estacionamento ");
        if (checkCafe.isChecked()) comodidades.append("Café ");

        // Tipo de espaço
        String tipoEspaco = radioSim.isChecked() ? "Compartilhado" : "Privado";

        // Debug / confirmação
        Toast.makeText(this,
                "Espaço cadastrado!\n" +
                        "Comodidades: " + comodidades.toString() + "\n" +
                        "Tipo: " + tipoEspaco,
                Toast.LENGTH_LONG).show();

        finish();
    }
}