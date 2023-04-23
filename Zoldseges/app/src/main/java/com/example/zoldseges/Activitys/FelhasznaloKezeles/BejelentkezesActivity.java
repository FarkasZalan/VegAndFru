package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.Activitys.KosarActivity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class BejelentkezesActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    private Button bejelentkezesButton;
    private Button regisztracio;
    private TextView email;
    private TextView jelszo;
    private TextView bejelentkezesText;
    private ProgressBar progressBarBejelentkezes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bejelentkezes);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bejelentkezés");
        auth = FirebaseAuth.getInstance();
        FirebaseUser felhasznalo = auth.getCurrentUser();
        if (felhasznalo != null) {
            super.onBackPressed();
            startActivity(new Intent(this, FiokActicity.class));
            finish();
        }

        email = findViewById(R.id.emailLogin);
        jelszo = findViewById(R.id.jelszoLogin);
        bejelentkezesText = findViewById(R.id.bejelentkezesText);
        progressBarBejelentkezes = findViewById(R.id.progressBarBejelentkezes);
        bejelentkezesButton = findViewById(R.id.bejelentkezesButton);
        regisztracio = findViewById(R.id.regisztracio);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_bejelentkezett_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.kosarfiok) {
            startActivity(new Intent(BejelentkezesActivity.this, KosarActivity.class));
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

    public void onLogin(View view) {
        String emailAdress = email.getText().toString();
        String jelszoTxt = jelszo.getText().toString();
        if (!emailAdress.equals("") && !jelszoTxt.equals("")) {
            if (isEmailValid(emailAdress)) {
                progressBarBejelentkezes.setVisibility(View.VISIBLE);
                bejelentkezesText.setVisibility(View.VISIBLE);
                email.setVisibility(View.GONE);
                jelszo.setVisibility(View.GONE);
                bejelentkezesButton.setVisibility(View.GONE);
                regisztracio.setVisibility(View.GONE);

                auth.signInWithEmailAndPassword(emailAdress, jelszoTxt).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Sikeres bejelentkezés!", Toast.LENGTH_LONG).show();
                        kosarLista.clear();
                        onProfil();
                    } else {
                        if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            megjelenit();
                            Toast.makeText(getApplicationContext(), "Nincs ilyen email címmel regisztrált felhasználó!", Toast.LENGTH_LONG).show();
                        }
                        if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                            megjelenit();
                            Toast.makeText(getApplicationContext(), "Rossz jelszót adtál meg!", Toast.LENGTH_LONG).show();
                        }

                    }
                }).addOnFailureListener(e -> {
                    megjelenit();
                });
            } else {
                Toast.makeText(getApplicationContext(), "Érvénytelen email címet adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Ahhoz hogy be tudj jelentkezni minden adatot meg kell hogy adj!", Toast.LENGTH_LONG).show();
        }
    }

    public void megjelenit() {
        progressBarBejelentkezes.setVisibility(View.GONE);
        bejelentkezesText.setVisibility(View.GONE);
        email.setVisibility(View.VISIBLE);
        jelszo.setVisibility(View.VISIBLE);
        bejelentkezesButton.setVisibility(View.VISIBLE);
        regisztracio.setVisibility(View.VISIBLE);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void onProfil() {
        super.onBackPressed();
        startActivity(new Intent(this, FiokActicity.class));
        finish();
    }

    public void onRegisterOpen(View view) {
        super.onBackPressed();
        startActivity(new Intent(this, RegisztracioActivity.class));
    }
}