package com.example.zoldseges.DAOS;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class TermekVasarloknakAdapter extends RecyclerView.Adapter<TermekVasarloknakAdapter.VasarloknakViewHolder> {

    private Context context;
    private ArrayList<Termek> termekekLista;
    private VasarloNezetTermekek vasarloNezetTermekek;

    public TermekVasarloknakAdapter(Context context, ArrayList<Termek> termekekLista, VasarloNezetTermekek vasarloNezetTermekek) {
        this.context = context;
        this.termekekLista = termekekLista;
        this.vasarloNezetTermekek = vasarloNezetTermekek;
    }

    @NonNull
    @Override
    public VasarloknakViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.termek_megjelenites_vasarloknak, parent, false);
        return new TermekVasarloknakAdapter.VasarloknakViewHolder(view, vasarloNezetTermekek);
    }

    @Override
    public void onBindViewHolder(@NonNull VasarloknakViewHolder holder, int position) {
        holder.termekNeveVasarloknak.setText(termekekLista.get(position).getNev());

        if (!termekekLista.get(position).getTermekKepe().isEmpty()) {

            Glide.with(context).load(termekekLista.get(position).getTermekKepe()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.termekKepeVasarloknak);
        } else {
            Glide.with(context).load(R.drawable.standard_item_picture).into(holder.termekKepeVasarloknak);
        }
    }

    @Override
    public int getItemCount() {
        return termekekLista.size();
    }

    public static class VasarloknakViewHolder extends RecyclerView.ViewHolder {

        private CardView termekCardVasarloknak;
        private ImageView termekKepeVasarloknak;
        private TextView termekNeveVasarloknak;

        private Button termekKosarba;

        public VasarloknakViewHolder(@NonNull View itemView, VasarloNezetTermekek vasarloNezetTermekek) {
            super(itemView);

            termekCardVasarloknak = itemView.findViewById(R.id.termekCardVasarloknak);
            termekKepeVasarloknak = itemView.findViewById(R.id.termekKepeVasarloknak);
            termekNeveVasarloknak = itemView.findViewById(R.id.termekNeveVasarloknak);
            termekKosarba = itemView.findViewById(R.id.termekKosarba);

            termekCardVasarloknak.setOnClickListener(v -> {
                if (vasarloNezetTermekek != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        vasarloNezetTermekek.onTermek(posi);
                    }
                }
            });

            termekKosarba.setOnClickListener(v -> {
                if (vasarloNezetTermekek != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        vasarloNezetTermekek.onKosarba(posi);
                    }
                }
            });
        }
    }
}
