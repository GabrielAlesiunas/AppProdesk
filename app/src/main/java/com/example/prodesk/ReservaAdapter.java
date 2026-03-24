package com.example.prodesk;

import android.content.Context;
import android.view.*;
import android.widget.*;

import java.util.List;

public class ReservaAdapter extends BaseAdapter {

    Context context;
    List<ReservaModel> lista;

    public ReservaAdapter(Context context, List<ReservaModel> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() { return lista.size(); }

    @Override
    public Object getItem(int i) { return lista.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);

        ImageView img = view.findViewById(R.id.img);
        TextView nome = view.findViewById(R.id.nome);
        TextView data = view.findViewById(R.id.data);
        TextView horario = view.findViewById(R.id.horario);
        TextView pagamento = view.findViewById(R.id.pagamento);
        TextView status = view.findViewById(R.id.status);

        ReservaModel r = lista.get(i);

        img.setImageResource(r.imagem);
        nome.setText(r.nome);
        data.setText(r.dataInicio + " até " + r.dataFim);
        horario.setText(r.horario);
        pagamento.setText("Pagamento: " + r.pagamento);
        status.setText(r.status);

        return view;
    }
}