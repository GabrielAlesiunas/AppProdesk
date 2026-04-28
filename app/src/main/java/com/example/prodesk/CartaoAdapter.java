package com.example.prodesk;

import android.app.AlertDialog;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartaoAdapter extends RecyclerView.Adapter<CartaoAdapter.ViewHolder> {

    List<Cartao> lista;

    public CartaoAdapter(List<Cartao> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumero, txtNome, txtValidade;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNumero = itemView.findViewById(R.id.txtNumero);
            txtNome = itemView.findViewById(R.id.txtNome);
            txtValidade = itemView.findViewById(R.id.txtValidade);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cartao, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Cartao c = lista.get(position);

        holder.txtNumero.setText(c.numero);
        holder.txtNome.setText(c.nome);
        holder.txtValidade.setText(c.validade);

        // 🔥 EDITAR AO CLICAR
        holder.itemView.setOnClickListener(v -> {

            EditText input = new EditText(v.getContext());
            input.setText(c.nome);

            new AlertDialog.Builder(v.getContext())
                    .setTitle("Editar nome do cartão")
                    .setView(input)
                    .setPositiveButton("Salvar", (dialog, which) -> {

                        String novoNome = input.getText().toString().trim();

                        if (novoNome.isEmpty()) {
                            Toast.makeText(v.getContext(), "Nome inválido", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        FirebaseFirestore.getInstance()
                                .collection("usuarios")
                                .document(FirebaseAuth.getInstance().getUid())
                                .collection("cartoes")
                                .document(c.id)
                                .update("nome", novoNome);

                        c.nome = novoNome;
                        notifyItemChanged(position);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}