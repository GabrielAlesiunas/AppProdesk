package com.example.prodesk;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ViewHolder> {

    List<ReservaModel> lista;

    public ReservaAdapter(Object context, List<ReservaModel> lista) {
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

        // NOME
        holder.nome.setText(r.nomeEspaco);

        // DATA
        holder.data.setText(
                formatarData(r.dataInicio) + " até " + formatarData(r.dataFim)
        );

        // HORÁRIO
        holder.horario.setText(
                formatarHora(r.dataInicio) + " - " + formatarHora(r.dataFim)
        );

        // VALOR
        holder.valor.setText("Total: R$ " + String.format(Locale.getDefault(), "%.2f", r.valorTotal));

        // IMAGEM BASE64
        if (r.imagem != null && !r.imagem.isEmpty()) {
            try {
                String clean = r.imagem.replace("\n", "").replace(" ", "");
                byte[] bytes = Base64.decode(clean, Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.img.setImageBitmap(bmp);
            } catch (Exception e) {
                holder.img.setImageResource(android.R.color.darker_gray);
            }
        } else {
            holder.img.setImageResource(android.R.color.darker_gray);
        }

        if ("PIX".equals(r.pagamento)) {
            holder.txtPagamento.setText("💸 Pago via Pix");
        } else if ("Cartão".equals(r.pagamento)) {
            holder.txtPagamento.setText("💳 Pago no cartão");
        } else {
            holder.txtPagamento.setText("Pagamento não informado");
        }

        // BOTÃO AVALIAR
        holder.btnAvaliar.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), AvaliacaoActivity.class);
            intent.putExtra("id", r.espacoId);
            intent.putExtra("nome", r.nomeEspaco);
            v.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nome, data, horario, valor, txtPagamento; // 🔥 adiciona aqui
        ImageView img;
        Button btnAvaliar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nome = itemView.findViewById(R.id.txtNome);
            data = itemView.findViewById(R.id.txtData);
            horario = itemView.findViewById(R.id.txtHorario);
            valor = itemView.findViewById(R.id.txtValor);
            img = itemView.findViewById(R.id.imgEspaco);
            btnAvaliar = itemView.findViewById(R.id.btnAvaliar);

            txtPagamento = itemView.findViewById(R.id.txtPagamento); // 🔥 ESSENCIAL
        }
    }

    // =========================
    // FORMATADORES
    // =========================
    private String formatarData(long millis) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date(millis));
    }

    private String formatarHora(long millis) {
        return new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(new Date(millis));
    }

    // =========================
    // DIALOG DE AVALIAÇÃO
    // =========================
    private void mostrarDialog(View v, String nomeEspaco) {

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
        builder.setTitle("Avaliar " + nomeEspaco);

        LinearLayout layout = new LinearLayout(v.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        EditText comentario = new EditText(v.getContext());
        comentario.setHint("Digite sua opinião...");
        layout.addView(comentario);

        RatingBar rating = new RatingBar(v.getContext());
        rating.setNumStars(5);
        rating.setStepSize(1);
        layout.addView(rating);

        builder.setView(layout);

        builder.setPositiveButton("Enviar", (dialog, which) -> {

            float nota = rating.getRating();
            String texto = comentario.getText().toString();

            Toast.makeText(v.getContext(),
                    "Avaliação enviada\nNota: " + nota,
                    Toast.LENGTH_SHORT).show();

            // 🔥 AQUI você pode salvar no Firestore depois
        });

        builder.setNegativeButton("Cancelar", null);

        builder.show();
    }
}