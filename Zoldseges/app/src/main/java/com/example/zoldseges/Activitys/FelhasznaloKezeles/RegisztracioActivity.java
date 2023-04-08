package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.DAOS.Felhasznalo;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisztracioActivity extends AppCompatActivity {
    private static final int ImageCode = 1;
    public static String id = "";
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private TextView nev;
    private TextView email;
    private TextView telefonszam;
    private TextView lakcim;
    private TextView jelszo;
    private TextView jelszoUjra;
    private TextView cegNev;
    private TextView adoszam;
    private ImageView termekKepBeallitas;
    private TextView termekKepCim;
    private TextView szekhely;
    private CheckBox cegE;
    private Button regisztracioButton;
    private SwitchCompat eladoE;
    private LinearLayout regCegesCuccok;
    private Map<String, Object> felhasznalok = new HashMap<>();
    private ProgressBar progressBarRegisztracio;
    private TextView regisztracioText;
    private StorageReference storageReference;
    private Uri imageUrl;
    String boltKep;
    private Felhasznalo felhasznalo1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisztracio);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("BoltKepek");

        nev = findViewById(R.id.nev);
        regCegesCuccok = findViewById(R.id.regCegesCuccok);
        progressBarRegisztracio = findViewById(R.id.progressBarRegisztracio);
        regisztracioText = findViewById(R.id.regisztracioText);
        regisztracioButton = findViewById(R.id.regisztracioButton);
        email = findViewById(R.id.email);
        telefonszam = findViewById(R.id.telefonszam);
        lakcim = findViewById(R.id.lakcim);
        jelszo = findViewById(R.id.jelszo);
        jelszoUjra = findViewById(R.id.jelszoUjra);
        cegNev = findViewById(R.id.cegNev);
        adoszam = findViewById(R.id.adoszam);
        szekhely = findViewById(R.id.szekhely);
        termekKepBeallitas = findViewById(R.id.termekKepBeallitas);
        termekKepCim = findViewById(R.id.termekKepCim);
        cegE = findViewById(R.id.cegE);
        eladoE = findViewById(R.id.eladoE);
        eladotRegisztralE();
        cegetRegisztral();
    }

    public void cegetRegisztral() {
        cegE.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cegNev.setVisibility(View.VISIBLE);
                adoszam.setVisibility(View.VISIBLE);
                szekhely.setVisibility(View.VISIBLE);
                imageUrl = null;
            } else {
                cegNev.setVisibility(View.GONE);
                adoszam.setVisibility(View.GONE);
                szekhely.setVisibility(View.GONE);
            }
            termekKepBeallitas.setVisibility(View.GONE);
            termekKepCim.setVisibility(View.GONE);
        });
    }

    public void eladotRegisztralE() {
        eladoE.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cegE.setVisibility(View.GONE);
                cegNev.setVisibility(View.VISIBLE);
                adoszam.setVisibility(View.VISIBLE);
                szekhely.setVisibility(View.VISIBLE);
                termekKepBeallitas.setVisibility(View.VISIBLE);
                termekKepCim.setVisibility(View.VISIBLE);
            } else {
                imageUrl = null;
                cegE.setVisibility(View.VISIBLE);
                cegNev.setVisibility(View.GONE);
                adoszam.setVisibility(View.GONE);
                szekhely.setVisibility(View.GONE);
                termekKepBeallitas.setVisibility(View.GONE);
                termekKepCim.setVisibility(View.GONE);
                if (cegE.isChecked()) {
                    cegNev.setVisibility(View.VISIBLE);
                    adoszam.setVisibility(View.VISIBLE);
                    szekhely.setVisibility(View.VISIBLE);
                    termekKepBeallitas.setVisibility(View.GONE);
                    termekKepCim.setVisibility(View.GONE);
                }
            }
        });
    }


    public void onRegister(View view) {
        String nev = this.nev.getText().toString();
        String email = this.email.getText().toString();
        String telefonszam = this.telefonszam.getText().toString();
        String lakcim = this.lakcim.getText().toString();
        String jelszo = this.jelszo.getText().toString();
        String jelszoRepeat = this.jelszoUjra.getText().toString();
        String cegNev = this.cegNev.getText().toString();
        String adoszam = this.adoszam.getText().toString();
        String szekhely = this.szekhely.getText().toString();
        final String[] felhasznaloTipus = {""};

        //auth és adatb hibakezelésekkel
        if (!nev.isEmpty() && !email.isEmpty() && !telefonszam.isEmpty() && !lakcim.isEmpty() && !jelszo.isEmpty()) {
            if (cegE.isChecked() && !cegNev.isEmpty() && !adoszam.isEmpty() && !szekhely.isEmpty() || !cegE.isChecked() && !eladoE.isChecked() || eladoE.isChecked() && !cegNev.isEmpty() && !adoszam.isEmpty() && !szekhely.isEmpty()) {
                if (isEmailValid(email)) {
                    if (jelszo.equals(jelszoRepeat)) {
                        eltuntet();
                        auth.createUserWithEmailAndPassword(email, jelszo).addOnCompleteListener(this, task -> {
                            if (!task.isSuccessful()) {
                                if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "The given password is invalid. [ Password should be at least 6 characters ]")) {
                                    Toast.makeText(getApplicationContext(), "A jelszónak minimum 6 karakterből kell álnia!", Toast.LENGTH_LONG).show();
                                    megjelenit();
                                }
                                if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                    Toast.makeText(getApplicationContext(), "A megadott email cím már használatban van!", Toast.LENGTH_LONG).show();
                                    megjelenit();
                                }
                            } else {
                                if (cegE.isChecked()) {
                                    felhasznaloTipus[0] = "Cég/Vállalat";
                                }
                                if (eladoE.isChecked()) {
                                    felhasznaloTipus[0] = "Eladó cég/vállalat";
                                }
                                if (!eladoE.isChecked() && !cegE.isChecked()) {
                                    felhasznaloTipus[0] = "magánszemély";
                                }
                                id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                if (imageUrl == null) {
                                    boltKep = "@drawable/standard_item_picture";
                                } else {
                                    auth.signInWithEmailAndPassword(this.email.getText().toString(), this.jelszo.getText().toString());

                                    kepFeltolt(imageUrl, felhasznaloTipus[0]);
                                }
                                this.felhasznalo1 = new Felhasznalo(nev, email, jelszo, telefonszam, lakcim, cegNev, adoszam, szekhely, felhasznaloTipus[0], boltKep);
                                felhasznalok = felhasznalo1.ujFelhasznalo(felhasznalo1);
                                DocumentReference reference = firestore.collection("felhasznalok").document(id);
                                reference.set(felhasznalok).addOnSuccessListener(adatbMent -> {
                                    if (imageUrl == null) {
                                        megjelenit();
                                        Toast.makeText(getApplicationContext(), "Sikeresen regisztráltál, " + felhasznalo1.getNev() + "!", Toast.LENGTH_LONG).show();
                                        vissza();
                                    }
                                }).addOnFailureListener(e -> megjelenit());
                            }
                        });

                    } else {
                        Toast.makeText(getApplicationContext(), "Nem egyeznek a megadott jelszavak!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Érvénytelen email címet adtál meg!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Ha szeretnél céget regisztrálni muszáj megadni az ahhoz tartozó adatok is!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Nem maradhatnak mezők üresen!", Toast.LENGTH_LONG).show();
        }
    }

    public void megjelenit() {
        this.progressBarRegisztracio.setVisibility(View.GONE);
        this.regisztracioText.setVisibility(View.GONE);
        if (cegE.isChecked() || eladoE.isChecked()) {
            this.szekhely.setVisibility(View.VISIBLE);
            this.cegNev.setVisibility(View.VISIBLE);
            this.adoszam.setVisibility(View.VISIBLE);
            this.nev.setVisibility(View.VISIBLE);
            if (eladoE.isChecked()) {
                this.termekKepBeallitas.setVisibility(View.VISIBLE);
                this.termekKepCim.setVisibility(View.VISIBLE);
            }
        }
        this.nev.setVisibility(View.VISIBLE);
        this.email.setVisibility(View.VISIBLE);
        this.telefonszam.setVisibility(View.VISIBLE);
        this.lakcim.setVisibility(View.VISIBLE);
        this.jelszo.setVisibility(View.VISIBLE);
        this.jelszoUjra.setVisibility(View.VISIBLE);
        this.regisztracioButton.setVisibility(View.VISIBLE);
        this.regCegesCuccok.setVisibility(View.VISIBLE);
    }

    public void eltuntet() {
        this.progressBarRegisztracio.setVisibility(View.VISIBLE);
        this.regisztracioText.setVisibility(View.VISIBLE);
        this.szekhely.setVisibility(View.GONE);
        this.cegNev.setVisibility(View.GONE);
        this.adoszam.setVisibility(View.GONE);
        this.nev.setVisibility(View.GONE);
        this.termekKepBeallitas.setVisibility(View.GONE);
        this.termekKepCim.setVisibility(View.GONE);
        this.email.setVisibility(View.GONE);
        this.telefonszam.setVisibility(View.GONE);
        this.lakcim.setVisibility(View.GONE);
        this.jelszo.setVisibility(View.GONE);
        this.jelszoUjra.setVisibility(View.GONE);
        this.regisztracioButton.setVisibility(View.GONE);
        this.regCegesCuccok.setVisibility(View.INVISIBLE);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void vissza() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        auth.signOut();
        finish();
    }

    public void onLoginOpen(View view) {
        super.onBackPressed();
    }

    //innentől lefele végig a képfeltöltés

    public void onTermekKepFeltoltesReg(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, ImageCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageCode && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            termekKepBeallitas.setImageURI(imageUrl);
        }
    }

    public void kepFeltolt(Uri uri, String felhasznaloTipus) {
        StorageReference kepNeve = storageReference.child("bolt_" + id + "_" + uri.getLastPathSegment());
        kepNeve.putFile(uri).addOnSuccessListener(taskSnapshot -> kepNeve.getDownloadUrl().addOnSuccessListener(uri1 -> {
            this.boltKep = kepNeve.toString();
            Map<String, Object> ujFelhasznalo;
            Felhasznalo kepes = new Felhasznalo(nev.getText().toString(), email.getText().toString(), jelszo.getText().toString(), telefonszam.getText().toString(),
                    lakcim.getText().toString(), cegNev.getText().toString(), adoszam.getText().toString(), szekhely.getText().toString(), felhasznaloTipus, boltKep);
            ujFelhasznalo = kepes.ujFelhasznalo(kepes);
            //ha tölt fel képet akkor frissűlnek az adatai az adatb-ben
            firestore.collection("felhasznalok").document(id).set(ujFelhasznalo).addOnSuccessListener(unused -> {
                megjelenit();
                Toast.makeText(getApplicationContext(), "Sikeresen regisztráltál, " + felhasznalo1.getNev() + "!", Toast.LENGTH_LONG).show();
                vissza();
            });
            megjelenit();
        })).addOnProgressListener(snapshot -> {
            eltuntet();
        }).addOnFailureListener(e -> {
            megjelenit();
            Toast.makeText(getApplicationContext(), "Váratlan hiba történt!", Toast.LENGTH_LONG).show();
        });
    }
}