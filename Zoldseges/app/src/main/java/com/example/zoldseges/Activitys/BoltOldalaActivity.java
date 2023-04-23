package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.DAOS.TermekVasarloknakAdapter;
import com.example.zoldseges.DAOS.VasarloNezetTermekek;
import com.example.zoldseges.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
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
    private FirebaseAuth auth;
    private MenuItem kosar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_oldala);

        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

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

        getSupportActionBar().setTitle(uzletNeve);

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
        invalidateOptionsMenu();
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
                int color = getResources().getColor(R.color.white, getTheme());
                int forground = getResources().getColor(R.color.ures_kep, getTheme());
                appBarBolt.setBackgroundColor(color);
                kepBoltba.setForeground(new ColorDrawable(forground));
                kepBoltba.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(BoltOldalaActivity.this).load(R.drawable.grocery_store).into(kepBoltba);
            }
        } else {
            int color = getResources().getColor(R.color.white, getTheme());
            int forground = getResources().getColor(R.color.ures_kep, getTheme());
            appBarBolt.setBackgroundColor(color);
            kepBoltba.setForeground(new ColorDrawable(forground));
            kepBoltba.setScaleType(ImageView.ScaleType.FIT_CENTER);
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
        getMenuInflater().inflate(R.menu.vissza_menu, menu);
        kosar = menu.findItem(R.id.kosar);
        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;
                String tipus = value.getString("felhasznaloTipus");
                assert tipus != null;
                kosar.setVisible(!tipus.equals("Eladó cég/vállalat"));
            });
        } else {
            kosar.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fiok) {
            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(this, FiokActicity.class));
            } else {
                startActivity(new Intent(this, BejelentkezesActivity.class));
            }
        }

        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
        }
        if (item.getItemId() == R.id.kosar) {
            startActivity(new Intent(BoltOldalaActivity.this, KosarActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosar);

        FrameLayout rootVieww = (FrameLayout) menuItem.getActionView();
        FrameLayout kor = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo);
        TextView korSzamlalo = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo_text);
        if (kosarLista != null && kosarLista.size() != 0) {
            kor.setVisibility(View.VISIBLE);
            korSzamlalo.setText(String.valueOf(kosarLista.size()));
        } else {
            kor.setVisibility(View.GONE);
        }
        rootVieww.setOnClickListener(view -> {
            onOptionsItemSelected(menuItem);
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onTermek(int position) {
        Intent intent = new Intent(BoltOldalaActivity.this, TermekOldalActivity.class);
        intent.putExtra("termekNeve", termekekListaja.get(position).getNev());
        intent.putExtra("termekSulya", termekekListaja.get(position).getTermekSulya());
        intent.putExtra("termekAra", termekekListaja.get(position).getAr());
        intent.putExtra("termekKepe", termekekListaja.get(position).getTermekKepe());
        intent.putExtra("termekKeszlet", termekekListaja.get(position).getRaktaronLevoMennyiseg());
        intent.putExtra("termekId", termekekListaja.get(position).getSajatId());
        intent.putExtra("uzletId", termekekListaja.get(position).getUzletId());
        intent.putExtra("ossztermekCollection", termekekListaja.get(position).getOsszTermekColectionId());

        startActivity(intent);
    }

    public void onVissza(View view) {
        finish();
        super.onBackPressed();
    }
}