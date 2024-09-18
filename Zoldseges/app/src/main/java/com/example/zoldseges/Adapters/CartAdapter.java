package com.example.zoldseges.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.zoldseges.DAOS.CartDAO;
import com.example.zoldseges.Models.CartItem;
import com.example.zoldseges.R;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final Context context;
    ArrayList<CartItem> cartItems;
    CartDAO cartDAO;

    // Total amount to be paid
    private double totalAmount = 0;

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, CartDAO cartDAO) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartDAO = cartDAO;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_items, parent, false);
        return new CartViewHolder(view, cartDAO);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        // Set product name
        holder.productNameTextView.setText(cartItems.get(position).getProduct().getProductName());

        // Display quantity (as integer if whole number, otherwise as decimal)
        if (cartItems.get(position).getQuantity() % 1 == 0) {
            holder.quantityEditText.setText(String.valueOf((int) cartItems.get(position).getQuantity()));
        } else {
            holder.quantityEditText.setText(String.valueOf(cartItems.get(position).getQuantity()));
        }

        // Display weight or unit
        if (cartItems.get(position).getProduct().getProductWeight() == -1.0) {
            holder.weightOrUnitTextView.setText("db");
        } else {
            holder.weightOrUnitTextView.setText("kg");
        }

        // Calculate total amount
        totalAmount += cartItems.get(position).getProduct().getPrice() * cartItems.get(position).getQuantity();

        // Load product image
        if (!cartItems.get(position).getProduct().getProductImage().isEmpty()) {
            holder.progressLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(cartItems.get(position).getProduct().getProductImage()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressLayout.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressLayout.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.productImageView);
        } else {
            holder.progressLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.standard_item_picture).into(holder.productImageView);
        }

        // Show total amount and finalize order button on the last item
        if (position >= getItemCount() - 1) {
            holder.finalizeOrderButton.setVisibility(View.VISIBLE);
            holder.totalAmountTextView.setVisibility(View.VISIBLE);

            int roundedAmount = (int) Math.round(totalAmount);
            holder.totalAmountTextView.append(" " + Math.max(roundedAmount, 1) + " Ft");
        }

        // Animate the cart item
        holder.cartCard.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.one_column_animation));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView productImageView;
        TextView productNameTextView;
        EditText quantityEditText;
        TextView editCartTextView;
        TextView deleteCartTextView;
        TextView cancelEditTextView;
        TextView weightOrUnitTextView;
        RelativeLayout progressLayout;
        CardView cartCard;

        TextView totalAmountTextView;
        Button finalizeOrderButton;

        public CartViewHolder(@NonNull View itemView, CartDAO cartDAO) {
            super(itemView);

            // Initialize UI components
            productImageView = itemView.findViewById(R.id.kosartermekKep);
            productNameTextView = itemView.findViewById(R.id.kosarTermekNeve);
            weightOrUnitTextView = itemView.findViewById(R.id.sulyVagyDb);
            quantityEditText = itemView.findViewById(R.id.mennyisegKosar);
            editCartTextView = itemView.findViewById(R.id.szerkesztesKosar);
            deleteCartTextView = itemView.findViewById(R.id.torlesKosar);
            cancelEditTextView = itemView.findViewById(R.id.megseKosar);
            progressLayout = itemView.findViewById(R.id.progressKosarLayout);
            cartCard = itemView.findViewById(R.id.kosarCard);
            totalAmountTextView = itemView.findViewById(R.id.fizetendoOsszegKosar);
            finalizeOrderButton = itemView.findViewById(R.id.rendelesVeglegesitesehez);

            // Handle cart item click
            cartCard.setOnClickListener(v -> {
                if (cartDAO != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        cartDAO.onProductSelect(position);
                    }
                }
            });

            // Handle edit quantity
            AtomicBoolean successfulEdit = new AtomicBoolean(false);
            AtomicReference<Double> previousQuantity = new AtomicReference<>((double) 0);

            editCartTextView.setOnClickListener(v -> {
                if (cartDAO != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (deleteCartTextView.getVisibility() == View.VISIBLE) {
                            // Switch to edit mode
                            deleteCartTextView.setVisibility(View.GONE);
                            cancelEditTextView.setVisibility(View.VISIBLE);
                            quantityEditText.setEnabled(true);
                            quantityEditText.requestFocus();
                            quantityEditText.setSelection(quantityEditText.length());
                            editCartTextView.setBackgroundResource(R.drawable.save_icon);
                            previousQuantity.set(Double.parseDouble(quantityEditText.getText().toString()));
                        } else {
                            // Switch to view mode
                            deleteCartTextView.setVisibility(View.VISIBLE);
                            editCartTextView.setBackgroundResource(R.drawable.modification_icon);
                            quantityEditText.setEnabled(false);
                            cancelEditTextView.setVisibility(View.GONE);

                            // Update cart with new quantity
                            if (quantityEditText.getText().toString().isEmpty()) {
                                successfulEdit.set(cartDAO.onEdit(position, 0));
                            } else {
                                successfulEdit.set(cartDAO.onEdit(position, Double.parseDouble(quantityEditText.getText().toString())));
                            }

                            // Revert to previous quantity if edit failed
                            if (!successfulEdit.get()) {
                                if (previousQuantity.get() % 1 == 0) {
                                    quantityEditText.setText(String.valueOf(previousQuantity.get().intValue()));
                                } else {
                                    quantityEditText.setText(String.valueOf(previousQuantity.get()));
                                }
                            }
                        }

                    }
                }
            });

            // Handle cancel edit
            cancelEditTextView.setOnClickListener(v -> {
                deleteCartTextView.setVisibility(View.VISIBLE);
                quantityEditText.setEnabled(false);
                cancelEditTextView.setVisibility(View.GONE);
                editCartTextView.setBackgroundResource(R.drawable.modification_icon);
                if (previousQuantity.get() % 1 == 0) {
                    quantityEditText.setText(String.valueOf(previousQuantity.get().intValue()));
                } else {
                    quantityEditText.setText(String.valueOf(previousQuantity.get()));
                }
            });

            // Handle delete item
            deleteCartTextView.setOnClickListener(v -> {
                if (cartDAO != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        cartDAO.onDelete(position);
                    }
                }
            });

            // Handle finalize order
            finalizeOrderButton.setOnClickListener(v -> {
                if (cartDAO != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        cartDAO.onProceedToPayment();
                    }
                }
            });
        }
    }
}
