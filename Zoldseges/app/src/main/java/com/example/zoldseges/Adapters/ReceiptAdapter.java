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
import com.example.zoldseges.DAOS.ReceiptDAO;
import com.example.zoldseges.Models.Receipt;
import com.example.zoldseges.R;
import java.util.ArrayList;


public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptViewHolder> {

    private Context context;
    private ArrayList<Receipt> receipts;
    private ReceiptDAO receiptDAO;

    private boolean isSeller;

    public ReceiptAdapter(Context context, ArrayList<Receipt> receipts, ReceiptDAO receiptDAO, boolean isSeller) {
        this.context = context;
        this.receipts = receipts;
        this.receiptDAO = receiptDAO;
        this.isSeller = isSeller;
    }

    @NonNull
    @Override
    public ReceiptViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new ReceiptViewHolder(view, receiptDAO);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiptViewHolder holder, int position) {
        if (!isSeller) {
            displayForCustomer(holder, position);
        } else {
            displayForSeller(holder, position);
        }
        // Animate the receipt card
        holder.receiptCard.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.one_column_animation));
    }

    // Method to display receipt details for customers
    public void displayForCustomer(@NonNull ReceiptViewHolder holder, int position) {
        holder.storeNameTextView.setText(receipts.get(position).getStoreName());
        holder.orderDateTextView.setText(receipts.get(position).getOrderDate());
        String totalAmount = receipts.get(position).getTotalAmount() + " Ft";
        holder.totalAmountTextView.setText(totalAmount);

        if (!receipts.get(position).getStoreImage().isEmpty()) {
            holder.progressLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(receipts.get(position).getStoreImage()).addListener(new RequestListener<Drawable>() {
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
            }).into(holder.receiptImageView);
        } else {
            holder.progressLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.grocery_store).into(holder.receiptImageView);
        }
    }

    // Method to display receipt details for sellers
    public void displayForSeller(@NonNull ReceiptViewHolder holder, int position) {
        holder.storeNameTextView.setText(receipts.get(position).getCustomerName());
        holder.orderDateTextView.setText(receipts.get(position).getOrderDate());
        String totalAmount = receipts.get(position).getTotalAmount() + " Ft";
        holder.totalAmountTextView.setText(totalAmount);

        holder.progressLayout.setVisibility(View.VISIBLE);
        Glide.with(context).load(R.drawable.grocery_store).addListener(new RequestListener<Drawable>() {
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
        }).into(holder.receiptImageView);
    }

    @Override
    public int getItemCount() {
        return receipts.size();
    }

    public static class ReceiptViewHolder extends RecyclerView.ViewHolder {

        private CardView receiptCard;
        private RelativeLayout progressLayout;
        private ImageView receiptImageView;
        private TextView storeNameTextView;
        private TextView orderDateTextView;
        private TextView totalAmountTextView;

        public ReceiptViewHolder(@NonNull View itemView, ReceiptDAO receiptDAO) {
            super(itemView);

            // Initialize UI components
            receiptCard = itemView.findViewById(R.id.nyugtaCard);
            progressLayout = itemView.findViewById(R.id.progressNyugtaLayout);
            receiptImageView = itemView.findViewById(R.id.nyugtaOsszegzesKep);
            storeNameTextView = itemView.findViewById(R.id.nyugtaUzletNeve);
            orderDateTextView = itemView.findViewById(R.id.nyugtaDatum);
            totalAmountTextView = itemView.findViewById(R.id.nyugtaOsszeg);

            // Handle card click
            receiptCard.setOnClickListener(v -> {
                if (receiptDAO != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        receiptDAO.onReceiptSelect(position);
                    }
                }
            });
        }
    }
}
