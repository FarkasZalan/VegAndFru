package com.example.zoldseges.DAOS;

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
import com.example.zoldseges.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


public class NyugtaAdapter extends RecyclerView.Adapter<NyugtaAdapter.NyugtaOsszegzesViewHolder> {

    private Context context;
    private ArrayList<Nyugta> nyugtakListaja;
    private NyugtaDAO nyugtaDAO;

    private boolean eladoE;

    public NyugtaAdapter(Context context, ArrayList<Nyugta> nyugtakListaja, NyugtaDAO nyugtaDAO, boolean eladoE) {
        this.context = context;
        this.nyugtakListaja = nyugtakListaja;
        this.nyugtaDAO = nyugtaDAO;
        this.eladoE = eladoE;
    }

    @NonNull
    @Override
    public NyugtaAdapter.NyugtaOsszegzesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rendeles_elem, parent, false);
        return new NyugtaAdapter.NyugtaOsszegzesViewHolder(view, nyugtaDAO);
    }

    @Override
    public void onBindViewHolder(@NonNull NyugtaAdapter.NyugtaOsszegzesViewHolder holder, int position) {
        if (!eladoE) {
            vasarlo(holder, position);
        } else {
            elado(holder, position);
        }
        holder.nyugtaCard.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.egy_oszlopos_animacio));
    }

    public void vasarlo(@NonNull NyugtaAdapter.NyugtaOsszegzesViewHolder holder, int position) {
        holder.nyugtaUzletNeve.setText(nyugtakListaja.get(position).getUzletNeve());
        holder.nyugtaDatum.setText(nyugtakListaja.get(position).getDatum());
        String osszeg = nyugtakListaja.get(position).getVegosszeg() + " Ft";
        holder.nyugtaOsszeg.setText(osszeg);
        if (!nyugtakListaja.get(position).getUzletKepe().isEmpty()) {
            holder.progressNyugtaLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(nyugtakListaja.get(position).getUzletKepe()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressNyugtaLayout.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressNyugtaLayout.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.nyugtaOsszegzesKep);
        } else {
            holder.progressNyugtaLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.grocery_store).into(holder.nyugtaOsszegzesKep);
        }
    }

    public void elado(@NonNull NyugtaAdapter.NyugtaOsszegzesViewHolder holder, int position) {
        holder.nyugtaUzletNeve.setText(nyugtakListaja.get(position).getNev());
        holder.nyugtaDatum.setText(nyugtakListaja.get(position).getDatum());
        String osszeg = nyugtakListaja.get(position).getVegosszeg() + " Ft";
        holder.nyugtaOsszeg.setText(osszeg);

        holder.progressNyugtaLayout.setVisibility(View.VISIBLE);
        Glide.with(context).load(R.drawable.grocery_store).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                holder.progressNyugtaLayout.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                holder.progressNyugtaLayout.setVisibility(View.GONE);
                return false;
            }
        }).into(holder.nyugtaOsszegzesKep);
    }

    @Override
    public int getItemCount() {
        return nyugtakListaja.size();
    }

    public static class NyugtaOsszegzesViewHolder extends RecyclerView.ViewHolder {

        private CardView nyugtaCard;
        private RelativeLayout progressNyugtaLayout;
        private ImageView nyugtaOsszegzesKep;
        private TextView nyugtaUzletNeve;
        private TextView nyugtaDatum;
        private TextView nyugtaOsszeg;

        public NyugtaOsszegzesViewHolder(@NonNull View itemView, NyugtaDAO nyugtaDAO) {
            super(itemView);

            nyugtaCard = itemView.findViewById(R.id.nyugtaCard);
            progressNyugtaLayout = itemView.findViewById(R.id.progressNyugtaLayout);
            nyugtaOsszegzesKep = itemView.findViewById(R.id.nyugtaOsszegzesKep);
            nyugtaUzletNeve = itemView.findViewById(R.id.nyugtaUzletNeve);
            nyugtaDatum = itemView.findViewById(R.id.nyugtaDatum);
            nyugtaOsszeg = itemView.findViewById(R.id.nyugtaOsszeg);

            nyugtaCard.setOnClickListener(v -> {
                if (nyugtaDAO != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        nyugtaDAO.onNyugta(position);
                    }
                }
            });
        }
    }
}
