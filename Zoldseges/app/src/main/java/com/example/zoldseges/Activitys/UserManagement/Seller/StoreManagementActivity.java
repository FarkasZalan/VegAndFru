package com.example.zoldseges.Activitys.UserManagement.Seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.Activitys.CartActivity;
import com.example.zoldseges.Activitys.ProductPageActivity;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.Adapters.ProductAdapter;
import com.example.zoldseges.DAOS.ProductSelectorSellerView;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class StoreManagementActivity extends AppCompatActivity implements ProductSelectorSellerView {
    FirebaseFirestore db;  // Firestore database reference
    FirebaseAuth auth; // Firebase Authentication instance

    // UI elements
    RecyclerView productRecyclerView;
    private RelativeLayout firstProductLayout;
    private ImageView addFProductImageView;
    private ImageView storeImageView;
    private RelativeLayout addProductLayout;

    // Adapter for the RecyclerView
    private ProductAdapter productAdapter;

    // Progress and loading indicators
    private TextView loadingTextView;
    private ProgressBar loadingProgressBar;

    // Map for products data
    private ArrayList<Product> productList;

    // variables to display store's products
    String storeId;
    DatabaseReference databaseReference;
    CollectionReference storeReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_management);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        databaseReference = FirebaseDatabase.getInstance().getReference();
        productRecyclerView = findViewById(R.id.termekekRecyclerview);
        firstProductLayout = findViewById(R.id.elsoTermekLayout);
        addFProductImageView = findViewById(R.id.termekHozzaad);
        storeImageView = findViewById(R.id.kep1);
        addProductLayout = findViewById(R.id.plus);
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        loadingTextView = findViewById(R.id.betoltesTextBoltKezeles);
        loadingProgressBar = findViewById(R.id.progressBarBoltKezeles);
        hideElements();

        // Set up the RecyclerView with a GridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(layoutManager);
        productRecyclerView.setHasFixedSize(true);

        // Initialize store's products list
        productList = new ArrayList<>();

        this.loadingTextView.setText(R.string.betoltes);

        // Initialize UI visibility and fetch data
        clearProductsList();
        getDataFromFirebase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideElements();
        // Reinitialize RecyclerView layout manager and data list and display store's products in 2 column
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(layoutManager);
        productRecyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();

        productList = new ArrayList<>();
        this.loadingTextView.setText(R.string.betoltes);

        // Refresh UI and fetch new data
        clearProductsList();
        getDataFromFirebase();

    }

    // Show loading progress and hide product details when content is being loaded
    private void hideElements() {
        firstProductLayout.setVisibility(View.GONE);
        addFProductImageView.setVisibility(View.GONE);
        storeImageView.setVisibility(View.INVISIBLE);
        loadingProgressBar.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        productRecyclerView.setVisibility(View.GONE);
        addProductLayout.setVisibility(View.GONE);
    }

    // Hide loading progress and show product details when content is being loaded
    private void showElements() {
        loadingProgressBar.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        storeImageView.setVisibility(View.VISIBLE);
        addProductLayout.setVisibility(View.VISIBLE);
        if (productList.size() > 0) {
            firstProductLayout.setVisibility(View.GONE);
            addFProductImageView.setVisibility(View.VISIBLE);
            productRecyclerView.setVisibility(View.VISIBLE);
        } else {
            firstProductLayout.setVisibility(View.VISIBLE);
        }
    }

    private void getDataFromFirebase() {
        // Create a query to fetch products from Firestore
        Query query = FirebaseFirestore.getInstance().collection("uzletek");
        query.whereEqualTo("tulajId", Objects.requireNonNull(auth.getCurrentUser()).getUid())
                .orderBy("cegNev", Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {

                    // Loop through each store that matches the query
                    for (QueryDocumentSnapshot storeData : task.getResult()) {
                        storeId = storeData.getId();
                        Uri uri = Uri.parse(storeData.getString("boltKepe"));

                        // Set the action bar title to the store's name and enable the back button
                        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                        getSupportActionBar().setTitle(storeData.getString("cegNev"));

                        // Check if a store image is provided, and load it using Glide
                        if (storeData.getString("boltKepe") != null && !Objects.requireNonNull(storeData.getString("boltKepe")).isEmpty()) {
                            try {
                                if (!StoreManagementActivity.this.isFinishing()) {
                                    Glide.with(StoreManagementActivity.this).load(uri).placeholder(R.drawable.grocery_store).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            // Show the UI elements if image load fails
                                            showElements();
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    }).into(storeImageView); // Load the image into the ImageView
                                }
                            } catch (Exception e) {
                                showElements();
                                Glide.with(StoreManagementActivity.this).load(R.drawable.grocery_store).into(storeImageView);
                            }
                        } else {
                            showElements();
                            Glide.with(StoreManagementActivity.this).load(R.drawable.grocery_store).into(storeImageView);
                        }

                        // Firestore reference to the products collection within the store
                        storeReference = db.collection("uzletek").document(storeId).collection("termekek");
                        storeReference.get().addOnCompleteListener(productFetchTask -> {
                            if (productFetchTask.isSuccessful()) {
                                clearProductsList();

                                // Loop through each product document and add it to the product list
                                for (QueryDocumentSnapshot productData : productFetchTask.getResult()) {
                                    Product product = new Product();

                                    // Set product details from Firestore
                                    product.setProductImage(productData.getString("termekKepe"));
                                    product.setProductName(productData.getString("termekNeve"));
                                    product.setStoreId(productData.getString("uzletId"));
                                    product.setProductWeight(Objects.requireNonNull(productData.getDouble("termekSulya")));
                                    product.setPrice(Objects.requireNonNull(productData.getDouble("termekAra")));
                                    product.setAvailableStockQuantity(Objects.requireNonNull(productData.getDouble("raktaronLevoMennyiseg")));
                                    product.setProductId(productData.getId());
                                    product.setAllProductCollectionId(productData.getString("osszTermekCollection"));

                                    productList.add(product);

                                }
                                productList.sort(Comparator.comparing(Product::getProductName));

                                // Set up the product adapter and attach it to the RecyclerView
                                productAdapter = new ProductAdapter(getApplicationContext(), productList, StoreManagementActivity.this);
                                productRecyclerView.setAdapter(productAdapter);
                                showElements();
                            }
                        });
                    }
                });
    }

    private void clearProductsList() {
        // Check if the store list is initialized
        if (productList != null) {
            // Clear the store list
            productList.clear();

            // Notify the adapter about the data change if it's initialized
            if (productAdapter != null) {
                productAdapter.notifyDataSetChanged();
            }
        } else {
            // Initialize the store list if it is null
            productList = new ArrayList<>();
        }
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
        view.setOnClickListener(v -> startActivity(new Intent(StoreManagementActivity.this, CartActivity.class)));
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

    public void onUjTermek(View view) {
        startActivity(new Intent(this, NewProductActivity.class));
    }

    // if user clicked some of the products from the store then open Product page
    @Override
    public void onItemView(int position) {
        Intent intent = new Intent(StoreManagementActivity.this, ProductPageActivity.class);
        intent.putExtra("productName", productList.get(position).getProductName());
        intent.putExtra("productWeight", productList.get(position).getProductWeight());
        intent.putExtra("productUnitPrice", productList.get(position).getPrice());
        intent.putExtra("productImage", productList.get(position).getProductImage());
        intent.putExtra("productStockCount", productList.get(position).getAvailableStockQuantity());
        intent.putExtra("productId", productList.get(position).getProductId());
        intent.putExtra("storeId", productList.get(position).getStoreId());

        startActivity(intent);
    }

    // if user clicked some of the product to modify then open the Edit Product page
    @Override
    public void onItemModify(int position) {
        Intent intent = new Intent(StoreManagementActivity.this, EditProductActivity.class);
        intent.putExtra("productName", productList.get(position).getProductName());
        intent.putExtra("productWeight", productList.get(position).getProductWeight());
        intent.putExtra("productUnitPrice", productList.get(position).getPrice());
        intent.putExtra("productStockCount", productList.get(position).getAvailableStockQuantity());
        intent.putExtra("productImage", productList.get(position).getProductImage());
        intent.putExtra("storeId", productList.get(position).getStoreId());
        intent.putExtra("productId", productList.get(position).getProductId());
        intent.putExtra("allProductsCollectionId", productList.get(position).getAllProductCollectionId());

        startActivity(intent);
    }

    @Override
    public void onItemDelete(int position) {
        // Create an AlertDialog for confirmation before deleting a product
        AlertDialog.Builder torlesLaertBuilder = new AlertDialog.Builder(this);
        torlesLaertBuilder.setTitle("Törlés");
        torlesLaertBuilder.setIcon(R.mipmap.ic_launcher);
        torlesLaertBuilder.setMessage("Biztosan törölni szeretnéd a(z) " + productList.get(position).getProductName() + " termékedet?");
        torlesLaertBuilder.setCancelable(true);

        AlertDialog deleteAlertDialog = torlesLaertBuilder.create();

        // Set the buttons functions
        deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Törlés", (dialog, which) -> {
            hideElements();
            String id = productList.get(position).getProductId();

            // Reference to the document in the "osszesTermek" collection to delete the product globally
            DocumentReference allProductsCollectionRef = db.collection("osszesTermek").document(productList.get(position).getAllProductCollectionId());
            allProductsCollectionRef.delete();

            // Reference to the document in the store's "termekek" collection to delete the product
            DocumentReference storeProductRef = db.collection("uzletek").document(storeId).collection("termekek").document(id);
            storeProductRef.delete().addOnCompleteListener(deleteTask -> {
                if (!deleteTask.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Váratlan hiba történt!", Toast.LENGTH_LONG).show();
                }
            });

            // Check if the product has an associated image and delete it from Firebase Storage
            if (!productList.get(position).getProductImage().isEmpty()) {
                StorageReference imageDeleteRef = FirebaseStorage.getInstance().getReferenceFromUrl(productList.get(position).getProductImage());
                imageDeleteRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> imageDeleteTask) {
                        if (imageDeleteTask.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Sikeres eltávolítás!", Toast.LENGTH_LONG).show();
                            loadingTextView.setText(R.string.torles);
                            hideElements();
                            clearProductsList();
                            getDataFromFirebase();
                            deleteAlertDialog.dismiss();
                        }
                    }
                });
            } else {
                // If no image is associated, complete deletion process
                Toast.makeText(getApplicationContext(), "Sikeres eltávolítás!", Toast.LENGTH_LONG).show();
                hideElements();
                clearProductsList();
                getDataFromFirebase();
                deleteAlertDialog.dismiss();
            }
        });

        deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Mégse", (dialog, which) -> deleteAlertDialog.dismiss());
        deleteAlertDialog.show();

        // Buttons design
        deleteAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
        deleteAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white, getTheme()));
    }
}