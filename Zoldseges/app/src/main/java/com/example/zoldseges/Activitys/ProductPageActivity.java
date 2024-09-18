package com.example.zoldseges.Activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.example.zoldseges.Activitys.UserManagement.LoginActivity;
import com.example.zoldseges.Activitys.UserManagement.ProfileActivity;
import com.example.zoldseges.Models.CartItem;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Objects;

public class ProductPageActivity extends AppCompatActivity {
    private FirebaseAuth auth; // Firebase Authentication instance
    FirebaseFirestore db; // Firestore database reference

    // Data from the Intent
    String productImageUrl;
    String storeId;
    String productId;
    String stock;
    String allProductsCollection;

    // UI Elements
    ImageView productImageView;
    TextView productNameTextView;
    TextView productWeightTextView;
    TextView productPriceTextView;
    TextView productMaxQuantityTextView;
    LinearLayout quantityInputLayout;
    LinearLayout productWeightLayout;
    LinearLayout productPriceLayout;
    LinearLayout maxQuantityLayout;
    EditText quantityEditText;
    Button addToCartButton;

    // Progress and loading indicators
    ProgressBar productLoadingProgressBar;
    TextView productLoadingTextView;

    // Prompt message to login
    private TextView loginPromptTextView;

    // Product Data
    double productWeight;
    int productStockQuantity;
    private String productName;
    double productUnitPrice;

    // Cart Management
    private MenuItem cartMenuItem;
    private FrameLayout cartBadgeLayout;
    private TextView cartBadgeCountTextView;

