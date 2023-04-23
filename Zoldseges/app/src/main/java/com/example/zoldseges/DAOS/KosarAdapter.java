package com.example.zoldseges.DAOS;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
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
import com.example.zoldseges.R;

import java.util.ArrayList;


public class KosarAdapter extends RecyclerView.Adapter<KosarAdapter.KosarViewHolder> {

    private final Context context;
    ArrayList<KosarElem> kosarElemLista;
    KosarIranyito kosarIranyito;

    public KosarAdapter(Context context, ArrayList<KosarElem> kosarElemLista, KosarIranyito kosarIranyito) {
        this.context = context;
        this.kosarElemLista = kosarElemLista;
        this.kosarIranyito = kosarIranyito;
    }


    @NonNull
    @Override
    public KosarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.kosar_elemek, parent, false);
        return new KosarAdapter.KosarViewHolder(view, kosarIranyito);
    }

    @Override
    public void onBindViewHolder(@NonNull KosarViewHolder holder, int position) {
        holder.kosarTermekNeve.setText(kosarElemLista.get(position).getTermek().getNev());

        if (kosarElemLista.get(position).getMennyiseg() % 1 == 0) {
            holder.mennyisegKosar.setText(String.valueOf((int) kosarElemLista.get(position).getMennyiseg()));
        } else {
            holder.mennyisegKosar.setText(String.valueOf(kosarElemLista.get(position).getMennyiseg()));
        }

        if (!kosarElemLista.get(position).getTermek().getTermekKepe().isEmpty()) {
            holder.progressKosarLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(kosarElemLista.get(position).getTermek().getTermekKepe()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressKosarLayout.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressKosarLayout.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.kosartermekKep);
        } else {
            holder.progressKosarLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.standard_item_picture).into(holder.kosartermekKep);
        }
    }

    @Override
    public int getItemCount() {
        return kosarElemLista.size();
    }

    public static class KosarViewHolder extends RecyclerView.ViewHolder {

        ImageView kosartermekKep;
        TextView kosarTermekNeve;
        EditText mennyisegKosar;
        TextView szerkesztesKosar;
        TextView torlesKosar;
        TextView megseKosar;
        RelativeLayout progressKosarLayout;
        CardView kosarCard;

        public KosarViewHolder(@NonNull View itemView, KosarIranyito kosarIranyito) {
            super(itemView);

            kosartermekKep = itemView.findViewById(R.id.kosartermekKep);
            kosarTermekNeve = itemView.findViewById(R.id.kosarTermekNeve);
            mennyisegKosar = itemView.findViewById(R.id.mennyisegKosar);
            szerkesztesKosar = itemView.findViewById(R.id.szerkesztesKosar);
            torlesKosar = itemView.findViewById(R.id.torlesKosar);
            megseKosar = itemView.findViewById(R.id.megseKosar);
            progressKosarLayout = itemView.findViewById(R.id.progressKosarLayout);
            kosarCard = itemView.findViewById(R.id.kosarCard);

            kosarCard.setOnClickListener(v -> {
                if (kosarIranyito != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        kosarIranyito.onTermek(position);
                    }
                }
            });

            szerkesztesKosar.setOnClickListener(v -> {
                if (kosarIranyito != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (torlesKosar.getVisibility() == View.VISIBLE) {
                            torlesKosar.setVisibility(View.GONE);
                            megseKosar.setVisibility(View.VISIBLE);
                            mennyisegKosar.setEnabled(true);
                            mennyisegKosar.requestFocus();
                            szerkesztesKosar.setBackgroundResource(R.drawable.ment);
                        } else {
                            torlesKosar.setVisibility(View.VISIBLE);
                            szerkesztesKosar.setBackgroundResource(R.drawable.szerkesztes);
                            mennyisegKosar.setEnabled(false);
                            megseKosar.setVisibility(View.GONE);
                            kosarIranyito.onSzerkesztes(position);
                        }

                    }
                }
            });

            megseKosar.setOnClickListener(v -> {
                torlesKosar.setVisibility(View.VISIBLE);
                mennyisegKosar.setEnabled(false);
                megseKosar.setVisibility(View.GONE);
                szerkesztesKosar.setBackgroundResource(R.drawable.szerkesztes);
            });

            torlesKosar.setOnClickListener(v -> {
                if (kosarIranyito != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        kosarIranyito.onTorles(position);
                    }
                }
            });
        }
    }
}
