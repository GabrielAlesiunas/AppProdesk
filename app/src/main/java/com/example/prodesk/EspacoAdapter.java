package com.example.prodesk;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EspacoAdapter extends RecyclerView.Adapter<EspacoAdapter.ViewHolder> {

    private List<Espaco> lista;
    private Context context;

    public EspacoAdapter(List<Espaco> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    // CRIA O CARD (layout)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_espaco, parent, false);
        return new ViewHolder(view);
    }

    // PREENCHE OS DADOS
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Espaco espaco = lista.get(position);

        holder.nome.setText(espaco.getNome());
        holder.descricao.setText(espaco.getDescricao());
        holder.preco.setText(espaco.getPreco());

        // BOTÃO VER MAIS
        holder.btnVerMais.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), DetalheActivity.class);
            intent.putExtra("nome", espaco.getNome());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    // COMPONENTES DO CARD
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nome, descricao, preco;
        Button btnVerMais;

        public ViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.txtNome);
            descricao = itemView.findViewById(R.id.txtDescricao);
            preco = itemView.findViewById(R.id.txtPreco);
            btnVerMais = itemView.findViewById(R.id.btnVerMais);
        }
    }
}