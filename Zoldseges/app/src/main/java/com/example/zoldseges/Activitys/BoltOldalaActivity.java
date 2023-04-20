package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.zoldseges.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BoltOldalaActivity extends AppCompatActivity {

    StorageReference storageReference;
    FirebaseFirestore db;

    String boltKepe;
    String uzletId;
    String tulajId;
    String szekhely;
    String uzletNeve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_oldala);

        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        db = FirebaseFirestore.getInstance();

        boltKepe = getIntent().getStringExtra("boltKepe");
        uzletNeve = getIntent().getStringExtra("uzletNeve");
        tulajId = getIntent().getStringExtra("tulajId");
        uzletId = getIntent().getStringExtra("uzletId");
        szekhely = getIntent().getStringExtra("szekhely");

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
}