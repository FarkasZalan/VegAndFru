package com.example.zoldseges.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.example.zoldseges.DAOS.ProductSelectorSellerView;
import com.example.zoldseges.Models.Product;
import com.example.zoldseges.R;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final Context context;
    private ArrayList<Product> productList;
    private ProductSelectorSellerView productSelectorSellerView;

    public ProductAdapter(Context context, ArrayList<Product> productList, ProductSelectorSellerView productSelectorSellerView) {
        this.context = context;
        this.productList = productList;
        this.productSelectorSellerView = productSelectorSellerView;
    }


    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for the product
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_display_for_seller, parent, false);
        return new ProductViewHolder(view, productSelectorSellerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // Set product name
        holder.productNameTextView.setText(productList.get(position).getProductName());

        // Load product image using Glide
        if (!productList.get(position).getProductImage().isEmpty()) {
            holder.progressImageLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(productList.get(position).getProductImage()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressImageLayout.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressImageLayout.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.productImageView);
        } else {
            holder.progressImageLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.standard_item_picture).into(holder.productImageView);
        }

        // Animate the product card view
        holder.productCard.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.two_column_animation));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImageView;
        TextView productNameTextView;

        Button modifyButton;
        Button deleteButton;
        CardView productCard;
        RelativeLayout progressImageLayout;

        public ProductViewHolder(@NonNull View itemView, ProductSelectorSellerView productSelectorSellerView) {
            super(itemView);

            // Initialize UI components
            productImageView = itemView.findViewById(R.id.termekKepeBoltKezeles);
            productNameTextView = itemView.findViewById(R.id.termekNeveBoltKezelese);

            modifyButton = itemView.findViewById(R.id.termekModosit);
            deleteButton = itemView.findViewById(R.id.termekTorles);
            productCard = itemView.findViewById(R.id.termekCard);
            progressImageLayout = itemView.findViewById(R.id.progressTermekKepEladoLayout);

            // Handle card click event
            productCard.setOnClickListener(v -> {
                if (productSelectorSellerView != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        productSelectorSellerView.onItemView(posi);
                    }
                }
            });

            // Handle modify button click event
            modifyButton.setOnClickListener(v -> {
                if (productSelectorSellerView != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        productSelectorSellerView.onItemModify(posi);
                    }
                }
            });

            // Handle delete button click event
            deleteButton.setOnClickListener(v -> {
                if (productSelectorSellerView != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        productSelectorSellerView.onItemDelete(posi);
                    }
                }
            });

        }
    }
}
