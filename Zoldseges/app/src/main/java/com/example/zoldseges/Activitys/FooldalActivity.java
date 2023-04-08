package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.zoldseges.Activitys.FelhasznaloKezeles.BejelentkezesActivity;
import com.example.zoldseges.Activitys.FelhasznaloKezeles.FiokActicity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FooldalActivity extends AppCompatActivity {


    private TextView korText;

    private int kosarMennyiseg = 0;

    private FirebaseUser felhasznalo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        felhasznalo = auth.getCurrentUser();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.kereso);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQueryHint("Keresés...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kosar:
                startActivity(new Intent(this, KosarActivity.class));
                return true;
            case R.id.fiok:
                if (felhasznalo != null) {
                    startProfile();
                } else {
                    startLogin();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void startProfile() {
        startActivity(new Intent(this, FiokActicity.class));
    }

    public void startLogin() {
        startActivity(new Intent(this, BejelentkezesActivity.class));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosar);
        FrameLayout rootVieww = (FrameLayout) menuItem.getActionView();

        FrameLayout kor = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo);
        korText = rootVieww.findViewById(R.id.kosar_mennyiseg_szamlalo_text);

        rootVieww.setOnClickListener(view -> {
            onOptionsItemSelected(menuItem);//tehát meghívja az optionItemSelected függvényt és az fog történni amit oda megadok
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void updateCart() {
        kosarMennyiseg++;
        if (kosarMennyiseg > 0) {
            korText.setText(String.valueOf(kosarMennyiseg));
        } else {
            korText.setText("");
        }
    }
}