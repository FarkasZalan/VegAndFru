package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado.BoltKezelesActivity;
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.DAOS.TermekVasarloknakAdapter;
import com.example.zoldseges.DAOS.Uzlet;
import com.example.zoldseges.DAOS.UzletAdapter;
import com.example.zoldseges.DAOS.VasarloNezetTermekek;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class BoltOldalaActivity extends AppCompatActivity implements VasarloNezetTermekek {

    StorageReference storageReference;
    FirebaseFirestore db;

    String boltKepe;
    String uzletId;
    String tulajId;
    String szekhely;
    String uzletNeve;

    private ProgressBar progressBolt;
    private TextView betoltesBolt;
    private RecyclerView boltTermekei;
    private AppBarLayout appBarBolt;
    private ImageView kepBoltba;

    private ArrayList<Termek> termekekListaja;
    private TermekVasarloknakAdapter adapter;
    private boolean ures = false;
    private RelativeLayout nincsTermekLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_oldala);

        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        db = FirebaseFirestore.getInstance();

        progressBolt = findViewById(R.id.progressBolt);
        betoltesBolt = findViewById(R.id.betoltesBolt);
        boltTermekei = findViewById(R.id.boltTermekei);
        appBarBolt = findViewById(R.id.appBarBolt);
        kepBoltba = findViewById(R.id.kepBoltba);
        nincsTermekLayout = findViewById(R.id.nincsTermekLayout);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        boltTermekei.setLayoutManager(layoutManager);
        boltTermekei.setHasFixedSize(true);

        boltKepe = getIntent().getStringExtra("boltKepe");
        uzletNeve = getIntent().getStringExtra("uzletNeve");
        tulajId = getIntent().getStringExtra("tulajId");
        uzletId = getIntent().getStringExtra("uzletId");
        szekhely = getIntent().getStringExtra("szekhely");

        termekekListaja = new ArrayList<>();

        eltuntet();
        clearAll();
        getDataFromFireBase();

    }

    @Override
    protected void onResume() {
        super.onResume();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        boltTermekei.setLayoutManager(layoutManager);
        boltTermekei.setHasFixedSize(true);

        termekekListaja = new ArrayList<>();
        eltuntet();
        clearAll();
        getDataFromFireBase();
    }

    private void getDataFromFireBase() {
        kepMegjelenitese();
        Query query = FirebaseFirestore.getInstance().collection("uzletek").document(uzletId).collection("termekek");
        query.whereEqualTo("uzletId", uzletId).orderBy("termekNeve", Query.Direction.ASCENDING).get().addOnCompleteListener(termekek -> {
            if (termekek.isSuccessful()) {
                clearAll();
                for (QueryDocumentSnapshot adat : termekek.getResult()) {
                    Termek termek = new Termek();
                    termek.setTermekKepe(adat.getString("termekKepe"));
                    termek.setNev(adat.getString("termekNeve"));
                    termek.setUzletId(adat.getString("uzletId"));
                    termek.setTermekSulya(Objects.requireNonNull(adat.getDouble("termekSulya")));
                    termek.setAr(Objects.requireNonNull(adat.getDouble("termekAra")));
                    termek.setRaktaronLevoMennyiseg(Objects.requireNonNull(adat.getDouble("raktaronLevoMennyiseg")));
                    termek.setSajatId(adat.getId());
                    termek.setOsszTermekColectionId(adat.getString("osszTermekCollection"));
                    termekekListaja.add(termek);
                }
                if (termekekListaja.isEmpty()) {
                    ures = true;
                } else {
                    ures = false;
                    adapter = new TermekVasarloknakAdapter(getApplicationContext(), termekekListaja, BoltOldalaActivity.this);
                    boltTermekei.setAdapter(adapter);
                }
                megjelenit();
            }
        });
    }

    public void kepMegjelenitese() {
        kepBoltba.setScaleType(ImageView.ScaleType.CENTER);
        if (boltKepe != null && !boltKepe.isEmpty()) {
            Uri uri = Uri.parse(boltKepe);
            try {
                if (!this.isFinishing()) {
                    Glide.with(BoltOldalaActivity.this).load(uri).placeholder(R.drawable.grocery_store).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            megjelenit();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(kepBoltba);
                }
            } catch (Exception e) {
                Glide.with(BoltOldalaActivity.this).load(R.drawable.grocery_store).into(kepBoltba);
            }
        } else {
            int color = getResources().getColor(R.color.white, getTheme());
            appBarBolt.setBackgroundColor(color);

            Glide.with(BoltOldalaActivity.this).load(R.drawable.grocery_store).into(kepBoltba);
        }
    }

    private void clearAll() {
        if (termekekListaja != null) {
            termekekListaja.clear();

            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        } else {
            termekekListaja = new ArrayList<>();
        }
    }

    private void eltuntet() {
        progressBolt.setVisibility(View.VISIBLE);
        betoltesBolt.setVisibility(View.VISIBLE);
        boltTermekei.setVisibility(View.GONE);
        nincsTermekLayout.setVisibility(View.GONE);
        appBarBolt.setVisibility(View.INVISIBLE);
    }

    private void megjelenit() {
        progressBolt.setVisibility(View.GONE);
        betoltesBolt.setVisibility(View.GONE);
        appBarBolt.setVisibility(View.VISIBLE);
        if (!ures) {
            boltTermekei.setVisibility(View.VISIBLE);
            nincsTermekLayout.setVisibility(View.GONE);
        } else {
            boltTermekei.setVisibility(View.GONE);
            nincsTermekLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fooldalra_iranyito, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.vissza) {
            finish();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTermek(int position) {

    }

    @Override
    public void onKosarba(int position) {

    }

    public void onVissza(View view) {
        finish();
        super.onBackPressed();
    }
}