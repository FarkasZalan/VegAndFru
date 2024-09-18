package com.example.zoldseges.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.DAOS.CustomerViewProducts;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.R;

import java.util.ArrayList;

public class CustomerProductAdapter extends RecyclerView.Adapter<CustomerProductAdapter.CustomerProductViewHolder> {

    private Context context;
    private ArrayList<Product> productList;
    private CustomerViewProducts customerViewProducts;

    public CustomerProductAdapter(Context context, ArrayList<Product> productList, CustomerViewProducts customerViewProducts) {
        this.context = context;
        this.productList = productList;
        this.customerViewProducts = customerViewProducts;
    }

    @NonNull
    @Override
    public CustomerProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for customer product view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_display_for_costumers, parent, false);
        return new CustomerProductViewHolder(view, customerViewProducts);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerProductViewHolder holder, int position) {
        // Set product name
        holder.productNameTextView.setText(productList.get(position).getProductName());

        // Load product image using Glide
        if (productList.get(position).getProductImage() != null && !productList.get(position).getProductImage().isEmpty()) {
            holder.progressImageLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(productList.get(position).getProductImage()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    // Hide progress and set default image if loading fails
                    holder.progressImageLayout.setVisibility(View.GONE);
                    Glide.with(context).load(R.drawable.standard_item_picture).into(holder.productImageView);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    // Hide progress after image is successfully loaded
                    holder.progressImageLayout.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.productImageView);
        } else {
            // Use default image if product image is not available
            holder.progressImageLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.standard_item_picture).into(holder.productImageView);
        }

        // Format and set product price and stock information
        String priceText;
        String stockText;

        if (productList.get(position).getProductWeight() == -1) {
            // Pricing per unit
            priceText = ((int) productList.get(position).getPrice()) + " Ft/db";
            stockText = "Még további " + (int) productList.get(position).getAvailableStockQuantity() + " db";
        } else {
            // Pricing per kilogram
            priceText = (int) productList.get(position).getPrice() + " Ft/kg";
            stockText = "Még további " + (int) productList.get(position).getAvailableStockQuantity() + " kg";
        }
        holder.productPriceTextView.setText(priceText);
        holder.productStockTextView.setText(stockText);

        // Animate the product card view
        holder.productCardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.two_column_animation));

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class CustomerProductViewHolder extends RecyclerView.ViewHolder {

        private CardView productCardView;
        private ImageView productImageView;
        private TextView productNameTextView;

        private TextView productPriceTextView;
        private TextView productStockTextView;
        private RelativeLayout progressImageLayout;

        public CustomerProductViewHolder(@NonNull View itemView, CustomerViewProducts customerViewProducts) {
            super(itemView);

            // Initialize UI components
            productCardView = itemView.findViewById(R.id.termekCardVasarloknak);
            productImageView = itemView.findViewById(R.id.termekKepeVasarloknak);
            productNameTextView = itemView.findViewById(R.id.termekNeveVasarloknak);
            productPriceTextView = itemView.findViewById(R.id.termekAraVasarloknak);
            productStockTextView = itemView.findViewById(R.id.termekKeszletVasarloknak);

            progressImageLayout = itemView.findViewById(R.id.progressTermekKepLayout);

            // Handle card click event to view product details
            productCardView.setOnClickListener(v -> {
                if (customerViewProducts != null) {
                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        customerViewProducts.onProduct(position);
                    }
                }
            });

        }
    }
}
