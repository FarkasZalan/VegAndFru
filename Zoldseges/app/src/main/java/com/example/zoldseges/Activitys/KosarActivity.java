package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.DAOS.KosarAdapter;
import com.example.zoldseges.DAOS.KosarElem;
import com.example.zoldseges.DAOS.KosarIranyito;
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.DAOS.UzletAdapter;
import com.example.zoldseges.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class KosarActivity extends AppCompatActivity implements KosarIranyito {

    private FirebaseAuth auth;

    private FirebaseFirestore db;

    private ProgressBar progressKosar;
    private TextView betoltesKosar;
    private RecyclerView kosarElemei;
    private AppBarLayout appBarKosar;

    private KosarAdapter kosarAdapter;
    private ArrayList<KosarElem> kosarListaja;

    private RelativeLayout nincsTermekKosarbanLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kosar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Kosár");

        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;
                String tipus = value.getString("felhasznaloTipus");
                assert tipus != null;
                if (tipus.equals("Eladó cég/vállalat")) {
                    finish();
                    super.onBackPressed();
                }
            });
        }
        progressKosar = findViewById(R.id.progressKosar);
        betoltesKosar = findViewById(R.id.betoltesKosar);
        kosarElemei = findViewById(R.id.kosarElemei);
        appBarKosar = findViewById(R.id.appBarKosar);
        nincsTermekKosarbanLayout = findViewById(R.id.nincsTermekKosarbanLayout);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        kosarElemei.setLayoutManager(layoutManager);
        kosarElemei.setHasFixedSize(true);

        kosarListaja = new ArrayList<>();
        eltuntet();
        clearList();
        getDataFromFireBase();
    }

    private void getDataFromFireBase() {
        for (KosarElem kosarElem : kosarLista) {
            KosarElem elem = new KosarElem(kosarElem.getTermek(), kosarElem.getMennyiseg());
            kosarListaja.add(elem);
        }
        kosarAdapter = new KosarAdapter(getApplicationContext(), kosarListaja, KosarActivity.this);
        kosarElemei.setAdapter(kosarAdapter);
        megjelenit();
    }

    private void clearList() {
        if (kosarListaja != null) {
            kosarListaja.clear();

            if (kosarAdapter != null) {
                kosarAdapter.notifyDataSetChanged();
            }
        } else {
            kosarListaja = new ArrayList<>();
        }
    }

    public void eltuntet() {
        progressKosar.setVisibility(View.VISIBLE);
        betoltesKosar.setVisibility(View.VISIBLE);
        kosarElemei.setVisibility(View.GONE);
        appBarKosar.setVisibility(View.INVISIBLE);
        nincsTermekKosarbanLayout.setVisibility(View.GONE);

    }

    public void megjelenit() {
        if (kosarListaja.size() > 0) {
            kosarElemei.setVisibility(View.VISIBLE);
        } else {
            nincsTermekKosarbanLayout.setVisibility(View.VISIBLE);
        }
        progressKosar.setVisibility(View.GONE);
        betoltesKosar.setVisibility(View.GONE);
        appBarKosar.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kosar_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        eltuntet();
        clearList();
        getDataFromFireBase();
        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;
                String tipus = value.getString("felhasznaloTipus");
                assert tipus != null;
                if (tipus.equals("Eladó cég/vállalat")) {
                    finish();
                    super.onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fiokKosar) {
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSzerkesztes(int position, double ujMennyiseg) {
        boolean sikeresSzerkesztes = false;
        if (ujMennyiseg > 0) {
            if (ujMennyiseg <= kosarListaja.get(position).getTermek().getRaktaronLevoMennyiseg()) {
                for (KosarElem kosar : kosarLista) {
                    if (kosar.getTermek().getSajatId().equals(kosarListaja.get(position).getTermek().getSajatId())) {
                        kosar.setMennyiseg(ujMennyiseg);
                        Toast.makeText(getApplicationContext(), "Sikeresen frissítetted a kosaradat!", Toast.LENGTH_LONG).show();
                        sikeresSzerkesztes = true;
                        eltuntet();
                        clearList();
                        getDataFromFireBase();
                    }
                }
            } else {
                String keszlet;
                if (kosarListaja.get(position).getTermek().getTermekSulya() != -1.0) {
                    keszlet = kosarListaja.get(position).getTermek().getRaktaronLevoMennyiseg() + " kg";
                } else {
                    keszlet = kosarListaja.get(position).getTermek().getRaktaronLevoMennyiseg() + " db";
                }
                Toast.makeText(getApplicationContext(), "Maximum csak " + keszlet + "-ot rendelhetsz ebből a termékből!", Toast.LENGTH_LONG).show();
            }
        } else {
            onTorles(position);
        }
        return sikeresSzerkesztes;
    }

    @Override
    public void onTorles(int position) {
        AlertDialog.Builder torlesLaertBuilder = new AlertDialog.Builder(this);
        torlesLaertBuilder.setTitle("Törlés");
        torlesLaertBuilder.setIcon(R.mipmap.ic_launcher);
        torlesLaertBuilder.setMessage("Biztosan törölni szeretnéd a(z) " + kosarListaja.get(position).getTermek().getNev() + " termékedet a kosárból?");
        torlesLaertBuilder.setCancelable(true);

        AlertDialog torlesAlert = torlesLaertBuilder.create();

        torlesAlert.setButton(DialogInterface.BUTTON_POSITIVE, "Törlés", (dialog, which) -> {
            eltuntet();
            kosarLista.remove(position);
            clearList();
            getDataFromFireBase();
        });


        torlesAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Mégse", (dialog, which) -> torlesAlert.dismiss());
        torlesAlert.show();
        torlesAlert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
        torlesAlert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white, getTheme()));
    }

    @Override
    public void onTermek(int position) {
        Intent intent = new Intent(KosarActivity.this, TermekOldalActivity.class);
        intent.putExtra("termekNeve", kosarListaja.get(position).getTermek().getNev());
        intent.putExtra("termekSulya", kosarListaja.get(position).getTermek().getTermekSulya());
        intent.putExtra("termekAra", kosarListaja.get(position).getTermek().getAr());
        intent.putExtra("termekKepe", kosarListaja.get(position).getTermek().getTermekKepe());
        intent.putExtra("termekKeszlet", kosarListaja.get(position).getTermek().getRaktaronLevoMennyiseg());
        intent.putExtra("termekId", kosarListaja.get(position).getTermek().getSajatId());
        intent.putExtra("uzletId", kosarListaja.get(position).getTermek().getUzletId());
        intent.putExtra("ossztermekCollection", kosarListaja.get(position).getTermek().getOsszTermekColectionId());

        startActivity(intent);
    }

    @Override
    public void onFizeteshez() {
        Intent intent = new Intent(KosarActivity.this, FizetesActivity.class);
        startActivity(intent);
    }

    public void onVissza(View view) {
        finish();
        super.onBackPressed();
    }
}