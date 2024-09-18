package com.example.zoldseges.Activitys;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.zoldseges.Activitys.UserManagement.TermsActivity;
import com.example.zoldseges.Activitys.UserManagement.LoginActivity;
import com.example.zoldseges.Activitys.UserManagement.ProfileActivity;
import com.example.zoldseges.Adapters.NotificationHandler;
import com.example.zoldseges.Models.CartItem;
import com.example.zoldseges.Models.Receipt;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase Authentication instance
    private FirebaseFirestore db; // Firestore database reference
    private DocumentReference userDocumentReference; // Document reference for the paying user

    // Progress and loading indicators
    private TextView loadingText;
    private ProgressBar paymentProgressBar;

    // UI Elements
    private ImageView paymentImage;
    private EditText customerNameEditText;
    private EditText customerEmailEditText;
    private EditText customerPhoneEditText;
    private EditText customerShippingAddressEditText;
    private EditText customerCompanyTaxIdEditText;
    private EditText customerCompanyAddressEditText;
    private TextView cartContentTextView;
    private TextView totalAmountTextView;
    private TextView updateDataTextView;
    private Button placeOrderButton;
    private CheckBox termsAcceptedCheckBox;

    // Total amount
    private double amountToBePaid = 0;

    // Cart Management
    private MenuItem cartMenuItem;
    String cartItems;
    private TextView orderedProductTextView;

    // if the buyer is company need more fields for shipping
    private boolean isCompany;

    // Store Data
    private String storeId;
    String storeName;
    String storeImage;
    String storeEmail;
    String storeAddress;
    String storePhoneNumber;
    Map<Product, Double> stockReduction;

    // Notification for the buyer and seller from the payement
    private NotificationHandler notificationHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize Firebase and Firestore
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // if no user is logged in then redirect to login page
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Set up the ActionBar
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);

            getSupportActionBar().setTitle("Fizetés"); // Set the ActionBar title to Payment

            // Initialize UI elements
            stockReduction = new HashMap<>();
            paymentProgressBar = findViewById(R.id.progressFizetes);
            loadingText = findViewById(R.id.betoltesFizetes);
            paymentImage = findViewById(R.id.fizetesKep);
            customerNameEditText = findViewById(R.id.megrendeloNeve);
            customerEmailEditText = findViewById(R.id.megrendeloEmailCime);
            customerPhoneEditText = findViewById(R.id.megrendeloTelefonszama);
            customerShippingAddressEditText = findViewById(R.id.megrendeloSzallitasiCime);
            customerCompanyTaxIdEditText = findViewById(R.id.megrendeloCegAdoszama);
            customerCompanyAddressEditText = findViewById(R.id.megrendeloCegSzekhelye);
            cartContentTextView = findViewById(R.id.kosarTartalma);
            totalAmountTextView = findViewById(R.id.vegosszeg);
            placeOrderButton = findViewById(R.id.rendelesLeadasa);
            orderedProductTextView = findViewById(R.id.rendelendoTermekText);
            termsAcceptedCheckBox = findViewById(R.id.aszfElfogad);
            updateDataTextView = findViewById(R.id.adatModosit);
            totalAmountTextView.setText("");
            cartContentTextView.setText("");

            // Set the initial visibility of the UI elements
            hideElements();
            modifyDataText();
            acceptTerms();

            // Get current user data from Firestore and initialize notification handler for payment
            userDocumentReference = db.collection("felhasznalok").document(Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getUid()));
            isCompany = loadUserData();
            loadPurchaseDetails();
            notificationHandler = new NotificationHandler(PaymentActivity.this);
        }

    }

    private void modifyDataText() {
        SpannableString modifyDataString = new SpannableString("Adatok módosítása"); // "Modify Data"
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Redirect to Account Activity when clicked
                startActivity(new Intent(PaymentActivity.this, ProfileActivity.class));
            }
        };

        // Apply clickable and colored span to "Adatok módosítása" (characters 7 to 17)
        modifyDataString.setSpan(clickableSpan, 7, 17, 0);
        modifyDataString.setSpan(new URLSpan(""), 7, 17, 0);
        modifyDataString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(PaymentActivity.this, R.color.purple_500)), 7, 17, 0);

        // Make the TextView clickable and assign the SpannableString
        updateDataTextView.setMovementMethod(LinkMovementMethod.getInstance());
        updateDataTextView.setText(modifyDataString, TextView.BufferType.SPANNABLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if user is still logged in; if not, redirect to login
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(PaymentActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Revalidate terms and conditions and check if the cart is empty
            acceptTerms();
            if (cartItemList.size() == 0) {
                finish();
                super.onBackPressed();
                super.onBackPressed();
            }
        }
    }

    /**
     * Load user data from Firestore and update UI fields based on whether the user is a company or an individual
     * Returns true if the user is a company, false otherwise
     */
    private boolean loadUserData() {
        userDocumentReference.addSnapshotListener((userSnapshot, error) -> {
            assert userSnapshot != null;
            // if the logged in user who want to pay is a company or an individual
            if (Objects.equals(userSnapshot.getString("felhasznaloTipus"), "Cég/Vállalat")) {
                customerCompanyTaxIdEditText.setVisibility(View.VISIBLE);
                customerCompanyAddressEditText.setVisibility(View.VISIBLE);
                customerNameEditText.setHint("Üzlet neve*");
                customerCompanyTaxIdEditText.setText(userSnapshot.getString("adoszam"));
                customerCompanyAddressEditText.setText(userSnapshot.getString("szekhely"));
                customerNameEditText.setText(userSnapshot.getString("cegNev"));
                isCompany = true;
            } else {
                customerCompanyTaxIdEditText.setVisibility(View.GONE);
                customerCompanyAddressEditText.setVisibility(View.GONE);
                customerNameEditText.setHint("Megrendelő neve*");
                customerNameEditText.setText(userSnapshot.getString("nev"));
                isCompany = false;
            }
            customerEmailEditText.setText(userSnapshot.getString("email"));
            customerPhoneEditText.setText(userSnapshot.getString("telefonszam"));
            customerShippingAddressEditText.setText(userSnapshot.getString("lakcim"));
        });
        return isCompany;
    }

    private void acceptTerms() {
        // Create a SpannableString for the "Elfogadom az ÁSZF-t" text
        SpannableString termsText = new SpannableString("Elfogadom az ÁSZF-t");

        // Make a portion of the text clickable
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                // Redirect to the Terms and Conditions activity when clicked
                startActivity(new Intent(PaymentActivity.this, TermsActivity.class));
            }
        };

        // Set the clickable span to the relevant part of the text (characters 13-17 for "ÁSZF")
        termsText.setSpan(clickableSpan, 13, 17, 0);
        termsText.setSpan(new URLSpan(""), 13, 17, 0);
        termsText.setSpan(new ForegroundColorSpan(ContextCompat.getColor(PaymentActivity.this, R.color.purple_500)), 13, 17, 0);

        // Make the checkbox's text clickable and set the SpannableString
        termsAcceptedCheckBox.setMovementMethod(LinkMovementMethod.getInstance());
        termsAcceptedCheckBox.setText(termsText, TextView.BufferType.SPANNABLE);
    }

    private void loadPurchaseDetails() {
        // Initialize total amount to zero first
        amountToBePaid = 0;

        // Iterate through each cart item
        for (CartItem cartItem : cartItemList) {
            // Calculate the price for the current item based on quantity
            double itemTotalPrice = cartItem.getProduct().getPrice() * cartItem.getQuantity();
            int roundedItemPrice;

            // Ensure the price is at least 1 Ft if it's 0 or negative after rounding
            if ((int) Math.round(itemTotalPrice) <= 0) {
                roundedItemPrice = 1;
            } else {
                roundedItemPrice = (int) Math.round(itemTotalPrice);
            }

            // Build the cart item string for display (with or without decimals in quantity)
            if (cartItem.getQuantity() % 1 == 0) {
                cartItems = (int) cartItem.getQuantity() + "x " + cartItem.getProduct().getProductName() + "  " + roundedItemPrice + " Ft" + "\n";
            } else {
                cartItems = cartItem.getQuantity() + "x " + cartItem.getProduct().getProductName() + "  " + roundedItemPrice + " Ft" + "\n";
            }

            // Create a Product object and update stock reduction map
            Product product = new Product(cartItem.getProduct().getProductName(),
                    cartItem.getProduct().getPrice(),
                    cartItem.getProduct().getAvailableStockQuantity(),
                    cartItem.getProduct().getProductWeight(),
                    cartItem.getProduct().getProductImage(),
                    cartItem.getProduct().getStoreId(),
                    cartItem.getProduct().getAllProductCollectionId()
            );
            product.setProductId(cartItem.getProduct().getProductId());
            stockReduction.put(product, cartItem.getQuantity());

            // Add the item's price to the total amount to be paid
            amountToBePaid += cartItem.getProduct().getPrice() * cartItem.getQuantity();

            // Append the current item details to the cart content display
            cartContentTextView.append(cartItems);
        }
        // Add 5000 Ft shipping fee to the total
        amountToBePaid += 5000;

        // Format the total amount for display (rounded if needed)
        String totalAmountText;
        if (amountToBePaid % 1 == 0) {
            totalAmountText = "Végösszeg: " + ((int) Math.round(amountToBePaid)) + " Ft\n(+5000 Ft szállítási költség)";
        } else {
            totalAmountText = "Végösszeg: " + Math.round(amountToBePaid) + " Ft\n(+5000 Ft szállítási költség)";
        }

        // Retrieve the actual store ID from the cart items
        for (CartItem elem : cartItemList) {
            storeId = elem.getProduct().getStoreId();
        }

        // Fetch store details from Firestore
        DocumentReference storeReference = db.collection("uzletek").document(storeId);
        storeReference.addSnapshotListener((storeSnapshot, error) -> {
            assert storeSnapshot != null;
            // Retrieve store details from the document
            storeImage = storeSnapshot.getString("boltKepe");
            storeName = storeSnapshot.getString("cegNev");
            storeAddress = storeSnapshot.getString("szekhely");
            String ownerId = storeSnapshot.getString("tulajId");

            assert ownerId != null;

            // Fetch store owner details
            DocumentReference ownerReference = db.collection("felhasznalok").document(ownerId);
            ownerReference.addSnapshotListener((ownerSnapshot, error1) -> {
                assert ownerSnapshot != null;
                // Retrieve owner's contact information
                storeEmail = ownerSnapshot.getString("email");
                storePhoneNumber = ownerSnapshot.getString("telefonszam");
            });
            // Update the UI to show the data
            showElements();
        });
        // Set the total amount in the corresponding TextView
        totalAmountTextView.setText(totalAmountText);
    }

    // Show loading progress and hide product details when content is being loaded
    public void hideElements() {
        paymentProgressBar.setVisibility(View.VISIBLE);
        loadingText.setVisibility(View.VISIBLE);
        paymentImage.setVisibility(View.INVISIBLE);
        customerNameEditText.setVisibility(View.GONE);
        customerEmailEditText.setVisibility(View.GONE);
        customerPhoneEditText.setVisibility(View.GONE);
        customerShippingAddressEditText.setVisibility(View.GONE);
        customerCompanyTaxIdEditText.setVisibility(View.GONE);
        customerCompanyAddressEditText.setVisibility(View.GONE);
        cartContentTextView.setVisibility(View.GONE);
        totalAmountTextView.setVisibility(View.GONE);
        placeOrderButton.setVisibility(View.GONE);
        orderedProductTextView.setVisibility(View.GONE);
        updateDataTextView.setVisibility(View.GONE);
        termsAcceptedCheckBox.setVisibility(View.GONE);
    }

    // Hide loading progress and show product details when content is being loaded
    public void showElements() {
        paymentProgressBar.setVisibility(View.GONE);
        loadingText.setVisibility(View.GONE);
        paymentImage.setVisibility(View.VISIBLE);
        customerNameEditText.setVisibility(View.VISIBLE);
        customerEmailEditText.setVisibility(View.VISIBLE);
        customerPhoneEditText.setVisibility(View.VISIBLE);
        customerShippingAddressEditText.setVisibility(View.VISIBLE);
        loadUserData();
        cartContentTextView.setVisibility(View.VISIBLE);
        totalAmountTextView.setVisibility(View.VISIBLE);
        placeOrderButton.setVisibility(View.VISIBLE);
        orderedProductTextView.setVisibility(View.VISIBLE);
        updateDataTextView.setVisibility(View.VISIBLE);
        termsAcceptedCheckBox.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_menu, menu);

        // Find the cart menu item for visibility
        View view = menu.findItem(R.id.kosar).getActionView();
        cartMenuItem = menu.findItem(R.id.kosar);
        cartMenuItem.setVisible(false);

        // Open the cart activity when the cart menu item is selected
        view.setOnClickListener(v -> startActivity(new Intent(PaymentActivity.this, CartActivity.class)));
        return super.onCreateOptionsMenu(menu);
    }

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
        return super.onOptionsItemSelected(item);
    }

    public void onOrderSubmission(View view) {
        // Check if the terms and conditions checkbox is checked
        if (termsAcceptedCheckBox.isChecked()) {
            // Check if all the required fields are filled out
            if ((isCompany && !customerNameEditText.getText().toString().isEmpty() && !customerEmailEditText.getText().toString().isEmpty() && !customerPhoneEditText.getText().toString().isEmpty() && !customerShippingAddressEditText.getText().toString().isEmpty() && !customerCompanyTaxIdEditText.getText().toString().isEmpty() && !customerCompanyAddressEditText.getText().toString().isEmpty()) ||
                    ((!isCompany && !customerNameEditText.getText().toString().isEmpty() && !customerEmailEditText.getText().toString().isEmpty() && !customerPhoneEditText.getText().toString().isEmpty() && !customerShippingAddressEditText.getText().toString().isEmpty()))) {

                // Fetch customer details from EditText fields
                String customerName = customerNameEditText.getText().toString();
                String customerEmail = customerEmailEditText.getText().toString();
                String customerPhone = customerPhoneEditText.getText().toString();
                String shippingAddress = customerShippingAddressEditText.getText().toString();
                String taxNumber = customerCompanyTaxIdEditText.getText().toString();
                String companyAddress = customerCompanyAddressEditText.getText().toString();

                // Create a new receipt document reference in Firestore
                DocumentReference receiptDocumentRef = db.collection("nyugtak").document();

                // Format the current date and time
                String totalAmount = String.valueOf((int) Math.round(amountToBePaid));
                Calendar calendar = Calendar.getInstance();
                Date currentDate = calendar.getTime();
                DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault());
                String timestamp = dateFormat.format(currentDate);

                // Create a new receipt object with all the details
                Receipt newReceipt = new Receipt(receiptDocumentRef.getId(),
                        totalAmount, timestamp,
                        storeId,
                        cartContentTextView.getText().toString(),
                        Objects.requireNonNull(auth.getCurrentUser()).getUid()
                );

                // Set store and customer information in the receipt object
                newReceipt.setStoreImage(storeImage);
                newReceipt.setStoreName(storeName);
                newReceipt.setStoreEmail(storeEmail);
                newReceipt.setStorePhone(storePhoneNumber);
                newReceipt.setStoreBusinessAddress(storeAddress);
                newReceipt.setCustomerName(customerName);
                newReceipt.setCustomerEmail(customerEmail);
                newReceipt.setCustomerPhone(customerPhone);
                newReceipt.setDeliveryAddress(shippingAddress);
                newReceipt.setTaxNumber(taxNumber);
                newReceipt.setBusinessAddress(companyAddress);

                // Create a map to store receipt data
                Map<String, String> receiptMap = newReceipt.createReceiptMap(newReceipt);

                // Hide unnecessary UI elements while processing the order
                hideElements();

                // Save the receipt data to Firestore
                receiptDocumentRef.set(receiptMap).addOnCompleteListener(paymentTask -> {
                    if (paymentTask.isSuccessful()) {
                        // Proceed to next step once the receipt is successfully stored
                        onReceiptGenerated(receiptMap);
                    }
                });

            } else {
                // Show a toast message if required fields are missing
                Toast.makeText(getApplicationContext(), "Nem maradhatnak mezők üresen!", Toast.LENGTH_LONG).show();
            }
        } else {
            // Show a toast message if terms and conditions are not accepted
            Toast.makeText(getApplicationContext(), "Előbb el kell fogadnod az Általános Szerződési Feltételeket!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is called after a receipt has been successfully created.
     * It handles the update of product stock quantities and notifies the user of a successful order.
     */
    private void onReceiptGenerated(Map<String, String> receiptData) {
        // Create an intent to move to the ReceiptActivity
        Intent intent = new Intent(PaymentActivity.this, ReceiptActivity.class);

        // Pass receipt ID and post-payment flag to the next activity
        for (Map.Entry<String, String> entry : receiptData.entrySet()) {
            if (entry.getKey().equals("nyugtaId")) {
                intent.putExtra("receiptId", entry.getValue());
                intent.putExtra("isPostPayment", true);
            }
        }

        // Get the reference to the store's products collection
        CollectionReference storeProductsCollection = db.collection("uzletek").document(storeId).collection("termekek");

        // Retrieve the products from Firestore
        storeProductsCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // Iterate over each product in the store
                for (QueryDocumentSnapshot productSnapshot : task.getResult()) {
                    Product updatedProduct;
                    Map<String, Object> updatedProductMap;
                    String productId = productSnapshot.getId();
                    DocumentReference productDocumentRef = storeProductsCollection.document(productSnapshot.getId());

                    // Check each product in the stock reduction list
                    for (Map.Entry<Product, Double> stockReductionEntry : stockReduction.entrySet()) {
                        // If the product matches the ID, update its stock quantity
                        if (stockReductionEntry.getKey().getProductId().equals(productId)) {
                            double originalStock = stockReductionEntry.getKey().getAvailableStockQuantity();
                            double quantityToReduce = stockReductionEntry.getValue();
                            stockReductionEntry.setValue(originalStock - quantityToReduce);
                            stockReductionEntry.getKey().setAvailableStockQuantity(stockReductionEntry.getValue());

                            // Update Firestore with the reduced stock
                            updatedProduct = stockReductionEntry.getKey();
                            updatedProductMap = updatedProduct.addNewProduct(updatedProduct);
                            productDocumentRef.set(updatedProductMap);
                        }
                    }
                }

                // Notify the user of a successful order
                notificationHandler.sendNotification();

                // Clear the cart after successful order
                cartItemList.clear();

                // Start the ReceiptActivity
                startActivity(intent);
            }
        });

    }
}