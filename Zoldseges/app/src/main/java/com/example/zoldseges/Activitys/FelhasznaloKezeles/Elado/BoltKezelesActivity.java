package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zoldseges.Activitys.FelhasznaloKezeles.RegisztracioActivity;
import com.example.zoldseges.R;

public class BoltKezelesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_kezeles);
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

    public void onVissza(View view) {
        super.onBackPressed();
    }

    public void onUjTermek(View view) {
        startActivity(new Intent(this, UjTermekActivity.class));
    }
}