package com.example.zoldseges.DAOS;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


public class KosarAdapter extends RecyclerView.Adapter<KosarAdapter.KosarViewHolder> {

    private final Context context;
    ArrayList<KosarElem> kosarElemLista;
    KosarIranyito kosarIranyito;

    private double fizetendoOsszeg = 0;

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

        if (kosarElemLista.get(position).getTermek().getTermekSulya() == -1.0) {
            holder.sulyVagyDb.setText("db");
        } else {
            holder.sulyVagyDb.setText("kg");
        }

        fizetendoOsszeg += kosarElemLista.get(position).getTermek().getAr() * kosarElemLista.get(position).getMennyiseg();

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

        if (position >= getItemCount() - 1) {
            holder.rendelesVeglegesitesehez.setVisibility(View.VISIBLE);
            holder.fizetendoOsszegKosar.setVisibility(View.VISIBLE);
            int osszeg;
            if ((int) Math.round(fizetendoOsszeg) <= 0) {
                osszeg = 1;
            } else {
                osszeg = (int) Math.round(fizetendoOsszeg);
            }
            holder.fizetendoOsszegKosar.append(" " + osszeg + " Ft");
        }

        holder.kosarCard.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.egy_oszlopos_animacio));
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
        TextView sulyVagyDb;
        RelativeLayout progressKosarLayout;
        CardView kosarCard;

        TextView fizetendoOsszegKosar;
        Button rendelesVeglegesitesehez;

        public KosarViewHolder(@NonNull View itemView, KosarIranyito kosarIranyito) {
            super(itemView);

            kosartermekKep = itemView.findViewById(R.id.kosartermekKep);
            kosarTermekNeve = itemView.findViewById(R.id.kosarTermekNeve);
            sulyVagyDb = itemView.findViewById(R.id.sulyVagyDb);
            mennyisegKosar = itemView.findViewById(R.id.mennyisegKosar);
            szerkesztesKosar = itemView.findViewById(R.id.szerkesztesKosar);
            torlesKosar = itemView.findViewById(R.id.torlesKosar);
            megseKosar = itemView.findViewById(R.id.megseKosar);
            progressKosarLayout = itemView.findViewById(R.id.progressKosarLayout);
            kosarCard = itemView.findViewById(R.id.kosarCard);
            fizetendoOsszegKosar = itemView.findViewById(R.id.fizetendoOsszegKosar);
            rendelesVeglegesitesehez = itemView.findViewById(R.id.rendelesVeglegesitesehez);

            kosarCard.setOnClickListener(v -> {
                if (kosarIranyito != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        kosarIranyito.onTermek(position);
                    }
                }
            });

            AtomicBoolean sikeresSzerkesztes = new AtomicBoolean(false);
            AtomicReference<Double> regiMennyiseg = new AtomicReference<>((double) 0);
            szerkesztesKosar.setOnClickListener(v -> {
                if (kosarIranyito != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (torlesKosar.getVisibility() == View.VISIBLE) {
                            torlesKosar.setVisibility(View.GONE);
                            megseKosar.setVisibility(View.VISIBLE);
                            mennyisegKosar.setEnabled(true);
                            mennyisegKosar.requestFocus();
                            mennyisegKosar.setSelection(mennyisegKosar.length());
                            szerkesztesKosar.setBackgroundResource(R.drawable.ment);
                            regiMennyiseg.set(Double.parseDouble(mennyisegKosar.getText().toString()));
                        } else {
                            torlesKosar.setVisibility(View.VISIBLE);
                            szerkesztesKosar.setBackgroundResource(R.drawable.szerkesztes);
                            mennyisegKosar.setEnabled(false);
                            megseKosar.setVisibility(View.GONE);
                            if (mennyisegKosar.getText().toString().isEmpty()) {
                                sikeresSzerkesztes.set(kosarIranyito.onSzerkesztes(position, 0));
                            } else {
                                sikeresSzerkesztes.set(kosarIranyito.onSzerkesztes(position, Double.parseDouble(mennyisegKosar.getText().toString())));
                            }
                            if (!sikeresSzerkesztes.get()) {
                                if (regiMennyiseg.get() % 1 == 0) {
                                    mennyisegKosar.setText(String.valueOf(regiMennyiseg.get().intValue()));
                                } else {
                                    mennyisegKosar.setText(String.valueOf(regiMennyiseg.get()));
                                }
                            }
                        }

                    }
                }
            });

            megseKosar.setOnClickListener(v -> {
                torlesKosar.setVisibility(View.VISIBLE);
                mennyisegKosar.setEnabled(false);
                megseKosar.setVisibility(View.GONE);
                szerkesztesKosar.setBackgroundResource(R.drawable.szerkesztes);
                if (regiMennyiseg.get() % 1 == 0) {
                    mennyisegKosar.setText(String.valueOf(regiMennyiseg.get().intValue()));
                } else {
                    mennyisegKosar.setText(String.valueOf(regiMennyiseg.get()));
                }
            });

            torlesKosar.setOnClickListener(v -> {
                if (kosarIranyito != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        kosarIranyito.onTorles(position);
                    }
                }
            });

            rendelesVeglegesitesehez.setOnClickListener(v -> {
                if (kosarIranyito != null) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION) {
                        kosarIranyito.onFizeteshez();
                    }
                }
            });
        }
    }
}
