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

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private Context context;
    private List<ItemCard> itemList;

    public ItemAdapter(Context context, List<ItemCard> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_layout, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemCard item = itemList.get(position);
        holder.description.setText(item.getDescription());
        holder.tag.setText( item.getTag());
        holder.address.setText(item.getAddress());
      //  holder.publishedDate.setText(item.getPublishedDate().toString());
        holder.status.setText(item.getStatus());


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

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView description, tag, address, status;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage2);
            description = itemView.findViewById(R.id.itemDescription);
            tag = itemView.findViewById(R.id.itemTag);
            address = itemView.findViewById(R.id.itemAddress);
            status = itemView.findViewById(R.id.itemStatus);
        }
    }
}
