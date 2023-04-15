package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zoldseges.R;

public class TermekSzerkeszteseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termek_szerkesztese);
        String termekNeve = getIntent().getStringExtra("termekNeve");
        double termekSulya = getIntent().getDoubleExtra("termekSulya", 0);
        double termekegysegara = getIntent().getDoubleExtra("termekegysegara", 0);
        double termekDbSZama = getIntent().getDoubleExtra("termekDbSZama", 0);
        String termekKepe = getIntent().getStringExtra("termekKepe");
        String uzletId = getIntent().getStringExtra("uzletId");
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
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}