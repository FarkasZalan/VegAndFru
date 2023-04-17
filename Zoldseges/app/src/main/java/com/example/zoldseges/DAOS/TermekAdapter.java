package com.example.zoldseges.DAOS;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class TermekAdapter extends RecyclerView.Adapter<TermekAdapter.TermekViewHolder> {

    private static final String tag = "RecyclerView";
    private Context context;
    private ArrayList<Termek> termekLista;
    private TermekValaszto termekValaszto;

    public TermekAdapter(Context context, ArrayList<Termek> termekLista, TermekValaszto termekValaszto) {
        this.context = context;
        this.termekLista = termekLista;
        this.termekValaszto = termekValaszto;
    }


    @NonNull
    @Override
    public TermekAdapter.TermekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.termek_megjelenites, parent, false);
        return new TermekViewHolder(view, termekValaszto);
    }

    @Override
    public void onBindViewHolder(@NonNull TermekAdapter.TermekViewHolder holder, int position) {
        holder.termekNeve.setText(termekLista.get(position).getNev());

        if (!termekLista.get(position).getTermekKepe().isEmpty()) {

            Glide.with(context).load(termekLista.get(position).getTermekKepe()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(holder.termekKepe);
        } else {
            Glide.with(context).load(R.drawable.standard_item_picture).into(holder.termekKepe);
        }
    }

    @Override
    public int getItemCount() {
        return termekLista.size();
    }


    public static class TermekViewHolder extends RecyclerView.ViewHolder {
        ImageView termekKepe;
        TextView termekNeve;

        Button modositButton;
        Button torlesButton;
        CardView termekCard;

        public TermekViewHolder(@NonNull View itemView, TermekValaszto termekValaszto) {
            super(itemView);

            termekKepe = itemView.findViewById(R.id.termekKepeBoltKezeles);
            termekNeve = itemView.findViewById(R.id.termekNeveBoltKezelese);

            modositButton = itemView.findViewById(R.id.termekModosit);
            torlesButton = itemView.findViewById(R.id.termekTorles);
            termekCard = itemView.findViewById(R.id.termekCard);

            termekCard.setOnClickListener(v -> {
                if (termekValaszto != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        termekValaszto.onItemMegtekint(posi);
                    }
                }
            });

            modositButton.setOnClickListener(v -> {
                if (termekValaszto != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        termekValaszto.onItemModosit(posi);
                    }
                }
            });

            torlesButton.setOnClickListener(v -> {
                if (termekValaszto != null) {
                    int posi = getAdapterPosition();

                    if (posi != RecyclerView.NO_POSITION) {
                        termekValaszto.onItemTorles(posi);
                    }
                }
            });

        }
    }
}
