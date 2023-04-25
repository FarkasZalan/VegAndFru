package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zoldseges.Activitys.KosarActivity;
import com.example.zoldseges.DAOS.Felhasznalo;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisztracioActivity extends AppCompatActivity {
    private static final int ImageCode = 1;
    public static String id = "";
    private FirebaseFirestore db;
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
    private Button bejelentkezes;
    private SwitchCompat eladoE;
    private LinearLayout regCegesCuccok;
    private Map<String, Object> felhasznalok = new HashMap<>();
    private ProgressBar progressBarRegisztracio;
    private TextView regisztracioText;
    private StorageReference storageReference;
    private Uri imageUrl;
    String boltKep = "";
    DocumentReference uzletek;
    String uzletId = "";
    private Felhasznalo felhasznalo1;

    private String felhasznaloTipus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regisztracio);

        db = FirebaseFirestore.getInstance();
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
        termekKepBeallitas.setClipToOutline(true); //kép radius aktiválása
        termekKepCim = findViewById(R.id.termekKepCim);
        cegE = findViewById(R.id.cegE);
        eladoE = findViewById(R.id.eladoE);
        bejelentkezes = findViewById(R.id.bejelentkezes);
        eladotRegisztralE();
        cegetRegisztral();

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Regisztráció");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_bejelentkezett_menu, menu);
        MenuItem kosar = menu.findItem(R.id.kosarfiok);
        if (auth.getCurrentUser() == null) {
            kosar.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
        }
        if (item.getItemId() == R.id.kosarfiok) {
            startActivity(new Intent(RegisztracioActivity.this, KosarActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosarfiok);

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
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, BejelentkezesActivity.class));
    }

    public void cegetRegisztral() {
        cegE.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cegNev.setVisibility(View.VISIBLE);
                adoszam.setVisibility(View.VISIBLE);
                szekhely.setVisibility(View.VISIBLE);
                imageUrl = null;
                Glide.with(RegisztracioActivity.this).load(R.drawable.grocery_store).into(termekKepBeallitas);
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
                lakcim.setVisibility(View.GONE);
                cegNev.setVisibility(View.VISIBLE);
                adoszam.setVisibility(View.VISIBLE);
                szekhely.setVisibility(View.VISIBLE);
                termekKepBeallitas.setVisibility(View.VISIBLE);
                termekKepCim.setVisibility(View.VISIBLE);
                this.nev.setHint("Tulajdonos teljes neve*");
            } else {
                imageUrl = null;
                Glide.with(RegisztracioActivity.this).load(R.drawable.grocery_store).into(termekKepBeallitas);
                cegE.setVisibility(View.VISIBLE);
                lakcim.setVisibility(View.VISIBLE);
                cegNev.setVisibility(View.GONE);
                adoszam.setVisibility(View.GONE);
                szekhely.setVisibility(View.GONE);
                termekKepBeallitas.setVisibility(View.GONE);
                termekKepCim.setVisibility(View.GONE);
                this.nev.setHint("Teljes név*");
                if (cegE.isChecked()) {
                    cegNev.setVisibility(View.VISIBLE);
                    adoszam.setVisibility(View.VISIBLE);
                    szekhely.setVisibility(View.VISIBLE);
                    termekKepBeallitas.setVisibility(View.GONE);
                    termekKepCim.setVisibility(View.GONE);
                    this.nev.setHint("Tulajdonos teljes neve*");
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


        //auth és adatb hibakezelésekkel
        if ((!eladoE.isChecked() && !nev.isEmpty() && !email.isEmpty() && !telefonszam.isEmpty() && !lakcim.isEmpty() && !jelszo.isEmpty()) || (eladoE.isChecked() && !nev.isEmpty() && !email.isEmpty() && !telefonszam.isEmpty() && !jelszo.isEmpty())) {
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
                                    felhasznaloTipus = "Cég/Vállalat";
                                }
                                if (eladoE.isChecked()) {
                                    felhasznaloTipus = "Eladó cég/vállalat";
                                }
                                if (!eladoE.isChecked() && !cegE.isChecked()) {
                                    felhasznaloTipus = "magánszemély";
                                }
                                id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                                if (felhasznaloTipus.equals("Eladó cég/vállalat")) {
                                    uzletek = db.collection("uzletek").document();
                                    uzletId = uzletek.getId();
                                    this.felhasznalo1 = new Felhasznalo(nev, email, telefonszam, "", cegNev, adoszam, szekhely, felhasznaloTipus, uzletId);
                                    if (imageUrl != null) {
                                        auth.signInWithEmailAndPassword(this.email.getText().toString(), this.jelszo.getText().toString());
                                        kepFeltolt(imageUrl);
                                    } else {
                                        Map<String, String> uzletParameterek = new HashMap<>();
                                        uzletParameterek.put("cegNev", cegNev);
                                        uzletParameterek.put("szekhely", szekhely);
                                        uzletParameterek.put("boltKepe", boltKep);
                                        uzletParameterek.put("tulajId", id);
                                        uzletek.set(uzletParameterek);
                                    }
                                }
                                this.felhasznalo1 = new Felhasznalo(nev, email, telefonszam, lakcim, cegNev, adoszam, szekhely, felhasznaloTipus, uzletId);
                                felhasznalok = felhasznalo1.ujFelhasznalo(felhasznalo1);
                                DocumentReference reference = db.collection("felhasznalok").document(id);
                                reference.set(felhasznalok).addOnSuccessListener(adatbMent -> {
                                    if (imageUrl == null) {
                                        super.onBackPressed();
                                        Toast.makeText(getApplicationContext(), "Sikeresen regisztráltál, " + felhasznalo1.getNev() + "!", Toast.LENGTH_LONG).show();
                                        sikeresRegisztracio();
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
                Toast.makeText(getApplicationContext(), "Ha szeretnél céget regisztrálni muszáj megadni az ahhoz tartozó adatokat is!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Nem maradhat egy csillaggal jelölt mező sem üresen!", Toast.LENGTH_LONG).show();
        }
    }

    public void megjelenit() {
        this.progressBarRegisztracio.setVisibility(View.GONE);
        this.regisztracioText.setVisibility(View.GONE);
        this.nev.setHint("Teljes név*");
        if (cegE.isChecked() || eladoE.isChecked()) {
            this.szekhely.setVisibility(View.VISIBLE);
            this.cegNev.setVisibility(View.VISIBLE);
            this.adoszam.setVisibility(View.VISIBLE);
            this.nev.setVisibility(View.VISIBLE);
            if (eladoE.isChecked()) {
                this.termekKepBeallitas.setVisibility(View.VISIBLE);
                this.termekKepCim.setVisibility(View.VISIBLE);
                this.nev.setHint("Tulajdonos teljes neve*");
            }
        }
        this.nev.setVisibility(View.VISIBLE);
        this.email.setVisibility(View.VISIBLE);
        this.telefonszam.setVisibility(View.VISIBLE);
        this.lakcim.setVisibility(View.VISIBLE);
        this.jelszo.setVisibility(View.VISIBLE);
        this.jelszoUjra.setVisibility(View.VISIBLE);
        this.regisztracioButton.setVisibility(View.VISIBLE);
        this.bejelentkezes.setVisibility(View.VISIBLE);
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
        this.bejelentkezes.setVisibility(View.GONE);
        this.regCegesCuccok.setVisibility(View.INVISIBLE);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void sikeresRegisztracio() {
        super.onBackPressed();
        super.onBackPressed();
        kosarLista.clear();
        startActivity(new Intent(this, FiokActicity.class));
        finish();
    }

    public void onLoginOpen(View view) {
        super.onBackPressed();
        startActivity(new Intent(this, BejelentkezesActivity.class));
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

    public void kepFeltolt(Uri uri) {
        StorageReference kepNeve = storageReference.child("bolt_" + id + "_" + uri.getLastPathSegment());
        kepNeve.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                kepNeve.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> feltoltes) {
                        if (feltoltes.isSuccessful()) {
                            boltKep = String.valueOf(feltoltes.getResult());
                            //ha tölt fel képet akkor frissűlnek az adatai az adatb-ben
                            Map<String, String> uzletParameterek = new HashMap<>();
                            uzletParameterek.put("cegNev", cegNev.getText().toString());
                            uzletParameterek.put("Szekhely", szekhely.getText().toString());
                            uzletParameterek.put("boltKepe", boltKep);
                            uzletParameterek.put("tulajId", id);
                            uzletek = db.collection("uzletek").document(uzletId);
                            uzletek.set(uzletParameterek).addOnSuccessListener(uzletbeTolt -> {
                                Toast.makeText(getApplicationContext(), "Sikeresen regisztráltál, " + felhasznalo1.getNev() + "!", Toast.LENGTH_LONG).show();
                                RegisztracioActivity.super.onBackPressed();
                                sikeresRegisztracio();
                            });
                        }
                    }
                });
            }
        }).addOnProgressListener(snapshot -> eltuntet()).addOnFailureListener(e -> megjelenit());
    }
}