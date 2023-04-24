package com.example.zoldseges;

import static com.example.zoldseges.Activitys.TermekOldalActivity.kosarLista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.Activitys.FizetesActivity;
import com.example.zoldseges.Activitys.KosarActivity;
import com.example.zoldseges.Activitys.TermekOldalActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class NyugtaActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private MenuItem kosar;
    private FirebaseFirestore db;

    private FrameLayout kor;
    private TextView korSzamlalo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nyugta);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);


        getSupportActionBar().setTitle("Fizetés");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_menu, menu);
        View view = menu.findItem(R.id.kosar).getActionView();
        kosar = menu.findItem(R.id.kosar);
        kosar.setVisible(kosarLista.size() != 0);
        view.setOnClickListener(v -> startActivity(new Intent(NyugtaActivity.this, KosarActivity.class)));
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
            startActivity(new Intent(NyugtaActivity.this, KosarActivity.class));
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
}