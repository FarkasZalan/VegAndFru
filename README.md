# VegAndFru (2023)

## Specifications
The application includes authenticated login and registration features. After registering, users are directed immediately to their profile page, so they do not need to re-enter the information they just provided. However, they can log out and log in with a different account if needed.

For users who have already registered but cannot remember their password, there is an option to request a password reset email by providing the email associated with their account.

### User Types
There are three types of users that can be registered:

 1. Private Customer
 2. Corporate Customer
 3. Seller (which must always be a company)
    
During registration, users provide the required information. For sellers, there is an additional optional field for uploading a store image. Once the registration process is completed (with a short waiting period if an image is uploaded), successful registrants are directed to their profile page.

On the profile page, users can view their VAT ID, access customer support (with my contact information for issues), and manage their profile data. For customers, this includes viewing order history. For sellers, it includes managing received orders and their store.

### Data Modification
Users can modify any information in their profile. Once changes are successfully saved (with a short waiting period for image changes or uploads), they can review their updated information.

### Store Management
The seller can manage their store by viewing existing products (if any) and adding new ones. When adding a new product, the seller can specify whether the product should be priced by quantity or weight (kg), and optionally attach an image to the product. After a successful save, the seller is redirected to the product list, where they can review their current products, and perform CRUD operations—such as deleting or modifying—on them.

### Viewing Orders
Both buyers and sellers can view their previously placed orders in a list format, along with the associated receipts.

### Homepage
On the homepage, stores are displayed, and the page itself generates delivery times and shipping fees (a fixed 3-5 days nationwide, with a cost of 5000 HUF).
Users can search for stores using the search bar at the top. When they enter a search term, a list of stores is shown where there is a match (even partial) with the store name or the names of products available in the store.
Clicking on any store will display a list of products available in that store. Users can add products to their cart and, by opening the cart, review the list of items they wish to order. From the cart, they can proceed to the checkout.

### Store
Within each store, all users can view the products available and information about the seller. Unregistered users also have the option to log in or register from the product page.

Any user (excluding the seller and unregistered users) can add selected products to their cart, provided the quantity ordered does not exceed the available stock.

### Cart
When a user adds a product to the cart, they can delete items from the cart or modify the quantity. If the quantity is set to 0 or removed, it is interpreted as a request to delete the item.
Users can only order products from one store at a time. If they want to add products from a different store to the cart, they must first empty the cart before adding new items.
The total amount is displayed below the products in the cart, excluding the shipping cost.

### Placing an Order
Customers can proceed to the payment page directly from their cart. They do not need to enter any additional information, as their details are automatically displayed. However, they have the option to edit their information if needed (which redirects to the account settings).
After a successful payment, the customer receives a notification about the successful order (via NotificationManager). Clicking on this notification allows them to view their placed order. Customers can also access this order, along with all other previous orders, by navigating to the "My Orders" section within their profile.
Similarly, if the payment is successful, the seller can view the orders in their profile under the "Orders" section.

## Additional Information

Sample User Accounts for Demonstration:
 - Seller accounts: elado1@x.com, elado2@x.com, ..., elado9@x.com
 - Personal customer account: farkaszalan2001@gmail.com

Default Seller Account Credentials:
 - Email: elado1-9@x.com (e.g., tesco account: elado2@x.com)
 - Password: 123456
Any other email not in the database can be registered.
