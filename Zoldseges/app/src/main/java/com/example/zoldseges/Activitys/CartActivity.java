package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zoldseges.Activitys.UserManagement.LoginActivity;
import com.example.zoldseges.Activitys.UserManagement.ProfileActivity;
import com.example.zoldseges.Adapters.CartAdapter;
import com.example.zoldseges.Models.CartItem;
import com.example.zoldseges.DAOS.CartDAO;
import com.example.zoldseges.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Objects;

public class CartActivity extends AppCompatActivity implements CartDAO {
    private FirebaseAuth auth; // Firebase Authentication instance
    private FirebaseFirestore db; // Firestore database reference

    // Progress and loading indicators
    private ProgressBar progressBarCart;
    private TextView loadingTextView;

    // UI Components
    private RecyclerView cartRecyclerView;
    private AppBarLayout appBarCart;

    // Adapter for the RecyclerView
    private CartAdapter cartAdapter;

    // Data list for cart products
    private ArrayList<CartItem> cartList;

    // Handle if the cart have no products
    private RelativeLayout nincsTermekKosarbanLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize Firebase and UI components
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Set up the ActionBar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Kosár");

        // if no user is logged in then redirect to login page
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            if (auth.getCurrentUser() != null) {
                // if user is logged in, but the user is seller
                DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
                reference.addSnapshotListener((value, error) -> {
                    assert value != null;
                    String userType = value.getString("felhasznaloTipus");
                    assert userType != null;
                    if (userType.equals("Eladó cég/vállalat")) {
                        finish();
                        super.onBackPressed();
                    }
                });
            }

            // Initialize UI elements
            progressBarCart = findViewById(R.id.progressKosar);
            loadingTextView = findViewById(R.id.betoltesKosar);
            cartRecyclerView = findViewById(R.id.kosarElemei);
            appBarCart = findViewById(R.id.appBarKosar);
            nincsTermekKosarbanLayout = findViewById(R.id.nincsTermekKosarbanLayout);

            // Set up the RecyclerView with a GridLayoutManager
            GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
            cartRecyclerView.setLayoutManager(layoutManager);
            cartRecyclerView.setHasFixedSize(true);

            // Initialize data list
            cartList = new ArrayList<>();

