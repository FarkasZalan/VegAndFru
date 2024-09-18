package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.Activitys.UserManagement.LoginActivity;
import com.example.zoldseges.Activitys.UserManagement.ProfileActivity;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.Adapters.CustomerProductAdapter;
import com.example.zoldseges.DAOS.CustomerViewProducts;
import com.example.zoldseges.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Objects;

public class StorePageActivity extends AppCompatActivity implements CustomerViewProducts {

    StorageReference storageReference; // Firebase Storage reference
    FirebaseFirestore db; // Firestore database reference
    private FirebaseAuth auth; // Firebase Authentication instance

    // Data from the Intent
    String storeImage;
    String storeId;
    String ownerId;
    String businessAddress;
    String storeName;

    // UI Elements
    private RecyclerView storeProductsRecyclerView;
    private AppBarLayout appBarBolt;
    private ImageView storeImageView;

    // Progress and loading indicators
    private ProgressBar loadingProgressBar;
    private TextView loadingTextView;

    // Data list for store products
    private ArrayList<Product> productList;

    // Adapter for the RecyclerView
    private CustomerProductAdapter productAdapter;

    // Handle if the store have no products
    private boolean storeIsEmpty = false;
    private RelativeLayout storeIsEmptyLayout;

    // Menu item
    private MenuItem cartMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_page);

        // Initialize Firebase and UI components
        storageReference = FirebaseStorage.getInstance().getReference().child("TermekKepek");
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Initialize UI elements
        loadingProgressBar = findViewById(R.id.progressBolt);
        loadingTextView = findViewById(R.id.betoltesBolt);
        storeProductsRecyclerView = findViewById(R.id.boltTermekei);
        appBarBolt = findViewById(R.id.appBarBolt);
        storeImageView = findViewById(R.id.kepBoltba);
        storeIsEmptyLayout = findViewById(R.id.nincsTermekLayout);

        // Set up the RecyclerView with a GridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        storeProductsRecyclerView.setLayoutManager(layoutManager);
        storeProductsRecyclerView.setHasFixedSize(true);

        // Get the store details from the intent
        storeImage = getIntent().getStringExtra("storeImage");
        storeName = getIntent().getStringExtra("storeName");
        ownerId = getIntent().getStringExtra("ownerId");
        storeId = getIntent().getStringExtra("storeId");
        businessAddress = getIntent().getStringExtra("businessAddress");

        // Set the ActionBar title to the store name
        getSupportActionBar().setTitle(storeName);

        // Initialize store's products list
        productList = new ArrayList<>();

        // Initialize UI visibility and fetch data
        hideElements();
        clearProductsList();
        getDataFromFireBase();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reinitialize RecyclerView layout manager and data list and display store's products in 2 column
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        storeProductsRecyclerView.setLayoutManager(layoutManager);
        storeProductsRecyclerView.setHasFixedSize(true);

        productList = new ArrayList<>();

        // Refresh UI and fetch new data
        hideElements();
        clearProductsList();
        getDataFromFireBase();
        invalidateOptionsMenu(); // Refresh the options menu
    }

    private void getDataFromFireBase() {
        // Create a query to fetch products from Firestore
        displayStoreImage();
        Query query = FirebaseFirestore.getInstance().collection("uzletek").document(storeId).collection("termekek");
        // Order the results by products name in ascending order
        query.whereEqualTo("uzletId", storeId).orderBy("termekNeve", Query.Direction.ASCENDING).get().addOnCompleteListener(productsCollection -> {
            if (productsCollection.isSuccessful()) {
                clearProductsList(); // Clear the existing product list
                for (QueryDocumentSnapshot productDocument : productsCollection.getResult()) {
                    // Create a new Product object and populate its fields
                    Product product = new Product();
                    product.setProductImage(productDocument.getString("termekKepe"));
                    product.setProductName(productDocument.getString("termekNeve"));
                    product.setStoreId(productDocument.getString("uzletId"));
                    product.setProductWeight(Objects.requireNonNull(productDocument.getDouble("termekSulya")));
                    product.setPrice(Objects.requireNonNull(productDocument.getDouble("termekAra")));
                    product.setAvailableStockQuantity(Objects.requireNonNull(productDocument.getDouble("raktaronLevoMennyiseg")));
                    product.setProductId(productDocument.getId());
                    product.setAllProductCollectionId(productDocument.getString("osszTermekCollection"));

                    // Add the product to the products list
                    productList.add(product);
                }
                // Handle if the store have products
                if (productList.isEmpty()) {
                    storeIsEmpty = true;
                } else {
                    storeIsEmpty = false;

                    // Initialize the adapter with the products list and set it to the RecyclerView
                    productAdapter = new CustomerProductAdapter(getApplicationContext(), productList, StorePageActivity.this);
                    storeProductsRecyclerView.setAdapter(productAdapter);
                }
                // Update the UI to show the data
                showElements();
            }
        });
    }

    public void displayStoreImage() {
        // Position of the image
        storeImageView.setScaleType(ImageView.ScaleType.CENTER);

        // Check if the image URL is valid (not null and not empty)
        if (storeImage != null && !storeImage.isEmpty()) {
            // Parse the image URL into a URI object
            Uri uri = Uri.parse(storeImage);
            try {
                // Ensure the activity is not finishing before attempting to load the image
                if (!this.isFinishing()) {
                    // Use Glide to load the image from the provided URL and set it into the ImageView
                    Glide.with(StorePageActivity.this).load(uri).placeholder(R.drawable.grocery_store).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            showElements();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(storeImageView); // Load the image into the productImageView
                }
            } catch (Exception e) {
                // Set a fallback foreground color when an error occurs
                int color = getResources().getColor(R.color.white, getTheme());
                int forground = getResources().getColor(R.color.ures_kep, getTheme());
                appBarBolt.setBackgroundColor(color);
                storeImageView.setForeground(new ColorDrawable(forground));
                storeImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(StorePageActivity.this).load(R.drawable.grocery_store).into(storeImageView);
            }
        } else {
            // If no image URL is provided, display the default placeholder
            int color = getResources().getColor(R.color.white, getTheme());
            int forground = getResources().getColor(R.color.ures_kep, getTheme());
            appBarBolt.setBackgroundColor(color);
            storeImageView.setForeground(new ColorDrawable(forground));
            storeImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(StorePageActivity.this).load(R.drawable.grocery_store).into(storeImageView);
        }
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

    // Show loading progress and hide product details when content is being loaded
    private void hideElements() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        storeProductsRecyclerView.setVisibility(View.GONE);
        storeIsEmptyLayout.setVisibility(View.GONE);
        appBarBolt.setVisibility(View.INVISIBLE);
    }

    // Hide loading progress and show product details when content is being loaded
    private void showElements() {
        loadingProgressBar.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        appBarBolt.setVisibility(View.VISIBLE);
        if (!storeIsEmpty) {
            storeProductsRecyclerView.setVisibility(View.VISIBLE);
            storeIsEmptyLayout.setVisibility(View.GONE);
        } else {
            storeProductsRecyclerView.setVisibility(View.GONE);
            storeIsEmptyLayout.setVisibility(View.VISIBLE);
        }
    }

    // The toolbar when the user is at the store page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_menu, menu);

        // Find the cart menu item for visibility toggling based on user type
        cartMenuItem = menu.findItem(R.id.kosar);

        // Check if a user is currently logged in
        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;

                // Get the userType field from the user's document
                String userType = value.getString("felhasznaloTipus");
                if (userType == null) {
                    auth.signOut();
                    finish();
                } else {
                    // Show the cart menu item if the user is not a "seller"
                    cartMenuItem.setVisible(!userType.equals("Eladó cég/vállalat"));
                }
            });
        } else {
            // Hide the cart menu item if the user is not logged in
            cartMenuItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    // Handle item selection from the toolbar options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fiok) {
            // Redirect to profile activity if user is logged in otherwise  to the login activity
            if (auth.getCurrentUser() != null) {
                startActivity(new Intent(this, ProfileActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        }

        if (item.getItemId() == android.R.id.home) {
            // Close the current activity and go back to the previous activity (home)
            finish();
            super.onBackPressed();
        }
        if (item.getItemId() == R.id.kosar) {
            // Redirect to cart activity
            startActivity(new Intent(StorePageActivity.this, CartActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    // Prepare the cart menu item and update the cart badge based on cart item count
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosar);

        // Get the root view of the cart menu item (which contains the cart badge layout)
        FrameLayout cartMenuItemRootView = (FrameLayout) menuItem.getActionView();

        // Find the cart badge layout and text view in the custom menu item layout
        FrameLayout cartBadgeLayout = cartMenuItemRootView.findViewById(R.id.kosar_mennyiseg_szamlalo);
        TextView cartBadgeCountTextView = cartMenuItemRootView.findViewById(R.id.kosar_mennyiseg_szamlalo_text);

        // Update the cart badge visibility and count based on the number of items in the cart
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

    @Override
    public void onProduct(int position) {
        // Handle product item selection from the list
        Intent intent = new Intent(StorePageActivity.this, ProductPageActivity.class);

        // Pass the product details to the Product page activity
        intent.putExtra("productName", productList.get(position).getProductName());
        intent.putExtra("productWeight", productList.get(position).getProductWeight());
        intent.putExtra("productUnitPrice", productList.get(position).getPrice());
        intent.putExtra("productImageUrl", productList.get(position).getProductImage());
        intent.putExtra("productStockQuantity", productList.get(position).getAvailableStockQuantity());
        intent.putExtra("productId", productList.get(position).getProductId());
        intent.putExtra("storeId", productList.get(position).getStoreId());
        intent.putExtra("allProductsCollection", productList.get(position).getAllProductCollectionId());

        // Start the product page activity
        startActivity(intent);
    }

    public void onBack(View view) {
        finish();
        super.onBackPressed();
    }
}