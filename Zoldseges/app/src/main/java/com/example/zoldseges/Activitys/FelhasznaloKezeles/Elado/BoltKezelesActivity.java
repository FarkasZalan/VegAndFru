package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.DAOS.TermekAdapter;
import com.example.zoldseges.DAOS.TermekValaszto;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class BoltKezelesActivity extends AppCompatActivity implements TermekValaszto {

    RecyclerView recyclerView;
    FirebaseFirestore db;

    private ArrayList<Termek> termekLista;
    String uzletId;
    FirebaseAuth auth;
    private TermekAdapter termekAdapter;

    DatabaseReference databaseReference;
    CollectionReference uzletReference;

    private ImageView kep;
    private RelativeLayout plus;
    private TextView betoltesTextBoltKezeles;
    private ProgressBar progressBarBoltKezeles;
    private int listaSzam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_kezeles);

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.termekekRecyclerview);
        kep = findViewById(R.id.kep1);
        plus = findViewById(R.id.plus);
        betoltesTextBoltKezeles = findViewById(R.id.betoltesTextBoltKezeles);
        progressBarBoltKezeles = findViewById(R.id.progressBarBoltKezeles);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();

        termekLista = new ArrayList<>();
        clearAll();
        eltuntet();
        getDataFromFirebase();
    }


    @Override
    protected void onResume() {
        super.onResume();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();


        termekLista = new ArrayList<>();
        clearAll();
        eltuntet();
        getDataFromFirebase();

    }

    private void eltuntet() {
        kep.setVisibility(View.INVISIBLE);
        progressBarBoltKezeles.setVisibility(View.VISIBLE);
        betoltesTextBoltKezeles.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        plus.setVisibility(View.GONE);
    }

    private void megjelenit() {
        kep.setVisibility(View.VISIBLE);
        progressBarBoltKezeles.setVisibility(View.GONE);
        betoltesTextBoltKezeles.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        plus.setVisibility(View.VISIBLE);
    }

    private void getDataFromFirebase() {
        DocumentReference felhasznaloReference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        felhasznaloReference.addSnapshotListener((value, error) -> {
            assert value != null;
            uzletId = value.getString("uzletId");
            DocumentReference uzletReferenceKephez;
            uzletReferenceKephez = db.collection("uzletek").document(uzletId);
            uzletReferenceKephez.addSnapshotListener((uzlet, error1) -> {
                assert uzlet != null;
                Uri uri;
                if (Objects.requireNonNull(uzlet.getString("boltKepe")).isEmpty() || Objects.equals(uzlet.getString("boltKepe"), "null") || uzlet.getString("boltKepe") == null) {
                    uri = null;
                    kep.setScaleType(ImageView.ScaleType.CENTER);
                } else {
                    uri = Uri.parse(uzlet.getString("boltKepe"));
                    kep.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }

                try {
                    if (!this.isFinishing()) {
                        Glide.with(BoltKezelesActivity.this).load(uri).placeholder(R.drawable.grocery_store).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                megjelenit();
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                                return false;
                            }
                        }).into(kep);
                    }
                } catch (Exception e) {
                    Glide.with(BoltKezelesActivity.this).load(R.drawable.grocery_store).into(kep);
                }
            });
            uzletReference = db.collection("uzletek").document(uzletId).collection("termekek");
            uzletReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        clearAll();
                        for (QueryDocumentSnapshot adat : task.getResult()) {
                            Termek termek = new Termek();

                            termek.setTermekKepe(adat.getString("termekKepe"));
                            termek.setNev(adat.getString("termekNeve"));
                            termek.setUzletId(adat.getString("uzletId"));
                            termek.setTermekSulya(Objects.requireNonNull(adat.getDouble("termekSulya")));
                            termek.setAr(Objects.requireNonNull(adat.getDouble("termekAra")));
                            termek.setRaktaronLevoMennyiseg(Objects.requireNonNull(adat.getDouble("raktaronLevoMennyiseg")));
                            termekLista.add(termek);
                        }
                        termekLista.sort(Comparator.comparing(Termek::getNev));
                        termekAdapter = new TermekAdapter(getApplicationContext(), termekLista, BoltKezelesActivity.this);
                        recyclerView.setAdapter(termekAdapter);
                        termekAdapter.notifyDataSetChanged();
                        listaSzam = termekLista.size();
                        megjelenit();
                    }
                }
            });
        });
    }

    private void clearAll() {
        if (termekLista != null) {
            termekLista.clear();

            if (termekAdapter != null) {
                termekAdapter.notifyDataSetChanged();
            }
        } else {
            termekLista = new ArrayList<>();
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
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onUjTermek(View view) {
        startActivity(new Intent(this, UjTermekActivity.class));
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(BoltKezelesActivity.this, TermekSzerkeszteseActivity.class);
        intent.putExtra("termekNeve", termekLista.get(position).getNev());
        intent.putExtra("termekSulya", termekLista.get(position).getTermekSulya());
        intent.putExtra("termekegysegara", termekLista.get(position).getAr());
        intent.putExtra("termekDbSZama", termekLista.get(position).getRaktaronLevoMennyiseg());
        intent.putExtra("termekKepe", termekLista.get(position).getTermekKepe());
        intent.putExtra("uzletId", termekLista.get(position).getUzletId());

        startActivity(intent);
    }
}