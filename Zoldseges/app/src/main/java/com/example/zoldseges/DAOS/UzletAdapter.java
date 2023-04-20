package com.example.zoldseges.DAOS;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.util.ArrayList;

public class UzletAdapter extends RecyclerView.Adapter<UzletAdapter.UzletViewHolder> {

    private Context context;
    private ArrayList<Uzlet> uzletekListaja;
    private UzletValaszto uzletValaszto;

    public UzletAdapter(Context context, ArrayList<Uzlet> uzletekListaja, UzletValaszto uzletValaszto) {
        this.context = context;
        this.uzletekListaja = uzletekListaja;
        this.uzletValaszto = uzletValaszto;
    }


    @NonNull
    @Override
    public UzletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.uzlet_megjelenitese, parent, false);
        return new UzletAdapter.UzletViewHolder(view, uzletValaszto);
    }

    @Override
    public void onBindViewHolder(@NonNull UzletViewHolder holder, int position) {
        holder.uzletNeve.setText(uzletekListaja.get(position).getUzletNeve());
        holder.uzletVarhatoSzallitasiIdeje.setText(uzletekListaja.get(position).getSzallitasIdotartama());
        holder.uzletSzallitasiKoltsege.setText(uzletekListaja.get(position).getSzallitasiDij());

        if (uzletekListaja.get(position).getBoltKepe() != null && !uzletekListaja.get(position).getBoltKepe().isEmpty()) {

            Glide.with(context).load(uzletekListaja.get(position).getBoltKepe()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.uzletKepe);
        } else {
            Glide.with(context).load(R.drawable.grocery_store).into(holder.uzletKepe);
        }
    }

    @Override
    public int getItemCount() {
        return uzletekListaja.size();
    }

    public static class UzletViewHolder extends RecyclerView.ViewHolder {

        private ImageView uzletKepe;
        private CardView uzletCard;
        private TextView uzletNeve;
        private TextView uzletVarhatoSzallitasiIdeje;
        private TextView uzletSzallitasiKoltsege;

        public UzletViewHolder(@NonNull View itemView, UzletValaszto uzletValaszto) {
            super(itemView);
            uzletKepe = itemView.findViewById(R.id.uzletKepe);
            uzletKepe.setClipToOutline(true); //üzlet kép radius aktiválás
            uzletCard = itemView.findViewById(R.id.uzletCard);
            uzletNeve = itemView.findViewById(R.id.uzletNeve);
            uzletVarhatoSzallitasiIdeje = itemView.findViewById(R.id.uzletVarhatoSzallitasiIdeje);
            uzletSzallitasiKoltsege = itemView.findViewById(R.id.uzletSzallitasiKoltsege);

            uzletCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (uzletValaszto != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            uzletValaszto.onUzletValaszt(position);
                        }
                    }
                }
            });
        }
    }
}
