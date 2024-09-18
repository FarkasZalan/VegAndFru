package com.example.zoldseges.Activitys.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.example.zoldseges.R;
import java.util.Objects;

public class CustomerServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_costumer_service);

        // Ensure the Action Bar has a back button and set the title to "Ügyfélszolgálat"
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ügyfélszolgálat");
    }

    // The toolbar when the user is at the customer service page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the options menu
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        // Find the cart menu item and hide it as it is not needed here
        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);
        cartMenuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar's back button click event
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the current activity
            super.onBackPressed(); // Trigger the default back button action
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to handle the back button click on the screen
    public void onBack(View view) {
        super.onBackPressed();
    }

    // Method to handle the Discord button click
    public void onDiscord(View view) {
        // Attempt to launch the Discord app using a specific user link
        Intent discordIntent = getPackageManager().getLaunchIntentForPackage("com.discord.gg/Kolbász#9234");

        if (discordIntent == null) {
            // If Discord is not installed, open the URL in a web browser
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/Kolbász#9234")));
        } else {
            // If Discord is installed, launch the app with the given user link
            startActivity(discordIntent);
        }
    }

    // Method to handle the Facebook button click
    public void onFacebook(View view) {
        // Open the user's Facebook profile using an intent
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/zalan.farkas.73")));
    }
}