package com.example.zoldseges.Activitys.FelhasznaloKezeles;

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

public class ElfelejtettJelszoActivity extends AppCompatActivity {

    private EditText resetEmailpsw;
    FirebaseAuth auth;
    private TextView uzenetpswReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elfelejtett_jelszo);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Jelszó visszaállítás");
        resetEmailpsw = findViewById(R.id.resetEmailpsw);
        uzenetpswReset = findViewById(R.id.uzenetpswReset);
        auth = FirebaseAuth.getInstance();
    }

    public void onEmailKuldese(View view) {
        if (!resetEmailpsw.getText().toString().isEmpty()) {

            String email = resetEmailpsw.getText().toString();
            boolean validEmail = isEmailValid(email);
            if (validEmail) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    try {
                        if (task.isSuccessful()) {
                            hideKeyboard(ElfelejtettJelszoActivity.this);
                            uzenetpswReset.startAnimation(AnimationUtils.loadAnimation(ElfelejtettJelszoActivity.this, R.anim.egy_oszlopos_animacio));
                            uzenetpswReset.setVisibility(View.VISIBLE);

                        } else {
                            if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                Toast.makeText(getApplicationContext(), "Nincs ilyen email címmel regisztrált felhasználó!", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (NullPointerException exception) {
                        Toast.makeText(getApplicationContext(), "Váratlan hiba történt, kérjük ismételje meg a műveletet később!", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Érvénytelen email címet adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}