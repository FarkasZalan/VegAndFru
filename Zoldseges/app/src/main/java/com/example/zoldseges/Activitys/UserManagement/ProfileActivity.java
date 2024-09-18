package com.example.zoldseges.Activitys.UserManagement;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.Activitys.UserManagement.Seller.StoreManagementActivity;
import com.example.zoldseges.Activitys.CartActivity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db; // Firestore database reference
    private FirebaseAuth auth;  // Firebase Authentication instance

    //  UI Components
    private TextView profilePageTitle;
    private Button ordersButton;
    private Button logoutButton;
    private Button sellerStoreButton;
    private Button continueButton;
    private Button cancelButton;
    private Button personalDataButton;
    private Button customerServiceButton;
    private Button termsButton;

    // for a small security check if want to access to the user or store data
    private TextView passwordLabel;
    private EditText passwordField;
    public boolean wantsToAccessData;

    // for authentication
    private LinearLayout loadingProgress;

    // store if the user is seller otherwise orders
    public boolean isOrderOrStore;
    public boolean isSeller;

    private DocumentReference userDocumentReference;
    private FirebaseUser currentUser;

    // Cart Management
    private MenuItem cartMenuItem;
    private FrameLayout cartBadgeLayout;
    private TextView cartBadgeCountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profil");

        // Initialize Firebase and Firestore
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // if no user is logged in then redirect to login page
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            currentUser = auth.getCurrentUser();
            if (auth.getCurrentUser() == null) {
                super.onBackPressed();
                startActivity(new Intent(this, LoginActivity.class));
            }

            // Initialize UI elements
            profilePageTitle = findViewById(R.id.cimProfil);
            profilePageTitle.setText(R.string.profilom);
            ordersButton = findViewById(R.id.rendeleseim);
            sellerStoreButton = findViewById(R.id.bolt);
            personalDataButton = findViewById(R.id.adataim);
            logoutButton = findViewById(R.id.kijelentkezes);
            customerServiceButton = findViewById(R.id.ugyfelszolgalat);
            termsButton = findViewById(R.id.aszf);
            loadingProgress = findViewById(R.id.ellenorzoProgress);

            continueButton = findViewById(R.id.beleptet);
            cancelButton = findViewById(R.id.megse);
            passwordLabel = findViewById(R.id.pswLbL);
            passwordField = findViewById(R.id.pswTetx);

            try {
                // Retrieve user document from Firestore and check if they are a seller
                userDocumentReference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                checkIfSeller();
            } catch (Exception e) {
                super.onBackPressed();
                auth.signOut();
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        }

    }

    // Check if the user is a seller
    public void checkIfSeller() {
        userDocumentReference.addSnapshotListener(this, (value, error) -> {
            assert value != null;
            if (Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat")) {
                sellerStoreButton.setVisibility(View.VISIBLE);
                ordersButton.setText(R.string.rendelesek);
            } else {
                sellerStoreButton.setVisibility(View.GONE);
                ordersButton.setText(R.string.rendeleseim);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        profilePageTitle.setText(R.string.profilom);

        // If the user is not logged in, redirect to the login activity
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Hide the loading progress and password fields, then show main profile options
            loadingProgress.setVisibility(View.GONE);
            passwordField.setVisibility(View.GONE);
            passwordLabel.setVisibility(View.GONE);
            continueButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            profilePageTitle.setText(R.string.profilom);

            // Show buttons for accessing personal details, orders, etc.
            personalDataButton.setVisibility(View.VISIBLE);
            ordersButton.setVisibility(View.VISIBLE);
            checkIfSeller();
            customerServiceButton.setVisibility(View.VISIBLE);
            termsButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        // Find the cart menu and display if the current user type is not seller
        cartMenuItem = menu.findItem(R.id.kosarfiok);
        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;
                String userType = value.getString("felhasznaloTipus");
                if (userType == null) {
                    auth.signOut();
                    finish();
                } else {
                    cartMenuItem.setVisible(!userType.equals("Eladó cég/vállalat"));
                }
            });
        } else {
            cartMenuItem.setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Close the current activity and go back to the previous activity (home)
            finish();
            super.onBackPressed();
            return true;
        }
        if (item.getItemId() == R.id.kosarfiok) {
            // Go to Cart activity
            startActivity(new Intent(ProfileActivity.this, CartActivity.class));
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
        cartBadgeLayout = cartMenuItemRootView.findViewById(R.id.kosar_mennyiseg_szamlalo);
        cartBadgeCountTextView = cartMenuItemRootView .findViewById(R.id.kosar_mennyiseg_szamlalo_text);

        // Update the cart badge visibility and count based on the number of items in the cart
        if (cartItemList != null && cartItemList.size() != 0) {
            cartBadgeLayout.setVisibility(View.VISIBLE);
            cartBadgeCountTextView.setText(String.valueOf(cartItemList.size()));
        } else {
            cartBadgeLayout.setVisibility(View.GONE);
        }

        // Call the onOptionsItemSelected method and this method handle the menu item selection
        cartMenuItemRootView.setOnClickListener(view -> {
            // Trigger the same behavior as when the cart menu item is selected
            onOptionsItemSelected(menuItem);
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void onLogOut(View view) {
        // Creating an AlertDialog for the logout confirmation
        AlertDialog.Builder logoutAlertBuilder = new AlertDialog.Builder(this);
        logoutAlertBuilder.setTitle("Kijelentkezés");
        logoutAlertBuilder.setIcon(R.mipmap.ic_launcher);
        String logoutMessage;

        // Setting the logout message based on cart content
        if (cartItemList.size() == 0) {
            logoutMessage = "Bizttosan ki szeretnél jelentkezni?";
        } else {
            logoutMessage = "Bizttosan ki szeretnél jelentkezni?\nÍgy a kosarad tartalma is törlődni fog!";
        }
        logoutAlertBuilder.setMessage(logoutMessage);

        // Make the dialog cancellable
        logoutAlertBuilder.setCancelable(true);

        AlertDialog logoutAlert = logoutAlertBuilder.create();

        // Setting the positive "Log Out" button with a listener to handle sign-out
        logoutAlert.setButton(DialogInterface.BUTTON_POSITIVE, "Kijelentkezés", (dialog, which) -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Sikeres kijelentkezés", Toast.LENGTH_LONG).show();

            // Clear cart if it exists
            if (cartItemList != null) {
                cartItemList.clear();
            }
            ProfileActivity.super.onBackPressed();
            finish();
        });

        // Cancel button
        logoutAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Mégse", (dialog, which) -> logoutAlert.dismiss());
        logoutAlert.show();

        // Customizing the "Log Out" button with color and text color
        int color = getResources().getColor(R.color.red, getTheme());
        int textColor = getResources().getColor(R.color.white, getTheme());
        logoutAlert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(color);
        logoutAlert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(textColor);
    }

    public void displaySecurityCheck() {
        // Set the profile title based on whether the user is accessing data or managing orders/store
        if (wantsToAccessData && !isOrderOrStore) {
            profilePageTitle.setText(R.string.profilom);
        }
        if (isOrderOrStore && !isSeller) {
            profilePageTitle.setText(R.string.rendeleseim);
        }

        if (isOrderOrStore && isSeller) {
            profilePageTitle.setText(R.string.bolt_kezelese);
        }

        passwordField.setVisibility(View.VISIBLE);
        passwordField.setText("");
        passwordLabel.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);

        personalDataButton.setVisibility(View.GONE);
        ordersButton.setVisibility(View.GONE);
        sellerStoreButton.setVisibility(View.GONE);
        customerServiceButton.setVisibility(View.GONE);
        termsButton.setVisibility(View.GONE);
        logoutButton.setVisibility(View.GONE);
    }

    public void onPersonalData(View view) {
        wantsToAccessData = true;
        isOrderOrStore = false;
        displaySecurityCheck();
    }

    // Hide the security check fields and reset the page to the default state
    public void onCancel(View view) {
        passwordField.setVisibility(View.GONE);
        passwordLabel.setVisibility(View.GONE);
        continueButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        wantsToAccessData = false;
        profilePageTitle.setText(R.string.profilom);
        isOrderOrStore = false;
        personalDataButton.setVisibility(View.VISIBLE);
        ordersButton.setVisibility(View.VISIBLE);
        checkIfSeller();
        customerServiceButton.setVisibility(View.VISIBLE);
        termsButton.setVisibility(View.VISIBLE);
        logoutButton.setVisibility(View.VISIBLE);
    }

    // Hide loading progress and show profile details when content is being loaded
    public void showElements() {
        loadingProgress.setVisibility(View.GONE);
        continueButton.setVisibility(View.VISIBLE);
        cancelButton.setVisibility(View.VISIBLE);
        passwordField.setVisibility(View.VISIBLE);
        passwordLabel.setVisibility(View.VISIBLE);
    }

    // Show loading progress and hide product details when content is being loaded
    public void hideElements() {
        loadingProgress.setVisibility(View.VISIBLE);
        continueButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
        passwordField.setVisibility(View.GONE);
        passwordLabel.setVisibility(View.GONE);
    }

    // Check if the password is correct to access to the data
    public void onSecurityCheck(View view) {
        if (passwordField.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Előbb meg kell adnod a jelszavad!", Toast.LENGTH_LONG).show();
            showElements();
        } else {
            hideElements();
            auth.signInWithEmailAndPassword(Objects.requireNonNull(currentUser.getEmail()), passwordField.getText().toString()).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    openActivityManager();
                } else {
                    Toast.makeText(getApplicationContext(), "Hibás jelszó!", Toast.LENGTH_LONG).show();
                    showElements();
                }
            });
        }
    }
    public void openActivityManager() {

        if (wantsToAccessData && !isOrderOrStore) {
            startActivity(new Intent(this, UserDetailsActivity.class));
        } else {
            if (isOrderOrStore && !isSeller) {
                startActivity(new Intent(this, OrdersActivity.class));
            }
            if (isOrderOrStore && isSeller) {
                startActivity(new Intent(this, StoreManagementActivity.class));
            }

        }
    }

    public void onOrders(View view) {
        isSeller = false;
        isOrderOrStore = true;
        displaySecurityCheck();
    }

    public void onStoreManagementPage(View view) {
        isSeller = true;
        isOrderOrStore = true;
        displaySecurityCheck();
    }

    public void onCostumerServicePage(View view) {
        startActivity(new Intent(this, CustomerServiceActivity.class));
    }

    public void onTermsPage(View view) {
        startActivity(new Intent(this, TermsActivity.class));

    }
}