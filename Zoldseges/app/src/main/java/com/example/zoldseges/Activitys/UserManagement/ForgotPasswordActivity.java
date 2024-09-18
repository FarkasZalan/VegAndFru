package com.example.zoldseges.Activitys.UserManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    FirebaseAuth auth;  // Firebase Authentication instance

    // this is the email address to which we will send the password reset link
    private EditText userEmailForResetPassword;

    // This is the message what the user returns after the button click
    private TextView resetPasswordMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Jelszó visszaállítás");

        // Initialize Firebase and UI components
        userEmailForResetPassword = findViewById(R.id.resetEmailpsw);
        resetPasswordMessage = findViewById(R.id.uzenetpswReset);

        auth = FirebaseAuth.getInstance();
    }


    public void onSendEmail(View view) {
        // Check if the email field is not empty
        if (!userEmailForResetPassword.getText().toString().isEmpty()) {

            String email = userEmailForResetPassword.getText().toString();
            boolean validEmail = isEmailValid(email); // Validate email format

            if (validEmail) {
                // Send the password reset email using Firebase Authentication
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful()) {
                            hideKeyboard(ForgotPasswordActivity.this); // Hide the keyboard

                            // Animate and show the reset password message
                            resetPasswordMessage.startAnimation(AnimationUtils.loadAnimation(ForgotPasswordActivity.this, R.anim.one_column_animation));
                            resetPasswordMessage.setVisibility(View.VISIBLE);

                        } else {
                            // Handle case when email is not registered
                            if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                Toast.makeText(getApplicationContext(), "Nincs ilyen email címmel regisztrált felhasználó!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (NullPointerException exception) {
                        // Catch unexpected errors
                        Toast.makeText(getApplicationContext(), "Váratlan hiba történt, kérjük ismételje meg a műveletet később!", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // Show a toast for invalid email format
                Toast.makeText(getApplicationContext(), "Érvénytelen email címet adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
            // Show a toast if the email field is empty
            Toast.makeText(getApplicationContext(), "Hogy visszaállítsd a jelszavad muszáj megadnod az email címed!", Toast.LENGTH_LONG).show();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // The toolbar when the user is at the store page
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);
        cartMenuItem.setVisible(false);
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
        return super.onOptionsItemSelected(item);
    }
}