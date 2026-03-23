package com.example.prodesk;

import android.os.Bundle;
import android.widget.*;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class EspacosActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText edtBusca;
    Button btnPerfil;

    LinearLayout bottomSheet;
    BottomSheetBehavior<LinearLayout> behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espacos);

        edtBusca = findViewById(R.id.edtBusca);
        btnPerfil = findViewById(R.id.btnPerfil);

        bottomSheet = findViewById(R.id.bottomSheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    // 🔥 CLASSE DO ESPAÇO
    class Espaco {
        String nome;
        String preco;
        String descricao;
        int imagem;

        public Espaco(String nome, String preco, String descricao, int imagem) {
            this.nome = nome;
            this.preco = preco;
            this.descricao = descricao;
            this.imagem = imagem;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng local = new LatLng(-23.5505, -46.6333);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 13));

        // MOCKS
        adicionarEspaco(-23.5505, -46.6333,
                new Espaco("Coworking SP", "R$ 250", "Café • Ar • Internet", R.drawable.coworking01));

        adicionarEspaco(-23.5520, -46.6300,
                new Espaco("Espaço Centro", "R$ 257", "Wi-Fi • Ar", R.drawable.coworking01));

        adicionarEspaco(-23.5480, -46.6350,
                new Espaco("Sala Premium", "R$ 237", "Café • Internet", R.drawable.coworking01));

        // 👇 CLICK NO MARKER
        mMap.setOnMarkerClickListener(marker -> {

            Espaco espaco = (Espaco) marker.getTag();

            if (espaco != null) {
                mostrarBottomSheet(espaco);
            }

            return true;
        });
    }

    private void adicionarEspaco(double lat, double lng, Espaco espaco) {

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(espaco.preco));

        marker.setTag(espaco);
    }

    private void mostrarBottomSheet(Espaco espaco) {

        ImageView img = findViewById(R.id.imgEspaco);
        TextView nome = findViewById(R.id.txtNome);
        TextView preco = findViewById(R.id.txtPreco);
        TextView desc = findViewById(R.id.txtDescricao);

        img.setImageResource(espaco.imagem);
        nome.setText(espaco.nome);
        preco.setText(espaco.preco);
        desc.setText(espaco.descricao);

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}