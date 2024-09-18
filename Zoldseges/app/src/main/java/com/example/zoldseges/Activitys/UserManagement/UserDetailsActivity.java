package com.example.zoldseges.Activitys.UserManagement;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.zoldseges.Models.User;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserDetailsActivity extends AppCompatActivity {

    private StorageReference storageReference; // Firebase Storage reference
    private DocumentReference currentUserDocumentReference;
    private FirebaseAuth auth;  // Firebase Authentication instance
    private FirebaseUser currentUser;
    private FirebaseFirestore db; // Firestore database reference

    // UI Elements
    TextView nameTextView;
    TextView emailTextView;
    TextView phoneTextView;
    TextView shippingAddressTextView;
    TextView passwordTextView;
    TextView passwordConfirmTextView;
    TextView businessAddressTextView;
    TextView companyNameTextView;
    TextView taxNumberTextView;
    TextView companyNameLabel;
    TextView taxNumberLabel;
    TextView businessAddressLabel;
    TextView nameLabel;
    TextView emailLabel;
    TextView phoneLabel;
    TextView shippingAddressLabel;
    TextView passwordLabel;
    TextView passwordConfirmLabel;
    ImageView productImageEdit;
    TextView productImageTitleEdit;
    private LinearLayout shippingAddressLayout;
    Button editButton;
    Button backButton;

    // Progress and loading indicators
    ProgressBar updateProgressBar;
    TextView updateTextView;

    // Variables for user data and image handling
    private Map<String, Object> updatedUserData = new HashMap<>();
    String storeImageUrl;
    private Uri selectedImageUri;
    private Uri oldImageUri;
    private DocumentReference storeReference;
    final String[] userType = {""};
    private String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // Setup action bar
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Adatok módosítása");

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // if no user is logged in then redirect to login page
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(UserDetailsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            // Initialize UI elements
            currentUser = auth.getCurrentUser();
            nameTextView = findViewById(R.id.nevAdatSzerkezto);
            updateProgressBar = findViewById(R.id.progressBarModositas);
            updateTextView = findViewById(R.id.modositasText);
            editButton = findViewById(R.id.adatokSzerkesztese);
            backButton = findViewById(R.id.vissza);
            emailTextView = findViewById(R.id.emailAdatSzerkezto);
            phoneTextView = findViewById(R.id.telefonszamAdatSzerkezto);
            shippingAddressTextView = findViewById(R.id.lakcimAdatSzerkezto);
            passwordTextView = findViewById(R.id.jelszoAdatSzerkezto);
            passwordConfirmTextView = findViewById(R.id.jelszoUjraAdatSzerkezto);
            businessAddressTextView = findViewById(R.id.szekhelyAdatSzerkezto);
            companyNameTextView = findViewById(R.id.cegNevAdatSzerkezto);
            taxNumberTextView = findViewById(R.id.adoszamAdatSzerkezto);
            companyNameLabel = findViewById(R.id.cegnevLabel);
            taxNumberLabel = findViewById(R.id.adoszamLabel);
            businessAddressLabel = findViewById(R.id.szekhelyLabel);
            storeImageUrl = "";
            nameLabel = findViewById(R.id.nevLabel);
            emailLabel = findViewById(R.id.emailLabel);
            phoneLabel = findViewById(R.id.telszamLabel);
            shippingAddressLabel = findViewById(R.id.szalitasiCimLabell);
            passwordLabel = findViewById(R.id.paswLabel);
            passwordConfirmLabel = findViewById(R.id.paswRepeatLabel);
            productImageEdit = findViewById(R.id.termekKepBeallitasModosit);
            productImageEdit.setClipToOutline(true); //kép radius aktiválása
            productImageTitleEdit = findViewById(R.id.termekKepCimModosit);
            shippingAddressLayout = findViewById(R.id.szallitasiCimLayout);
            currentUserDocumentReference = db.collection("felhasznalok").document(Objects.requireNonNull(auth.getCurrentUser()).getUid());
            storageReference = FirebaseStorage.getInstance().getReference().child("BoltKepek");

            // Set the initial visibility of the UI elements
            hideElements();
            loadUserData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if no user is logged in then redirect to login page
        if (auth.getCurrentUser() == null) {
            finish();
            Intent intent = new Intent(UserDetailsActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * Loads and displays the user's data, including store image if the user is a seller.
     */
    public void loadUserData() {
        currentUserDocumentReference.addSnapshotListener((value, error) -> {
            assert value != null;
            userType[0] = value.getString("felhasznaloTipus");

            // Populate UI elements with user data
            nameTextView.setText(value.getString("nev"));
            emailTextView.setText(value.getString("email"));
            phoneTextView.setText(value.getString("telefonszam"));
            shippingAddressTextView.setText(value.getString("lakcim"));
            businessAddressTextView.setText(value.getString("szekhely"));
            companyNameTextView.setText(value.getString("cegNev"));
            taxNumberTextView.setText(value.getString("adoszam"));

            if (Objects.equals(value.getString("felhasznaloTipus"), "Eladó cég/vállalat")) {
                storeId = value.getString("uzletId"); // Get store ID for sellers
                storeReference = db.collection("uzletek").document(Objects.requireNonNull(storeId));

                // Load store image if available
                storeReference.addSnapshotListener((store, error1) -> {
                    assert store != null;
                    if (Objects.requireNonNull(store.getString("boltKepe")).isEmpty() || Objects.equals(store.getString("boltKepe"), "null") || store.getString("boltKepe") == null) {
                        oldImageUri = null;
                    } else {
                        oldImageUri = Uri.parse(store.getString("boltKepe"));
                    }
                    updateTextView.setText(R.string.betoltes);
                    try {
                        // Load the store image using Glide, handle errors
                        if (!this.isFinishing()) {
                            Glide.with(UserDetailsActivity.this)
                                    .load(oldImageUri)
                                    .placeholder(R.drawable.grocery_store)
                                    .listener(new RequestListener<Drawable>() {
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
                            }).into(productImageEdit);
                        }
                    } catch (Exception e) {
                        Glide.with(UserDetailsActivity.this).load(R.drawable.grocery_store).into(productImageEdit);
                    }
                });
            } else {
                showElements();
            }
        });
    }

    // Hide loading progress and show product details when content is being loaded
    public void showElements() {
        this.updateProgressBar.setVisibility(View.GONE);
        this.updateTextView.setVisibility(View.GONE);
        this.nameTextView.setVisibility(View.VISIBLE);
        this.emailTextView.setVisibility(View.VISIBLE);
        this.phoneTextView.setVisibility(View.VISIBLE);
        this.passwordTextView.setVisibility(View.VISIBLE);
        this.passwordConfirmTextView.setVisibility(View.VISIBLE);
        this.editButton.setVisibility(View.VISIBLE);
        this.backButton.setVisibility(View.VISIBLE);
        this.nameLabel.setVisibility(View.VISIBLE);
        this.emailLabel.setVisibility(View.VISIBLE);
        this.phoneLabel.setVisibility(View.VISIBLE);
        this.shippingAddressLayout.setVisibility(View.VISIBLE);
        this.passwordLabel.setVisibility(View.VISIBLE);
        this.passwordConfirmLabel.setVisibility(View.VISIBLE);
        handleCompanyOrSellerView();
    }

    /**
     * Determines whether to display company-related fields for sellers and companies.
     */
    public void handleCompanyOrSellerView() {
        if (userType[0].equals("Eladó cég/vállalat") || (userType[0].equals("Cég/Vállalat"))) {
            businessAddressTextView.setVisibility(View.VISIBLE);
            companyNameTextView.setVisibility(View.VISIBLE);
            taxNumberTextView.setVisibility(View.VISIBLE);
            companyNameLabel.setVisibility(View.VISIBLE);
            taxNumberLabel.setVisibility(View.VISIBLE);
            businessAddressLabel.setVisibility(View.VISIBLE);
            shippingAddressLayout.setVisibility(View.VISIBLE);
            productImageEdit.setVisibility(View.GONE);
            productImageTitleEdit.setVisibility(View.GONE);

            this.nameTextView.setHint("Tulajdonos teljes neve*");

            // If user is a seller, show store image options
            if (userType[0].equals("Eladó cég/vállalat")) {
                productImageEdit.setVisibility(View.VISIBLE);
                productImageTitleEdit.setVisibility(View.VISIBLE);
                shippingAddressTextView.setText("");
                shippingAddressLayout.setVisibility(View.GONE);
            }
        } else {
            // For regular users, hide company-specific fields
            this.nameTextView.setHint("Teljes név*");
            businessAddressTextView.setVisibility(View.GONE);
            companyNameTextView.setVisibility(View.GONE);
            taxNumberTextView.setVisibility(View.GONE);
            companyNameLabel.setVisibility(View.GONE);
            taxNumberLabel.setVisibility(View.GONE);
            businessAddressLabel.setVisibility(View.GONE);
            productImageEdit.setVisibility(View.GONE);
            productImageTitleEdit.setVisibility(View.GONE);
        }

    }

    // Show loading progress and hide product details when content is being loaded
    public void hideElements() {
        this.updateProgressBar.setVisibility(View.VISIBLE);
        this.updateTextView.setVisibility(View.VISIBLE);
        this.updateTextView.setText(R.string.modositas);
        this.nameTextView.setVisibility(View.GONE);
        this.emailTextView.setVisibility(View.GONE);
        this.phoneTextView.setVisibility(View.GONE);
        this.shippingAddressLayout.setVisibility(View.GONE);
        this.passwordTextView.setVisibility(View.GONE);
        this.passwordConfirmTextView.setVisibility(View.GONE);
        this.editButton.setVisibility(View.GONE);
        this.backButton.setVisibility(View.GONE);
        this.businessAddressTextView.setVisibility(View.GONE);
        this.companyNameTextView.setVisibility(View.GONE);
        this.taxNumberTextView.setVisibility(View.GONE);
        this.productImageEdit.setVisibility(View.GONE);
        this.productImageTitleEdit.setVisibility(View.GONE);
        this.companyNameLabel.setVisibility(View.GONE);
        this.nameLabel.setVisibility(View.GONE);
        this.emailLabel.setVisibility(View.GONE);
        this.phoneLabel.setVisibility(View.GONE);

        this.passwordLabel.setVisibility(View.GONE);
        this.passwordConfirmLabel.setVisibility(View.GONE);
        this.taxNumberLabel.setVisibility(View.GONE);
        this.businessAddressLabel.setVisibility(View.GONE);
    }

    // The toolbar when the user is at the registration page
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);
        cartMenuItem.setVisible(false);
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

    // Prepare the cart menu item and update the cart badge based on cart item count
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem menuItem = menu.findItem(R.id.kosarfiok);

        // Get the root view of the cart menu item (which contains the cart badge layout)
        FrameLayout cartMenuItemRootView = (FrameLayout) menuItem.getActionView();

        // Find the cart badge layout and text view in the custom menu item layout
        FrameLayout cartBadgeLayout = cartMenuItemRootView.findViewById(R.id.kosar_mennyiseg_szamlalo);

        // Update the cart badge visibility and count based on the number of items in the cart
        TextView cartBadgeCountTextView = cartMenuItemRootView.findViewById(R.id.kosar_mennyiseg_szamlalo_text);
        if (cartItemList != null && cartItemList.size() != 0) {
            cartBadgeLayout.setVisibility(View.VISIBLE);
            cartBadgeCountTextView.setText(String.valueOf(cartItemList.size()));
        } else {
            cartBadgeLayout.setVisibility(View.GONE); // Hide the cart badge if the cart is empty
        }

        // Call the onOptionsItemSelected method and this method handle the menu item selection
        cartMenuItemRootView.setOnClickListener(view -> {
            onOptionsItemSelected(menuItem);
        });

        return super.onPrepareOptionsMenu(menu);
    }

    public void onBack(View view) {
        super.onBackPressed();
    }


    public void onUpdateUserDetails(View view) {
        // Retrieve user input from UI elements
        String newEmail = emailTextView.getText().toString();
        String newPassword = passwordTextView.getText().toString();
        String confirmPassword = passwordConfirmTextView.getText().toString();
        String newName = nameTextView.getText().toString();
        String newPhone = phoneTextView.getText().toString();
        String newShippingAddress = shippingAddressTextView.getText().toString();
        String newCompanyName = companyNameTextView.getText().toString();
        String newBusinessAddress = businessAddressTextView.getText().toString();
        String newTaxNumber = taxNumberTextView.getText().toString();
        AtomicBoolean hasError = new AtomicBoolean(false);

        // Validation logic to check if fields are not empty based on user type
        if (((!newEmail.isEmpty() && !newName.isEmpty() && !newPhone.isEmpty() && !newShippingAddress.isEmpty() && !newCompanyName.isEmpty() && !newBusinessAddress.isEmpty() && !newTaxNumber.isEmpty()) && companyNameTextView.getVisibility() == View.VISIBLE)
                || ((!newEmail.isEmpty() && !newName.isEmpty() && !newPhone.isEmpty() && !newShippingAddress.isEmpty()) && companyNameTextView.getVisibility() == View.GONE)
                || userType[0].equals("Eladó cég/vállalat") && !newEmail.isEmpty() && !newName.isEmpty() && !newPhone.isEmpty() && !newCompanyName.isEmpty() && !newBusinessAddress.isEmpty() && !newTaxNumber.isEmpty()) {

            // Validate email format
            if (isEmailValid(newEmail)) {
                hideElements();

                // Update user document in Firestore
                currentUserDocumentReference.addSnapshotListener((userSnapShot, error) -> {
                    assert userSnapShot != null;
                    this.storeImageUrl = String.valueOf(oldImageUri);
                    User friss = new User(newName, newEmail, newPhone, newShippingAddress, newCompanyName, newTaxNumber, newBusinessAddress, userType[0], storeId);
                    updatedUserData = friss.addUser(friss);
                });
                String previousEmail = currentUser.getEmail();

                // Update email in Firebase Auth
                currentUser.updateEmail(newEmail).addOnCompleteListener(task -> {
                    assert previousEmail != null;
                    if (task.isSuccessful()) {

                        // Update password if provided
                        if (!passwordTextView.getText().toString().isEmpty() || !passwordConfirmTextView.getText().toString().isEmpty()) {
                            if (confirmPassword.equals(newPassword)) {
                                if (newPassword.length() >= 6) {
                                    currentUser.updatePassword(newPassword);
                                } else {
                                    showElements();
                                    Toast.makeText(getApplicationContext(), "A jelszónak minimum 6 karakterből kell álnia!", Toast.LENGTH_LONG).show();
                                    hasError.set(true);
                                    currentUser.updateEmail(previousEmail);
                                }
                            } else {
                                showElements();
                                Toast.makeText(getApplicationContext(), "Nem egyeznek a megadott jelszavak!", Toast.LENGTH_LONG).show();
                                hasError.set(true);
                                currentUser.updateEmail(previousEmail);
                            }
                        }

                        // Update user data in Firestore if no errors
                        if (!hasError.get()) {
                            if (userType[0].equals("Eladó cég/vállalat")) {
                                if (selectedImageUri == null) {
                                    Map<String, String> storeParameters = new HashMap<>();
                                    storeParameters.put("cegNev", companyNameTextView.getText().toString());
                                    storeParameters.put("szekhely", businessAddressTextView.getText().toString());
                                    if (storeImageUrl.equals("null") || storeImageUrl.isEmpty()) {
                                        storeImageUrl = "";
                                    }
                                    storeParameters.put("boltKepe", storeImageUrl);
                                    storeParameters.put("tulajId", Objects.requireNonNull(currentUser.getUid()));
                                    storeReference.set(storeParameters);
                                    db.collection("felhasznalok").document(currentUser.getUid()).set(updatedUserData).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                                            showElements();
                                        }
                                    });
                                } else {
                                    uploadImage(selectedImageUri, userType[0]);
                                }
                            } else {
                                db.collection("felhasznalok").document(currentUser.getUid()).set(updatedUserData).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                                        showElements();
                                    }
                                });
                            }
                        }
                    } else {
                        // Handle email update failure
                        if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "The email address is already in use by another account.")) {
                            showElements();
                            Toast.makeText(getApplicationContext(), "A megadott email cím már használatban van!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } else {
                Toast.makeText(getApplicationContext(), "Érvénytelen email címet adtál meg!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Amit változtatni szeretnél azt nem hagyhatod üresen!", Toast.LENGTH_LONG).show();
        }
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Opens the gallery for the user to select an image to upload
    public void onUploadStoreImage(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(UserDetailsActivity.this).load(selectedImageUri).into(productImageEdit);
        }
    }

    // Uploads an image to Firebase Storage and updates the user's store data with the image URL
    public void uploadImage(Uri uri, String userType) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        StorageReference imageReference;

        if (oldImageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference();
            imageReference = storageReference.child(oldImageUri.getLastPathSegment());
        } else {
            imageReference = storageReference.child("bolt_" + userId + "_" + uri.getLastPathSegment());
        }

        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Once the image is successfully uploaded, retrieve the download URL
                imageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        db.collection("felhasznalok").document(currentUser.getUid()).set(updatedUserData);
                        storeImageUrl = String.valueOf(task.getResult());

                        // Update store data with new image URL
                        Map<String, String> storeParameters = new HashMap<>();
                        storeParameters.put("cegNev", companyNameTextView.getText().toString());
                        storeParameters.put("szekhely", businessAddressTextView.getText().toString());
                        storeParameters.put("boltKepe", storeImageUrl);
                        storeParameters.put("tulajId", Objects.requireNonNull(currentUser.getUid()));
                        storeReference.set(storeParameters).addOnSuccessListener(uzletetFrissit -> {
                            Glide.with(UserDetailsActivity.this).load(oldImageUri).placeholder(R.drawable.grocery_store).listener(new RequestListener<Drawable>() {
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
                            }).into(productImageEdit);
                        });
                        Toast.makeText(getApplicationContext(), "Sikeres frissítés!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }).addOnProgressListener(snapshot -> hideElements()) // Hide UI elements during upload
                .addOnFailureListener(e -> showElements()); // Show UI elements again if upload fails
    }
}