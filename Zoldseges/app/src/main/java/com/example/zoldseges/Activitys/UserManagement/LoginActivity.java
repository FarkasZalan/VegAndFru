package com.example.zoldseges.Activitys.UserManagement;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;

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

import com.example.zoldseges.Activitys.CartActivity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase Authentication instance

    // UI Elements
    private Button loginButton;
    private Button registrationButton;
    private Button forgotPasswordButton;
    private TextView emailTextView;
    private TextView passwordTextView;

    // Progress and loading indicators
    private TextView loginMessageTextView;
    private ProgressBar loginProgressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Bejelentkezés");

        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        // If the user is already logged in, navigate to the ProfileActivity
        if (currentUser != null) {
            super.onBackPressed();
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }

        // Initialize UI elements
        emailTextView = findViewById(R.id.emailLogin);
        passwordTextView = findViewById(R.id.jelszoLogin);
        loginMessageTextView = findViewById(R.id.bejelentkezesText);
        loginProgressBar = findViewById(R.id.progressBarBejelentkezes);
        loginButton = findViewById(R.id.bejelentkezesButton);
        registrationButton = findViewById(R.id.regisztracio);
        forgotPasswordButton = findViewById(R.id.elfelejtettJelszoButton);
    }

    // The toolbar when the user is at the store page
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);
        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);
        if (auth.getCurrentUser() == null) {
            cartMenuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    // Handle item selection from the toolbar options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.kosarfiok) {
            startActivity(new Intent(LoginActivity.this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    // Prepare the cart menu item and update the cart badge based on cart item count
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosarfiok);

        // Get the root view of the cart menu item (which contains the cart badge layout)
        FrameLayout cartMenuItemRootView = (FrameLayout) menuItem.getActionView();

        // Find the cart badge layout and text view in the custom menu item layout
        FrameLayout cartBadgeLayout = cartMenuItemRootView.findViewById(R.id.kosar_mennyiseg_szamlalo);

        // Update the cart badge visibility and count based on the number of items in the cart
        TextView cartBadgeCountTextView = cartMenuItemRootView.findViewById(R.id.kosar_mennyiseg_szamlalo_text);
        if (cartItemList != null && cartItemList.size() != 0) {
            cartBadgeLayout.setVisibility(View.VISIBLE); // Show the cart badge
            cartBadgeCountTextView.setText(String.valueOf(cartItemList.size())); // Display the number of items in the cart
        } else {
            cartBadgeLayout.setVisibility(View.GONE); // Hide the cart badge if the cart is empty
        }

        // Call the onOptionsItemSelected method and this method handle the menu item selection
        cartMenuItemRootView.setOnClickListener(view -> {
            onOptionsItemSelected(menuItem);
        });

        return super.onPrepareOptionsMenu(menu);
    }

    // Method to handle login button click
    public void onLogin(View view) {
        String emailAdress = emailTextView.getText().toString();
        String passwordText = passwordTextView.getText().toString();

        // Check if the email and password fields are not empty
        if (!emailAdress.equals("") && !passwordText.equals("")) {
            if (isEmailValid(emailAdress)) {
                // Show progress bar and hide other UI elements while login is in progress
                loginProgressBar.setVisibility(View.VISIBLE);
                loginMessageTextView.setVisibility(View.VISIBLE);
                emailTextView.setVisibility(View.GONE);
                passwordTextView.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);
                registrationButton.setVisibility(View.GONE);
                forgotPasswordButton.setVisibility(View.GONE);

                // Attempt to log in using Firebase Authentication
                auth.signInWithEmailAndPassword(emailAdress, passwordText).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Successful login, display message and navigate to profile
                        Toast.makeText(getApplicationContext(), "Sikeres bejelentkezés!", Toast.LENGTH_LONG).show();
                        onProfil();
                    } else {
                        // Handle different error cases (user not found or incorrect password)
                        if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")) {
                            showElements();
                            Toast.makeText(getApplicationContext(), "Nincs ilyen emailTextView címmel regisztrált felhasználó!", Toast.LENGTH_LONG).show();
                        }
                        if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                            showElements();
                            Toast.makeText(getApplicationContext(), "Rossz jelszót adtál meg!", Toast.LENGTH_LONG).show();
                        }

                    }
                }).addOnFailureListener(e -> {
                    showElements();
                });
            } else {
                Toast.makeText(getApplicationContext(), "Érvénytelen emailTextView címet adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Ahhoz hogy be tudj jelentkezni minden adatot meg kell hogy adj!", Toast.LENGTH_LONG).show();
        }
    }

    // Hide loading progress and show product details when content is being loaded
    public void showElements() {
        loginProgressBar.setVisibility(View.GONE);
        loginMessageTextView.setVisibility(View.GONE);
        emailTextView.setVisibility(View.VISIBLE);
        passwordTextView.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        registrationButton.setVisibility(View.VISIBLE);
        forgotPasswordButton.setVisibility(View.VISIBLE);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Go to profile activity and clear cart list
    public void onProfil() {
        super.onBackPressed();
        cartItemList.clear();
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }

    public void onRegisterOpen(View view) {
        super.onBackPressed();
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    // Open forgot password activity
    public void onResetPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}