package com.example.zoldseges.DAOS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.zoldseges.R;

import java.util.ArrayList;

public class TermekAdapter extends RecyclerView.Adapter<TermekAdapter.TermekViewHolder> {

    private static final String tag = "RecyclerView";
    private Context context;
    private ArrayList<Termek> termekLista;

    public TermekAdapter(Context context, ArrayList<Termek> termekLista) {
        this.context = context;
        this.termekLista = termekLista;
    }


    @NonNull
    @Override
    public TermekAdapter.TermekViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.termek_megjelenites, parent, false);

        return new TermekViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TermekAdapter.TermekViewHolder holder, int position) {
        holder.termekNeve.setText(termekLista.get(position).getNev());


        Glide.with(context).load(termekLista.get(position).getTermekKepe()).into(holder.termekKepe);
    }

    @Override
    public int getItemCount() {
        return termekLista.size();
    }


    public class TermekViewHolder extends RecyclerView.ViewHolder {
        ImageView termekKepe;
        TextView termekNeve;

        public TermekViewHolder(@NonNull View itemView) {
            super(itemView);

            termekKepe = itemView.findViewById(R.id.termekKepeBoltKezeles);
            termekNeve = itemView.findViewById(R.id.termekNeveBoltKezelese);
        }
    }
}
