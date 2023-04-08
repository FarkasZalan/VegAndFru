package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.DAOS.Felhasznalo;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdataimActivity extends AppCompatActivity {
    TextView nevV;
    TextView emailV;
    TextView telSzamV;
    TextView szallitasiCimV;
    TextView jelszoV;
    TextView jelszoUjraV;
    TextView szekhelyV;
    TextView cegNevV;
    TextView adoszamV;

    TextView cegnevLabel;
    TextView adoszamLabel;
    TextView szekhelyLabel;
    TextView nevLabel;
    TextView emailLabel;
    TextView telszamLabel;
    TextView szalitasiCimLabell;
    TextView paswLabel;
    TextView paswRepeatLabel;
    ImageView termekKepBeallitasModosit;
    TextView termekKepCimModosit;

    ProgressBar progressBarModositas;
    TextView modositasText;
    Button adatokSzerkesztese;

    private DocumentReference reference;
    private FirebaseAuth auth;
    private FirebaseUser felhasznalo;
    private FirebaseFirestore db;

    private Map<String, Object> ujFelhasznalo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adataim);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        felhasznalo = auth.getCurrentUser();

        nevV = findViewById(R.id.nevAdatSzerkezto);
        progressBarModositas = findViewById(R.id.progressBarModositas);
        modositasText = findViewById(R.id.modositasText);
        adatokSzerkesztese = findViewById(R.id.adatokSzerkesztese);
        emailV = findViewById(R.id.emailAdatSzerkezto);
        telSzamV = findViewById(R.id.telefonszamAdatSzerkezto);
        szallitasiCimV = findViewById(R.id.lakcimAdatSzerkezto);
        jelszoV = findViewById(R.id.jelszoAdatSzerkezto);
        jelszoUjraV = findViewById(R.id.jelszoUjraAdatSzerkezto);
        szekhelyV = findViewById(R.id.szekhelyAdatSzerkezto);
        cegNevV = findViewById(R.id.cegNevAdatSzerkezto);
        adoszamV = findViewById(R.id.adoszamAdatSzerkezto);
        cegnevLabel = findViewById(R.id.cegnevLabel);
        adoszamLabel = findViewById(R.id.adoszamLabel);
        szekhelyLabel = findViewById(R.id.szekhelyLabel);

        nevLabel = findViewById(R.id.nevLabel);
        emailLabel = findViewById(R.id.emailLabel);
        telszamLabel = findViewById(R.id.telszamLabel);
        szalitasiCimLabell = findViewById(R.id.szalitasiCimLabell);
        paswLabel = findViewById(R.id.paswLabel);
        paswRepeatLabel = findViewById(R.id.paswRepeatLabel);

        termekKepBeallitasModosit = findViewById(R.id.termekKepBeallitasModosit);
        termekKepCimModosit = findViewById(R.id.termekKepCimModosit);


        reference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        cegVagyElado();
    }

    public void cegVagyElado() {
        reference.addSnapshotListener(this, (value, error) -> {
            assert value != null;
            if (Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat") || Objects.equals(value.getString("felhasznaloTipus"), "Cég/Vállalat")) {
                szekhelyV.setVisibility(View.VISIBLE);
                cegNevV.setVisibility(View.VISIBLE);
                adoszamV.setVisibility(View.VISIBLE);
                cegnevLabel.setVisibility(View.VISIBLE);
                adoszamLabel.setVisibility(View.VISIBLE);
                szekhelyLabel.setVisibility(View.VISIBLE);
                termekKepBeallitasModosit.setVisibility(View.GONE);
                termekKepCimModosit.setVisibility(View.GONE);
                if (Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat")) {
                    termekKepBeallitasModosit.setVisibility(View.VISIBLE);
                    termekKepCimModosit.setVisibility(View.VISIBLE);
                }

            } else {
                szekhelyV.setVisibility(View.GONE);
                cegNevV.setVisibility(View.GONE);
                adoszamV.setVisibility(View.GONE);
                cegnevLabel.setVisibility(View.GONE);
                adoszamLabel.setVisibility(View.GONE);
                szekhelyLabel.setVisibility(View.GONE);
                termekKepBeallitasModosit.setVisibility(View.GONE);
                termekKepCimModosit.setVisibility(View.GONE);
            }
            nevV.setText(value.getString("nev"));
            emailV.setText(value.getString("email"));
            telSzamV.setText(value.getString("telefonszam"));
            szallitasiCimV.setText(value.getString("lakcim"));
            szekhelyV.setText(value.getString("szekhely"));
            cegNevV.setText(value.getString("cegNev"));
            adoszamV.setText(value.getString("adoszam"));
        });
    }

    public void megjelenit() {
        this.progressBarModositas.setVisibility(View.GONE);
        this.modositasText.setVisibility(View.GONE);

        this.nevV.setVisibility(View.VISIBLE);
        this.emailV.setVisibility(View.VISIBLE);
        this.telSzamV.setVisibility(View.VISIBLE);
        this.szallitasiCimV.setVisibility(View.VISIBLE);
        this.jelszoV.setVisibility(View.VISIBLE);
        this.jelszoUjraV.setVisibility(View.VISIBLE);
        this.adatokSzerkesztese.setVisibility(View.VISIBLE);

        this.nevLabel.setVisibility(View.VISIBLE);
        this.emailLabel.setVisibility(View.VISIBLE);
        this.telszamLabel.setVisibility(View.VISIBLE);
        this.szalitasiCimLabell.setVisibility(View.VISIBLE);
        this.paswLabel.setVisibility(View.VISIBLE);
        this.paswRepeatLabel.setVisibility(View.VISIBLE);

        cegVagyElado();
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

    public void onVissza(View view) {
        super.onBackPressed();
    }


    public void onAdatSzerkesztes(View view) {
        String ujEmail = emailV.getText().toString();
        String ujJelszo = jelszoV.getText().toString();
        String ujJelszoUjra = jelszoUjraV.getText().toString();
        String ujNev = nevV.getText().toString();
        String ujTelefon = telSzamV.getText().toString();
        String ujSzallitasiCim = szallitasiCimV.getText().toString();
        String ujCegNev = cegNevV.getText().toString();
        String ujSzekhely = szekhelyV.getText().toString();
        String ujAdoszam = adoszamV.getText().toString();
        AtomicBoolean voltHiba = new AtomicBoolean(false);


        if (((!ujEmail.isEmpty() && !ujNev.isEmpty() && !ujTelefon.isEmpty() && !ujSzallitasiCim.isEmpty() && !ujCegNev.isEmpty() && !ujSzekhely.isEmpty() && !ujAdoszam.isEmpty()) && cegNevV.getVisibility() == View.VISIBLE) || ((!ujEmail.isEmpty() && !ujNev.isEmpty() && !ujTelefon.isEmpty() && !ujSzallitasiCim.isEmpty()) && cegNevV.getVisibility() == View.GONE)) {
            if (isEmailValid(ujEmail)) {
                this.progressBarModositas.setVisibility(View.VISIBLE);
                this.modositasText.setVisibility(View.VISIBLE);
                this.nevV.setVisibility(View.GONE);
                this.emailV.setVisibility(View.GONE);
                this.telSzamV.setVisibility(View.GONE);
                this.szallitasiCimV.setVisibility(View.GONE);
                this.jelszoV.setVisibility(View.GONE);
                this.jelszoUjraV.setVisibility(View.GONE);
                this.adatokSzerkesztese.setVisibility(View.GONE);
                this.szekhelyV.setVisibility(View.GONE);
                this.cegNevV.setVisibility(View.GONE);
                this.adoszamV.setVisibility(View.GONE);
                this.termekKepBeallitasModosit.setVisibility(View.GONE);
                this.termekKepCimModosit.setVisibility(View.GONE);

                this.cegnevLabel.setVisibility(View.GONE);
                this.nevLabel.setVisibility(View.GONE);
                this.emailLabel.setVisibility(View.GONE);
                this.telszamLabel.setVisibility(View.GONE);
                this.szalitasiCimLabell.setVisibility(View.GONE);
                this.paswLabel.setVisibility(View.GONE);
                this.paswRepeatLabel.setVisibility(View.GONE);
                this.adoszamLabel.setVisibility(View.GONE);
                this.szekhelyLabel.setVisibility(View.GONE);
                reference.addSnapshotListener((value, error) -> {
                    assert value != null;
                    Felhasznalo friss = new Felhasznalo(ujNev, ujEmail, "", ujTelefon, ujSzallitasiCim, ujCegNev, ujAdoszam, ujSzekhely, value.getString("felhasznaloTipus"));
                    ujFelhasznalo = friss.ujFelhasznalo(friss);
                });
                String regiEmail = felhasznalo.getEmail();

                felhasznalo.updateEmail(ujEmail).addOnCompleteListener(task -> {
                    assert regiEmail != null;
                    if (task.isSuccessful()) {

                        if (!jelszoV.getText().toString().isEmpty() || !jelszoUjraV.getText().toString().isEmpty()) {
                            if (ujJelszoUjra.equals(ujJelszo)) {
                                if (ujJelszo.length() >= 6) {
                                    felhasznalo.updatePassword(ujJelszo);
                                } else {
                                    megjelenit();
                                    Toast.makeText(getApplicationContext(), "A jelszónak minimum 6 karakterből kell álnia!", Toast.LENGTH_LONG).show();
                                    voltHiba.set(true);
                                    felhasznalo.updateEmail(regiEmail);
                                }
                            } else {
                                megjelenit();
                                Toast.makeText(getApplicationContext(), "Nem egyeznek a megadott jelszavak!", Toast.LENGTH_LONG).show();
                                voltHiba.set(true);
                                felhasznalo.updateEmail(regiEmail);
                            }
                        }


                        if (!voltHiba.get()) {
                            megjelenit();
                            db.collection("felhasznalok").document(felhasznalo.getUid()).set(ujFelhasznalo).addOnCompleteListener(task1 -> Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show());
                        }
                    } else {
                        if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "The email address is already in use by another account.")) {
                            megjelenit();
                            Toast.makeText(getApplicationContext(), "A megadott email cím már használatban van!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Érvénytelen email címet adtál meg!", Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(getApplicationContext(), "Amit változtatni szeretnél azt nem hagyhatod üresen!", Toast.LENGTH_LONG).show();

        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void onTermekKepFeltoltesModosit(View view) {
        Toast.makeText(getApplicationContext(), "Sikeres képfeltöltés!", Toast.LENGTH_LONG).show();
    }
}