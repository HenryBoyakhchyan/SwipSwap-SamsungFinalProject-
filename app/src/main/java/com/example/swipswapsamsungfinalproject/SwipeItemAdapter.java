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
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        List<String> categories = item.getCategories();
        String categoryList = "";
        for (String category : categories) {
            categoryList = categoryList.equals("") ? category : categoryList + ", " + category;
        }

        holder.categories.setText(categoryList);

        String formatedPublishedDate = convertFierbaseDate(item.getPublishedDate().toString());
        holder.publishedDate.setText(formatedPublishedDate);

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
        TextView description, categories, address, publishedDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.item_name);
            categories = itemView.findViewById(R.id.item_categories);
            address = itemView.findViewById(R.id.item_location);
            publishedDate = itemView.findViewById(R.id.item_published_date);
            itemImage = itemView.findViewById(R.id.item_image);
        }
    }

    public String convertFierbaseDate(String firestoreTimestamp){

        String secondsString = firestoreTimestamp.substring(firestoreTimestamp.indexOf("seconds=") + 8, firestoreTimestamp.indexOf(",")).trim();
        String nanosString = firestoreTimestamp.substring(firestoreTimestamp.indexOf("nanoseconds=") + 12, firestoreTimestamp.indexOf(")")).trim();

        long seconds = Long.parseLong(secondsString);
        int nanos = Integer.parseInt(nanosString);

        Timestamp timestamp = new Timestamp(seconds, nanos);
        Date date = timestamp.toDate();

        SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.US);
        String formattedDate = format.format(date);

        return formattedDate;
    }
}
