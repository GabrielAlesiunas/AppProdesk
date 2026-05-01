package com.example.prodesk;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class RecuperarSenhaActivity extends AppCompatActivity {

    EditText email, novaSenha, confirmarSenha;
    Button btnAlterar;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);

        email = findViewById(R.id.email);
        novaSenha = findViewById(R.id.novaSenha);
        confirmarSenha = findViewById(R.id.confirmarSenha);
        btnAlterar = findViewById(R.id.btnAlterar);

        db = FirebaseFirestore.getInstance();

        btnAlterar.setOnClickListener(v -> {

            String emailStr = email.getText().toString().trim();
            String senhaStr = novaSenha.getText().toString();
            String confirmarStr = confirmarSenha.getText().toString();

            // VALIDAÇÃO
            if (!Patterns.EMAIL_ADDRESS.matcher(emailStr).matches()) {
                email.setError("Email inválido");
                return;
            }

            if (senhaStr.length() < 4) {
                novaSenha.setError("Senha muito curta");
                return;
            }

            if (!senhaStr.equals(confirmarStr)) {
                confirmarSenha.setError("Senhas não coincidem");
                return;
            }

            // BUSCAR USUÁRIO NO FIRESTORE
            db.collection("usuarios")
                    .whereEqualTo("email", emailStr)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        if (!queryDocumentSnapshots.isEmpty()) {

                            String userId = queryDocumentSnapshots.getDocuments()
                                    .get(0).getId();

                            // ATUALIZAR SENHA
                            db.collection("usuarios")
                                    .document(userId)
                                    .update("senha", senhaStr)
                                    .addOnSuccessListener(unused -> {

                                        Toast.makeText(this,
                                                "Senha alterada com sucesso!",
                                                Toast.LENGTH_SHORT).show();

                                        finish();
                                    })
                                    .addOnFailureListener(e -> {

                                        Toast.makeText(this,
                                                "Erro: " + e.getMessage(),
                                                Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            email.setError("Usuário não encontrado");
                        }
                    });
        });
    }
}