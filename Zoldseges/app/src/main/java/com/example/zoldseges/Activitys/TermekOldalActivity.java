package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.example.zoldseges.DAOS.KosarElem;
import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    private String termekNeve;

    String ossztermekCollection;
    String keszlet;

    private FrameLayout kor;
    private TextView korSzamlalo;
    private TextView bejlelentkezKosarhoz;

    public static ArrayList<KosarElem> kosarLista = new ArrayList<>();

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
        bejlelentkezKosarhoz = findViewById(R.id.bejlelentkezKosarhoz);
        termekBetoltText = findViewById(R.id.termekBetoltText);

        termekNeve = getIntent().getStringExtra("termekNeve");
        termekSulya = getIntent().getDoubleExtra("termekSulya", 0);
        termekegysegara = getIntent().getDoubleExtra("termekAra", 0);
        if (termekegysegara % 1 == 0) {
            termekegysegara = (int) termekegysegara;
        }
        termekDbSZama = (int) getIntent().getDoubleExtra("termekKeszlet", 0);
        termekKepe = getIntent().getStringExtra("termekKepe");
        uzletId = getIntent().getStringExtra("uzletId");
        termekId = getIntent().getStringExtra("termekId");
        ossztermekCollection = getIntent().getStringExtra("ossztermekCollection");

        getSupportActionBar().setTitle(termekNeve);
        eltuntet();
        kepMegjelenitese(termekKepe);
        termekNeveBolt.setText(termekNeve);
        String ar;

        if (termekSulya == -1.0) {
            termekSulyBoltLayout.setVisibility(View.GONE);
            ar = " " + (int) termekegysegara + " Ft/db";
            keszlet = " " + termekDbSZama + " db";
            rendelendoMennyiseg.setHint(R.string.rendelendo_mennyiseg_db);
        } else {
            String suly = " " + termekSulya + " kg";
            termekSulyBolt.setText(suly);
            termekSulyBoltLayout.setVisibility(View.VISIBLE);
            rendelendoMennyiseg.setHint(R.string.rendelendo_mennyiseg_kg);
            ar = " " + (int) termekegysegara + " Ft/kg";
            keszlet = " " + termekDbSZama + " kg";
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
        rendelendoMennyiseg.setText("");
        bejelentkezSpan();
        eltuntet();
        kepMegjelenitese(termekKepe);
        invalidateOptionsMenu();

    }

    private void bejelentkezSpan() {
        if (auth.getCurrentUser() != null) {
            bejlelentkezKosarhoz.setVisibility(View.GONE);
        } else {
            bejlelentkezKosarhoz.setVisibility(View.VISIBLE);
            SpannableString bejelentkezes = new SpannableString("Már van fiókod? Jelentkezz be!");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    startActivity(new Intent(TermekOldalActivity.this, BejelentkezesActivity.class));
                }
            };
            bejelentkezes.setSpan(clickableSpan, 16, 30, 0);
            bejelentkezes.setSpan(new URLSpan(""), 16, 30, 0);
            bejelentkezes.setSpan(new ForegroundColorSpan(ContextCompat.getColor(TermekOldalActivity.this, R.color.purple_500)), 16, 30, 0);

            bejlelentkezKosarhoz.setMovementMethod(LinkMovementMethod.getInstance());

            bejlelentkezKosarhoz.setText(bejelentkezes, TextView.BufferType.SPANNABLE);
        }
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
        termekNeveBolt.setVisibility(View.VISIBLE);
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
            kosar.setVisible(false);
        }
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
        if (item.getItemId() == R.id.kosar) {
            startActivity(new Intent(TermekOldalActivity.this, KosarActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosar);

        FrameLayout rootVieww = (FrameLayout) menuItem.getActionView();
        kor = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo);
        korSzamlalo = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo_text);
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

    public void onKosarba(View view) {
        if (auth.getCurrentUser() != null) {
            if (!rendelendoMennyiseg.getText().toString().isEmpty()) {
                double rendelendo = Double.parseDouble(rendelendoMennyiseg.getText().toString());
                if (rendelendo <= termekDbSZama) {
                    if (rendelendo > 0) {
                        Termek termek = new Termek(termekNeve, termekegysegara, termekDbSZama, termekSulya, termekKepe, uzletId, ossztermekCollection);
                        termek.setSajatId(termekId);
                        KosarElem ujElem = new KosarElem(termek, rendelendo);
                        if (kosarLista.size() == 0) {
                            kosarLista.add(ujElem);
                            Toast.makeText(getApplicationContext(), "Sikeresen hozzáadtad a kosaradhoz!", Toast.LENGTH_LONG).show();
                            korSzamlalo.setText(String.valueOf(kosarLista.size()));
                            kor.setVisibility(View.VISIBLE);
                        } else {
                            boolean benneVan = false;
                            for (KosarElem kosar : kosarLista) {
                                if (kosar.getTermek().getSajatId().equals(this.termekId)) {
                                    kosar.setMennyiseg(rendelendo);
                                    benneVan = true;
                                    Toast.makeText(getApplicationContext(), "Sikeresen frissítetted a kosaradat!", Toast.LENGTH_LONG).show();
                                    korSzamlalo.setText(String.valueOf(kosarLista.size()));
                                    kor.setVisibility(View.VISIBLE);
                                }
                            }
                            if (!benneVan) {
                                boolean masikUzlet = false;
                                for (KosarElem egyezik : kosarLista) {
                                    if (!ujElem.getTermek().getUzletId().equals(egyezik.getTermek().getUzletId())) {
                                        masikUzlet = true;
                                    }
                                }
                                if (masikUzlet) {
                                    alert(ujElem);
                                } else {
                                    kosarLista.add(ujElem);
                                    Toast.makeText(getApplicationContext(), "Sikeresen hozzáadtad a kosaradhoz!", Toast.LENGTH_LONG).show();
                                    korSzamlalo.setText(String.valueOf(kosarLista.size()));
                                    kor.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Semmiből sem rendelhetsz 0-t!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Maximum csak " + keszlet + "-ot rendelhetsz ebből a termékből!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Előbb meg kell adnod, hogy mennyit szeretnél rendelni ebből a termékből!", Toast.LENGTH_LONG).show();
            }
        } else {
            bejelentkezSpan();
            Toast.makeText(getApplicationContext(), "Előbb be kell jelentkezned ahhoz, hogy rendelhess valamit!", Toast.LENGTH_LONG).show();
        }
    }

    public void alert(KosarElem ujElem) {
        AlertDialog.Builder torlesLaertBuilder = new AlertDialog.Builder(this);
        torlesLaertBuilder.setTitle("Eltávolítod a korábbi termékeket?");
        torlesLaertBuilder.setIcon(R.mipmap.ic_launcher);
        torlesLaertBuilder.setMessage("Már másik üzlet terméke is a kosaradban van. Ki szeretnéd venni őket?");
        torlesLaertBuilder.setCancelable(true);

        AlertDialog torlesAlert = torlesLaertBuilder.create();

        torlesAlert.setButton(DialogInterface.BUTTON_POSITIVE, "Kosár ürítése", (dialog, which) -> {
            kosarLista.clear();

            kosarLista.add(ujElem);
            Toast.makeText(getApplicationContext(), "Sikeresen hozzáadtad a kosaradhoz!", Toast.LENGTH_LONG).show();
            korSzamlalo.setText(String.valueOf(kosarLista.size()));
            kor.setVisibility(View.VISIBLE);
            invalidateMenu();
        });


        torlesAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Mégse", (dialog, which) -> torlesAlert.dismiss());
        torlesAlert.show();
        torlesAlert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
        torlesAlert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white, getTheme()));
    }
}