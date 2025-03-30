package com.example.luyentapgk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LandScapeAdapter extends RecyclerView.Adapter<LandScapeAdapter.ItemLandHolder> {
    Context context;
    ArrayList<LandScape> lsData;

    public LandScapeAdapter(Context context, ArrayList<LandScape> lsData) {
        this.context = context;
        this.lsData = lsData;
    }

    @NonNull
    @Override
    public ItemLandHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater cai_bom = LayoutInflater.from(context);
        View vItem = cai_bom.inflate(R.layout.item_land,parent,false);
        ItemLandHolder viewholderCreated = new ItemLandHolder(vItem);
        return viewholderCreated;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemLandHolder holder, int position) {
        // lay doi tuong hien thi
        LandScape landScapeHienThi = lsData.get(position);
        // trich thong tin
        String capTion = landScapeHienThi.getLandCation();
        String tenFileAnh = landScapeHienThi.getLandImageFileName();
        // dat vao cac truong thong tin holder
        holder.tvCapTion.setText(capTion);
        // dat anh
            String packageName = holder.itemView.getContext().getPackageName();
            int imagesID = holder.itemView.getResources().getIdentifier(tenFileAnh,"mipmap",packageName);
        holder.imageViewLandScape.setImageResource(imagesID);
    }

    @Override
    public int getItemCount() {
        return lsData.size();
    }

    class ItemLandHolder extends RecyclerView.ViewHolder{
        TextView tvCapTion;
        ImageView imageViewLandScape;

        public ItemLandHolder(@NonNull View itemView) {
            super(itemView);
            tvCapTion = itemView.findViewById(R.id.textViewCation);
            imageViewLandScape = itemView.findViewById(R.id.imageViewLand);
        }
    }

}
