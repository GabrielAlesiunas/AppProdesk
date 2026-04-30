package com.example.prodesk;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EspacoAdapter extends RecyclerView.Adapter<EspacoAdapter.ViewHolder> {

    private List<Espaco> lista;
    private Context context;

    public EspacoAdapter(List<Espaco> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_espaco, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Espaco e = lista.get(position);

        holder.nome.setText(e.getNome());
        holder.descricao.setText(e.getDescricao());

        // 💰 PREÇO FORMATADO
        holder.preco.setText("R$ " + e.getPreco() + " /H");

        // 🖼 IMAGEM ESPAÇO
        if (e.getImagem() != null && !e.getImagem().isEmpty()) {
            byte[] bytes = Base64.decode(e.getImagem(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.img.setImageBitmap(bitmap);
        }

        // 🔥 CLIQUE DETALHE
        holder.btn.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalheActivity.class);
            intent.putExtra("id", e.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nome, descricao, preco;
        ImageView img;
        Button btn;

        public ViewHolder(View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.txtNome);
            descricao = itemView.findViewById(R.id.txtDescricao);
            preco = itemView.findViewById(R.id.txtPreco);
            img = itemView.findViewById(R.id.imgEspaco);
            btn = itemView.findViewById(R.id.btnVerMais);
        }
    }
}