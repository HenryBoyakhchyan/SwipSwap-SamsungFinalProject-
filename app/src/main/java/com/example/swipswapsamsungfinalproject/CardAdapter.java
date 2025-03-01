package com.example.swipswapsamsungfinalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// CardAdapter.java - Adapter for RecyclerView
public class CardAdapter { //extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
//    private List<ItemCard> itemList;
//    private Context context;
//
//    public CardAdapter(Context context, List<ItemCard> itemList) {
//        this.context = context;
//        this.itemList = itemList;
//    }
//
//    @NonNull
//    @Override
//    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.card_item, parent, false);
//        return new CardViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
//        ItemCard item = itemList.get(position);
//        holder.title.setText(item.getTitle());
//        holder.description.setText(item.getDescription());
//        Glide.with(context).load(item.getImageUrl()).into(holder.imageView);
//    }
//
//    @Override
//    public int getItemCount() {
//        return itemList.size();
//    }
//
//    public void updateList(List<ItemCard> filteredList) {
//
//    }
//
//    public static class CardViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        TextView title, description;
//
//        public CardViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.itemImage);
//            title = itemView.findViewById(R.id.itemID);
//            description = itemView.findViewById(R.id.description);
//        }
//    }
}