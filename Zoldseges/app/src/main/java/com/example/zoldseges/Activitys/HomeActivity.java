package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.zoldseges.Activitys.UserManagement.LoginActivity;
import com.example.zoldseges.Activitys.UserManagement.ProfileActivity;
import com.example.zoldseges.Models.Store;

import com.example.zoldseges.Adapters.StoreAdapter;
import com.example.zoldseges.DAOS.StoreSelectorDAO;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class HomeActivity extends AppCompatActivity implements StoreSelectorDAO {

    private FirebaseFirestore db; // Firestore database reference
    FirebaseAuth auth; // Firebase Authentication instance
    private FirebaseUser currentUser; // Reference to the currently authenticated user

    // UI Components
    RecyclerView storesRecyclerView;
    ImageView homeLogoImageView;
    AppBarLayout homeAppBar;

    // Data lists
    ArrayList<Store> storeList;
    private ArrayList<Store> filteredStoreList;

    // Adapter for the RecyclerView
    private StoreAdapter storeAdapter;

    // Progress and loading indicators
    private ProgressBar progressBarHomePage;
    private TextView loadingTextView;

    // Searching UI
    private SearchView searchView;
    private RelativeLayout noSearchResultsLayout;

    // Menu item
    private MenuItem cartMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize Firebase and UI components
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        storesRecyclerView = findViewById(R.id.fooldalBoltjai);
        homeLogoImageView = findViewById(R.id.kepFooldalra);
        homeAppBar = findViewById(R.id.appBarfooldal);
        progressBarHomePage = findViewById(R.id.progressFooldal);
        loadingTextView = findViewById(R.id.betoltesFooldal);
        noSearchResultsLayout = findViewById(R.id.nincsKeresesiEredmenyLayout);

        // Set up the RecyclerView with a GridLayoutManager
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        storesRecyclerView.setLayoutManager(layoutManager);
        storesRecyclerView.setHasFixedSize(true);

        // Initialize data lists
        storeList = new ArrayList<>();
        filteredStoreList = new ArrayList<>();

        // Initialize UI visibility and fetch data
        hideElements();
        clearStoreList();
        getDataFromFireBase();
    }

    /**
     * Hides UI elements that are not needed while data is being fetched.
     */
    private void hideElements() {
        progressBarHomePage.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        homeAppBar.setVisibility(View.INVISIBLE);
        storesRecyclerView.setVisibility(View.GONE);
    }

    /**
     * Displays UI elements once data has been successfully fetched.
     */
    private void displayElements() {
        progressBarHomePage.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        homeAppBar.setVisibility(View.VISIBLE);
        storesRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reinitialize RecyclerView layout manager and data lists and display stores in 1 column
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        storesRecyclerView.setLayoutManager(layoutManager);
        storesRecyclerView.setHasFixedSize(true);
        auth = FirebaseAuth.getInstance();
        storeList = new ArrayList<>();
        filteredStoreList = new ArrayList<>();

        // Refresh UI and fetch new data
        hideElements();
        clearStoreList();
        getDataFromFireBase();
        invalidateOptionsMenu(); // Refresh the options menu
    }

    private void clearStoreList() {
        // Check if the store list is initialized
        if (storeList != null) {
            // Clear the store list
            storeList.clear();

            // Notify the adapter about the data change if it's initialized
            if (storeAdapter != null) {
                storeAdapter.notifyDataSetChanged();
            }
        } else {
            // Initialize the store list if it is null
            storeList = new ArrayList<>();
        }
    }


    private void getDataFromFireBase() {
        // Create a query to fetch stores from Firestore
        Query query = FirebaseFirestore.getInstance().collection("uzletek");

        // Order the results by store name in ascending order and listen for changes
        query.orderBy("cegNev", Query.Direction.ASCENDING).addSnapshotListener((storeCollection, error) -> {
            clearStoreList(); // Clear the existing store list
            assert storeCollection != null; // Ensure the collection is not null
            for (QueryDocumentSnapshot storeDocument : storeCollection) {
                // Create a new Store object and populate its fields
                Store store = new Store();
                store.setStoreName(storeDocument.getString("cegNev"));
                store.setStoreImage(storeDocument.getString("boltKepe"));
                store.setBusinessAddress(storeDocument.getString("szekhely"));
                store.setOwnerId(storeDocument.getString("tulajId"));
                store.setStoreId(storeDocument.getId());
                store.setShippingCost(store.getShippingCost());
                store.setDeliveryDuration(store.getDeliveryDuration());

                // Add the store to the store list
                storeList.add(store);
            }

            // Initialize the adapter with the store list and set it to the RecyclerView
            storeAdapter = new StoreAdapter(getApplicationContext(), storeList, HomeActivity.this);
            storesRecyclerView.setAdapter(storeAdapter);

            // Update the UI to show the data
            displayElements();
        });
    }

    // The toolbar when the user is at the home page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu and configure menu items
        getMenuInflater().inflate(R.menu.menu_bar, menu);
        MenuItem menuItem = menu.findItem(R.id.kereso);
        searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setQuery("", false);
        searchView.setQueryHint("Keresés...");
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // Configure the cart menu item
        cartMenuItem = menu.findItem(R.id.kosarFooldal);

        if (auth.getCurrentUser() != null) {
            DocumentReference reference = db.collection("felhasznalok").document(auth.getCurrentUser().getUid());
            reference.addSnapshotListener((value, error) -> {
                if (value != null) {
                    // Show or hide the cart menu item based on user type
                    String userType = value.getString("felhasznaloTipus");
                    if (userType != null) {
                        cartMenuItem.setVisible(!userType.equals("Eladó cég/vállalat"));
                    } else {
                        // Sign out the user if userType is null
                        auth.signOut();
                        cartMenuItem.setVisible(false);
                    }
                } else {
                    // Sign out the user if user document is null
                    auth.signOut();
                    cartMenuItem.setVisible(false);
                }
            });
        } else {
            // Hide the cart menu item if there is no authenticated user
            cartMenuItem.setVisible(false);
        }

        // Set up the search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchingText) {
                filterList(searchingText);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void filterList(String searchingText) {
        CollectionReference allProductsCollection = db.collection("osszesTermek");
        if (searchingText.isEmpty()) {
            // Show all stores if the search query is empty
            noSearchResultsLayout.setVisibility(View.GONE);
            storesRecyclerView.setVisibility(View.VISIBLE);
            filteredStoreList.clear();
            getDataFromFireBase();
        } else {
            // Filter stores based on the search query
            allProductsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        filteredStoreList.clear();
                        for (QueryDocumentSnapshot storeDocument : task.getResult()) {
                            // get the product name and the store what contains the product
                            String productName = storeDocument.getString("termekNeve");
                            String storeId = storeDocument.getString("uzletId");

                            // go through on all the stores to find a store name with the searching text
                            // or a product in some of the stores
                            for (Store store : storeList) {
                                assert productName != null;
                                // if the store document matches the store list element
                                if (store.getStoreId().equals(storeId)) {
                                    // Add store to filtered list if product name matches the query
                                    if (productName.toLowerCase().contains(searchingText.toLowerCase()) && !filteredStoreList.contains(store)) {
                                        filteredStoreList.add(store);
                                    }
                                }
                                // Add store to filtered list if store name matches the query
                                if (store.getStoreName().toLowerCase().contains(searchingText.toLowerCase()) && !filteredStoreList.contains(store)) {
                                    filteredStoreList.add(store);
                                }
                            }
                        }
                        if (filteredStoreList.isEmpty()) {
                            // Show no results layout if no stores match the query
                            noSearchResultsLayout.setVisibility(View.VISIBLE);
                            storesRecyclerView.setVisibility(View.GONE);
                        } else {
                            // Sort and display the filtered stores
                            filteredStoreList.sort(Comparator.comparing(Store::getStoreName));

                            storeAdapter = new StoreAdapter(getApplicationContext(), filteredStoreList, HomeActivity.this);
                            storesRecyclerView.setAdapter(storeAdapter);
                            noSearchResultsLayout.setVisibility(View.GONE);
                            storesRecyclerView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Handle errors and retry fetching data
                        noSearchResultsLayout.setVisibility(View.GONE);
                        getDataFromFireBase();
                        filteredStoreList.clear();
                        Toast.makeText(getApplicationContext(), "Váratlan hiba történt!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle menu item selections
        if (item.getItemId() == R.id.kosarFooldal) {
            // Open the cart activity when the cart menu item is selected
            startActivity(new Intent(this, CartActivity.class));
            return true;
        }
        if (item.getItemId() == R.id.fiokFooldal) {
            // Open the profile activity if the user is logged in, otherwise open the login activity
            if (currentUser != null) {
                startProfile();
            } else {
                startLogin();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Start the profile activity
    public void startProfile() {
        startActivity(new Intent(this, ProfileActivity.class));
    }

    // Start the login activity
    public void startLogin() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // Prepare the options menu before it is displayed
        final MenuItem menuItem = menu.findItem(R.id.kosarFooldal);

        // Retrieve the custom view of the cart menu item
        FrameLayout cartView = (FrameLayout) menuItem.getActionView();
        FrameLayout quantityBadge = cartView.findViewById(R.id.kosar_mennyiseg_szamlalo);
        TextView badgeCounter = cartView.findViewById(R.id.kosar_mennyiseg_szamlalo_text);

        if (auth.getCurrentUser() != null) {
            if (cartItemList != null && cartItemList.size() != 0) {
                // Show the notification badge with the cart item count if the cart is not empty
                quantityBadge.setVisibility(View.VISIBLE);
                badgeCounter.setText(String.valueOf(cartItemList.size()));
            } else {
                // Hide the notification badge if the cart is empty
                quantityBadge.setVisibility(View.GONE);
            }
        } else {
            // Hide the notification badge if the user is not authenticated
            quantityBadge.setVisibility(View.GONE);
        }

        // Set a click listener on the custom cart view to trigger the menu item action
        cartView.setOnClickListener(view -> {
            onOptionsItemSelected(menuItem); // Call the onOptionsItemSelected method and this method handle the menu item selection
        });

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onStoreSelected(int position) {
        // Handle store item selection from the list
        Intent intent = new Intent(HomeActivity.this, StorePageActivity.class);

        // If the user didn't searched for a store or a product then pass the stores details to the store page activity
        if (filteredStoreList.isEmpty()) {
            intent.putExtra("storeName", storeList.get(position).getStoreName());
            intent.putExtra("businessAddress", storeList.get(position).getBusinessAddress());
            intent.putExtra("storeId", storeList.get(position).getStoreId());
            intent.putExtra("ownerId", storeList.get(position).getOwnerId());
            intent.putExtra("storeImage", storeList.get(position).getStoreImage());

        } else {
            // If the user searched for a store or product then pass the filtered stores details to the store page activity
            intent.putExtra("storeName", filteredStoreList.get(position).getStoreName());
            intent.putExtra("businessAddress", filteredStoreList.get(position).getBusinessAddress());
            intent.putExtra("storeId", filteredStoreList.get(position).getStoreId());
            intent.putExtra("ownerId", filteredStoreList.get(position).getOwnerId());
            intent.putExtra("storeImage", filteredStoreList.get(position).getStoreImage());
        }

        // Start the store page activity
        startActivity(intent);

        // Reset the search view
        searchView.setQuery("", false);
        searchView.setQueryHint("Keresés...");
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }
    }

    // Reset the search view when the back button is clicked
    public void onBack(View view) {
        searchView.setQuery("", false);
        searchView.setQueryHint("Keresés...");
    }
}