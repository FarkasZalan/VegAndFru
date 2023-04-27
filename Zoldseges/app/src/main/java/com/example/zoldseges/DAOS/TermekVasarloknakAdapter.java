package com.example.zoldseges.DAOS;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.zoldseges.R;

import org.w3c.dom.Text;

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

        if (termekekLista.get(position).getTermekKepe() != null && !termekekLista.get(position).getTermekKepe().isEmpty()) {
            holder.progressTermekKepLayout.setVisibility(View.VISIBLE);

            Glide.with(context).load(termekekLista.get(position).getTermekKepe()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    holder.progressTermekKepLayout.setVisibility(View.GONE);
                    Glide.with(context).load(R.drawable.standard_item_picture).into(holder.termekKepeVasarloknak);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    holder.progressTermekKepLayout.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.termekKepeVasarloknak);
        } else {
            holder.progressTermekKepLayout.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.standard_item_picture).into(holder.termekKepeVasarloknak);
        }
        String ar;
        String keszlet;

        if (termekekLista.get(position).getTermekSulya() == -1) {
            ar = ((int) termekekLista.get(position).getAr()) + " Ft/db";
            keszlet = "Még további " + (int) termekekLista.get(position).getRaktaronLevoMennyiseg() + " db";
        } else {
            ar = (int) termekekLista.get(position).getAr() + " Ft/kg";
            keszlet = "Még további " + (int) termekekLista.get(position).getRaktaronLevoMennyiseg() + " kg";
        }
        holder.termekAraVasarloknak.setText(ar);
        holder.termekKeszletVasarloknak.setText(keszlet);

        holder.termekCardVasarloknak.startAnimation(AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.ket_oszlopos_animacio));

    }

    @Override
    public int getItemCount() {
        return termekekLista.size();
    }

    public static class VasarloknakViewHolder extends RecyclerView.ViewHolder {

        private CardView termekCardVasarloknak;
        private ImageView termekKepeVasarloknak;
        private TextView termekNeveVasarloknak;

        private TextView termekAraVasarloknak;
        private TextView termekKeszletVasarloknak;
        private RelativeLayout progressTermekKepLayout;

        public VasarloknakViewHolder(@NonNull View itemView, VasarloNezetTermekek vasarloNezetTermekek) {
            super(itemView);

            termekCardVasarloknak = itemView.findViewById(R.id.termekCardVasarloknak);
            termekKepeVasarloknak = itemView.findViewById(R.id.termekKepeVasarloknak);
            termekNeveVasarloknak = itemView.findViewById(R.id.termekNeveVasarloknak);
            termekAraVasarloknak = itemView.findViewById(R.id.termekAraVasarloknak);
            termekKeszletVasarloknak = itemView.findViewById(R.id.termekKeszletVasarloknak);

            progressTermekKepLayout = itemView.findViewById(R.id.progressTermekKepLayout);

            termekCardVasarloknak.setOnClickListener(v -> {
                if (vasarloNezetTermekek != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        vasarloNezetTermekek.onTermek(posi);
                    }
                }
            });

        }
    }
}
