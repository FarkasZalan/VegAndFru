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
import com.example.zoldseges.DAOS.StoreSelectorDAO;
import com.example.zoldseges.Models.Store;
import com.example.zoldseges.R;
import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.StoreViewHolder> {

    private Context context;
    private ArrayList<Store> storeList;
    private StoreSelectorDAO storeSelectorDAO;

    public StoreAdapter(Context context, ArrayList<Store> storeList, StoreSelectorDAO storeSelectorDAO) {
        this.context = context;
        this.storeList = storeList;
        this.storeSelectorDAO = storeSelectorDAO;
    }

    // Method to update the store list and notify the adapter of changes
    public void filterStores(ArrayList<Store> szurtLista) {
        this.storeList = szurtLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for displaying a store item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_store, parent, false);
        return new StoreViewHolder(view, storeSelectorDAO);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {
        // Set store name
        holder.storeNameTextView.setText(storeList.get(position).getStoreName());

        // Set expected delivery time
        holder.expectedDeliveryTimeTextView.setText(storeList.get(position).getDeliveryDuration());

        // Set shipping cost
        holder.shippingCostTextView.setText(storeList.get(position).getShippingCost());

        // Load store image using Glide
        if (storeList.get(position).getStoreImage() != null && !storeList.get(position).getStoreImage().isEmpty() && !storeList.get(position).getStoreImage().equals("null")) {
            holder.imageProgressLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(storeList.get(position).getStoreImage()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    // Hide progress layout and set default image if loading fails
                    holder.imageProgressLayout.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    // Hide progress layout when image is successfully loaded
                    holder.imageProgressLayout.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.storeImageView);
        } else {
            // Use default image if store image is not available
            holder.imageProgressLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.grocery_store).into(holder.storeImageView);
        }

        // Animate the store card view
        holder.storeCardView.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.one_column_animation));
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public static class StoreViewHolder extends RecyclerView.ViewHolder {

        private ImageView storeImageView;
        private CardView storeCardView;
        private TextView storeNameTextView;
        private TextView expectedDeliveryTimeTextView;
        private TextView shippingCostTextView;
        private RelativeLayout imageProgressLayout;

        public StoreViewHolder(@NonNull View itemView, StoreSelectorDAO storeSelectorDAO) {
            super(itemView);

            // Initialize UI components
            storeImageView = itemView.findViewById(R.id.storeImage);
            storeImageView.setClipToOutline(true); // Enable rounded corners for store image
            storeCardView = itemView.findViewById(R.id.uzletCard);
            storeNameTextView = itemView.findViewById(R.id.storeName);
            expectedDeliveryTimeTextView = itemView.findViewById(R.id.uzletVarhatoSzallitasiIdeje);
            shippingCostTextView = itemView.findViewById(R.id.uzletSzallitasiKoltsege);
            imageProgressLayout = itemView.findViewById(R.id.progressBoltKepLayout);

            // Handle card click event to select a store
            storeCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (storeSelectorDAO != null) {
                        int position = getBindingAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            storeSelectorDAO.onStoreSelected(position);
                        }
                    }
                }
            });
        }
    }
}
