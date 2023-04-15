package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado.BoltKezelesActivity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class FiokActicity extends AppCompatActivity {


    private TextView cimProfil;

    private Button rendelesek;
    private Button kijelentkezes;
    private Button bolt;

    private Button tovabb;
    private Button megse;
    private Button adataim;

    private Button ugyfelszolgalat;
    private Button aszf;

    private TextView pswLbL;
    private EditText psw;
    public boolean eladeE;
    public boolean adatokhozAkarMenni;

    public boolean rendelesVagyBolt;

    private DocumentReference reference;
    private FirebaseAuth auth;
    private FirebaseUser felhasznalo;

    private LinearLayout ellenorzoProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiok);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        felhasznalo = auth.getCurrentUser();
        if (auth.getCurrentUser() == null) {
            super.onBackPressed();
            startActivity(new Intent(this, BejelentkezesActivity.class));
        }

        cimProfil = findViewById(R.id.cimProfil);
        rendelesek = findViewById(R.id.rendeleseim);
        bolt = findViewById(R.id.bolt);
        adataim = findViewById(R.id.adataim);
        kijelentkezes = findViewById(R.id.kijelentkezes);
        ugyfelszolgalat = findViewById(R.id.ugyfelszolgalat);
        aszf = findViewById(R.id.aszf);
        ellenorzoProgress = findViewById(R.id.ellenorzoProgress);

        tovabb = findViewById(R.id.beleptet);
        megse = findViewById(R.id.megse);
        pswLbL = findViewById(R.id.pswLbL);
        psw = findViewById(R.id.pswTetx);

        try {
            reference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
            eladoE();
        } catch (Exception e) {
            super.onBackPressed();
            auth.signOut();
            FirebaseAuth.getInstance().signOut();
            finish();
        }


    }

    public void eladoE() {
        reference.addSnapshotListener(this, (value, error) -> {
            assert value != null;
            if (Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat")) {
                bolt.setVisibility(View.VISIBLE);
                bolt.animate();
                rendelesek.setVisibility(View.INVISIBLE);
            } else {
                bolt.setVisibility(View.INVISIBLE);
                rendelesek.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ellenorzoProgress.setVisibility(View.GONE);
        psw.setVisibility(View.GONE);
        pswLbL.setVisibility(View.GONE);
        tovabb.setVisibility(View.GONE);
        megse.setVisibility(View.GONE);
        cimProfil.setText(R.string.profilom);

        adataim.setVisibility(View.VISIBLE);
        eladoE();
        ugyfelszolgalat.setVisibility(View.VISIBLE);
        aszf.setVisibility(View.VISIBLE);
        kijelentkezes.setVisibility(View.VISIBLE);
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

    public void onLogOut(View view) {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(getApplicationContext(), "Sikeres kijelentkezés", Toast.LENGTH_LONG).show();
        super.onBackPressed();
        finish();
    }

    public void vedelemBe() {
        if (adatokhozAkarMenni && !rendelesVagyBolt) {
            cimProfil.setText(R.string.profilom);
        }
        if (rendelesVagyBolt && !eladeE) {
            cimProfil.setText(R.string.rendeleseim);
        }

        if (rendelesVagyBolt && eladeE) {
            cimProfil.setText(R.string.bolt_kezelese);
        }
        psw.setVisibility(View.VISIBLE);
        psw.setText("");
        pswLbL.setVisibility(View.VISIBLE);
        tovabb.setVisibility(View.VISIBLE);
        megse.setVisibility(View.VISIBLE);
        adataim.setVisibility(View.GONE);
        rendelesek.setVisibility(View.GONE);
        bolt.setVisibility(View.GONE);
        ugyfelszolgalat.setVisibility(View.GONE);
        aszf.setVisibility(View.GONE);
        kijelentkezes.setVisibility(View.GONE);
    }

    public void onAdatok(View view) {
        adatokhozAkarMenni = true;
        rendelesVagyBolt = false;
        vedelemBe();
    }

    public void onMegse(View view) {
        psw.setVisibility(View.GONE);
        pswLbL.setVisibility(View.GONE);
        tovabb.setVisibility(View.GONE);
        megse.setVisibility(View.GONE);
        adatokhozAkarMenni = false;
        cimProfil.setText(R.string.profilom);
        rendelesVagyBolt = false;
        adataim.setVisibility(View.VISIBLE);
        eladoE();
        ugyfelszolgalat.setVisibility(View.VISIBLE);
        aszf.setVisibility(View.VISIBLE);
        kijelentkezes.setVisibility(View.VISIBLE);
    }

    public void megjelenit() {
        ellenorzoProgress.setVisibility(View.GONE);
        tovabb.setVisibility(View.VISIBLE);
        megse.setVisibility(View.VISIBLE);
        psw.setVisibility(View.VISIBLE);
        pswLbL.setVisibility(View.VISIBLE);
    }

    public void elrejt() {
        ellenorzoProgress.setVisibility(View.VISIBLE);
        tovabb.setVisibility(View.GONE);
        megse.setVisibility(View.GONE);
        psw.setVisibility(View.GONE);
        pswLbL.setVisibility(View.GONE);
    }

    public void onTovabb(View view) {
        if (psw.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Előbb meg kell adnod a jelszavad!", Toast.LENGTH_LONG).show();
            megjelenit();
        } else {
            elrejt();
            auth.signInWithEmailAndPassword(Objects.requireNonNull(felhasznalo.getEmail()), psw.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    megnyito();
                } else {
                    Toast.makeText(getApplicationContext(), "Hibás jelszó!", Toast.LENGTH_LONG).show();
                    megjelenit();
                }
            });
        }
    }

    public void megnyito() {

        if (adatokhozAkarMenni && !rendelesVagyBolt) {
            startActivity(new Intent(this, AdataimActivity.class));
        } else {
            if (rendelesVagyBolt && !eladeE) {
                startActivity(new Intent(this, RendeleseimActivity.class));
            }
            if (rendelesVagyBolt && eladeE) {
                startActivity(new Intent(this, BoltKezelesActivity.class));
            }

        }
    }

    public void onRendelesek(View view) {
        eladeE = false;
        rendelesVagyBolt = true;
        vedelemBe();
    }

    public void onBoltKezelese(View view) {
        eladeE = true;
        rendelesVagyBolt = true;
        vedelemBe();
    }

    public void onUgyfelszolgalat(View view) {
        startActivity(new Intent(this, UgyfelszolgalatActivity.class));
    }

    public void onAszf(View view) {
        startActivity(new Intent(this, AszfActicity.class));

    }
}