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
import android.widget.SearchView;
import android.widget.TextView;


import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado.BoltKezelesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado.TermekSzerkeszteseActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.DAOS.TermekAdapter;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class FooldalActivity extends AppCompatActivity implements UzletValaszto {

    private FirebaseFirestore db;
    private TextView korText;

    private int kosarMennyiseg = 0;

    private FirebaseUser felhasznalo;

    RecyclerView fooldalBoltjai;
    ImageView kepFooldalra;
    AppBarLayout appBarfooldal;

    ArrayList<Uzlet> uzletekListaja;

    private UzletAdapter uzletAdapter;
    FirebaseAuth auth;

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

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        fooldalBoltjai.setLayoutManager(layoutManager);
        fooldalBoltjai.setHasFixedSize(true);

        uzletekListaja = new ArrayList<>();

        clearList();
        getDataFromFireBase();

    }

    @Override
    protected void onResume() {
        super.onResume();

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        fooldalBoltjai.setLayoutManager(layoutManager);
        fooldalBoltjai.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();

        uzletekListaja = new ArrayList<>();
        clearList();
        getDataFromFireBase();

    }

    private void getDataFromFireBase() {
        CollectionReference uzletek = db.collection("uzletek");
        uzletek.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> uzletekCollection) {
                if (uzletekCollection.isSuccessful()) {
                    clearList();
                    for (QueryDocumentSnapshot adat : uzletekCollection.getResult()) {
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
                    uzletekListaja.sort(Comparator.comparing(Uzlet::getUzletNeve));
                    uzletAdapter = new UzletAdapter(getApplicationContext(), uzletekListaja, FooldalActivity.this);
                    fooldalBoltjai.setAdapter(uzletAdapter);
                }
            }
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Keresés...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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
        korText = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo_text);

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
}