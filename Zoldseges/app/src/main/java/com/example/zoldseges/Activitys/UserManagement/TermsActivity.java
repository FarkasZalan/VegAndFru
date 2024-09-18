package com.example.zoldseges.Activitys.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zoldseges.R;

import java.util.Objects;

public class TermsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Ensure the Action Bar has a back button and set the title to "ÁSZF"
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ászf");
    }

    // The toolbar when the user is at the terms page
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the options menu
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        // Find the cart menu item and hide it as it is not needed here
        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);
        cartMenuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // Handle item selection from the toolbar options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar's back button click event
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the current activity
            super.onBackPressed();  // Trigger the default back button action
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to handle the back button click on the screen
    public void onBack(View view) {
        finish();
        super.onBackPressed();
    }
}