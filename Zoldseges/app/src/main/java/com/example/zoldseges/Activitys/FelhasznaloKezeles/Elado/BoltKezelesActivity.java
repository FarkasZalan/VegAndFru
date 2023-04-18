package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

import static java.security.AccessController.getContext;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
import android.widget.Toast;

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
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

    private RelativeLayout elsoTermekLayout;
    private ImageView termekHozzaad;
    private AppBarLayout appBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_kezeles);


        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.termekekRecyclerview);
        elsoTermekLayout = findViewById(R.id.elsoTermekLayout);
        termekHozzaad = findViewById(R.id.termekHozzaad);
        kep = findViewById(R.id.kep1);
        plus = findViewById(R.id.plus);
        appBarLayout = findViewById(R.id.appBarLayout);
        betoltesTextBoltKezeles = findViewById(R.id.betoltesTextBoltKezeles);
        progressBarBoltKezeles = findViewById(R.id.progressBarBoltKezeles);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        auth = FirebaseAuth.getInstance();

        termekLista = new ArrayList<>();

        this.betoltesTextBoltKezeles.setText(R.string.betoltes);
        eltuntet();
        clearAll();
        getDataFromFirebase();
    }


    @Override
    protected void onResume() {
        super.onResume();
        eltuntet();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();

        termekLista = new ArrayList<>();
        this.betoltesTextBoltKezeles.setText(R.string.betoltes);
        clearAll();
        getDataFromFirebase();

    }

    private void eltuntet() {
        elsoTermekLayout.setVisibility(View.GONE);
        termekHozzaad.setVisibility(View.GONE);
        kep.setVisibility(View.INVISIBLE);
        progressBarBoltKezeles.setVisibility(View.VISIBLE);
        betoltesTextBoltKezeles.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        plus.setVisibility(View.GONE);
    }

    private void megjelenit() {
        progressBarBoltKezeles.setVisibility(View.GONE);
        betoltesTextBoltKezeles.setVisibility(View.GONE);
        kep.setVisibility(View.VISIBLE);
        plus.setVisibility(View.VISIBLE);
        if (termekLista.size() > 0) {
            elsoTermekLayout.setVisibility(View.GONE);
            termekHozzaad.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            elsoTermekLayout.setVisibility(View.VISIBLE);
        }
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
                    int color = getResources().getColor(R.color.white, getTheme());
                    appBarLayout.setBackgroundColor(color);
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
                                //megjelenit();
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
                            termek.setSajatId(adat.getId());
                            termekLista.add(termek);

                        }
                        termekLista.sort(Comparator.comparing(Termek::getNev));
                        termekAdapter = new TermekAdapter(getApplicationContext(), termekLista, BoltKezelesActivity.this);
                        recyclerView.setAdapter(termekAdapter);
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
    public void onItemMegtekint(int position) {
        //termek oldala
        Toast.makeText(getApplicationContext(), "Megtekintes", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onItemModosit(int position) {
        Intent intent = new Intent(BoltKezelesActivity.this, TermekSzerkeszteseActivity.class);
        intent.putExtra("termekNeve", termekLista.get(position).getNev());
        intent.putExtra("termekSulya", termekLista.get(position).getTermekSulya());
        intent.putExtra("termekegysegara", termekLista.get(position).getAr());
        intent.putExtra("termekDbSZama", termekLista.get(position).getRaktaronLevoMennyiseg());
        intent.putExtra("termekKepe", termekLista.get(position).getTermekKepe());
        intent.putExtra("uzletId", termekLista.get(position).getUzletId());
        intent.putExtra("termekId", termekLista.get(position).getSajatId());

        startActivity(intent);
    }

    @Override
    public void onItemTorles(int position) {
        AlertDialog.Builder torlesLaertBuilder = new AlertDialog.Builder(this);
        torlesLaertBuilder.setTitle("Törlés");
        torlesLaertBuilder.setIcon(R.mipmap.ic_launcher);
        torlesLaertBuilder.setMessage("Biztosan törölni szeretnéd a(z) " + termekLista.get(position).getNev() + " termékedet?");
        torlesLaertBuilder.setCancelable(true);

        AlertDialog torlesAlert = torlesLaertBuilder.create();

        torlesAlert.setButton(DialogInterface.BUTTON_POSITIVE, "Törlés", (dialog, which) -> {
            eltuntet();
            String id = termekLista.get(position).getSajatId();
            DocumentReference torloRef = db.collection("uzletek").document(uzletId).collection("termekek").document(id);
            torloRef.delete().addOnCompleteListener(torles -> {
                if (!torles.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Váratlan hiba történt!", Toast.LENGTH_LONG).show();
                }
            });
            if (!termekLista.get(position).getTermekKepe().isEmpty()) {
                StorageReference kepTorlese = FirebaseStorage.getInstance().getReferenceFromUrl(termekLista.get(position).getTermekKepe());
                kepTorlese.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> kepTorles) {
                        if (kepTorles.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sikeres eltávolítás!", Toast.LENGTH_LONG).show();
                            betoltesTextBoltKezeles.setText(R.string.torles);
                            eltuntet();
                            clearAll();
                            getDataFromFirebase();
                            torlesAlert.dismiss();
                        }
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Sikeres eltávolítás!", Toast.LENGTH_LONG).show();
                eltuntet();
                clearAll();
                getDataFromFirebase();
                torlesAlert.dismiss();
            }
        });

        torlesAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Mégse", (dialog, which) -> torlesAlert.dismiss());
        torlesAlert.show();
        torlesAlert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
        torlesAlert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white, getTheme()));
    }
}