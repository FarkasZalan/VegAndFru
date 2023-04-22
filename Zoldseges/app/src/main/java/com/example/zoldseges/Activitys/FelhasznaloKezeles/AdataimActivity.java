package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.Activitys.KosarActivity;
import com.example.zoldseges.DAOS.Felhasznalo;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
    Button vissza;
    String boltKep;
    private DocumentReference reference;
    private FirebaseAuth auth;
    private FirebaseUser felhasznalo;
    private FirebaseFirestore db;
    private Map<String, Object> ujFelhasznalo = new HashMap<>();

    private StorageReference storageReference;

    private Uri imageUrl;
    private Uri regiUri;
    private DocumentReference uzletReference;

    final String[] felhasznaloTipus = {""};

    private String uzletId;

    private LinearLayout szallitasiCimLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adataim);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Adatok módosítása");

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        felhasznalo = auth.getCurrentUser();
        nevV = findViewById(R.id.nevAdatSzerkezto);
        progressBarModositas = findViewById(R.id.progressBarModositas);
        modositasText = findViewById(R.id.modositasText);
        adatokSzerkesztese = findViewById(R.id.adatokSzerkesztese);
        vissza = findViewById(R.id.vissza);
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
        boltKep = "";
        nevLabel = findViewById(R.id.nevLabel);
        emailLabel = findViewById(R.id.emailLabel);
        telszamLabel = findViewById(R.id.telszamLabel);
        szalitasiCimLabell = findViewById(R.id.szalitasiCimLabell);
        paswLabel = findViewById(R.id.paswLabel);
        paswRepeatLabel = findViewById(R.id.paswRepeatLabel);
        termekKepBeallitasModosit = findViewById(R.id.termekKepBeallitasModosit);
        termekKepBeallitasModosit.setClipToOutline(true); //kép radius aktiválása
        termekKepCimModosit = findViewById(R.id.termekKepCimModosit);
        szallitasiCimLayout = findViewById(R.id.szallitasiCimLayout);
        reference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        storageReference = FirebaseStorage.getInstance().getReference().child("BoltKepek");
        eltuntet();
        kepMegjelenitese();

    }

    public void kepMegjelenitese() {
        reference.addSnapshotListener((value, error) -> {
            assert value != null;
            felhasznaloTipus[0] = value.getString("felhasznaloTipus");

            nevV.setText(value.getString("nev"));
            emailV.setText(value.getString("email"));
            telSzamV.setText(value.getString("telefonszam"));
            szallitasiCimV.setText(value.getString("lakcim"));
            szekhelyV.setText(value.getString("szekhely"));
            cegNevV.setText(value.getString("cegNev"));
            adoszamV.setText(value.getString("adoszam"));
            if (Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat")) {
                uzletId = value.getString("uzletId");
                uzletReference = db.collection("uzletek").document(Objects.requireNonNull(uzletId));
                uzletReference.addSnapshotListener((uzlet, error1) -> {
                    assert uzlet != null;
                    if (Objects.requireNonNull(uzlet.getString("boltKepe")).isEmpty() || Objects.equals(uzlet.getString("boltKepe"), "null") || uzlet.getString("boltKepe") == null) {
                        regiUri = null;
                    } else {
                        regiUri = Uri.parse(uzlet.getString("boltKepe"));
                    }
                    modositasText.setText(R.string.betoltes);
                    try {
                        if (!this.isFinishing()) {
                            Glide.with(AdataimActivity.this).load(regiUri).placeholder(R.drawable.grocery_store).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    megjelenit();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    megjelenit();
                                    return false;
                                }
                            }).into(termekKepBeallitasModosit);
                        }
                    } catch (Exception e) {
                        Glide.with(AdataimActivity.this).load(R.drawable.grocery_store).into(termekKepBeallitasModosit);
                    }
                });
            } else {
                megjelenit();
            }
        });
    }

    public void megjelenit() {
        this.progressBarModositas.setVisibility(View.GONE);
        this.modositasText.setVisibility(View.GONE);
        this.nevV.setVisibility(View.VISIBLE);
        this.emailV.setVisibility(View.VISIBLE);
        this.telSzamV.setVisibility(View.VISIBLE);
        this.jelszoV.setVisibility(View.VISIBLE);
        this.jelszoUjraV.setVisibility(View.VISIBLE);
        this.adatokSzerkesztese.setVisibility(View.VISIBLE);
        this.vissza.setVisibility(View.VISIBLE);
        this.nevLabel.setVisibility(View.VISIBLE);
        this.emailLabel.setVisibility(View.VISIBLE);
        this.telszamLabel.setVisibility(View.VISIBLE);
        this.szallitasiCimLayout.setVisibility(View.VISIBLE);
        this.paswLabel.setVisibility(View.VISIBLE);
        this.paswRepeatLabel.setVisibility(View.VISIBLE);
        cegVagyElado();
    }

    public void cegVagyElado() {

        if (felhasznaloTipus[0].equals("Eladó cég/vállalat") || (felhasznaloTipus[0].equals("Cég/Vállalat"))) {
            szekhelyV.setVisibility(View.VISIBLE);
            cegNevV.setVisibility(View.VISIBLE);
            adoszamV.setVisibility(View.VISIBLE);
            cegnevLabel.setVisibility(View.VISIBLE);
            adoszamLabel.setVisibility(View.VISIBLE);
            szekhelyLabel.setVisibility(View.VISIBLE);
            szallitasiCimLayout.setVisibility(View.VISIBLE);
            termekKepBeallitasModosit.setVisibility(View.GONE);
            termekKepCimModosit.setVisibility(View.GONE);
            if (felhasznaloTipus[0].equals("Eladó cég/vállalat")) {
                termekKepBeallitasModosit.setVisibility(View.VISIBLE);
                termekKepCimModosit.setVisibility(View.VISIBLE);
                szallitasiCimV.setText("");
                szallitasiCimLayout.setVisibility(View.GONE);
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

    }

    public void eltuntet() {
        this.progressBarModositas.setVisibility(View.VISIBLE);
        this.modositasText.setVisibility(View.VISIBLE);
        this.modositasText.setText(R.string.modositas);
        this.nevV.setVisibility(View.GONE);
        this.emailV.setVisibility(View.GONE);
        this.telSzamV.setVisibility(View.GONE);
        this.szallitasiCimLayout.setVisibility(View.GONE);
        this.jelszoV.setVisibility(View.GONE);
        this.jelszoUjraV.setVisibility(View.GONE);
        this.adatokSzerkesztese.setVisibility(View.GONE);
        this.vissza.setVisibility(View.GONE);
        this.szekhelyV.setVisibility(View.GONE);
        this.cegNevV.setVisibility(View.GONE);
        this.adoszamV.setVisibility(View.GONE);
        this.termekKepBeallitasModosit.setVisibility(View.GONE);
        this.termekKepCimModosit.setVisibility(View.GONE);
        this.cegnevLabel.setVisibility(View.GONE);
        this.nevLabel.setVisibility(View.GONE);
        this.emailLabel.setVisibility(View.GONE);
        this.telszamLabel.setVisibility(View.GONE);

        this.paswLabel.setVisibility(View.GONE);
        this.paswRepeatLabel.setVisibility(View.GONE);
        this.adoszamLabel.setVisibility(View.GONE);
        this.szekhelyLabel.setVisibility(View.GONE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_bejelentkezett_menu, menu);
        View view = menu.findItem(R.id.kosarfiok).getActionView();
        view.setOnClickListener(v -> startActivity(new Intent(AdataimActivity.this, KosarActivity.class)));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
            return true;
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


        if (((!ujEmail.isEmpty() && !ujNev.isEmpty() && !ujTelefon.isEmpty() && !ujSzallitasiCim.isEmpty() && !ujCegNev.isEmpty() && !ujSzekhely.isEmpty() && !ujAdoszam.isEmpty()) && cegNevV.getVisibility() == View.VISIBLE)
                || ((!ujEmail.isEmpty() && !ujNev.isEmpty() && !ujTelefon.isEmpty() && !ujSzallitasiCim.isEmpty()) && cegNevV.getVisibility() == View.GONE)
                || felhasznaloTipus[0].equals("Eladó cég/vállalat") && !ujEmail.isEmpty() && !ujNev.isEmpty() && !ujTelefon.isEmpty() && !ujCegNev.isEmpty() && !ujSzekhely.isEmpty() && !ujAdoszam.isEmpty()) {
            if (isEmailValid(ujEmail)) {
                eltuntet();
                reference.addSnapshotListener((value, error) -> {
                    assert value != null;
                    this.boltKep = String.valueOf(regiUri);
                    Felhasznalo friss = new Felhasznalo(ujNev, ujEmail, ujTelefon, ujSzallitasiCim, ujCegNev, ujAdoszam, ujSzekhely, felhasznaloTipus[0], uzletId);
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
                            if (felhasznaloTipus[0].equals("Eladó cég/vállalat")) {
                                if (imageUrl == null) {
                                    Map<String, String> uzletParameterek = new HashMap<>();
                                    uzletParameterek.put("cegNev", cegNevV.getText().toString());
                                    uzletParameterek.put("szekhely", szekhelyV.getText().toString());
                                    uzletParameterek.put("boltKepe", boltKep);
                                    uzletParameterek.put("tulajId", Objects.requireNonNull(felhasznalo.getUid()));
                                    uzletReference.set(uzletParameterek);
                                    db.collection("felhasznalok").document(felhasznalo.getUid()).set(ujFelhasznalo).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                                            megjelenit();
                                        }
                                    });
                                } else {
                                    kepFeltolt(imageUrl, felhasznaloTipus[0]);
                                }
                            } else {
                                db.collection("felhasznalok").document(felhasznalo.getUid()).set(ujFelhasznalo).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                                        megjelenit();
                                    }
                                });
                            }
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
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            Glide.with(AdataimActivity.this).load(imageUrl).into(termekKepBeallitasModosit);
        }
    }

    public void kepFeltolt(Uri uri, String felhasznaloTipus) {
        String id = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        StorageReference kepNeve;
        if (regiUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference();
            kepNeve = storageReference.child(regiUri.getLastPathSegment());
        } else {
            kepNeve = storageReference.child("bolt_" + id + "_" + uri.getLastPathSegment());
        }
        kepNeve.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                kepNeve.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        db.collection("felhasznalok").document(felhasznalo.getUid()).set(ujFelhasznalo);
                        boltKep = String.valueOf(task.getResult());
                        //ha tölt fel képet akkor frissűlnek az adatai az adatb-ben
                        Map<String, String> uzletParameterek = new HashMap<>();
                        uzletParameterek.put("cegNev", cegNevV.getText().toString());
                        uzletParameterek.put("szekhely", szekhelyV.getText().toString());
                        uzletParameterek.put("boltKepe", boltKep);
                        uzletParameterek.put("tulajId", Objects.requireNonNull(felhasznalo.getUid()));
                        uzletReference.set(uzletParameterek).addOnSuccessListener(uzletetFrissit -> {
                            Glide.with(AdataimActivity.this).load(regiUri).placeholder(R.drawable.grocery_store).listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    megjelenit();
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    megjelenit();
                                    return false;
                                }
                            }).into(termekKepBeallitasModosit);
                        });
                        Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnProgressListener(snapshot -> eltuntet()).addOnFailureListener(e -> megjelenit());
    }
}