            // Initialize UI visibility and fetch data
            hideElements();
            clearCartProductList();
            getCartItems();
        }
    }

    // this list is implemented in ProductPageActivity,
    // it's a static list so every time a user click to addCart button
    // this list save this element and can access from every activity
    private void getCartItems() {
        for (CartItem cartItem : cartItemList) {
            CartItem cartProduct = new CartItem(cartItem.getProduct(), cartItem.getQuantity());
            cartList.add(cartProduct);
        }

        // Initialize the adapter with the products list and set it to the RecyclerView
        cartAdapter = new CartAdapter(getApplicationContext(), cartList, CartActivity.this);
        cartRecyclerView.setAdapter(cartAdapter);

        // Update the UI to show the data
        showElements();
    }

    private void clearCartProductList() {
        // Check if the cart list is initialized
        if (cartList != null) {
            // Clear the cart list
            cartList.clear();

            // Notify the adapter about the data change if it's initialized
            if (cartAdapter != null) {
                cartAdapter.notifyDataSetChanged();
            }
        } else {
            // Initialize the cart list if it is null
            cartList = new ArrayList<>();
        }
    }

    // Show loading progress and hide product details when content is being loaded
    public void hideElements() {
        progressBarCart.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        cartRecyclerView.setVisibility(View.GONE);
        appBarCart.setVisibility(View.INVISIBLE);
        nincsTermekKosarbanLayout.setVisibility(View.GONE);

    }

    // Hide loading progress and hide product details when content is being loaded
    public void showElements() {
        // Handle if cart is empty
        if (cartList.size() > 0) {
            cartRecyclerView.setVisibility(View.VISIBLE);
        } else {
            nincsTermekKosarbanLayout.setVisibility(View.VISIBLE);
        }
        progressBarCart.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        appBarCart.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.cart_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the user is logged is
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(CartActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            hideElements();
            clearCartProductList();
            getCartItems();
            if (auth.getCurrentUser() != null) {
                DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
                reference.addSnapshotListener((value, error) -> {
                    assert value != null;
                    String userType = value.getString("felhasznaloTipus");
                    assert userType != null;
                    // if the user is a seller then close this page
                    if (userType.equals("Eladó cég/vállalat")) {
                        finish();
                        super.onBackPressed();
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.fiokKosar) {
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEdit(int position, double newQuantity) {
        boolean isEditSuccessful = false;

        // Check if the new quantity is valid
        if (newQuantity > 0) {
            // Check if the new quantity is less than or equal to the available stock
            if (newQuantity <= cartList.get(position).getProduct().getAvailableStockQuantity()) {
                for (CartItem cartItem : cartItemList) {
                    // Find the item in the cart and update its quantity
                    if (cartItem.getProduct().getProductId().equals(cartList.get(position).getProduct().getProductId())) {
                        cartItem.setQuantity(newQuantity);
                        Toast.makeText(getApplicationContext(), "Sikeresen frissítetted a kosaradat!", Toast.LENGTH_LONG).show();
                        isEditSuccessful = true;

                        // Refresh the cart display
                        hideElements();
                        clearCartProductList();
                        getCartItems();
                    }
                }
            } else {
                // If the new quantity exceeds the available stock, show an error message
                String avaiableStock;
                if (cartList.get(position).getProduct().getProductWeight() != -1.0) {
                    avaiableStock = cartList.get(position).getProduct().getAvailableStockQuantity() + " kg";
                } else {
                    avaiableStock = cartList.get(position).getProduct().getAvailableStockQuantity() + " db";
                }
                Toast.makeText(getApplicationContext(), "Maximum csak " + avaiableStock + "-ot rendelhetsz ebből a termékből!", Toast.LENGTH_LONG).show();
            }
        } else {
            // If the new quantity is zero or negative, delete the item from the cart
            onDelete(position);
        }
        return isEditSuccessful;
    }

    @Override
    public void onDelete(int position) {
        // Create an alert dialog to confirm deletion
        AlertDialog.Builder torlesLaertBuilder = new AlertDialog.Builder(this);
        torlesLaertBuilder.setTitle("Törlés");
        torlesLaertBuilder.setIcon(R.mipmap.ic_launcher);
        torlesLaertBuilder.setMessage("Biztosan törölni szeretnéd a(z) " + cartList.get(position).getProduct().getProductName() + " termékedet a kosárból?");
        torlesLaertBuilder.setCancelable(true);

        AlertDialog deleteAlertDialog = torlesLaertBuilder.create();

        // Set up the positive button to confirm deletion
        deleteAlertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Törlés", (dialog, which) -> {
            hideElements();
            cartItemList.remove(position);
            clearCartProductList();
            getCartItems();
        });

        // Set up the negative button to cancel the deletion
        deleteAlertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Mégse", (dialog, which) -> deleteAlertDialog.dismiss());
        deleteAlertDialog.show();

        // Customize button colors
        deleteAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
        deleteAlertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.white, getTheme()));
    }

    @Override
    public void onProductSelect(int position) {
        // Handle product item selection from the list
        Intent intent = new Intent(CartActivity.this, ProductPageActivity.class);

        // Pass the product details to the Product page activity
        intent.putExtra("productName", cartList.get(position).getProduct().getProductName());
        intent.putExtra("productWeight", cartList.get(position).getProduct().getProductWeight());
        intent.putExtra("productUnitPrice", cartList.get(position).getProduct().getPrice());
        intent.putExtra("productImageUrl", cartList.get(position).getProduct().getProductImage());
        intent.putExtra("productStockQuantity", cartList.get(position).getProduct().getAvailableStockQuantity());
        intent.putExtra("productId", cartList.get(position).getProduct().getProductId());
        intent.putExtra("storeId", cartList.get(position).getProduct().getStoreId());
        intent.putExtra("allProductsCollection", cartList.get(position).getProduct().getAllProductCollectionId());

        // Start the product page activity
        startActivity(intent);
    }

    // redirect to the payment page
    @Override
    public void onProceedToPayment() {
        Intent intent = new Intent(CartActivity.this, PaymentActivity.class);
        startActivity(intent);
    }

    // redirect to the home page
    public void onBack(View view) {
        finish();
        super.onBackPressed();
    }
}