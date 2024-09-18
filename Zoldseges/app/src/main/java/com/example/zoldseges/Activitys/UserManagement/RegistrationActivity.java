package com.example.zoldseges.Activitys.UserManagement;

import static com.example.zoldseges.Activitys.ProductPageActivity.cartItemList;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.zoldseges.Activitys.CartActivity;
import com.example.zoldseges.Models.User;
import com.example.zoldseges.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {
    private static final int IMAGE_REQUEST_CODE = 1; // Request code for selecting an image
    private StorageReference storageReference; // Firebase Storage reference
    private FirebaseFirestore db; // Firestore database reference
    private FirebaseAuth auth; // Firebase Authentication instance

    //  UI Components, text fields for registration form
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView phoneTextView;
    private TextView shippingAddressTextView;
    private TextView passwordTextView;
    private TextView confirmPasswordTextView;
    private TextView companyNameTextView;
    private TextView taxNumberTextView;
    private ImageView storeImageView;
    private TextView storeImageTitle;
    private TextView companyAddressTextView;
    private CheckBox isCompanyCheckbox;
    private Button registrationButton;
    private Button loginButton;
    private SwitchCompat isSellerSwitch;
    private LinearLayout companyFieldsLayout;

    // Data list for user save
    private Map<String, Object> usersMap = new HashMap<>();

    // Progress and loading indicators
    private ProgressBar registrationProgressBar;
    private TextView registrationLoadingText;

    // URI to store the selected image URL
    private Uri selectedImageUrl;

    // Store image URL for sellers
    String storeImageUrl = "";
    DocumentReference storeReference;

    // new user details (userId, userType, if seller registered then store id and a new user object to save the user)
    String storeId = "";
    private User newUserObject;
    public static String userId = "";
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Firestore and Auth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Initialize Firebase Storage for product images

        storageReference = FirebaseStorage.getInstance().getReference().child("BoltKepek");

        // Initialize UI elements
        nameTextView = findViewById(R.id.nev);
        companyFieldsLayout = findViewById(R.id.regCegesCuccok);
        registrationProgressBar = findViewById(R.id.progressBarRegisztracio);
        registrationLoadingText = findViewById(R.id.regisztracioText);
        registrationButton = findViewById(R.id.regisztracioButton);
        emailTextView = findViewById(R.id.email);
        phoneTextView = findViewById(R.id.telefonszam);
        shippingAddressTextView = findViewById(R.id.lakcim);
        passwordTextView = findViewById(R.id.jelszo);
        confirmPasswordTextView = findViewById(R.id.jelszoUjra);
        companyNameTextView = findViewById(R.id.cegNev);
        taxNumberTextView = findViewById(R.id.adoszam);
        companyAddressTextView = findViewById(R.id.szekhely);
        storeImageView = findViewById(R.id.uzletKepBeallitas);
        storeImageTitle = findViewById(R.id.uzletKepCim);
        isCompanyCheckbox = findViewById(R.id.cegE);
        isSellerSwitch = findViewById(R.id.eladoE);
        loginButton = findViewById(R.id.bejelentkezes);

        // Enable rounded corners for the store image view (radius setting)
        storeImageView.setClipToOutline(true);

        // Set up seller-specific registration
        handleSellerRegistration();

        // Set up company-specific registration
        handleCompanyRegistration();

        // Configure action bar with back navigation and title
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Regisztráció");
    }

    // The toolbar when the user is at the registration page
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu layout into the toolbar
        getMenuInflater().inflate(R.menu.back_logged_in_menu, menu);

        // Find the cart menu item for visibility
        MenuItem cartMenuItem = menu.findItem(R.id.kosarfiok);

        // Check if a user is currently logged in
        if (auth.getCurrentUser() == null) {
            cartMenuItem.setVisible(false);
        }
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

        // Redirect to cart activity
        if (item.getItemId() == R.id.kosarfiok) {
            startActivity(new Intent(RegistrationActivity.this, CartActivity.class));
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

    @Override
    public void onBackPressed() {
        // Handle back press by navigating to the login screen
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }

    // Method to handle registration when the user is registering a company
    public void handleCompanyRegistration() {
        isCompanyCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                companyNameTextView.setVisibility(View.VISIBLE);
                taxNumberTextView.setVisibility(View.VISIBLE);
                companyAddressTextView.setVisibility(View.VISIBLE);
                selectedImageUrl = null;
                Glide.with(RegistrationActivity.this).load(R.drawable.grocery_store).into(storeImageView);
            } else {
                companyNameTextView.setVisibility(View.GONE);
                taxNumberTextView.setVisibility(View.GONE);
                companyAddressTextView.setVisibility(View.GONE);
            }
            storeImageView.setVisibility(View.GONE);
            storeImageTitle.setVisibility(View.GONE);
        });
    }

    // Method to handle registration when the user is a seller
    public void handleSellerRegistration() {
        isSellerSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                isCompanyCheckbox.setVisibility(View.GONE);
                shippingAddressTextView.setVisibility(View.GONE);
                companyNameTextView.setVisibility(View.VISIBLE);
                taxNumberTextView.setVisibility(View.VISIBLE);
                companyAddressTextView.setVisibility(View.VISIBLE);
                storeImageView.setVisibility(View.VISIBLE);
                storeImageTitle.setVisibility(View.VISIBLE);
                this.nameTextView.setHint("Tulajdonos teljes neve*");
            } else {
                selectedImageUrl = null;
                Glide.with(RegistrationActivity.this).load(R.drawable.grocery_store).into(storeImageView);
                isCompanyCheckbox.setVisibility(View.VISIBLE);
                shippingAddressTextView.setVisibility(View.VISIBLE);
                companyNameTextView.setVisibility(View.GONE);
                taxNumberTextView.setVisibility(View.GONE);
                companyAddressTextView.setVisibility(View.GONE);
                storeImageView.setVisibility(View.GONE);
                storeImageTitle.setVisibility(View.GONE);
                this.nameTextView.setHint("Teljes név*");
                if (isCompanyCheckbox.isChecked()) {
                    companyNameTextView.setVisibility(View.VISIBLE);
                    taxNumberTextView.setVisibility(View.VISIBLE);
                    companyAddressTextView.setVisibility(View.VISIBLE);
                    storeImageView.setVisibility(View.GONE);
                    storeImageTitle.setVisibility(View.GONE);
                    this.nameTextView.setHint("Tulajdonos teljes neve*");
                }
            }
        });
    }


    public void onRegister(View view) {
        // Retrieving input from the registration form fields
        String fullName = this.nameTextView.getText().toString();
        String email = this.emailTextView.getText().toString();
        String phoneNumber = this.phoneTextView.getText().toString();
        String address = this.shippingAddressTextView.getText().toString();
        String password = this.passwordTextView.getText().toString();
        String confirmPassword = this.confirmPasswordTextView.getText().toString();
        String companyName = this.companyNameTextView.getText().toString();
        String taxNumber = this.taxNumberTextView.getText().toString();
        String companyAddress = this.companyAddressTextView.getText().toString();


        // Handling validation for authentication and database checks
        // If registering as a seller or regular user, validate that all required fields are filled
        if ((!isSellerSwitch.isChecked() && !fullName.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty() && !address.isEmpty() && !password.isEmpty()) ||
                (isSellerSwitch.isChecked() && !fullName.isEmpty() && !email.isEmpty() && !phoneNumber.isEmpty() && !password.isEmpty())) {

            // Additional validation for company registration
            if (isCompanyCheckbox.isChecked() && !companyName.isEmpty() && !taxNumber.isEmpty() && !companyAddress.isEmpty() ||
                    !isCompanyCheckbox.isChecked() && !isSellerSwitch.isChecked() ||
                    isSellerSwitch.isChecked() && !companyName.isEmpty() && !taxNumber.isEmpty() && !companyAddress.isEmpty()) {

                // Email validation
                if (isEmailValid(email)) {

                    // Passwords must match
                    if (password.equals(confirmPassword)) {
                        hideElements();

                        // Firebase Authentication: Create user with email and password
                        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {

                            // Handle unsuccessful task
                            if (!task.isSuccessful()) {
                                // Handle specific password length error
                                if (Objects.equals(Objects.requireNonNull(task.getException()).getMessage(), "The given password is invalid. [ Password should be at least 6 characters ]")) {
                                    Toast.makeText(getApplicationContext(), "A jelszónak minimum 6 karakterből kell álnia!", Toast.LENGTH_LONG).show();
                                    showElements();
                                }

                                // Handle email already in use error
                                if (task.getException().getMessage().equals("The emailTextView address is already in use by another account.")) {
                                    Toast.makeText(getApplicationContext(), "A megadott emailTextView cím már használatban van!", Toast.LENGTH_LONG).show();
                                    showElements();
                                }
                            } else {
                                // Set the user type based on registration options
                                if (isCompanyCheckbox.isChecked()) {
                                    userType = "Cég/Vállalat";
                                }
                                if (isSellerSwitch.isChecked()) {
                                    userType = "Eladó cég/vállalat";
                                }
                                if (!isSellerSwitch.isChecked() && !isCompanyCheckbox.isChecked()) {
                                    userType = "magánszemély";
                                }

                                // Get the user ID from Firebase
                                userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

                                // If registering as a seller company, set up the store reference in Firestore
                                if (userType.equals("Eladó cég/vállalat")) {
                                    storeReference = db.collection("uzletek").document();
                                    storeId = storeReference.getId();

                                    // Create new user object with company details
                                    this.newUserObject = new User(fullName, email, phoneNumber, "", companyName, taxNumber, companyAddress, userType, storeId);
                                    if (selectedImageUrl != null) {
                                        auth.signInWithEmailAndPassword(this.emailTextView.getText().toString(), this.passwordTextView.getText().toString());
                                        uploadImage(selectedImageUrl);
                                    } else {
                                        // Save store details in Firestore without image
                                        Map<String, String> storeDetails = new HashMap<>();
                                        storeDetails.put("cegNev", companyName);
                                        storeDetails.put("szekhely", companyAddress);
                                        storeDetails.put("boltKepe", storeImageUrl);
                                        storeDetails.put("tulajId", userId);
                                        storeReference.set(storeDetails);
                                    }
                                }

                                // Create user object and store user details in Firestore
                                this.newUserObject = new User(fullName, email, phoneNumber, address, companyName, taxNumber, companyAddress, userType, storeId);
                                usersMap = newUserObject.addUser(newUserObject);

                                // Reference to Firestore users collection
                                DocumentReference reference = db.collection("felhasznalok").document(userId);

                                // Store user details in Firestore
                                reference.set(usersMap).addOnSuccessListener(databaseSaveSuccess -> {
                                    // If no image was uploaded, registration is complete
                                    if (selectedImageUrl == null) {
                                        super.onBackPressed();
                                        Toast.makeText(getApplicationContext(), "Sikeresen regisztráltál, " + newUserObject.getFullName() + "!", Toast.LENGTH_LONG).show();
                                        successfulRegistration();
                                    }
                                }).addOnFailureListener(e -> showElements());
                            }
                        });

                    } else {
                        // Error handling
                        Toast.makeText(getApplicationContext(), "Nem egyeznek a megadott jelszavak!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Érvénytelen emailTextView címet adtál meg!", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Ha szeretnél céget regisztrálni muszáj megadni az ahhoz tartozó adatokat is!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Nem maradhat egy csillaggal jelölt mező sem üresen!", Toast.LENGTH_LONG).show();
        }
    }

    // Hide loading progress and show product details when content is being loaded
    public void showElements() {
        this.registrationProgressBar.setVisibility(View.GONE);
        this.registrationLoadingText.setVisibility(View.GONE);
        this.nameTextView.setHint("Teljes név*");
        if (isCompanyCheckbox.isChecked() || isSellerSwitch.isChecked()) {
            this.companyAddressTextView.setVisibility(View.VISIBLE);
            this.companyNameTextView.setVisibility(View.VISIBLE);
            this.taxNumberTextView.setVisibility(View.VISIBLE);
            this.nameTextView.setVisibility(View.VISIBLE);
            if (isSellerSwitch.isChecked()) {
                this.storeImageView.setVisibility(View.VISIBLE);
                this.storeImageTitle.setVisibility(View.VISIBLE);
                this.nameTextView.setHint("Tulajdonos teljes neve*");
            }
        }
        this.nameTextView.setVisibility(View.VISIBLE);
        this.emailTextView.setVisibility(View.VISIBLE);
        this.phoneTextView.setVisibility(View.VISIBLE);
        this.shippingAddressTextView.setVisibility(View.VISIBLE);
        this.passwordTextView.setVisibility(View.VISIBLE);
        this.confirmPasswordTextView.setVisibility(View.VISIBLE);
        this.registrationButton.setVisibility(View.VISIBLE);
        this.loginButton.setVisibility(View.VISIBLE);
        this.companyFieldsLayout.setVisibility(View.VISIBLE);
    }

    // Show loading indicators and display product details once content is available
    public void hideElements() {
        this.registrationProgressBar.setVisibility(View.VISIBLE);
        this.registrationLoadingText.setVisibility(View.VISIBLE);
        this.companyAddressTextView.setVisibility(View.GONE);
        this.companyNameTextView.setVisibility(View.GONE);
        this.taxNumberTextView.setVisibility(View.GONE);
        this.nameTextView.setVisibility(View.GONE);
        this.storeImageView.setVisibility(View.GONE);
        this.storeImageTitle.setVisibility(View.GONE);
        this.emailTextView.setVisibility(View.GONE);
        this.phoneTextView.setVisibility(View.GONE);
        this.shippingAddressTextView.setVisibility(View.GONE);
        this.passwordTextView.setVisibility(View.GONE);
        this.confirmPasswordTextView.setVisibility(View.GONE);
        this.registrationButton.setVisibility(View.GONE);
        this.loginButton.setVisibility(View.GONE);
        this.companyFieldsLayout.setVisibility(View.INVISIBLE);
    }

    // Validate if the email is in a correct format
    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Handle actions after a successful registration
    public void successfulRegistration() {
        super.onBackPressed();
        super.onBackPressed();
        cartItemList.clear(); // Clear the shopping cart items and redirect to the account activity
        startActivity(new Intent(this, ProfileActivity.class));
        finish();
    }

    // Open the login activity
    public void onLoginOpen(View view) {
        super.onBackPressed();
        startActivity(new Intent(this, LoginActivity.class));
    }

    //-------------------------------------- The following section handles the image upload process---------------------------------------------------------------


    // Trigger the image selection from the gallery
    public void onStoreImageUpload(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT); // Open the image gallery
        gallery.setType("image/*"); // Restrict to image files only
        startActivityForResult(gallery, IMAGE_REQUEST_CODE);  // Start the activity to select an image
    }

    // Check if the image was successfully selected
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUrl = data.getData(); // Get the image URI
            storeImageView.setImageURI(selectedImageUrl); // Display the selected image in the ImageView
        }
    }

    // Upload the image to Firebase Storage
    public void uploadImage(Uri uri) {
        // Set the name for the image file in Firebase Storage
        StorageReference imageNameReference = storageReference.child("bolt_" + userId + "_" + uri.getLastPathSegment());
        imageNameReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Once the image is successfully uploaded, retrieve the download URL
                imageNameReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> uploadTask) {
                        if (uploadTask.isSuccessful()) {
                            storeImageUrl = String.valueOf(uploadTask.getResult());

                            // If the image is uploaded, update the store data in Firestore
                            Map<String, String> storeDetails = new HashMap<>();
                            storeDetails.put("cegNev", companyNameTextView.getText().toString());
                            storeDetails.put("szekhely", companyAddressTextView.getText().toString());
                            storeDetails.put("boltKepe", storeImageUrl);
                            storeDetails.put("tulajId", userId);

                            // Save the store data in Firestore under "uzletek" collection
                            storeReference = db.collection("uzletek").document(storeId);
                            storeReference.set(storeDetails).addOnSuccessListener(uzletbeTolt -> {
                                Toast.makeText(getApplicationContext(), "Sikeresen regisztráltál, " + newUserObject.getFullName() + "!", Toast.LENGTH_LONG).show();
                                RegistrationActivity.super.onBackPressed();
                                successfulRegistration();
                            });
                        }
                    }
                });
            }
        }).addOnProgressListener(snapshot -> hideElements()) // Hide UI elements during upload
                .addOnFailureListener(e -> showElements()); // Show UI elements again if upload fails
    }
}