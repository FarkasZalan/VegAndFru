package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.badge.BadgeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class UjTermekActivity extends AppCompatActivity {

    private static final int ImageCode = 1;
    private TextView termekNeve;
    private TextView termekAra;
    private TextView termekKeszlet;
    private TextView mentestext;

    private ImageView elsoTermekKep;
    private TextView elsoTermekCim;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private Uri imageUrl;

    private ProgressBar progressBar;
    private Button mentes;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uj_termek);

        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        auth = FirebaseAuth.getInstance();

        termekNeve = findViewById(R.id.termekNeve);
        termekAra = findViewById(R.id.termekAra);
        termekKeszlet = findViewById(R.id.termekKeszlet);
        elsoTermekKep = findViewById(R.id.elsoTermekKep);
        elsoTermekCim = findViewById(R.id.elsoTermekCim);
        mentes = findViewById(R.id.mentes);
        mentestext = findViewById(R.id.mentestext);

        progressBar = findViewById(R.id.progressBar);
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
        if (!termekNeve.getText().toString().isEmpty()) {
            String nev = termekNeve.getText().toString();
            double ar = Double.parseDouble(termekAra.getText().toString());
            int dbSzam = Integer.parseInt(termekKeszlet.getText().toString());
            Toast.makeText(getApplicationContext(), "Sikeres mentés!", Toast.LENGTH_LONG).show();
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), "A csillaggal jelölt mezőket kötelező kitölteni!", Toast.LENGTH_LONG).show();
        }

        if (imageUrl != null) {
            kepFeltolt(imageUrl);
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
        if(requestCode == ImageCode && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            elsoTermekKep.setImageURI(imageUrl);
        }
    }
    public void kepFeltolt(Uri uri) {
        StorageReference kepNeve = storageReference.child("termek_" + Objects.requireNonNull(auth.getCurrentUser()).getUid() + "_" + uri.getLastPathSegment());
        kepNeve.putFile(uri).addOnSuccessListener(taskSnapshot -> kepNeve.getDownloadUrl().addOnSuccessListener(uri1 -> {
            progressBar.setVisibility(View.GONE);
            mentestext.setVisibility(View.GONE);
            this.elsoTermekKep.setVisibility(View.VISIBLE);
            this.termekNeve.setVisibility(View.VISIBLE);
            this.termekAra.setVisibility(View.VISIBLE);
            this.termekKeszlet.setVisibility(View.VISIBLE);
            this.elsoTermekCim.setVisibility(View.VISIBLE);
            mentes.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Sikeres képfeltöltés!", Toast.LENGTH_LONG).show();

        })).addOnProgressListener(snapshot -> {
            progressBar.setVisibility(View.VISIBLE);
            mentestext.setVisibility(View.VISIBLE);
            this.elsoTermekKep.setVisibility(View.GONE);
            this.termekNeve.setVisibility(View.INVISIBLE);
            this.termekAra.setVisibility(View.INVISIBLE);
            this.termekKeszlet.setVisibility(View.INVISIBLE);
            this.elsoTermekCim.setVisibility(View.INVISIBLE);
            mentes.setVisibility(View.INVISIBLE);
        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Váratlan hiba történt!", Toast.LENGTH_LONG).show());
    }
}