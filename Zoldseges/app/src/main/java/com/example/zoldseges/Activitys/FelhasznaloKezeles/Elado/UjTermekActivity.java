package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UjTermekActivity extends AppCompatActivity {

    private static final int ImageCode = 1;
    private TextView termekNeve;
    private TextView termekAra;
    private TextView termekSulyaAtlagosan;
    private TextView termekKeszlet;
    private TextView mentestext;

    private ImageView elsoTermekKep;
    private TextView elsoTermekCim;
    private DocumentReference termekek;
    private FirebaseFirestore db;
    private StorageReference storageReference;
    private Uri imageUrl;

    private ProgressBar progressBar;
    private Button mentes;
    private String termekKepe = "";
    private String uzletId;
    private Termek ujTermek;
    private String termekId;

    private SwitchCompat mertekegysegValaszto;

    private LinearLayout mertekegysegValasztoLayout;

    private Map<String, Object> termekMap = new HashMap<>();

    private boolean sulybanKellMerni = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uj_termek);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        FirebaseAuth auth = FirebaseAuth.getInstance();

        termekNeve = findViewById(R.id.termekNeve);
        termekAra = findViewById(R.id.termekAra);
        mertekegysegValasztoLayout = findViewById(R.id.mertekegysegValasztoLayout);
        termekSulyaAtlagosan = findViewById(R.id.termekSulyaAtlagosan);
        mertekegysegValaszto = findViewById(R.id.mertekegysegValaszto);
        termekKeszlet = findViewById(R.id.termekKeszlet);
        elsoTermekKep = findViewById(R.id.elsoTermekKep);
        elsoTermekCim = findViewById(R.id.elsoTermekCim);
        mentes = findViewById(R.id.mentes);
        mentestext = findViewById(R.id.mentestext);

        sulybanKellMerni = sulybanMerendoE();
        progressBar = findViewById(R.id.progressBar);
        DocumentReference felhasznaloReference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        felhasznaloReference.addSnapshotListener((value, error) -> {
            assert value != null;
            uzletId = value.getString("uzletId");
        });
    }

    private boolean sulybanMerendoE() {
        mertekegysegValaszto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                sulybanKellMerni = true;
                termekSulyaAtlagosan.setVisibility(View.VISIBLE);
                termekAra.setHint(R.string.termek_egysegara_kg);
                termekAra.setText("");
            } else {
                sulybanKellMerni = false;
                termekSulyaAtlagosan.setVisibility(View.GONE);
                termekAra.setText("");
                termekAra.setHint(R.string.termek_egysegara_db);
            }
        });
        return sulybanKellMerni;
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

    public void onVisszaBolthoz(View view) {
        super.onBackPressed();
    }

    public void onMentes(View view) {
        if ((!sulybanKellMerni && !termekNeve.getText().toString().isEmpty() && !termekAra.getText().toString().isEmpty() && !termekKeszlet.getText().toString().isEmpty())
                || (sulybanKellMerni && !termekNeve.getText().toString().isEmpty() && !termekAra.getText().toString().isEmpty() && !termekKeszlet.getText().toString().isEmpty() && !termekSulyaAtlagosan.getText().toString().isEmpty())) {
            eltuntet();
            String nev = termekNeve.getText().toString();
            int raktaranLevoDbSsam = Integer.parseInt(termekKeszlet.getText().toString());
            double ar = Double.parseDouble(termekAra.getText().toString());
            double sulya;
            if (!sulybanKellMerni) {
                sulya = -1;
            } else {
                sulya = Double.parseDouble(termekSulyaAtlagosan.getText().toString());
            }
            if (raktaranLevoDbSsam <= 0 || ar <= 0 || (sulybanKellMerni && sulya <= 0)) {
                Toast.makeText(getApplicationContext(), "Nem adhatsz meg semminek sem 0-t!", Toast.LENGTH_LONG).show();
                megjelenit();
            } else {
                termekek = db.collection("uzletek").document(uzletId).collection("termekek").document();
                termekId = termekek.getId();
                ujTermek = new Termek(nev, ar, raktaranLevoDbSsam, sulya, termekKepe, uzletId);
                if (imageUrl != null) {
                    kepFeltolt(imageUrl, sulya);
                } else {
                    termekMap = ujTermek.ujTermek(ujTermek);
                    termekek.set(termekMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sikeres mentés! ", Toast.LENGTH_LONG).show();
                            UjTermekActivity.super.onBackPressed();
                            finish();
                        }
                    });
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "A csillaggal jelölt mezőket kötelező kitölteni!", Toast.LENGTH_LONG).show();
        }

    }

    public void onTermekKepFeltoltes(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, ImageCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImageCode && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            elsoTermekKep.setImageURI(imageUrl);
        }
    }

    public void megjelenit() {
        progressBar.setVisibility(View.GONE);
        mentestext.setVisibility(View.GONE);
        this.elsoTermekKep.setVisibility(View.VISIBLE);
        this.termekNeve.setVisibility(View.VISIBLE);
        this.termekAra.setVisibility(View.VISIBLE);
        this.termekKeszlet.setVisibility(View.VISIBLE);
        this.elsoTermekCim.setVisibility(View.VISIBLE);
        this.mertekegysegValasztoLayout.setVisibility(View.VISIBLE);
        sulybanMerendoE();
        mentes.setVisibility(View.VISIBLE);
    }

    public void eltuntet() {
        progressBar.setVisibility(View.VISIBLE);
        mentestext.setVisibility(View.VISIBLE);
        this.elsoTermekKep.setVisibility(View.GONE);
        this.termekNeve.setVisibility(View.GONE);
        this.termekAra.setVisibility(View.GONE);
        this.mertekegysegValasztoLayout.setVisibility(View.GONE);
        this.termekKeszlet.setVisibility(View.GONE);
        this.elsoTermekCim.setVisibility(View.GONE);
        mentes.setVisibility(View.GONE);
    }

    public void kepFeltolt(Uri uri, double suly) {
        StorageReference kepNeve = storageReference.child("termek_" + uzletId + "_" + uri.getLastPathSegment());
        kepNeve.putFile(uri).addOnSuccessListener(taskSnapshot -> kepNeve.getDownloadUrl().addOnSuccessListener(uri1 -> {
            kepNeve.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> feltoltes) {
                    if (feltoltes.isSuccessful()) {
                        termekKepe = String.valueOf(feltoltes.getResult());
                        termekek = db.collection("uzletek").document(uzletId).collection("termekek").document(termekId);
                        ujTermek = new Termek(termekNeve.getText().toString(), Double.parseDouble(termekAra.getText().toString()), Integer.parseInt(termekKeszlet.getText().toString()), suly, termekKepe, uzletId);
                        termekMap = ujTermek.ujTermek(ujTermek);
                        termekek.set(termekMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Sikeres mentés! ", Toast.LENGTH_LONG).show();
                                UjTermekActivity.super.onBackPressed();
                                finish();
                            }
                        });
                    }
                }
            });
        })).addOnProgressListener(snapshot -> {
            eltuntet();
        }).addOnFailureListener(e -> megjelenit());
    }
}