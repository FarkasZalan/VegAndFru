package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.DAOS.Uzlet;

import com.example.zoldseges.DAOS.UzletAdapter;
import com.example.zoldseges.DAOS.UzletValaszto;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class FooldalActivity extends AppCompatActivity implements UzletValaszto {

    private FirebaseFirestore db;

    private FirebaseUser felhasznalo;

    RecyclerView fooldalBoltjai;
    ImageView kepFooldalra;
    AppBarLayout appBarfooldal;

    ArrayList<Uzlet> uzletekListaja;

    private UzletAdapter uzletAdapter;
    FirebaseAuth auth;

    private ProgressBar progressFooldal;

    private TextView betoltesFooldal;
    private SearchView searchView;
    private RelativeLayout nincsKeresesiEredmenyLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fooldal);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        felhasznalo = auth.getCurrentUser();
        fooldalBoltjai = findViewById(R.id.fooldalBoltjai);
        kepFooldalra = findViewById(R.id.kepFooldalra);
        appBarfooldal = findViewById(R.id.appBarfooldal);
        progressFooldal = findViewById(R.id.progressFooldal);
        betoltesFooldal = findViewById(R.id.betoltesFooldal);
        nincsKeresesiEredmenyLayout = findViewById(R.id.nincsKeresesiEredmenyLayout);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        fooldalBoltjai.setLayoutManager(layoutManager);
        fooldalBoltjai.setHasFixedSize(true);

        uzletekListaja = new ArrayList<>();

        eltuntet();
        clearList();
        getDataFromFireBase();

    }

    private void eltuntet() {
        progressFooldal.setVisibility(View.VISIBLE);
        betoltesFooldal.setVisibility(View.VISIBLE);
        appBarfooldal.setVisibility(View.INVISIBLE);
        fooldalBoltjai.setVisibility(View.GONE);
    }

    private void megjelenit() {
        progressFooldal.setVisibility(View.GONE);
        betoltesFooldal.setVisibility(View.GONE);
        appBarfooldal.setVisibility(View.VISIBLE);
        fooldalBoltjai.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        fooldalBoltjai.setLayoutManager(layoutManager);
        fooldalBoltjai.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();

        uzletekListaja = new ArrayList<>();
        eltuntet();
        clearList();
        getDataFromFireBase();

    }

    private void getDataFromFireBase() {
        Query query = FirebaseFirestore.getInstance().collection("uzletek");

        query.orderBy("cegNev", Query.Direction.ASCENDING).addSnapshotListener((uzletekCollection, error) -> {
            clearList();
            assert uzletekCollection != null;
            for (QueryDocumentSnapshot adat : uzletekCollection) {
                Uzlet bolt = new Uzlet();
                bolt.setUzletNeve(adat.getString("cegNev"));
                bolt.setBoltKepe(adat.getString("boltKepe"));
                bolt.setSzekhely(adat.getString("szekhely"));
                bolt.setTulajId(adat.getString("tulajId"));
                bolt.setUzletId(adat.getId());
                bolt.setSzallitasiDij(bolt.getSzallitasiDij());
                bolt.setSzallitasIdotartama(bolt.getSzallitasIdotartama());
                uzletekListaja.add(bolt);
            }
            uzletAdapter = new UzletAdapter(getApplicationContext(), uzletekListaja, FooldalActivity.this);
            fooldalBoltjai.setAdapter(uzletAdapter);
            megjelenit();
        });
    }

    private void clearList() {
        if (uzletekListaja != null) {
            uzletekListaja.clear();

            if (uzletAdapter != null) {
                uzletAdapter.notifyDataSetChanged();
            }
        } else {
            uzletekListaja = new ArrayList<>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.kereso);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Keresés...");

        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String keresendo) {
                filterList(keresendo);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterList(String keresendo) {
        ArrayList<Uzlet> szurtLista = new ArrayList<>();
        CollectionReference osszesTermekLista = db.collection("osszesTermek");
        if (keresendo.isEmpty()) {
            nincsKeresesiEredmenyLayout.setVisibility(View.GONE);
            fooldalBoltjai.setVisibility(View.VISIBLE);
            getDataFromFireBase();
        } else {
            osszesTermekLista.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        szurtLista.clear();
                        for (QueryDocumentSnapshot nev : task.getResult()) {
                            String termekNeve = nev.getString("termekNeve");
                            String uzletId = nev.getString("uzletId");
                            for (Uzlet uzlet : uzletekListaja) {
                                assert termekNeve != null;
                                if (uzlet.getUzletId().equals(uzletId)) {
                                    if (termekNeve.contains(keresendo) && !szurtLista.contains(uzlet)) {
                                        szurtLista.add(uzlet);
                                    }
                                }
                                if (uzlet.getUzletNeve().contains(keresendo) && !szurtLista.contains(uzlet)) {
                                    szurtLista.add(uzlet);
                                }
                            }
                        }
                        if (szurtLista.isEmpty()) {
                            nincsKeresesiEredmenyLayout.setVisibility(View.VISIBLE);
                            fooldalBoltjai.setVisibility(View.GONE);
                        } else {
                            szurtLista.sort(Comparator.comparing(Uzlet::getUzletNeve));
                            uzletAdapter = new UzletAdapter(getApplicationContext(), szurtLista, FooldalActivity.this);
                            fooldalBoltjai.setAdapter(uzletAdapter);
                            nincsKeresesiEredmenyLayout.setVisibility(View.GONE);
                            fooldalBoltjai.setVisibility(View.VISIBLE);
                        }
                    } else {
                        nincsKeresesiEredmenyLayout.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Váratlan hiba történt!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kosar:
                startActivity(new Intent(this, KosarActivity.class));
                return true;
            case R.id.fiok:
                if (felhasznalo != null) {
                    startProfile();
                } else {
                    startLogin();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startProfile() {
        startActivity(new Intent(this, FiokActicity.class));
    }

    public void startLogin() {
        startActivity(new Intent(this, BejelentkezesActivity.class));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosar);
        FrameLayout rootVieww = (FrameLayout) menuItem.getActionView();

        FrameLayout kor = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo);

        rootVieww.setOnClickListener(view -> {
            onOptionsItemSelected(menuItem);//tehát meghívja az optionItemSelected függvényt és az fog történni amit oda megadok
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onUzletValaszt(int position) {
        Intent intent = new Intent(FooldalActivity.this, BoltOldalaActivity.class);
        intent.putExtra("uzletNeve", uzletekListaja.get(position).getUzletNeve());
        intent.putExtra("szekhely", uzletekListaja.get(position).getSzekhely());
        intent.putExtra("uzletId", uzletekListaja.get(position).getUzletId());
        intent.putExtra("tulajId", uzletekListaja.get(position).getTulajId());
        intent.putExtra("boltKepe", uzletekListaja.get(position).getBoltKepe());

        startActivity(intent);
    }

    public void onVissza(View view) {
        searchView.setQuery("", false);
        searchView.setQueryHint("Keresés...");
    }
}