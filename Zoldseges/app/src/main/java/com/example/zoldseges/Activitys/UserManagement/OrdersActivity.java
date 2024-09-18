package com.example.zoldseges.Activitys.UserManagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.zoldseges.Activitys.ReceiptActivity;
import com.example.zoldseges.Models.Receipt;
import com.example.zoldseges.DAOS.ReceiptDAO;
import com.example.zoldseges.Adapters.ReceiptAdapter;
import com.example.zoldseges.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Objects;

public class OrdersActivity extends AppCompatActivity implements ReceiptDAO {

    private FirebaseAuth auth; // Firebase Authentication instance
    FirebaseFirestore db; // Firestore database reference

    // Data list for store receipts
    private ArrayList<Receipt> receiptList;

    // Adapter for the RecyclerView
    private ReceiptAdapter receiptAdapter;

    // Progress and loading indicators
    private ProgressBar loadingProgressBar;
    private TextView loadingTextView;

    // UI Elements
    private RecyclerView receiptsRecyclerView;
    private AppBarLayout appBarForReceipts;
    private ImageView receiptImageView;

    // Layout that will be shown when there are no receipts
    private RelativeLayout noReceiptsLayout;

    // looged-in user is seller
    private boolean isSeller;

    // the store the user ordered from
    private String sellerStoreId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // Set up the Action Bar with a back button and a title
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Rendeléseim");

        // Initialize Firebase Auth and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // If the user is not logged in, navigate them to the login screen
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(OrdersActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Initialize UI elements
            noReceiptsLayout = findViewById(R.id.nincsNyugtaLayout);
            loadingProgressBar = findViewById(R.id.progressNyugtak);
            loadingTextView = findViewById(R.id.betoltesNyugtak);
            receiptsRecyclerView = findViewById(R.id.nyugtak);
            appBarForReceipts = findViewById(R.id.appBarNyugta);
            receiptImageView = findViewById(R.id.kepNyugtakhoz);

            // Set up the RecyclerView with a GridLayoutManager
            GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
            receiptsRecyclerView.setLayoutManager(layoutManager);
            receiptsRecyclerView.setHasFixedSize(true);

            // Initialize receipts list
            receiptList = new ArrayList<>();

            // Initialize UI visibility and fetch data
            hideElements();
            clearReceiptList();
            checkIfUserIsSeller();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the user is not logged in, redirect them to the login screen
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(OrdersActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            hideElements();
            clearReceiptList();
            checkIfUserIsSeller();
        }
    }

    private void checkIfUserIsSeller() {
        DocumentReference userReference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());

        // Listen for user document changes and check if the user is a seller
        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                isSeller = Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat");
                sellerStoreId = value.getString("uzletId");

                // Fetch data from Firestore based on whether the user is a seller
                getDataFromFireBase(isSeller);
            }
        });
    }

    // Method to fetch receipt data from Firestore
    private void getDataFromFireBase(boolean isSeller) {
        Query query = FirebaseFirestore.getInstance().collection("nyugtak");
        // Order receipts by date in descending order
        query.orderBy("idopont", Query.Direction.DESCENDING).addSnapshotListener((nyugtakCollection, error) -> {
            assert nyugtakCollection != null;
            clearReceiptList();
            // If the user is not a seller, show only their own receipts
            if (!isSeller) {
                for (QueryDocumentSnapshot adat : nyugtakCollection) {
                    if (Objects.equals(adat.getString("rendeloId"), Objects.requireNonNull(auth.getCurrentUser()).getUid())) {
                        Receipt receipt = new Receipt();
                        receipt.setOrderDate(adat.getString("idopont"));
                        receipt.setReceiptId(adat.getString("nyugtaId"));
                        receipt.setCustomerId(Objects.requireNonNull(auth.getCurrentUser()).getUid());
                        receipt.setItems(adat.getString("termekek"));
                        receipt.setTotalAmount(adat.getString("vegosszeg"));
                        receipt.setStoreId(adat.getString("storeId"));
                        receipt.setStoreImage(adat.getString("boltKepe"));
                        receipt.setStoreName(adat.getString("uzletNeve"));
                        receipt.setCustomerName(adat.getString("rendeloNev"));
                        receiptList.add(receipt);
                    }
                }
            } else { // If the user is a seller, show receipts related to their store
                for (QueryDocumentSnapshot adat : nyugtakCollection) {
                    if (Objects.equals(adat.getString("uzletId"), sellerStoreId)) {
                        Receipt receipt = new Receipt();
                        receipt.setOrderDate(adat.getString("idopont"));
                        receipt.setReceiptId(adat.getString("nyugtaId"));
                        receipt.setCustomerId(adat.getString("rendeloId"));
                        receipt.setItems(adat.getString("termekek"));
                        receipt.setTotalAmount(adat.getString("vegosszeg"));
                        receipt.setStoreId(adat.getString("uzletId"));
                        receipt.setStoreImage(adat.getString("boltKepe"));
                        receipt.setStoreName(adat.getString("uzletNeve"));
                        receipt.setCustomerName(adat.getString("rendeloNev"));
                        receiptList.add(receipt);
                    }
                }
            }

            // Set up the adapter with the receipt list
            receiptAdapter = new ReceiptAdapter(getApplicationContext(), receiptList, OrdersActivity.this, isSeller);
            receiptsRecyclerView.setAdapter(receiptAdapter);

            // Update the UI to show the data
            showElements();

        });
    }

    private void clearReceiptList() {
        // Check if the receipt list is initialized
        if (receiptList != null) {
            receiptList.clear();

            // Notify the adapter about the data change if it's initialized
            if (receiptAdapter != null) {
                receiptAdapter.notifyDataSetChanged();
            }
        } else {
            // Initialize the receipt list if it is null
            receiptList = new ArrayList<>();
        }
    }

    // Hide loading progress and hide receipt details when content is being loaded
    public void showElements() {
        loadingProgressBar.setVisibility(View.GONE);
        loadingTextView.setVisibility(View.GONE);
        if (receiptList.size() == 0) {
            noReceiptsLayout.setVisibility(View.VISIBLE);
        } else {
            receiptsRecyclerView.setVisibility(View.VISIBLE);
        }
        appBarForReceipts.setVisibility(View.VISIBLE);
        receiptImageView.setVisibility(View.VISIBLE);
    }

    // Show loading progress and hide receipt details when content is being loaded
    public void hideElements() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loadingTextView.setVisibility(View.VISIBLE);
        noReceiptsLayout.setVisibility(View.GONE);
        receiptsRecyclerView.setVisibility(View.GONE);
        appBarForReceipts.setVisibility(View.INVISIBLE);
        receiptImageView.setVisibility(View.GONE);
    }

    // The toolbar when the user is at the own receipts page
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        // Find the cart menu item for visibility
        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);
        cartMenuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // Handle item selection from the toolbar options menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Close the current activity and go back to the previous activity (home)
        if (item.getItemId() == android.R.id.home) {
            finish();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * method if the user selected one of the receipt
     */
    @Override
    public void onReceiptSelect(int position) {
        // Handle receipt item selection from the list
        Intent intent = new Intent(OrdersActivity.this, ReceiptActivity.class);

        // Pass the receipt details to the Receipt page activity
        intent.putExtra("receiptId", receiptList.get(position).getReceiptId());
        intent.putExtra("isPostPayment", false);
        startActivity(intent);
    }

    // Navigate back and finish activity when user presses the "Back" button
    public void onBack(View view) {
        finish();
        super.onBackPressed();
    }
}