package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.Objects;

public class TermekOldalActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    String termekKepe;
    String uzletId;
    String termekId;

    ImageView kepTermek;
    TextView termekNeveBolt;
    TextView termekSulyBolt;
    TextView termekAraBolt;
    TextView termekMaxRendelheto;
    LinearLayout rendelenoMennyisegLayout;
    LinearLayout termekSulyBoltLayout;
    LinearLayout termekArBoltLayout;
    LinearLayout maxMennyisegBoltLayout;
    EditText rendelendoMennyiseg;
    Button kosarbaTermekOldal;
    ProgressBar progressBarTermekBetolt;
    TextView termekBetoltText;
    double termekSulya;

    int termekDbSZama;
    double termekegysegara;
    FirebaseFirestore db;

    private MenuItem kosar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termek_oldal);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        kepTermek = findViewById(R.id.kep2);
        termekNeveBolt = findViewById(R.id.termekNeveBolt);
        termekSulyBolt = findViewById(R.id.termekSulyBolt);
        termekAraBolt = findViewById(R.id.termekAraBolt);
        termekMaxRendelheto = findViewById(R.id.termekMaxRendelheto);
        rendelenoMennyisegLayout = findViewById(R.id.rendelenoMennyisegLayout);
        rendelendoMennyiseg = findViewById(R.id.rendelendoMennyiseg);
        termekSulyBoltLayout = findViewById(R.id.termekSulyBoltLayout);
        termekArBoltLayout = findViewById(R.id.termekArBoltLayout);
        maxMennyisegBoltLayout = findViewById(R.id.maxMennyisegBoltLayout);
        kosarbaTermekOldal = findViewById(R.id.kosarbaTermekOldal);
        progressBarTermekBetolt = findViewById(R.id.progressBarTermekBetolt);
        termekBetoltText = findViewById(R.id.termekBetoltText);

        String termekNeve = getIntent().getStringExtra("termekNeve");
        termekSulya = getIntent().getDoubleExtra("termekSulya", 0);
        termekegysegara = getIntent().getDoubleExtra("termekAra", 0);
        if (termekegysegara % 1 == 0) {
            termekegysegara = (int) termekegysegara;
        }
        termekDbSZama = (int) getIntent().getDoubleExtra("termekKeszlet", 0);
        termekKepe = getIntent().getStringExtra("termekKepe");
        uzletId = getIntent().getStringExtra("uzletId");
        termekId = getIntent().getStringExtra("termekId");

        getSupportActionBar().setTitle(termekNeve);

        eltuntet();
        kepMegjelenitese(termekKepe);
        termekNeveBolt.setText(termekNeve);
        String ar;
        String keszlet;
        if (termekSulya == -1.0) {
            termekSulyBoltLayout.setVisibility(View.GONE);
            ar = (int) termekegysegara + " Ft/db";
            keszlet = termekDbSZama + " db";
            rendelendoMennyiseg.setHint(R.string.rendelendo_mennyiseg_db);
        } else {
            String suly = termekSulya + " kg";
            termekSulyBolt.setText(suly);
            termekSulyBoltLayout.setVisibility(View.VISIBLE);
            rendelendoMennyiseg.setHint(R.string.rendelendo_mennyiseg_kg);
            ar = (int) termekegysegara + " Ft/kg";
            keszlet = termekDbSZama + " kg";
        }

        termekAraBolt.setText(ar);
        termekMaxRendelheto.setText(keszlet);
    }

    public void eladoE() {
        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;
                String tipus = value.getString("felhasznaloTipus");
                assert tipus != null;
                if (tipus.equals("Eladó cég/vállalat")) {
                    kosarbaTermekOldal.setVisibility(View.GONE);
                    rendelenoMennyisegLayout.setVisibility(View.GONE);
                } else {
                    kosarbaTermekOldal.setVisibility(View.VISIBLE);
                    rendelenoMennyisegLayout.setVisibility(View.VISIBLE);
                }
            });
        } else {
            kosarbaTermekOldal.setVisibility(View.VISIBLE);
            rendelenoMennyisegLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        eltuntet();
        kepMegjelenitese(termekKepe);
        invalidateOptionsMenu();
    }

    public void kepMegjelenitese(String url) {
        if (url != null && !url.isEmpty()) {

            Uri uri = Uri.parse(url);
            try {
                if (!this.isFinishing()) {
                    Glide.with(TermekOldalActivity.this).load(uri).placeholder(R.drawable.standard_item_picture).listener(new RequestListener<Drawable>() {
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
                    }).into(kepTermek);
                }
            } catch (Exception e) {
                megjelenit();
                int forground = getResources().getColor(R.color.ures_kep, getTheme());
                kepTermek.setForeground(new ColorDrawable(forground));
                kepTermek.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(TermekOldalActivity.this).load(R.drawable.standard_item_picture).into(kepTermek);
            }
        } else {
            megjelenit();
            kepTermek.setScaleType(ImageView.ScaleType.FIT_CENTER);
            int forground = getResources().getColor(R.color.ures_kep, getTheme());
            kepTermek.setForeground(new ColorDrawable(forground));
            Glide.with(TermekOldalActivity.this).load(R.drawable.standard_item_picture).into(kepTermek);
        }
    }

    public void eltuntet() {
        progressBarTermekBetolt.setVisibility(View.VISIBLE);
        termekBetoltText.setVisibility(View.VISIBLE);
        termekNeveBolt.setVisibility(View.GONE);
        kepTermek.setVisibility(View.INVISIBLE);
        kosarbaTermekOldal.setVisibility(View.GONE);
        maxMennyisegBoltLayout.setVisibility(View.GONE);
        termekArBoltLayout.setVisibility(View.GONE);
        termekSulyBoltLayout.setVisibility(View.GONE);
        rendelenoMennyisegLayout.setVisibility(View.GONE);
    }

    public void megjelenit() {
        progressBarTermekBetolt.setVisibility(View.GONE);
        termekBetoltText.setVisibility(View.GONE);
        termekNeveBolt.setVisibility(View.GONE);
        kepTermek.setVisibility(View.VISIBLE);
        eladoE();
        maxMennyisegBoltLayout.setVisibility(View.VISIBLE);
        termekArBoltLayout.setVisibility(View.VISIBLE);
        if (termekSulya != -1.0) {
            termekSulyBoltLayout.setVisibility(View.VISIBLE);
        }
        rendelenoMennyisegLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_menu, menu);
        View view = menu.findItem(R.id.kosar).getActionView();
        kosar = menu.findItem(R.id.kosar);
        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;
                String tipus = value.getString("felhasznaloTipus");
                assert tipus != null;
                kosar.setVisible(!tipus.equals("Eladó cég/vállalat"));
            });
        } else {
            kosar.setVisible(true);
        }
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

    public void onKosarba(View view) {
        if (!rendelendoMennyiseg.getText().toString().isEmpty()) {
            int rendelendo = Integer.parseInt(rendelendoMennyiseg.getText().toString());

        } else {
            Toast.makeText(getApplicationContext(), "Előbb meg kell adnod, hogy mennyit szeretnél rendelni ebből a termékből!", Toast.LENGTH_LONG).show();
        }

    }
}