package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.Activitys.KosarActivity;
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TermekSzerkeszteseActivity extends AppCompatActivity {

    private TextView cimTermekModositas;
    private EditText termekNeveTermekModositas;
    private LinearLayout mertekegysegValasztoTermekModositasLayout;
    private SwitchCompat mertekegysegValasztoTermekModositas;
    private EditText termekAraTermekModositas;
    private EditText termekKeszletTermekModositas;
    private ProgressBar progressBarTermekModositas;
    private TextView mentestextTermekModositas;
    private ImageView termekKepTermekModositas;
    private TextView termekCimTermekModositas;
    private Button mentesTermekModositas;
    private Button visszaTermekModositas;
    private EditText termekSulyaAtlagosanTermekModositas;
    private String uzletId;

    private String termekId;
    private FirebaseFirestore db;

    private boolean sulybanMerendo;
    private DocumentReference osszestermek;
    private StorageReference storageReference;
    private DocumentReference termekReference;
    private Uri imageUrl;
    private String termekKepe;
    private String regiKep;
    private String osszTermekColectionId;
    double termekDbSZama;
    private Map<String, Object> termekMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termek_szerkesztese);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Termék módosítása");

        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        db = FirebaseFirestore.getInstance();

        String termekNeve = getIntent().getStringExtra("termekNeve");
        double termekSulya = getIntent().getDoubleExtra("termekSulya", 0);
        double termekegysegara = getIntent().getDoubleExtra("termekegysegara", 0);
        termekDbSZama = getIntent().getDoubleExtra("termekDbSZama", 0);
        termekKepe = getIntent().getStringExtra("termekKepe");
        regiKep = getIntent().getStringExtra("termekKepe");
        uzletId = getIntent().getStringExtra("uzletId");
        termekId = getIntent().getStringExtra("termekId");
        osszTermekColectionId = getIntent().getStringExtra("osszTermekColectionId");
        termekReference = db.collection("uzletek").document(uzletId).collection("termekek").document(termekId);


        cimTermekModositas = findViewById(R.id.cimTermekModositas);
        termekNeveTermekModositas = findViewById(R.id.termekNeveTermekModositas);
        mertekegysegValasztoTermekModositasLayout = findViewById(R.id.mertekegysegValasztoTermekModositasLayout);
        mertekegysegValasztoTermekModositas = findViewById(R.id.mertekegysegValasztoTermekModositas);
        termekSulyaAtlagosanTermekModositas = findViewById(R.id.termekSulyaAtlagosanTermekModositas);
        termekAraTermekModositas = findViewById(R.id.termekAraTermekModositas);
        termekKeszletTermekModositas = findViewById(R.id.termekKeszletTermekModositas);
        progressBarTermekModositas = findViewById(R.id.progressBarTermekModositas);
        mentestextTermekModositas = findViewById(R.id.mentestextTermekModositas);
        termekKepTermekModositas = findViewById(R.id.termekKepTermekModositas);
        termekCimTermekModositas = findViewById(R.id.termekCimTermekModositas);
        mentesTermekModositas = findViewById(R.id.mentesTermekModositas);
        visszaTermekModositas = findViewById(R.id.visszaTermekModositas);

        termekNeveTermekModositas.setText(termekNeve);
        String keszlet;
        String ar;
        if (termekSulya == -1) {
            mertekegysegValasztoTermekModositas.setChecked(false);
            termekSulyaAtlagosanTermekModositas.setVisibility(View.GONE);
            termekSulyaAtlagosanTermekModositas.setText("");
            ar = (int) termekegysegara + " Ft/db";
            termekAraTermekModositas.setHint(ar);
            termekAraTermekModositas.setText(String.valueOf((int) termekegysegara));
            keszlet = (int) termekDbSZama + " db";
            termekKeszletTermekModositas.setHint(keszlet);
            termekKeszletTermekModositas.setText(String.valueOf((int) termekDbSZama));
        } else {
            mertekegysegValasztoTermekModositas.setChecked(true);
            String suly = termekSulya + " kg";
            termekSulyaAtlagosanTermekModositas.setHint(suly);
            termekSulyaAtlagosanTermekModositas.setText(String.valueOf((int) termekSulya));
            ar = (int) termekegysegara + " Ft/kg";
            termekAraTermekModositas.setHint(ar);
            termekAraTermekModositas.setText(String.valueOf((int) termekegysegara));
            termekSulyaAtlagosanTermekModositas.setVisibility(View.VISIBLE);
            keszlet = (int) termekDbSZama + " kg";
            termekKeszletTermekModositas.setHint(keszlet);
            termekKeszletTermekModositas.setText(String.valueOf((int) termekDbSZama));
        }

        sulybanMerendo = sulyabanMerendoE();
        if (!termekKepe.isEmpty()) {
            eltuntet();
            mentestextTermekModositas.setText(R.string.betoltes);
            kepMegjelenit(termekKepe);
        } else {
            Glide.with(TermekSzerkeszteseActivity.this).load(R.drawable.standard_item_picture).placeholder(R.drawable.standard_item_picture).into(termekKepTermekModositas);
        }
    }

    public boolean sulyabanMerendoE() {
        mertekegysegValasztoTermekModositas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sulybanMerendo = true;
                    termekSulyaAtlagosanTermekModositas.setVisibility(View.VISIBLE);
                    termekAraTermekModositas.setHint(R.string.termek_egysegara_kg);
                    termekAraTermekModositas.setText("");
                    termekKeszletTermekModositas.setText("");
                    termekKeszletTermekModositas.setHint("Készlet (kg)*");
                } else {
                    sulybanMerendo = false;
                    termekSulyaAtlagosanTermekModositas.setVisibility(View.GONE);
                    termekAraTermekModositas.setText("");
                    termekAraTermekModositas.setHint(R.string.termek_egysegara_db);
                    termekKeszletTermekModositas.setText("");
                    termekKeszletTermekModositas.setHint("Készlet (db)*");
                }
            }
        });
        return sulybanMerendo;
    }

    public void kepMegjelenit(String kep) {
        if (!kep.isEmpty()) {
            Uri uri = Uri.parse(kep);
            try {
                Glide.with(TermekSzerkeszteseActivity.this).load(uri).addListener(new RequestListener<Drawable>() {
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
                }).placeholder(R.drawable.standard_item_picture).into(termekKepTermekModositas);
            } catch (Exception e) {
                megjelenit();
                Glide.with(TermekSzerkeszteseActivity.this).load(R.drawable.standard_item_picture).placeholder(R.drawable.standard_item_picture).into(termekKepTermekModositas);
            }
        } else {
            Glide.with(TermekSzerkeszteseActivity.this).load(R.drawable.standard_item_picture).placeholder(R.drawable.standard_item_picture).into(termekKepTermekModositas);
        }
    }

    private void eltuntet() {
        progressBarTermekModositas.setVisibility(View.VISIBLE);
        mentestextTermekModositas.setVisibility(View.VISIBLE);
        cimTermekModositas.setVisibility(View.GONE);
        mentesTermekModositas.setVisibility(View.GONE);
        visszaTermekModositas.setVisibility(View.GONE);
        termekKepTermekModositas.setVisibility(View.GONE);
        termekCimTermekModositas.setVisibility(View.GONE);
        termekNeveTermekModositas.setVisibility(View.GONE);
        termekAraTermekModositas.setVisibility(View.GONE);
        mertekegysegValasztoTermekModositasLayout.setVisibility(View.GONE);
        termekKeszletTermekModositas.setVisibility(View.GONE);
    }

    public void megjelenit() {
        progressBarTermekModositas.setVisibility(View.GONE);
        mentestextTermekModositas.setVisibility(View.GONE);
        cimTermekModositas.setVisibility(View.VISIBLE);
        mentesTermekModositas.setVisibility(View.VISIBLE);
        visszaTermekModositas.setVisibility(View.VISIBLE);
        termekKepTermekModositas.setVisibility(View.VISIBLE);
        termekCimTermekModositas.setVisibility(View.VISIBLE);
        termekNeveTermekModositas.setVisibility(View.VISIBLE);
        termekAraTermekModositas.setVisibility(View.VISIBLE);
        mertekegysegValasztoTermekModositasLayout.setVisibility(View.VISIBLE);
        termekKeszletTermekModositas.setVisibility(View.VISIBLE);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_bejelentkezett_menu, menu);
        View view = menu.findItem(R.id.kosarfiok).getActionView();
        MenuItem kosar = menu.findItem(R.id.kosarfiok);
        kosar.setVisible(false);
        view.setOnClickListener(v -> startActivity(new Intent(TermekSzerkeszteseActivity.this, KosarActivity.class)));
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

    public void onVisszaTermekekhez(View view) {
        super.onBackPressed();
        finish();
    }

    public void onModositas(View view) {
        if (mertekegysegValasztoTermekModositas.isChecked()) {
            sulybanMerendo = true;
        } else {
            sulybanMerendo = false;
        }
        if (sulybanMerendo && !termekNeveTermekModositas.getText().toString().isEmpty() && !termekNeveTermekModositas.getText().toString().equals(" ") && !termekSulyaAtlagosanTermekModositas.getText().toString().isEmpty() && !termekAraTermekModositas.getText().toString().isEmpty() && !termekKeszletTermekModositas.getText().toString().isEmpty() ||
                !sulybanMerendo && !termekNeveTermekModositas.getText().toString().isEmpty() && !termekNeveTermekModositas.getText().toString().equals(" ") && !termekAraTermekModositas.getText().toString().isEmpty() && !termekKeszletTermekModositas.getText().toString().isEmpty()) {
            String nev = termekNeveTermekModositas.getText().toString();
            if ((sulybanMerendo && Double.parseDouble(termekAraTermekModositas.getText().toString()) <= 2147483647 && Double.parseDouble(termekKeszletTermekModositas.getText().toString()) <= 2147483647 && Double.parseDouble(termekSulyaAtlagosanTermekModositas.getText().toString()) <= 2147483647) ||
                    (!sulybanMerendo && Double.parseDouble(termekAraTermekModositas.getText().toString()) <= 2147483647 && Double.parseDouble(termekKeszletTermekModositas.getText().toString()) <= 2147483647)) {
                double suly;
                double ar = Double.parseDouble(termekAraTermekModositas.getText().toString());
                double keszlet = Double.parseDouble(termekKeszletTermekModositas.getText().toString());
                if (!sulybanMerendo) {
                    suly = -1;
                } else {
                    suly = Double.parseDouble(termekSulyaAtlagosanTermekModositas.getText().toString());
                }
                if ((sulybanMerendo && suly > 0 && ar > 0 && keszlet > 0) || (!sulybanMerendo && ar > 0 && keszlet > 0)) {
                    mentestextTermekModositas.setText(R.string.termek_modositasa_progress);
                    eltuntet();
                    if (imageUrl == null) {
                        if (regiKep.isEmpty()) {
                            termekKepe = "";
                        } else {
                            termekKepe = regiKep;
                        }
                        adatbazisModosit(nev, ar, keszlet, suly, termekKepe);
                    } else {
                        kepetCserel(imageUrl, nev, ar, keszlet, suly);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nem adhatsz meg semminek sem 0 értéket!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Túl nagy értékeket adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Minden változtatni kívánt mezőt ki kell tölts!", Toast.LENGTH_LONG).show();
        }

    }

    public void onTermekKepFeltoltesModositas(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            Glide.with(TermekSzerkeszteseActivity.this).load(imageUrl).into(termekKepTermekModositas);
        }
    }

    public void kepetCserel(Uri uri, String nev, double ar, double dbSzam, double suly) {
        StorageReference kepNeve;
        if (!termekKepe.isEmpty()) {
            kepNeve = FirebaseStorage.getInstance().getReferenceFromUrl(termekKepe);
        } else {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            kepNeve = storageReference.child("termek_" + uzletId + "_" + timeStamp);
        }
        kepNeve.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                kepNeve.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> feltoltes) {
                        if (feltoltes.isSuccessful()) {
                            termekKepe = String.valueOf(feltoltes.getResult());
                            adatbazisModosit(nev, ar, dbSzam, suly, termekKepe);
                        }
                    }
                });
            }
        }).addOnProgressListener(snapshot -> eltuntet()).addOnFailureListener(e -> megjelenit());
    }

    public void adatbazisModosit(String nev, double ar, double dbSzam, double suly, String kep) {
        db.collection("uzletek").document(uzletId).collection("termekek").document(termekId);
        osszestermek = db.collection("osszesTermek").document(osszTermekColectionId);
        Map<String, Object> osszTermekBovit = new HashMap<>();
        osszTermekBovit.put("termekNeve", nev);
        osszTermekBovit.put("uzletId", uzletId);
        osszestermek.set(osszTermekBovit);
        Termek frissitettTermek = new Termek(nev, ar, dbSzam, suly, kep, uzletId, osszTermekColectionId);
        termekMap = frissitettTermek.ujTermek(frissitettTermek);
        termekReference.set(termekMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                megjelenit();
            }
        });
    }
}