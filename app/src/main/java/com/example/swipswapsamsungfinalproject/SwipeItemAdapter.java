package com.example.swipswapsamsungfinalproject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SwipeItemAdapter extends RecyclerView.Adapter<SwipeItemAdapter.ViewHolder> {
    private Context context;
    private List<ItemCard> itemList;

    public SwipeItemAdapter(Context context, List<ItemCard> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCard item = itemList.get(position);
        holder.description.setText(item.getDescription());
        holder.address.setText(item.getAddress());
        holder.tag.setText(item.getTag());

       // Glide.with(context).load(item.getImageUrl()).into(holder.itemImage);
        if (item.getImageBlob() != null && !item.getImageBlob().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(item.getImageBlob(), Base64.DEFAULT);
                holder.itemImage.setImageBitmap(BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.itemImage.setImageResource(R.drawable.profile_placeholder); // Placeholder image
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView description, tag, address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.item_name);
            tag = itemView.findViewById(R.id.item_tag);
            address = itemView.findViewById(R.id.item_location);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }
}
