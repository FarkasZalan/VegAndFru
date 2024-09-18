package com.example.zoldseges.Activitys;

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
import android.widget.TextView;
import com.example.zoldseges.Activitys.UserManagement.LoginActivity;
import com.example.zoldseges.Activitys.UserManagement.ProfileActivity;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class ReceiptActivity extends AppCompatActivity {
    private FirebaseAuth auth; // Firebase Authentication for user management
    private FirebaseFirestore db; // Firestore database reference

    // UI Components
    private TextView orderedProductsTextView;
    private TextView sellerDetailsTextView;
    private TextView generalDetailsTextView;

    // Variables for storing receipt ID and payment status
    String receiptId;
    boolean isPostPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        // Initialize Firebase Firestore and Authentication
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Check if the user is logged in, if not redirect to the main page
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(ReceiptActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }

        // Set up action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Button references and UI initialization
        Button receiptConfirmationButton = findViewById(R.id.nyugtaRendben);
        Button receiptBackButton = findViewById(R.id.nyugtaVissza);
        orderedProductsTextView = findViewById(R.id.rendeltTermekek);
        TextView successfulOrderTextView = findViewById(R.id.sikeresRendelesText);
        sellerDetailsTextView = findViewById(R.id.eladoAdatai);
        generalDetailsTextView = findViewById(R.id.altalanosAdatok);

        orderedProductsTextView.setText("");
        sellerDetailsTextView.setText("");

        // Get receipt ID and payment status from intent
        receiptId = getIntent().getStringExtra("receiptId");
        isPostPayment = getIntent().getBooleanExtra("isPostPayment", false);

        // Configure UI based on whether this is post-payment or just a receipt viewing
        if (isPostPayment) {
            successfulOrderTextView.setText(R.string.sikeres_rendeles); // Successful order message
            receiptConfirmationButton.setVisibility(View.VISIBLE);
            receiptBackButton.setVisibility(View.GONE);
        } else {
            receiptConfirmationButton.setVisibility(View.GONE);
            receiptBackButton.setVisibility(View.VISIBLE);
            successfulOrderTextView.setText(R.string.nyugta);  // Receipt message
        }

        // Retrieve and display data from Firestore
        getDataFromDatabase();

        // Set action bar title
        getSupportActionBar().setTitle(R.string.nyugta);
    }

    /**
     * Fetches receipt data from Firestore and populates the UI components with the relevant information.
     */
    private void getDataFromDatabase() {
        // Fetch the document corresponding to the receipt ID
        DocumentReference receiptDocRef = db.collection("nyugtak").document(receiptId);
        receiptDocRef.addSnapshotListener((receiptRef, error) -> {
            assert receiptRef != null;

            // Append product details if they haven't been added yet
            if (!orderedProductsTextView.getText().toString().contains("Termékek:")) {
                orderedProductsTextView.append("Termékek: ");
            }
            if (!orderedProductsTextView.getText().toString().contains(Objects.requireNonNull(receiptRef.getString("termekek")))) {
                orderedProductsTextView.append("\n\n" + Objects.requireNonNull(receiptRef.getString("termekek")));
            }

            // Add customer details
            if (!orderedProductsTextView.getText().toString().contains("Rendelő adatai:")) {
                orderedProductsTextView.append("\n\nRendelő adatai: ");
            }
            if (!orderedProductsTextView.getText().toString().contains("Rendelő email címe: " + receiptRef.getString("rendeleoEmail"))) {
                orderedProductsTextView.append("\n\n\nRendelő email címe: " + receiptRef.getString("rendeleoEmail"));
            }

            // Check if tax number is provided, if not, just add the customer's name
            if (Objects.requireNonNull(receiptRef.getString("rendeloAdoszama")).isEmpty() || receiptRef.getString("rendeloAdoszama") == null) {
                if (!orderedProductsTextView.getText().toString().contains("Rendelő neve: " + receiptRef.getString("rendeloNev"))) {
                    orderedProductsTextView.append("\n\nRendelő neve: " + receiptRef.getString("rendeloNev"));
                }
            } else {
                // Add name, tax number, and billing address
                if (!orderedProductsTextView.getText().toString().contains("Rendelő neve: " + receiptRef.getString("rendeloNev"))) {
                    orderedProductsTextView.append("\n\nRendelő neve: " + receiptRef.getString("rendeloNev"));
                }
                if (!orderedProductsTextView.getText().toString().contains("Rendelő adószáma: " + receiptRef.getString("rendeloAdoszama"))) {
                    orderedProductsTextView.append("\n\nRendelő adószáma: " + receiptRef.getString("rendeloAdoszama"));
                }
                if (!orderedProductsTextView.getText().toString().contains("Rendelő számlázási címe: " + receiptRef.getString("rendeloSzekhely"))) {
                    orderedProductsTextView.append("\n\nRendelő számlázási címe: " + receiptRef.getString("rendeloSzekhely"));
                }
            }

            // Add remaining customer details like phone number and shipping address
            if (!orderedProductsTextView.getText().toString().contains("Rendelő telefonszáma: " + receiptRef.getString("rendeloTelefonszam"))) {
                orderedProductsTextView.append("\n\nRendelő telefonszáma: " + receiptRef.getString("rendeloTelefonszam"));
            }
            if (!orderedProductsTextView.getText().toString().contains("Szállítási cím: " + receiptRef.getString("rendeloSzallitasiCim"))) {
                orderedProductsTextView.append("\n\nSzállítási cím: " + receiptRef.getString("rendeloSzallitasiCim"));
            }

            // Add store details
            if (!sellerDetailsTextView.getText().toString().contains("Üzlet adatai:")) {
                sellerDetailsTextView.append("\n\nÜzlet adatai: ");
            }
            if (!sellerDetailsTextView.getText().toString().contains("Rendelő neve: " + receiptRef.getString("uzletNeve"))) {
                sellerDetailsTextView.append("\n\n\nÜzlet neve: " + receiptRef.getString("uzletNeve"));
            }
            if (!sellerDetailsTextView.getText().toString().contains("Üzlet email címe: " + receiptRef.getString("uzletEmailCIm"))) {
                sellerDetailsTextView.append("\n\nÜzlet email címe: " + receiptRef.getString("uzletEmailCIm"));
            }
            if (!sellerDetailsTextView.getText().toString().contains("Értesítési címe: " + receiptRef.getString("uzletErtesitesiCim"))) {
                sellerDetailsTextView.append("\n\nÉrtesítési címe: " + receiptRef.getString("uzletErtesitesiCim"));
            }
            if (!sellerDetailsTextView.getText().toString().contains("Üzlet telefonszáma: " + receiptRef.getString("uzletTelefonszam"))) {
                sellerDetailsTextView.append("\n\nÜzlet telefonszáma: " + receiptRef.getString("uzletTelefonszam"));
            }

            // Add general order details like date, ID, and total price
            if (!generalDetailsTextView.getText().toString().contains("\nRendelés időpontja: " + Objects.requireNonNull(receiptRef.getString("idopont")))) {
                generalDetailsTextView.append("\n\n\nRendelés időpontja: " + Objects.requireNonNull(receiptRef.getString("idopont")));
            }
            if (!generalDetailsTextView.getText().toString().contains("\nRendelés azonosító: " + receiptId)) {
                generalDetailsTextView.append("\n\nRendelés azonosító: " + receiptId);
            }
            if (!generalDetailsTextView.getText().toString().contains("\nVégösszeg: " + Objects.requireNonNull(receiptRef.getString("vegosszeg")) + " Ft")) {
                generalDetailsTextView.append("\n\nVégösszeg: " + Objects.requireNonNull(receiptRef.getString("vegosszeg")) + " Ft" + "\n(+5000 Ft szállítási költség)");
            }
        });
    }

    // The toolbar when the user is at the receipt page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.back_menu, menu);
        View view = menu.findItem(R.id.kosar).getActionView();
        MenuItem cartItem = menu.findItem(R.id.kosar);
        cartItem.setVisible(false); // Hides the cart button initially
        MenuItem accountItem = menu.findItem(R.id.fiok);
        accountItem.setVisible(false); // Hides account button
        view.setOnClickListener(v -> startActivity(new Intent(ReceiptActivity.this, CartActivity.class)));
        return super.onCreateOptionsMenu(menu);
    }

    // Handle toolbar navigation
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle account and navigation menu item selection
        if (item.getItemId() == R.id.fiok) {
            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }
        if (item.getItemId() == android.R.id.home) {
            // Handle back navigation
            if (auth.getCurrentUser() == null) {
                finish();
                Intent intent = new Intent(ReceiptActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            if (isPostPayment) {
                finish();
                Intent intent = new Intent(ReceiptActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                finish();
                super.onBackPressed();
            }
        }
        if (item.getItemId() == R.id.kosar) {
            startActivity(new Intent(ReceiptActivity.this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    // Handle cart menu item display
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem cartMenuItem = menu.findItem(R.id.kosar);

        // Get the root view of the cart menu item (which contains the cart badge layout)
        FrameLayout cartView = (FrameLayout) cartMenuItem.getActionView();

        // Find the cart badge layout and text view (different item quantity) in the custom menu item layout
        FrameLayout quantityBadge = cartView.findViewById(R.id.kosar_mennyiseg_szamlalo);
        TextView quantityTextView = cartView.findViewById(R.id.kosar_mennyiseg_szamlalo_text);

        // Update the cart badge visibility and count based on the number of items in the cart
        if (cartItemList != null && cartItemList.size() != 0) {
            quantityBadge.setVisibility(View.VISIBLE);
            quantityTextView.setText(String.valueOf(cartItemList.size()));
        } else {
            quantityBadge.setVisibility(View.GONE);
        }

        // Call the onOptionsItemSelected method and this method handle the menu item selection
        cartView.setOnClickListener(view -> onOptionsItemSelected(cartMenuItem));

        return super.onPrepareOptionsMenu(menu);
    }

    // Navigate back twice and finish activity when user presses the "Back" button
    public void onBack(View view) {

        super.onBackPressed();
        super.onBackPressed();
        finish();
    }

    // Redirect to main page when user presses the "Main Page" button
    public void onMainPage(View view) {
        finish();
        Intent intent = new Intent(ReceiptActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //  If user is not authenticated, redirect to the main page
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(ReceiptActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }
}