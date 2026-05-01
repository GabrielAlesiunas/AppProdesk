package com.example.prodesk;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ViewHolder> {

    List<ReservaModel> lista;

    public ReservaAdapter(HistoricoReservasActivity historicoReservasActivity, List<ReservaModel> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reserva, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ReservaModel r = lista.get(position);

        holder.nome.setText(r.nomeEspaco);

        holder.data.setText(
                formatar(r.dataInicio) + " até " + formatar(r.dataFim)
        );

        holder.valor.setText("R$ " + r.valorTotal);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nome, data, valor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.txtNome);
            data = itemView.findViewById(R.id.txtData);
            valor = itemView.findViewById(R.id.txtValor);
        }
    }

    private String formatar(long millis) {
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }
}