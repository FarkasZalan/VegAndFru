package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class NyugtaActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private TextView rendeltTermekek;
    private TextView eladoAdatai;
    private TextView altalanosAdatok;
    private TextView sikeresRendelesText;
    private Button nyugtaRendben;
    private Button nyugtaVissza;
    String nyugtaId;
    boolean fizetesUtan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyugta);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        nyugtaRendben = findViewById(R.id.nyugtaRendben);
        nyugtaVissza = findViewById(R.id.nyugtaVissza);
        rendeltTermekek = findViewById(R.id.rendeltTermekek);
        sikeresRendelesText = findViewById(R.id.sikeresRendelesText);
        eladoAdatai = findViewById(R.id.eladoAdatai);
        altalanosAdatok = findViewById(R.id.altalanosAdatok);
        rendeltTermekek.setText("");
        eladoAdatai.setText("");

        nyugtaId = getIntent().getStringExtra("nyugtaId");
        fizetesUtan = getIntent().getBooleanExtra("fizetesUtan", false);
        if (fizetesUtan) {
            sikeresRendelesText.setText(R.string.sikeres_rendeles);
            nyugtaRendben.setVisibility(View.VISIBLE);
            nyugtaVissza.setVisibility(View.GONE);
        } else {
            nyugtaRendben.setVisibility(View.GONE);
            nyugtaVissza.setVisibility(View.VISIBLE);
            sikeresRendelesText.setText("Nyugta");
        }
        getFromData();
        getSupportActionBar().setTitle("Fizetés");
    }

    private void getFromData() {


        DocumentReference nyugta = db.collection("nyugtak").document(nyugtaId);
        nyugta.addSnapshotListener((nyugtaRef, error) -> {
            assert nyugtaRef != null;
            String rendeloId = nyugtaRef.getString("rendeloId");

            assert rendeloId != null;
            DocumentReference rendelo = db.collection("felhasznalok").document(rendeloId);
            rendelo.addSnapshotListener((felhasznalo, errorFelhasznalo) -> {
                if (!rendeltTermekek.getText().toString().contains("Termékek:")) {
                    rendeltTermekek.append("Termékek: ");
                }
                if (!rendeltTermekek.getText().toString().contains(Objects.requireNonNull(nyugtaRef.getString("termkek")))) {
                    rendeltTermekek.append("\n\n" + Objects.requireNonNull(nyugtaRef.getString("termkek")));
                }
                if (!rendeltTermekek.getText().toString().contains("Rendelő adatai:")) {
                    rendeltTermekek.append("\n\nRendelő adatai: ");
                }
                assert felhasznalo != null;
                if (!rendeltTermekek.getText().toString().contains("Rendelő email címe: " + felhasznalo.getString("email"))) {
                    rendeltTermekek.append("\n\n\nRendelő email címe: " + felhasznalo.getString("email"));
                }
                if (Objects.equals(felhasznalo.getString("felhasznaloTipus"), "magánszemély")) {
                    if (!rendeltTermekek.getText().toString().contains("Rendelő neve: " + felhasznalo.getString("nev"))) {
                        rendeltTermekek.append("\n\nRendelő neve: " + felhasznalo.getString("nev"));
                    }
                } else {
                    if (!rendeltTermekek.getText().toString().contains("Rendelő neve: " + felhasznalo.getString("cegNev"))) {
                        rendeltTermekek.append("\n\nRendelő neve: " + felhasznalo.getString("cegNev"));
                    }
                    if (!rendeltTermekek.getText().toString().contains("Rendelő adószáma: " + felhasznalo.getString("adoszam"))) {
                        rendeltTermekek.append("\n\nRendelő adószáma: " + felhasznalo.getString("adoszam"));
                    }
                    if (!rendeltTermekek.getText().toString().contains("Rendelő számlázási címe: " + felhasznalo.getString("szekhely"))) {
                        rendeltTermekek.append("\n\nRendelő számlázási címe: " + felhasznalo.getString("szekhely"));
                    }
                }
                if (!rendeltTermekek.getText().toString().contains("Rendelő telefonszáma: " + felhasznalo.getString("telefonszam"))) {
                    rendeltTermekek.append("\n\nRendelő telefonszáma: " + felhasznalo.getString("telefonszam"));
                }
                if (!rendeltTermekek.getText().toString().contains("Szállítási cím: " + felhasznalo.getString("lakcim"))) {
                    rendeltTermekek.append("\n\nSzállítási cím: " + felhasznalo.getString("lakcim"));
                }
            });

            DocumentReference uzlet = db.collection("uzletek").document(Objects.requireNonNull(nyugtaRef.getString("uzletId")));
            uzlet.addSnapshotListener((value, errorUzlet) -> {
                assert value != null;
                DocumentReference tulaj = db.collection("felhasznalok").document(Objects.requireNonNull(value.getString("tulajId")));
                tulaj.addSnapshotListener((uzletTulaj, error1) -> {
                    if (!eladoAdatai.getText().toString().contains("Üzlet adatai:")) {
                        eladoAdatai.append("\n\nÜzlet adatai: ");
                    }
                    assert uzletTulaj != null;
                    if (!eladoAdatai.getText().toString().contains("Rendelő neve: " + uzletTulaj.getString("cegNev"))) {
                        eladoAdatai.append("\n\n\nÜzlet neve: " + uzletTulaj.getString("cegNev"));
                    }
                    if (!eladoAdatai.getText().toString().contains("Üzlet email címe: " + uzletTulaj.getString("email"))) {
                        eladoAdatai.append("\n\nÜzlet email címe: " + uzletTulaj.getString("email"));
                    }
                    if (!eladoAdatai.getText().toString().contains("Értesítési címe: " + uzletTulaj.getString("szekhely"))) {
                        eladoAdatai.append("\n\nÉrtesítési címe: " + uzletTulaj.getString("szekhely"));
                    }
                    if (!eladoAdatai.getText().toString().contains("Üzlet telefonszáma: " + uzletTulaj.getString("telefonszam"))) {
                        eladoAdatai.append("\n\nÜzlet telefonszáma: " + uzletTulaj.getString("telefonszam"));
                    }
                    if (!altalanosAdatok.getText().toString().contains("\nRendelés időpontja: " + Objects.requireNonNull(nyugtaRef.getString("idopont")))) {
                        altalanosAdatok.append("\n\n\nRendelés időpontja: " + Objects.requireNonNull(nyugtaRef.getString("idopont")));
                    }
                    if (!altalanosAdatok.getText().toString().contains("\nRendelés azonosító: " + nyugtaId)) {
                        altalanosAdatok.append("\n\nRendelés azonosító: " + nyugtaId);
                    }
                    if (!altalanosAdatok.getText().toString().contains("\nVégösszeg: " + Objects.requireNonNull(nyugtaRef.getString("vegosszeg")) + " Ft")) {
                        altalanosAdatok.append("\n\nVégösszeg: " + Objects.requireNonNull(nyugtaRef.getString("vegosszeg")) + " Ft");
                    }
                });
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_menu, menu);
        View view = menu.findItem(R.id.kosar).getActionView();
        MenuItem kosar = menu.findItem(R.id.kosar);
        kosar.setVisible(kosarLista.size() != 0);
        view.setOnClickListener(v -> startActivity(new Intent(NyugtaActivity.this, KosarActivity.class)));
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
            startActivity(new Intent(NyugtaActivity.this, KosarActivity.class));
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
        rootVieww.setOnClickListener(view -> onOptionsItemSelected(menuItem));

        return super.onPrepareOptionsMenu(menu);
    }

    public void onVissza(View view) {

        super.onBackPressed();
        super.onBackPressed();
        finish();
    }

    public void onFooldal(View view) {
        finish();
        Intent intent = new Intent(NyugtaActivity.this, FooldalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}