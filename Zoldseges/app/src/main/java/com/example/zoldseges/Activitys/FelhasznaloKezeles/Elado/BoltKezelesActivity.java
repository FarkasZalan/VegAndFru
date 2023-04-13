package com.example.zoldseges.Activitys.FelhasznaloKezeles.Elado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zoldseges.DAOS.Termek;
import com.example.zoldseges.DAOS.TermekAdapter;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class BoltKezelesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;

    private ArrayList<Termek> termekLista;
    String uzletId;
    FirebaseAuth auth;
    private TermekAdapter termekAdapter;

    DatabaseReference databaseReference;
    CollectionReference uzletReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bolt_kezeles);

        db = FirebaseFirestore.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.termekekRecyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();


        termekLista = new ArrayList<>();
        clearAll();
        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        DocumentReference felhasznaloReference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        felhasznaloReference.addSnapshotListener((value, error) -> {
            assert value != null;
            uzletId = value.getString("uzletId");
            uzletReference = db.collection("uzletek").document(uzletId).collection("termekek");
            uzletReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        clearAll();
                        for (QueryDocumentSnapshot adat : task.getResult()) {
                            Termek termek = new Termek();

                            termek.setTermekKepe(adat.getString("termekKepe"));
                            termek.setNev(adat.getString("termekNeve"));
                            termekLista.add(termek);
                        }
                        termekAdapter = new TermekAdapter(getApplicationContext(), termekLista);
                        recyclerView.setAdapter(termekAdapter);
                        termekAdapter.notifyDataSetChanged();
                    }
                }
            });
            // adapterMegjelenit();
        });
    }

    private void adapterMegjelenit() {
        Query query = databaseReference.child("uzletek").child(uzletId).child("termekek");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clearAll();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Termek termek = new Termek();

                    termek.setTermekKepe(Objects.requireNonNull(data.child("termekKepe").getValue()).toString());
                    termek.setNev(Objects.requireNonNull(data.child("termekNeve").getValue()).toString());

                    termekLista.add(termek);
                }

                termekAdapter = new TermekAdapter(getApplicationContext(), termekLista);
                recyclerView.setAdapter(termekAdapter);
                termekAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void clearAll() {
        if (termekLista != null) {
            termekLista.clear();

            if (termekAdapter != null) {
                termekAdapter.notifyDataSetChanged();
            }
        } else {
            termekLista = new ArrayList<>();
        }
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