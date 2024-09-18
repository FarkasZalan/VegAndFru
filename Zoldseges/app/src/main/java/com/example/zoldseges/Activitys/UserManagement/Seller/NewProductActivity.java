package com.example.zoldseges.Activitys.UserManagement.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoldseges.Activitys.CartActivity;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NewProductActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 1; // Request code for selecting an image
    private FirebaseFirestore db;  // Firestore database reference

    //  UI Components
    private TextView productNameTextView;
    private TextView addTitleText;
    private TextView productPriceTextView;
    private TextView productAverageWeightTextView;
    private TextView productStockTextView;
    private Button saveButton;
    private Button backToAddProductButton;

    // switch for quantity or weight price
    private SwitchCompat unitSwitch;
    private LinearLayout unitSwitchLayout;
    private boolean shouldMeasureByWeight = false;

    // UI elements to display when the user not add image to the product yet
    private TextView productImageText;
    private ImageView productImageView;
    private Uri imageUrl;
    private String productImagePath = "";
    private StorageReference storageReference; // Firebase Storage reference

    // Progress and loading indicators
    private ProgressBar progressBar;
    private TextView saveText;

    // variables for the new Product
    private String storeId;
    private Product newProduct;
    private String productId;
    private String allProductsCollectionId;
    private DocumentReference productDocumentRef;
    private DocumentReference allProductsDocumentRef;

    // Data list for product save
    private Map<String, Object> productMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Termék hozzáadása");

        // Initialize Firebase and UI components
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        productNameTextView = findViewById(R.id.termekNeve);
        addTitleText = findViewById(R.id.hozzaadasCim);
        productPriceTextView = findViewById(R.id.termekAra);
        unitSwitchLayout = findViewById(R.id.mertekegysegValasztoLayout);
        productAverageWeightTextView = findViewById(R.id.termekSulyaAtlagosan);
        unitSwitch = findViewById(R.id.mertekegysegValaszto);
        productStockTextView = findViewById(R.id.termekKeszlet);
        productImageView = findViewById(R.id.elsoTermekKep);
        productImageText = findViewById(R.id.elsoTermekCim);
        saveButton = findViewById(R.id.mentes);
        backToAddProductButton = findViewById(R.id.visszaTermekHozzaad);
        saveText = findViewById(R.id.mentestext);
        progressBar = findViewById(R.id.progressBar);

        // Enable rounded corners for the product image view (radius setting)
        productImageView.setClipToOutline(true);

        // Determine if the product should be measured by weight
        shouldMeasureByWeight = isMeasuredByWeight();

        // Fetch store ID from the user's Firestore document
        DocumentReference userReference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
        userReference.addSnapshotListener((value, error) -> {
            assert value != null;
            storeId = value.getString("uzletId");
        });
    }

    /**
     * Determines whether the product is measured by weight or quantity.
     * Updates the UI based on the selected unit type.
     * @return True if the product should be measured by weight, false otherwise.
     */
    private boolean isMeasuredByWeight() {
        // Listener for the unit switch toggle
        unitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // If the switch is turned on, the product is measured by weight
                shouldMeasureByWeight = true;
                productAverageWeightTextView.setVisibility(View.VISIBLE);
                productPriceTextView.setHint(R.string.termek_egysegara_kg);
                productStockTextView.setHint(R.string.keszleten_levok_kg);
                productPriceTextView.setText("");
            } else {
                // If the switch is turned off, the product is measured by quantity
                shouldMeasureByWeight = false;
                productAverageWeightTextView.setVisibility(View.GONE);
                productPriceTextView.setText("");
                productStockTextView.setHint(R.string.keszleten_levok_darabszama);
                productPriceTextView.setHint(R.string.termek_egysegara_db);
            }
        });
        return shouldMeasureByWeight;
    }

    // The toolbar when the user is at the registration page
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        // Find the cart menu item for visibility
        View view = menu.findItem(R.id.kosarfiok).getActionView();
        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);
        cartMenuItem.setVisible(false);

        // if clicked to the cart menu item
        view.setOnClickListener(v -> startActivity(new Intent(NewProductActivity.this, CartActivity.class)));
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

    public void onVisszaBolthoz(View view) {
        super.onBackPressed();
    }

    public void onSave(View view) {
        // Check if the product should be priced by weight or quantity
        shouldMeasureByWeight = unitSwitch.isChecked();

        // Ensure all required fields are filled in based on whether we are measuring by weight or quantity
        if ((!shouldMeasureByWeight && !productNameTextView.getText().toString().isEmpty() && !productPriceTextView.getText().toString().isEmpty() && !productStockTextView.getText().toString().isEmpty())
                || (shouldMeasureByWeight && !productNameTextView.getText().toString().isEmpty() && !productPriceTextView.getText().toString().isEmpty() && !productStockTextView.getText().toString().isEmpty() && !productAverageWeightTextView.getText().toString().isEmpty())) {

            // Ensure the numerical values don't exceed the maximum allowed limit
            if ((shouldMeasureByWeight && Double.parseDouble(productStockTextView.getText().toString()) <= 2147483647 && Double.parseDouble(productPriceTextView.getText().toString()) <= 2147483647 && Double.parseDouble(productAverageWeightTextView.getText().toString()) <= 2147483647) ||
                    (!shouldMeasureByWeight && Double.parseDouble(productStockTextView.getText().toString()) <= 2147483647 && Double.parseDouble(productPriceTextView.getText().toString()) <= 2147483647)) {

                // Hide UI elements while saving to avoid duplicate actions
                hideElements();

                String name = productNameTextView.getText().toString();
                double stockCount = Double.parseDouble(productStockTextView.getText().toString());
                double price = Double.parseDouble(productPriceTextView.getText().toString());
                double weight;

                // Set weight based on the product should price by quantity or weight
                if (!shouldMeasureByWeight) {
                    weight = -1;
                } else {
                    weight = Double.parseDouble(productAverageWeightTextView.getText().toString());
                }

                // Prevent any zero or negative values for price, stock, or weight
                if (stockCount <= 0 || price <= 0 || (shouldMeasureByWeight && weight <= 0)) {
                    Toast.makeText(getApplicationContext(), "Nem adhatsz meg semminek sem 0 értéket!", Toast.LENGTH_LONG).show();
                    showElements();
                } else {
                    productDocumentRef = db.collection("uzletek").document(storeId).collection("termekek").document();
                    allProductsDocumentRef = db.collection("osszesTermek").document();
                    allProductsCollectionId = allProductsDocumentRef.getId();

                    // Create a map to save new product to the all product collection
                    Map<String, Object> allProductsMap = new HashMap<>();
                    allProductsMap.put("termekNeve", name);
                    allProductsMap.put("uzletId", storeId);
                    allProductsDocumentRef.set(allProductsMap);

                    // Get the unique product ID and create a new product object
                    productId = productDocumentRef.getId();
                    newProduct = new Product(name, price, stockCount, weight, productImagePath, storeId, allProductsCollectionId);

                    // If an image URL is available, upload it
                    if (imageUrl != null) {
                        imageUpload(imageUrl, weight);
                    } else {
                        // If no image is uploaded, save the product data to Firestore
                        productMap = newProduct.addNewProduct(newProduct);
                        productDocumentRef.set(productMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Sikeres mentés! ", Toast.LENGTH_LONG).show();
                                NewProductActivity.super.onBackPressed();
                                finish();
                            }
                        });
                    }
                }
            } else { // Error handling
                Toast.makeText(getApplicationContext(), "Túl nagy értékeket adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "A csillaggal jelölt mezőket kötelező kitölteni!", Toast.LENGTH_LONG).show();
        }
    }

    // Hide loading progress and show product details when content is being loaded
    public void showElements() {
        progressBar.setVisibility(View.GONE);
        saveText.setVisibility(View.GONE);
        this.productImageView.setVisibility(View.VISIBLE);
        this.productNameTextView.setVisibility(View.VISIBLE);
        this.productPriceTextView.setVisibility(View.VISIBLE);
        this.productStockTextView.setVisibility(View.VISIBLE);
        this.productImageText.setVisibility(View.VISIBLE);
        this.unitSwitchLayout.setVisibility(View.VISIBLE);

        isMeasuredByWeight();
        saveButton.setVisibility(View.VISIBLE);
        backToAddProductButton.setVisibility(View.VISIBLE);
        addTitleText.setVisibility(View.VISIBLE);
    }

    // Show loading indicators and display product details once content is available
    public void hideElements() {
        progressBar.setVisibility(View.VISIBLE);
        saveText.setVisibility(View.VISIBLE);
        this.productImageView.setVisibility(View.GONE);
        this.productNameTextView.setVisibility(View.GONE);
        this.productPriceTextView.setVisibility(View.GONE);
        this.unitSwitchLayout.setVisibility(View.GONE);
        this.productStockTextView.setVisibility(View.GONE);
        this.productImageText.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        backToAddProductButton.setVisibility(View.GONE);
        addTitleText.setVisibility(View.GONE);
    }

    //-------------------------------------- The following section handles the image upload process---------------------------------------------------------------
    public void onProductImageUpload(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT); // Open the image gallery
        gallery.setType("image/*");  // Restrict to image files only
        startActivityForResult(gallery, IMAGE_REQUEST_CODE); // Start the activity to select an image
    }

    // Check if the image was successfully selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            productImageView.setImageURI(imageUrl);
        }
    }

    public void imageUpload(Uri uri, double productWeight) {
        // Generate a timestamp to uniquely name the image based on the current time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        StorageReference imageNameReference = storageReference.child("termek_" + storeId + "_" + timeStamp);

        // Upload the image to Firebase Storage
        imageNameReference.putFile(uri)
                .addOnSuccessListener(taskSnapshot ->
                        imageNameReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            imageNameReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> uploadTask) {
                    if (uploadTask.isSuccessful()) {
                        productImagePath = String.valueOf(uploadTask.getResult());
                        productDocumentRef = db.collection("uzletek").document(storeId).collection("termekek").document(productId);

                        // Create a new Product object with the provided details and image path
                        newProduct = new Product(productNameTextView.getText().toString(),
                                Double.parseDouble(productPriceTextView.getText().toString()),
                                Integer.parseInt(productStockTextView.getText().toString()),
                                productWeight,
                                productImagePath,
                                storeId,
                                allProductsCollectionId
                        );

                        // Convert the product object into a map for Firestore storage
                        productMap = newProduct.addNewProduct(newProduct);

                        // Save the product map to Firestore under the product document
                        productDocumentRef.set(productMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Sikeres mentés! ", Toast.LENGTH_LONG).show();
                                NewProductActivity.super.onBackPressed();
                                finish();
                            }
                        });
                    }
                }
            });
        })).addOnProgressListener(snapshot -> {
            hideElements();
        }).addOnFailureListener(e -> showElements());
    }
}