package com.example.prodesk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        public ViewHolder(View itemView) {
            super(itemView);
            txtNumero = itemView.findViewById(R.id.txtNumero);
            txtNome = itemView.findViewById(R.id.txtNome);
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

        // MOSTRAR SÓ OS 4 ÚLTIMOS
        String numero = cartao.getNumero();
        String finalCartao = "**** **** **** " + numero.substring(numero.length() - 4);

        holder.txtNumero.setText(finalCartao);
        holder.txtNome.setText(cartao.getNome());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}