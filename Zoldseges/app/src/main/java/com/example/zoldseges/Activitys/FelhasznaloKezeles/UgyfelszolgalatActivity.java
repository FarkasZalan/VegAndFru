package com.example.zoldseges.Activitys.FelhasznaloKezeles;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zoldseges.Activitys.KosarActivity;
import com.example.zoldseges.R;

import java.util.Objects;

public class UgyfelszolgalatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ugyfelszolgalat);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ÁSZF");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vissza_bejelentkezett_menu, menu);
        MenuItem kosar = menu.findItem(R.id.kosarfiok);
        kosar.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onVissza(View view) {
        super.onBackPressed();
    }
    public void onDiscord(View view) {
        Intent proba = getPackageManager().getLaunchIntentForPackage("com.discord.gg/Kolbász#9234");

        if (proba == null) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/Kolbász#9234")));
        } else {
            startActivity(proba);
        }
    }
    public void onFacebook(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/zalan.farkas.73")));
    }
}