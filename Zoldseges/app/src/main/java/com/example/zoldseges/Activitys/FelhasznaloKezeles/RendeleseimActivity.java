package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.Activitys.FooldalActivity;
import com.example.zoldseges.Activitys.KosarActivity;
import com.example.zoldseges.Activitys.NyugtaActivity;
import com.example.zoldseges.DAOS.Nyugta;
import com.example.zoldseges.DAOS.NyugtaDAO;
import com.example.zoldseges.DAOS.NyugtaAdapter;
import com.example.zoldseges.DAOS.Uzlet;
import com.example.zoldseges.DAOS.UzletAdapter;
import com.example.zoldseges.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RendeleseimActivity extends AppCompatActivity implements NyugtaDAO {

    private ArrayList<Nyugta> nyugtakListaja;

    private NyugtaAdapter nyugtaAdapter;

    private RelativeLayout nincsNyugtaLayout;

    private ProgressBar progressNyugtak;
    private TextView betoltesNyugtak;
    private RecyclerView nyugtak;
    private AppBarLayout appBarNyugta;
    private ImageView kepNyugtakhoz;
    private FirebaseAuth auth;
    FirebaseFirestore db;
    private boolean eladoE;

    private String eladoUzletId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendeleseim);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Rendeléseim");
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(RendeleseimActivity.this, BejelentkezesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            nincsNyugtaLayout = findViewById(R.id.nincsNyugtaLayout);
            progressNyugtak = findViewById(R.id.progressNyugtak);
            betoltesNyugtak = findViewById(R.id.betoltesNyugtak);
            nyugtak = findViewById(R.id.nyugtak);
            appBarNyugta = findViewById(R.id.appBarNyugta);
            kepNyugtakhoz = findViewById(R.id.kepNyugtakhoz);

            GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
            nyugtak.setLayoutManager(layoutManager);
            nyugtak.setHasFixedSize(true);

            nyugtakListaja = new ArrayList<>();

            eltuntet();
            clearList();
            eladoAFelhasznalo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(RendeleseimActivity.this, BejelentkezesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            eltuntet();
            clearList();
            eladoAFelhasznalo();
        }
    }

    private void eladoAFelhasznalo() {
        DocumentReference reference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                eladoE = Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat");
                eladoUzletId = value.getString("uzletId");
                getDataFromFireBase(eladoE);
            }
        });
    }

    private void getDataFromFireBase(boolean eladoE) {
        Query query = FirebaseFirestore.getInstance().collection("nyugtak");
        query.orderBy("idopont", Query.Direction.DESCENDING).addSnapshotListener((nyugtakCollection, error) -> {
            assert nyugtakCollection != null;
            clearList();

            if (!eladoE) {
                for (QueryDocumentSnapshot adat : nyugtakCollection) {
                    if (Objects.equals(adat.getString("rendeloId"), Objects.requireNonNull(auth.getCurrentUser()).getUid())) {
                        Nyugta nyugta = new Nyugta();
                        nyugta.setDatum(adat.getString("idopont"));
                        nyugta.setNyugtaId(adat.getString("nyugtaId"));
                        nyugta.setRendeloId(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        nyugta.setTermkek(adat.getString("termekek"));
                        nyugta.setVegosszeg(adat.getString("vegosszeg"));
                        nyugta.setUzletId(adat.getString("uzletId"));
                        nyugta.setUzletKepe(adat.getString("boltKepe"));
                        nyugta.setUzletNeve(adat.getString("uzletNeve"));
                        nyugta.setNev(adat.getString("rendeloNev"));
                        nyugtakListaja.add(nyugta);
                    }
                }
            } else {
                for (QueryDocumentSnapshot adat : nyugtakCollection) {
                    if (Objects.equals(adat.getString("uzletId"), eladoUzletId)) {
                        Nyugta nyugta = new Nyugta();
                        nyugta.setDatum(adat.getString("idopont"));
                        nyugta.setNyugtaId(adat.getString("nyugtaId"));
                        nyugta.setRendeloId(adat.getString("rendeloId"));
                        nyugta.setTermkek(adat.getString("termekek"));
                        nyugta.setVegosszeg(adat.getString("vegosszeg"));
                        nyugta.setUzletId(adat.getString("uzletId"));
                        nyugta.setUzletKepe(adat.getString("boltKepe"));
                        nyugta.setUzletNeve(adat.getString("uzletNeve"));
                        nyugta.setNev(adat.getString("rendeloNev"));
                        nyugtakListaja.add(nyugta);
                    }
                }
            }
            nyugtaAdapter = new NyugtaAdapter(getApplicationContext(), nyugtakListaja, RendeleseimActivity.this, eladoE);
            nyugtak.setAdapter(nyugtaAdapter);
            megjelenit();

        });
    }

    private void clearList() {
        if (nyugtakListaja != null) {
            nyugtakListaja.clear();

            if (nyugtaAdapter != null) {
                nyugtaAdapter.notifyDataSetChanged();
            }
        } else {
            nyugtakListaja = new ArrayList<>();
        }
    }

    public void megjelenit() {
        progressNyugtak.setVisibility(View.GONE);
        betoltesNyugtak.setVisibility(View.GONE);
        if (nyugtakListaja.size() == 0) {
            nincsNyugtaLayout.setVisibility(View.VISIBLE);
        } else {
            nyugtak.setVisibility(View.VISIBLE);
        }
        appBarNyugta.setVisibility(View.VISIBLE);
        kepNyugtakhoz.setVisibility(View.VISIBLE);
    }

    public void eltuntet() {
        progressNyugtak.setVisibility(View.VISIBLE);
        betoltesNyugtak.setVisibility(View.VISIBLE);
        nincsNyugtaLayout.setVisibility(View.GONE);
        nyugtak.setVisibility(View.GONE);
        appBarNyugta.setVisibility(View.INVISIBLE);
        kepNyugtakhoz.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_bejelentkezett_menu, menu);
        MenuItem kosar = menu.findItem(R.id.kosarfiok);
        kosar.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNyugta(int position) {
        Intent intent = new Intent(RendeleseimActivity.this, NyugtaActivity.class);
        intent.putExtra("nyugtaId", nyugtakListaja.get(position).getNyugtaId());
        intent.putExtra("fizetesUtan", false);
        startActivity(intent);
    }

    public void onVissza(View view) {
        finish();
        super.onBackPressed();
    }
}