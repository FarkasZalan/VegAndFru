package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.Activitys.FelhasznaloKezeles.AdataimActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.AszfActicity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.DAOS.KosarElem;
import com.example.zoldseges.DAOS.Nyugta;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class FizetesActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MenuItem kosar;
    private FirebaseFirestore db;

    private ProgressBar progressFizetes;
    private TextView betoltesFizetes;
    private ImageView fizetesKep;
    private EditText megrendeloNeve;
    private EditText megrendeloEmailCime;
    private EditText megrendeloTelefonszama;
    private EditText megrendeloSzallitasiCime;
    private EditText megrendeloCegAdoszama;
    private EditText megrendeloCegSzekhelye;
    private TextView kosarTartalma;
    private TextView vegosszeg;
    private TextView adatModosit;
    private Button rendelesLeadasa;
    private DocumentReference rendeloRef;
    private double fizetendo = 0;

    String kosarEleme;
    private TextView rendelendoTermekText;
    private boolean cegE;

    private CheckBox aszfElfogad;

    private String uzletId;
    String uzletNeve;
    String uzletKepe;
    String uzletEmailCime;
    String uzletSzekhely;
    String uzletTelefonszama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fizetes);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(FizetesActivity.this, FooldalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        getSupportActionBar().setTitle("Fizetés");

        progressFizetes = findViewById(R.id.progressFizetes);
        betoltesFizetes = findViewById(R.id.betoltesFizetes);
        fizetesKep = findViewById(R.id.fizetesKep);
        megrendeloNeve = findViewById(R.id.megrendeloNeve);
        megrendeloEmailCime = findViewById(R.id.megrendeloEmailCime);
        megrendeloTelefonszama = findViewById(R.id.megrendeloTelefonszama);
        megrendeloSzallitasiCime = findViewById(R.id.megrendeloSzallitasiCime);
        megrendeloCegAdoszama = findViewById(R.id.megrendeloCegAdoszama);
        megrendeloCegSzekhelye = findViewById(R.id.megrendeloCegSzekhelye);
        kosarTartalma = findViewById(R.id.kosarTartalma);
        vegosszeg = findViewById(R.id.vegosszeg);
        rendelesLeadasa = findViewById(R.id.rendelesLeadasa);
        rendelendoTermekText = findViewById(R.id.rendelendoTermekText);
        aszfElfogad = findViewById(R.id.aszfElfogad);
        adatModosit = findViewById(R.id.adatModosit);
        vegosszeg.setText("");
        kosarTartalma.setText("");
        eltuntet();
        adatModosit();
        aszf();
        rendeloRef = db.collection("felhasznalok").document(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getUid()));
        cegE = adatokFeltolt();
        vasarlasAdatai();


    }

    private void adatModosit() {
        SpannableString bejelentkezes = new SpannableString("Adatok módosítása");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(FizetesActivity.this, FiokActicity.class));
            }
        };
        bejelentkezes.setSpan(clickableSpan, 7, 17, 0);
        bejelentkezes.setSpan(new URLSpan(""), 7, 17, 0);
        bejelentkezes.setSpan(new ForegroundColorSpan(ContextCompat.getColor(FizetesActivity.this, R.color.purple_500)), 7, 17, 0);

        adatModosit.setMovementMethod(LinkMovementMethod.getInstance());

        adatModosit.setText(bejelentkezes, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(FizetesActivity.this, FooldalActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        aszf();
        if (kosarLista.size() == 0) {
            finish();
            super.onBackPressed();
            super.onBackPressed();
        }
    }

    private boolean adatokFeltolt() {
        rendeloRef.addSnapshotListener((felhasznalo, error) -> {
            assert felhasznalo != null;
            if (Objects.equals(felhasznalo.getString("felhasznaloTipus"), "Cég/Vállalat")) {
                megrendeloCegAdoszama.setVisibility(View.VISIBLE);
                megrendeloCegSzekhelye.setVisibility(View.VISIBLE);
                megrendeloNeve.setHint("Üzlet neve*");
                megrendeloCegAdoszama.setText(felhasznalo.getString("adoszam"));
                megrendeloCegSzekhelye.setText(felhasznalo.getString("szekhely"));
                megrendeloNeve.setText(felhasznalo.getString("cegNev"));
                cegE = true;
            } else {
                megrendeloCegAdoszama.setVisibility(View.GONE);
                megrendeloCegSzekhelye.setVisibility(View.GONE);
                megrendeloNeve.setHint("Megrendelő neve*");
                megrendeloNeve.setText(felhasznalo.getString("nev"));
                cegE = false;
            }
            megrendeloEmailCime.setText(felhasznalo.getString("email"));
            megrendeloTelefonszama.setText(felhasznalo.getString("telefonszam"));
            megrendeloSzallitasiCime.setText(felhasznalo.getString("lakcim"));
        });
        return cegE;
    }

    private void aszf() {
        SpannableString bejelentkezes = new SpannableString("Elfogadom az ÁSZF-t");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(FizetesActivity.this, AszfActicity.class));
            }
        };
        bejelentkezes.setSpan(clickableSpan, 13, 17, 0);
        bejelentkezes.setSpan(new URLSpan(""), 13, 17, 0);
        bejelentkezes.setSpan(new ForegroundColorSpan(ContextCompat.getColor(FizetesActivity.this, R.color.purple_500)), 13, 17, 0);

        aszfElfogad.setMovementMethod(LinkMovementMethod.getInstance());

        aszfElfogad.setText(bejelentkezes, TextView.BufferType.SPANNABLE);
    }

    private void vasarlasAdatai() {
        fizetendo = 0;
        for (KosarElem elem : kosarLista) {
            double ara = elem.getTermek().getAr() * elem.getMennyiseg();
            int kerekitveAr;
            if ((int) Math.round(ara) <= 0) {
                kerekitveAr = 1;
            } else {
                kerekitveAr = (int) Math.round(ara);
            }
            if (elem.getMennyiseg() % 1 == 0) {
                kosarEleme = (int) elem.getMennyiseg() + "x " + elem.getTermek().getNev() + "  " + kerekitveAr + " Ft" + "\n";
            } else {
                kosarEleme = elem.getMennyiseg() + "x " + elem.getTermek().getNev() + "  " + kerekitveAr + " Ft" + "\n";
            }
            fizetendo += elem.getTermek().getAr() * elem.getMennyiseg();
            kosarTartalma.append(kosarEleme);
        }
        String vegosszegText;
        fizetendo += 5000;
        if (fizetendo % 1 == 0) {
            vegosszegText = "Végösszeg: " + ((int) Math.round(fizetendo)) + " Ft\n(+5000 Ft szállítási költség)";
        } else {
            vegosszegText = "Végösszeg: " + Math.round(fizetendo) + " Ft\n(+5000 Ft szállítási költség)";
        }

        for (KosarElem elem : kosarLista) {
            uzletId = elem.getTermek().getUzletId();
        }
        DocumentReference uzlez = db.collection("uzletek").document(uzletId);
        uzlez.addSnapshotListener((value, error) -> {
            assert value != null;
            uzletKepe = value.getString("boltKepe");
            uzletNeve = value.getString("cegNev");
            uzletSzekhely = value.getString("szekhely");
            String tulajId = value.getString("tulajId");
            assert tulajId != null;
            DocumentReference tulaj = db.collection("felhasznalok").document(tulajId);
            tulaj.addSnapshotListener((value1, error1) -> {
                assert value1 != null;
                uzletEmailCime = value1.getString("email");
                uzletTelefonszama = value1.getString("telefonszam");
            });
            megjelenit();
        });
        vegosszeg.setText(vegosszegText);
    }

    public void eltuntet() {
        progressFizetes.setVisibility(View.VISIBLE);
        betoltesFizetes.setVisibility(View.VISIBLE);
        fizetesKep.setVisibility(View.INVISIBLE);
        megrendeloNeve.setVisibility(View.GONE);
        megrendeloEmailCime.setVisibility(View.GONE);
        megrendeloTelefonszama.setVisibility(View.GONE);
        megrendeloSzallitasiCime.setVisibility(View.GONE);
        megrendeloCegAdoszama.setVisibility(View.GONE);
        megrendeloCegSzekhelye.setVisibility(View.GONE);
        kosarTartalma.setVisibility(View.GONE);
        vegosszeg.setVisibility(View.GONE);
        rendelesLeadasa.setVisibility(View.GONE);
        rendelendoTermekText.setVisibility(View.GONE);
        adatModosit.setVisibility(View.GONE);
        aszfElfogad.setVisibility(View.GONE);
    }

    public void megjelenit() {
        progressFizetes.setVisibility(View.GONE);
        betoltesFizetes.setVisibility(View.GONE);
        fizetesKep.setVisibility(View.VISIBLE);
        megrendeloNeve.setVisibility(View.VISIBLE);
        megrendeloEmailCime.setVisibility(View.VISIBLE);
        megrendeloTelefonszama.setVisibility(View.VISIBLE);
        megrendeloSzallitasiCime.setVisibility(View.VISIBLE);
        adatokFeltolt();
        kosarTartalma.setVisibility(View.VISIBLE);
        vegosszeg.setVisibility(View.VISIBLE);
        rendelesLeadasa.setVisibility(View.VISIBLE);
        rendelendoTermekText.setVisibility(View.VISIBLE);
        adatModosit.setVisibility(View.VISIBLE);
        aszfElfogad.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_menu, menu);
        View view = menu.findItem(R.id.kosar).getActionView();
        kosar = menu.findItem(R.id.kosar);
        kosar.setVisible(false);
        view.setOnClickListener(v -> startActivity(new Intent(FizetesActivity.this, KosarActivity.class)));
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
        return super.onOptionsItemSelected(item);
    }

    public void onRendelesLeadasa(View view) {
        if (aszfElfogad.isChecked()) {
            if ((cegE && !megrendeloNeve.getText().toString().isEmpty() && !megrendeloEmailCime.getText().toString().isEmpty() && !megrendeloTelefonszama.getText().toString().isEmpty() && !megrendeloSzallitasiCime.getText().toString().isEmpty() && !megrendeloCegAdoszama.getText().toString().isEmpty() && !megrendeloCegSzekhelye.getText().toString().isEmpty()) ||
                    ((!cegE && !megrendeloNeve.getText().toString().isEmpty() && !megrendeloEmailCime.getText().toString().isEmpty() && !megrendeloTelefonszama.getText().toString().isEmpty() && !megrendeloSzallitasiCime.getText().toString().isEmpty()))) {
                String nev = megrendeloNeve.getText().toString();
                String email = megrendeloEmailCime.getText().toString();
                String telefon = megrendeloTelefonszama.getText().toString();
                String szallitasiCim = megrendeloSzallitasiCime.getText().toString();
                String adoszam = megrendeloCegAdoszama.getText().toString();
                String szekhely = megrendeloCegSzekhelye.getText().toString();


                DocumentReference nyugtak = db.collection("nyugtak").document();
                Map<String, String> nyugta;

                String osszeg = String.valueOf((int) Math.round(fizetendo));
                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                String idopont = dateFormat.format(date);
                Nyugta ujNyugta = new Nyugta(nyugtak.getId(), osszeg, idopont, uzletId, kosarTartalma.getText().toString(), Objects.requireNonNull(auth.getCurrentUser()).getUid());
                ujNyugta.setUzletKepe(uzletKepe);
                ujNyugta.setUzletNeve(uzletNeve);
                ujNyugta.setUzletEmail(uzletEmailCime);
                ujNyugta.setUzletTelefon(uzletTelefonszama);
                ujNyugta.setUzletSzekhely(uzletSzekhely);
                ujNyugta.setNev(nev);
                ujNyugta.setEmail(email);
                ujNyugta.setTelefonszam(telefon);
                ujNyugta.setSzallitasiCim(szallitasiCim);
                ujNyugta.setAdoszam(adoszam);
                ujNyugta.setSzekhely(szekhely);
                nyugta = ujNyugta.ujNyugta(ujNyugta);
                eltuntet();
                nyugtak.set(nyugta).addOnCompleteListener(fizetes -> {
                    if (fizetes.isSuccessful()) {
                        onNyugta(nyugta);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Nem maradhatnak mezők üresen!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Előbb el kell fogadnod az Általános Szerződési Feltételeket!", Toast.LENGTH_LONG).show();
        }
    }

    private void onNyugta(Map<String, String> nyugta) {
        Intent intent = new Intent(FizetesActivity.this, NyugtaActivity.class);
        for (Map.Entry<String, String> adatok : nyugta.entrySet()) {
            if (adatok.getKey().equals("nyugtaId")) {
                intent.putExtra("nyugtaId", adatok.getValue());
                intent.putExtra("fizetesUtan", true);
            }
        }
        kosarLista.clear();
        startActivity(intent);
    }
}