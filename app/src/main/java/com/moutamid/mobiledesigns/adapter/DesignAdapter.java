package com.moutamid.mobiledesigns.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moutamid.mobiledesigns.R;
import com.moutamid.mobiledesigns.model.DesignModel;

import java.util.ArrayList;

public class DesignAdapter extends RecyclerView.Adapter<DesignAdapter.DesignVH> {
    Context context;
    ArrayList<DesignModel> list;

    public DesignAdapter(Context context, ArrayList<DesignModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public DesignVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DesignVH(LayoutInflater.from(context).inflate(R.layout.design_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DesignVH holder, int position) {
        DesignModel model = list.get(holder.getAdapterPosition());
        Glide.with(context).load(model.image).into(holder.image);
        holder.name.setText(model.name);
        holder.desc.setText(model.description);

        holder.itemView.setOnClickListener(v -> {

        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DesignVH extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, desc;
        Button edit, delete;

        public DesignVH(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            desc = itemView.findViewById(R.id.desc);
        }
    }

}
