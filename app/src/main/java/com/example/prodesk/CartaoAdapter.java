package com.example.prodesk;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CartaoAdapter extends RecyclerView.Adapter<CartaoAdapter.ViewHolder> {

    List<Cartao> lista;

    public CartaoAdapter(List<Cartao> lista) {
        this.lista = lista;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNumero, txtNome;
        Button btnExcluir;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNumero = itemView.findViewById(R.id.txtNumero);
            txtNome = itemView.findViewById(R.id.txtNome);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }

    @NonNull
    @Override
    public CartaoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cartao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartaoAdapter.ViewHolder holder, int position) {

        Cartao cartao = lista.get(position);

        String finalCartao = "**** **** **** " + cartao.getNumero().substring(cartao.getNumero().length() - 4);

        holder.txtNumero.setText(finalCartao);
        holder.txtNome.setText(cartao.getNome());

        // 🔥 AÇÃO DE EXCLUIR
        holder.btnExcluir.setOnClickListener(v -> {
            lista.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, lista.size());
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}