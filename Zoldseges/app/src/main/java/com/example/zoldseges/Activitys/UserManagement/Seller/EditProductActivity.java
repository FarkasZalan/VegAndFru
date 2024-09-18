package com.example.zoldseges.Activitys.UserManagement.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.Activitys.CartActivity;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProductActivity extends AppCompatActivity {
    private FirebaseFirestore db; // Firestore database reference

    // UI elements
    private TextView titleEditProduct;
    private EditText productNameEdit;
    private EditText productPriceEdit;
    private EditText productStockEdit;
    private ImageView productImageEdit;
    private TextView productImageLabelEdit;
    private Button saveButtonEditProduct;
    private Button backButtonEditProduct;
    private EditText averageWeightEdit;

    // switch for quantity or weight price
    private LinearLayout unitSwitchLayout;
    private SwitchCompat unitSwitch;
    private boolean shouldMeasureByWeight;

    // Progress and loading indicators
    private ProgressBar progressBarEditProduct;
    private TextView saveTextEditProduct;

    // image upload/edit
    private StorageReference storageReference; // Firebase Storage reference
    private Uri imageUrl;
    private String productImage;
    private String oldImage;
    private String allProductsCollectionId;

    // Map for product data
    private Map<String, Object> productMap = new HashMap<>();

    // variables to edit product
    private String productId;
    private String storeId;
    private DocumentReference productReference;
    double productStockCount;
    private DocumentReference allProductsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Termék módosítása");

        // Firebase Storage reference for product images
        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        db = FirebaseFirestore.getInstance();

        String productName = getIntent().getStringExtra("productName");
        double productWeight = getIntent().getDoubleExtra("productWeight", 0);
        double productUnitPrice = getIntent().getDoubleExtra("productUnitPrice", 0);

        // Getting product details from the intent
        productStockCount = getIntent().getDoubleExtra("productStockCount", 0);
        productImage = getIntent().getStringExtra("productImage");
        oldImage = getIntent().getStringExtra("productImage");
        storeId = getIntent().getStringExtra("storeId");
        productId = getIntent().getStringExtra("productId");
        allProductsCollectionId = getIntent().getStringExtra("allProductsCollectionId");

        // Reference to the product document in Firestore
        productReference = db.collection("uzletek").document(storeId).collection("termekek").document(productId);

        // Initializing UI elements
        titleEditProduct = findViewById(R.id.cimTermekModositas);
        productNameEdit = findViewById(R.id.termekNeveTermekModositas);
        unitSwitchLayout = findViewById(R.id.mertekegysegValasztoTermekModositasLayout);
        unitSwitch = findViewById(R.id.mertekegysegValasztoTermekModositas);
        averageWeightEdit = findViewById(R.id.termekSulyaAtlagosanTermekModositas);
        productPriceEdit = findViewById(R.id.termekAraTermekModositas);
        productStockEdit = findViewById(R.id.termekKeszletTermekModositas);
        progressBarEditProduct = findViewById(R.id.progressBarTermekModositas);
        saveTextEditProduct = findViewById(R.id.mentestextTermekModositas);
        productImageEdit = findViewById(R.id.termekKepTermekModositas);
        productImageLabelEdit = findViewById(R.id.termekCimTermekModositas);
        saveButtonEditProduct = findViewById(R.id.mentesTermekModositas);
        backButtonEditProduct = findViewById(R.id.visszaTermekModositas);

        // Enable rounded corners for the product image view (radius setting)
        productImageEdit.setClipToOutline(true);

        productNameEdit.setText(productName);
        String stockText;
        String priceText;

        // Set the view based on whether the product is measured by weight or by quantity
        if (productWeight == -1) {
            unitSwitch.setChecked(false);
            averageWeightEdit.setVisibility(View.GONE);
            averageWeightEdit.setText("");
            priceText = (int) productUnitPrice + " Ft/db";
            productPriceEdit.setHint(priceText);
            productPriceEdit.setText(String.valueOf((int) productUnitPrice));
            stockText = (int) productStockCount + " db";
        } else {
            unitSwitch.setChecked(true);
            String weightText = productWeight + " kg";
            averageWeightEdit.setHint(weightText);
            averageWeightEdit.setText(String.valueOf(productWeight));
            priceText = (int) productUnitPrice + " Ft/kg";
            productPriceEdit.setHint(priceText);
            productPriceEdit.setText(String.valueOf((int) productUnitPrice));
            averageWeightEdit.setVisibility(View.VISIBLE);
            stockText = (int) productStockCount + " kg";
        }
        productStockEdit.setHint(stockText);
        productStockEdit.setText(String.valueOf((int) productStockCount));

        // Check if the product is measured by weight or quantity
        shouldMeasureByWeight = isMeasuredByWeight();

        // Load the product image if available
        if (!productImage.isEmpty()) {
            hideElements();
            saveTextEditProduct.setText(R.string.betoltes);
            kepMegjelenit(productImage);
        } else {
            // Load default image if no product image is provided
            Glide.with(EditProductActivity.this).load(R.drawable.standard_item_picture).placeholder(R.drawable.standard_item_picture).into(productImageEdit);
        }
    }

    /**
     * Determines whether the product is measured by weight or quantity.
     * Updates the UI based on the selected unit type.
     * @return True if the product should be measured by weight, false otherwise.
     */
    public boolean isMeasuredByWeight() {
        // Listener for the unit switch toggle
        unitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // If the switch is turned on, the product is measured by weight
                if (isChecked) {
                    shouldMeasureByWeight = true;
                    averageWeightEdit.setVisibility(View.VISIBLE);
                    productPriceEdit.setHint(R.string.termek_egysegara_kg);
                    productPriceEdit.setText("");
                    productStockEdit.setText("");
                    productStockEdit.setHint("Készlet (kg)*");
                } else {
                    // If the switch is turned off, the product is measured by quantity
                    shouldMeasureByWeight = false;
                    averageWeightEdit.setVisibility(View.GONE);
                    productPriceEdit.setText("");
                    productPriceEdit.setHint(R.string.termek_egysegara_db);
                    productStockEdit.setText("");
                    productStockEdit.setHint("Készlet (db)*");
                }
            }
        });
        return shouldMeasureByWeight;
    }

    public void kepMegjelenit(String kep) {
        if (!kep.isEmpty()) {
            Uri uri = Uri.parse(kep);
            try {
                Glide.with(EditProductActivity.this).load(uri).addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        showElements();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        showElements();
                        return false;
                    }
                }).placeholder(R.drawable.standard_item_picture).into(productImageEdit);
            } catch (Exception e) {
                showElements();
                Glide.with(EditProductActivity.this).load(R.drawable.standard_item_picture).placeholder(R.drawable.standard_item_picture).into(productImageEdit);
            }
        } else {
            Glide.with(EditProductActivity.this).load(R.drawable.standard_item_picture).placeholder(R.drawable.standard_item_picture).into(productImageEdit);
        }
    }

    // Show loading indicators and display product details once content is available
    private void hideElements() {
        progressBarEditProduct.setVisibility(View.VISIBLE);
        saveTextEditProduct.setVisibility(View.VISIBLE);
        titleEditProduct.setVisibility(View.GONE);
        saveButtonEditProduct.setVisibility(View.GONE);
        backButtonEditProduct.setVisibility(View.GONE);
        productImageEdit.setVisibility(View.GONE);
        productImageLabelEdit.setVisibility(View.GONE);
        productNameEdit.setVisibility(View.GONE);
        productPriceEdit.setVisibility(View.GONE);
        unitSwitchLayout.setVisibility(View.GONE);
        productStockEdit.setVisibility(View.GONE);
    }

    // Hide loading progress and show product details when content is being loaded
    public void showElements() {
        progressBarEditProduct.setVisibility(View.GONE);
        saveTextEditProduct.setVisibility(View.GONE);
        titleEditProduct.setVisibility(View.VISIBLE);
        saveButtonEditProduct.setVisibility(View.VISIBLE);
        backButtonEditProduct.setVisibility(View.VISIBLE);
        productImageEdit.setVisibility(View.VISIBLE);
        productImageLabelEdit.setVisibility(View.VISIBLE);
        productNameEdit.setVisibility(View.VISIBLE);
        productPriceEdit.setVisibility(View.VISIBLE);
        unitSwitchLayout.setVisibility(View.VISIBLE);
        productStockEdit.setVisibility(View.VISIBLE);
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
        view.setOnClickListener(v -> startActivity(new Intent(EditProductActivity.this, CartActivity.class)));
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

    public void onVisszaTermekekhez(View view) {
        super.onBackPressed();
        finish();
    }

    public void onEdit(View view) {
        // Check if the product should be priced by weight or quantity
        shouldMeasureByWeight = unitSwitch.isChecked();

        // Ensure all required fields are filled in based on whether we are measuring by weight or quantity
        if (shouldMeasureByWeight && !productNameEdit.getText().toString().isEmpty() && !productNameEdit.getText().toString().equals(" ") && !averageWeightEdit.getText().toString().isEmpty() && !productPriceEdit.getText().toString().isEmpty() && !productStockEdit.getText().toString().isEmpty() ||
                !shouldMeasureByWeight && !productNameEdit.getText().toString().isEmpty() && !productNameEdit.getText().toString().equals(" ") && !productPriceEdit.getText().toString().isEmpty() && !productStockEdit.getText().toString().isEmpty()) {
            String productName = productNameEdit.getText().toString();

            // Ensure the entered values are within valid ranges
            if ((shouldMeasureByWeight && Double.parseDouble(productPriceEdit.getText().toString()) <= 2147483647 && Double.parseDouble(productStockEdit.getText().toString()) <= 2147483647 && Double.parseDouble(averageWeightEdit.getText().toString()) <= 2147483647) ||
                    (!shouldMeasureByWeight && Double.parseDouble(productPriceEdit.getText().toString()) <= 2147483647 && Double.parseDouble(productStockEdit.getText().toString()) <= 2147483647)) {
                double weight;
                double price = Double.parseDouble(productPriceEdit.getText().toString());
                double stock = Double.parseDouble(productStockEdit.getText().toString());

                // Set weight based on the product should price by quantity or weight
                if (!shouldMeasureByWeight) {
                    weight = -1;
                } else {
                    weight = Double.parseDouble(averageWeightEdit.getText().toString());
                }

                // Prevent any zero or negative values for price, stock, or weight
                if ((shouldMeasureByWeight && weight > 0 && price > 0 && stock > 0) || (!shouldMeasureByWeight && price > 0 && stock > 0)) {
                    saveTextEditProduct.setText(R.string.termek_modositasa_progress);
                    hideElements();

                    // If no new image was selected, use the existing image or set it to an empty string if there was no image before
                    if (imageUrl == null) {
                        if (oldImage.isEmpty()) {
                            productImage = "";
                        } else {
                            // If a new image was selected, upload it
                            productImage = oldImage;
                        }
                        updateDatabase(productName, price, stock, weight, productImage);
                    } else { // Handle errors
                        changeImage(imageUrl, productName, price, stock, weight);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Nem adhatsz meg semminek sem 0 értéket!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Túl nagy értékeket adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Minden változtatni kívánt mezőt ki kell tölts!", Toast.LENGTH_LONG).show();
        }

    }

    public void updateDatabase(String productName, double price, double stockCount, double weight, String image) {
        db.collection("uzletek").document(storeId).collection("termekek").document(productId);
        allProductsReference = db.collection("osszesTermek").document(allProductsCollectionId);

        // Create a map for updating the 'all products' collection with the product's name and store ID
        Map<String, Object> osszTermekBovit = new HashMap<>();
        osszTermekBovit.put("termekNeve", productName);
        osszTermekBovit.put("storeId", storeId);

        // Update the 'all products' collection
        allProductsReference.set(osszTermekBovit);

        // Create a new Product object with the updated data and save it to firebase
        Product frissitettProduct = new Product(productName, price, stockCount, weight, image, storeId, allProductsCollectionId);

        // Convert the updated product into a map for Firestore
        productMap = frissitettProduct.addNewProduct(frissitettProduct);
        productReference.set(productMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                showElements();
            }
        });
    }

    //-------------------------------------- The following section handles the image upload process---------------------------------------------------------------
    public void onProductImageUpload(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT); // Open the image gallery
        gallery.setType("image/*"); // Restrict to image files only
        startActivityForResult(gallery, 2);  // Start the activity to select an image
    }

    // Check if the image was successfully selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUrl = data.getData();
            Glide.with(EditProductActivity.this).load(imageUrl).into(productImageEdit);
        }
    }

    public void changeImage(Uri uri, String nev, double productPrice, double stockCount, double productWeight) {
        // if there are available new image
        StorageReference imageNameReference;
        if (!productImage.isEmpty()) {
            imageNameReference = FirebaseStorage.getInstance().getReferenceFromUrl(productImage);
        } else {
            // Generate a timestamp to uniquely name the image based on the current time
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            imageNameReference = storageReference.child("termek_" + storeId + "_" + timeStamp);
        }

        // Upload the image to Firebase Storage
        imageNameReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                imageNameReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> uploadTask) {
                        if (uploadTask.isSuccessful()) {
                            // Create a new Product object with the provided details and image path
                            productImage = String.valueOf(uploadTask.getResult());
                            updateDatabase(nev, productPrice, stockCount, productWeight, productImage);
                        }
                    }
                });
            }
        }).addOnProgressListener(snapshot -> hideElements()).addOnFailureListener(e -> showElements());
    }
}