    // Static data list for cart products (other activities can access)
    public static ArrayList<CartItem> cartItemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_page);

        // Initialize Firebase and Firestore
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        // Initialize UI elements
        productImageView = findViewById(R.id.kep2);
        productNameTextView = findViewById(R.id.termekNeveBolt);
        productWeightTextView = findViewById(R.id.termekSulyBolt);
        productPriceTextView = findViewById(R.id.termekAraBolt);
        productMaxQuantityTextView = findViewById(R.id.termekMaxRendelheto);
        quantityInputLayout = findViewById(R.id.rendelenoMennyisegLayout);
        quantityEditText = findViewById(R.id.rendelendoMennyiseg);
        productWeightLayout = findViewById(R.id.termekSulyBoltLayout);
        productPriceLayout = findViewById(R.id.termekArBoltLayout);
        maxQuantityLayout = findViewById(R.id.maxMennyisegBoltLayout);
        addToCartButton = findViewById(R.id.kosarbaTermekOldal);
        productLoadingProgressBar = findViewById(R.id.progressBarTermekBetolt);
        loginPromptTextView = findViewById(R.id.bejlelentkezKosarhoz);
        productLoadingTextView = findViewById(R.id.termekBetoltText);

        // Get the product details from the intent
        productName = getIntent().getStringExtra("productName");
        productWeight = getIntent().getDoubleExtra("productWeight", 0);
        productUnitPrice = getIntent().getDoubleExtra("productUnitPrice", 0);
        // Format the unit price if it is a whole number
        if (productUnitPrice % 1 == 0) {
            productUnitPrice = (int) productUnitPrice;
        }
        productStockQuantity = (int) getIntent().getDoubleExtra("productStockQuantity", 0);
        productImageUrl = getIntent().getStringExtra("productImageUrl");
        storeId = getIntent().getStringExtra("storeId");
        productId = getIntent().getStringExtra("productId");
        allProductsCollection = getIntent().getStringExtra("allProductsCollection");

        // Set the ActionBar title to the product name
        getSupportActionBar().setTitle(productName);

        // Display product details
        hideElements();
        displayProductImage(productImageUrl);
        productNameTextView.setText(productName);

        // Format and display price and stock information
        String price;

        if (productWeight == -1.0) {
            productWeightLayout.setVisibility(View.GONE);
            price = " " + (int) productUnitPrice + " Ft/db";
            stock = " " + productStockQuantity + " db";
            quantityEditText.setHint(R.string.rendelendo_mennyiseg_db);
        } else {
            String weight = " " + productWeight + " kg";
            productWeightTextView.setText(weight);
            productWeightLayout.setVisibility(View.VISIBLE);
            quantityEditText.setHint(R.string.rendelendo_mennyiseg_kg);
            price = " " + (int) productUnitPrice + " Ft/kg";
            stock = " " + productStockQuantity + " kg";
        }

        productPriceTextView.setText(price);
        productMaxQuantityTextView.setText(stock);
    }

    /**
     * Verifies if the current user is a seller. If yes, hides certain UI elements
     * since sellers don't need to interact with the cart.
     */
    public void checkIfSeller() {
        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                assert value != null;
                String userType = value.getString("felhasznaloTipus");
                if (userType != null) {
                    if (userType.equals("Eladó cég/vállalat")) {
                        addToCartButton.setVisibility(View.GONE);
                        quantityInputLayout.setVisibility(View.GONE);
                    } else {
                        addToCartButton.setVisibility(View.VISIBLE);
                        quantityInputLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    auth.signOut();
                    finish();
                }
            });
        } else {
            addToCartButton.setVisibility(View.VISIBLE);
            quantityInputLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        quantityEditText.setText("");
        setupLoginPrompt();
        hideElements();
        displayProductImage(productImageUrl);
        invalidateOptionsMenu();

    }

    /**
     * Sets up the login prompt for unauthenticated users
     * and provides a clickable link to the login page.
     */
    private void setupLoginPrompt() {
        if (auth.getCurrentUser() != null) {
            loginPromptTextView.setVisibility(View.GONE);
        } else {
            loginPromptTextView.setVisibility(View.VISIBLE);
            SpannableString bejelentkezes = new SpannableString("Már van fiókod? Jelentkezz be!");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    startActivity(new Intent(ProductPageActivity.this, LoginActivity.class));
                }
            };
            bejelentkezes.setSpan(clickableSpan, 16, 30, 0);
            bejelentkezes.setSpan(new URLSpan(""), 16, 30, 0);
            bejelentkezes.setSpan(new ForegroundColorSpan(ContextCompat.getColor(ProductPageActivity.this, R.color.purple_500)), 16, 30, 0);

            loginPromptTextView.setMovementMethod(LinkMovementMethod.getInstance());

            loginPromptTextView.setText(bejelentkezes, TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * Loads and displays the product image from the provided URL using Glide.
     * If the image fails to load, it displays a placeholder image.
     *
     * @param imageUrl The URL of the product image
     */
    public void displayProductImage(String imageUrl) {
        // Check if the image URL is valid (not null and not empty)
        if (imageUrl != null && !imageUrl.isEmpty()) {

            // Parse the image URL into a URI object
            Uri uri = Uri.parse(imageUrl);
            try {
                // Ensure the activity is not finishing before attempting to load the image
                if (!this.isFinishing()) {
                    // Use Glide to load the image from the provided URL and set it into the ImageView
                    Glide.with(ProductPageActivity.this).load(uri).placeholder(R.drawable.standard_item_picture).listener(new RequestListener<Drawable>() {
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
                    }).into(productImageView); // Load the image into the productImageView
                }
            } catch (Exception e) {
                showElements();

                // Set a fallback foreground color when an error occurs
                int forground = getResources().getColor(R.color.ures_kep, getTheme());
                productImageView.setForeground(new ColorDrawable(forground));
                productImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                Glide.with(ProductPageActivity.this).load(R.drawable.standard_item_picture).into(productImageView);
            }
        } else {
            // If no image URL is provided, display the default placeholder
            showElements();
            productImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            int forground = getResources().getColor(R.color.ures_kep, getTheme());
            productImageView.setForeground(new ColorDrawable(forground));
            Glide.with(ProductPageActivity.this).load(R.drawable.standard_item_picture).into(productImageView);
        }
    }

    // Show loading indicators and display product details once content is available
    public void hideElements() {
        productLoadingProgressBar.setVisibility(View.VISIBLE);
        productLoadingTextView.setVisibility(View.VISIBLE);
        productNameTextView.setVisibility(View.GONE);
        productImageView.setVisibility(View.INVISIBLE);
        addToCartButton.setVisibility(View.GONE);
        maxQuantityLayout.setVisibility(View.GONE);
        productPriceLayout.setVisibility(View.GONE);
        productWeightLayout.setVisibility(View.GONE);
        quantityInputLayout.setVisibility(View.GONE);
    }

    // Hide loading progress and show product details when content is being loaded
    public void showElements() {
        productLoadingProgressBar.setVisibility(View.GONE);
        productLoadingTextView.setVisibility(View.GONE);
        productNameTextView.setVisibility(View.VISIBLE);
        productImageView.setVisibility(View.VISIBLE);
        checkIfSeller();
        maxQuantityLayout.setVisibility(View.VISIBLE);
        productPriceLayout.setVisibility(View.VISIBLE);
        if (productWeight != -1.0) {
            productWeightLayout.setVisibility(View.VISIBLE);
        }
        quantityInputLayout.setVisibility(View.VISIBLE);
    }

    // The toolbar when the user is at the product page
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

                // Get the 'userType' field from the user's document
                String userType = value.getString("felhasznaloTipus");
                if (userType != null) {
                    // Show the cart menu item if the user is not a "seller"
                    cartMenuItem.setVisible(!userType.equals("Eladó cég/vállalat"));
                } else {
                    auth.signOut();
                    finish();
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
            startActivity(new Intent(ProductPageActivity.this, CartActivity.class));
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

    public void addToCart(View view) {
        // Check if the user is logged in
        if (auth.getCurrentUser() != null) {
            // Check if the quantity input field is not empty
            if (!quantityEditText.getText().toString().isEmpty()) {
                double quantityToOrder = Double.parseDouble(quantityEditText.getText().toString());

                // Check if the requested quantity is less than or equal to the available stock
                if (quantityToOrder <= productStockQuantity) {
                    // Ensure the requested quantity is greater than 0
                    if (quantityToOrder > 0) {
                        // Create a new product object to add to the cart
                        Product product = new Product(productName, productUnitPrice, productStockQuantity, productWeight, productImageUrl, storeId, allProductsCollection);
                        product.setProductId(productId);
                        CartItem newCartItem = new CartItem(product, quantityToOrder);

                        // If the cart is empty, add the item and update the UI
                        if (cartItemList.size() == 0) {
                            cartItemList.add(newCartItem);
                            Toast.makeText(getApplicationContext(), "Sikeresen hozzáadtad a kosaradhoz!", Toast.LENGTH_LONG).show();
                            cartBadgeCountTextView.setText(String.valueOf(cartItemList.size()));
                            cartBadgeLayout.setVisibility(View.VISIBLE);
                        } else {
                            boolean alreadyInTheCart = false;

                            // Check if the product already exists in the cart
                            for (CartItem cartItem : cartItemList) {
                                if (cartItem.getProduct().getProductId().equals(this.productId)) {
                                    cartItem.setQuantity(quantityToOrder);
                                    alreadyInTheCart = true;
                                    Toast.makeText(getApplicationContext(), "Sikeresen frissítetted a kosaradat!", Toast.LENGTH_LONG).show();
                                    cartBadgeCountTextView.setText(String.valueOf(cartItemList.size()));
                                    cartBadgeLayout.setVisibility(View.VISIBLE);
                                }
                            }

                            // If the item does not exist in the cart, add it
                            if (!alreadyInTheCart) {
                                boolean productFromOtherStore = isProductFromOtherStore(newCartItem);

                                // If products from different stores are in the cart, prompt the user to remove them
                                if (productFromOtherStore) {
                                    showCartAlert(newCartItem);
                                } else {
                                    cartItemList.add(newCartItem);
                                    Toast.makeText(getApplicationContext(), "Sikeresen hozzáadtad a kosaradhoz!", Toast.LENGTH_LONG).show();
                                    cartBadgeCountTextView.setText(String.valueOf(cartItemList.size()));
                                    cartBadgeLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Semmiből sem rendelhetsz 0-t!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Maximum csak " + stock + "-ot rendelhetsz ebből a termékből!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Előbb meg kell adnod, hogy mennyit szeretnél rendelni ebből a termékből!", Toast.LENGTH_LONG).show();
            }
        } else {
            // If user is not logged in, prompt to log in
            setupLoginPrompt();
            Toast.makeText(getApplicationContext(), "Előbb be kell jelentkezned ahhoz, hogy rendelhess valamit!", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean isProductFromOtherStore(CartItem newCartItem) {
        boolean productFromOtherStore = false;

        // Check if the cart already contains products from a different store
        for (CartItem existingCartItem : cartItemList) {
            // Compare the new cart item store userId with the other cart items store userId
            if (!newCartItem.getProduct().getStoreId().equals(existingCartItem.getProduct().getStoreId())) {
                productFromOtherStore = true;
                break;
            }
        }
        return productFromOtherStore;
    }

    public void showCartAlert(CartItem ujElem) {
        // Create an AlertDialog to confirm clearing the cart when adding items from a different store
        AlertDialog.Builder clearCartAlertBuilder = new AlertDialog.Builder(this);
        clearCartAlertBuilder.setTitle("Eltávolítod a korábbi termékeket?");
        clearCartAlertBuilder.setIcon(R.mipmap.ic_launcher);
        clearCartAlertBuilder.setMessage("Már másik üzlet terméke is a kosaradban van. Ki szeretnéd venni őket?");
        clearCartAlertBuilder.setCancelable(true);

        // Create the alert dialog
        AlertDialog clearCartAlert = clearCartAlertBuilder.create();

        // Add action for the "Kosár ürítése" (Clear Cart) button
        clearCartAlert.setButton(DialogInterface.BUTTON_POSITIVE, "Kosár ürítése", (dialog, which) -> {
            cartItemList.clear();

            // Add the new item from a different store if the user want to clear the cart
            cartItemList.add(ujElem);
            Toast.makeText(getApplicationContext(), "Sikeresen hozzáadtad a kosaradhoz!", Toast.LENGTH_LONG).show();
            cartBadgeCountTextView.setText(String.valueOf(cartItemList.size()));
            cartBadgeLayout.setVisibility(View.VISIBLE);
            invalidateMenu();
        });

        // Add action for the "Mégse" (Cancel) button
        clearCartAlert.setButton(DialogInterface.BUTTON_NEGATIVE, "Mégse", (dialog, which) -> clearCartAlert.dismiss());

        // Display the alert dialog
        clearCartAlert.show();

        // Customize button colors for the alert dialog
        clearCartAlert.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
        clearCartAlert.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white, getTheme()));
    }
}