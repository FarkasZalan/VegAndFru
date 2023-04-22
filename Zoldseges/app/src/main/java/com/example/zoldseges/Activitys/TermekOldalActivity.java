package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.Objects;

public class TermekOldalActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    String termekKepe;
    String regiKep;
    String uzletId;
    String termekId;

    ImageView kepTermek;
    TextView termekNeveBolt;
    TextView termekSulyBolt;
    TextView termekAraBolt;
    TextView termekMaxRendelheto;
    LinearLayout rendelenoMennyisegLayout;
    EditText rendelendoMennyiseg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termek_oldal);

        auth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        getSupportActionBar().setTitle("név");

        String termekNeve = getIntent().getStringExtra("termekNeve");
        double termekSulya = getIntent().getDoubleExtra("termekSulya", 0);
        double termekegysegara = getIntent().getDoubleExtra("termekegysegara", 0);
        double termekDbSZama = getIntent().getDoubleExtra("termekDbSZama", 0);
        termekKepe = getIntent().getStringExtra("termekKepe");
        regiKep = getIntent().getStringExtra("termekKepe");
        uzletId = getIntent().getStringExtra("uzletId");
        termekId = getIntent().getStringExtra("termekId");
        kepTermek = findViewById(R.id.kep2);
        termekNeveBolt = findViewById(R.id.termekNeveBolt);
        termekSulyBolt = findViewById(R.id.termekSulyBolt);
        termekAraBolt = findViewById(R.id.termekAraBolt);
        termekMaxRendelheto = findViewById(R.id.termekMaxRendelheto);
        rendelenoMennyisegLayout = findViewById(R.id.rendelenoMennyisegLayout);
        rendelendoMennyiseg = findViewById(R.id.rendelendoMennyiseg);

        Uri uri = Uri.parse(termekKepe);
        Glide.with(this).load(uri).into(kepTermek);
        termekNeveBolt.setText(termekNeve);
        //termekSulyBolt.setText(termekSulya);
        //termekAraBolt.setText(termekegysegara);
        //termekMaxRendelheto.setText( termekDbSZama);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_menu, menu);
        View view = menu.findItem(R.id.kosar).getActionView();
        view.setOnClickListener(v -> startActivity(new Intent(TermekOldalActivity.this, KosarActivity.class)));
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
}