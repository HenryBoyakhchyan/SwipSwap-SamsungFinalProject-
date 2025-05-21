package com.example.swipswapsamsungfinalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        holder.address.setText(item.getAddress());
      //  holder.publishedDate.setText(item.getPublishedDate().toString());
        holder.status.setText(item.getStatus());

        List<String> categories = item.getCategories();
        String categoryList = "";
        for (String category : categories) {
            categoryList = categoryList.equals("") ? category : categoryList + ", " + category;
            }

       holder.categories.setText(categoryList);

        String formatedPublishedDate = convertFierbaseDate(item.getPublishedDate().toString());
        holder.publishedDate.setText(formatedPublishedDate);


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

        // Set status icon
        switch (item.getStatus()) {
            case "chosen":
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.statusYellow));
                break;
            case "accepted":
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.statusRed));
                break;
            case "published":
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.statusGrey));
                break;
            case "declined":
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.statusBlack));
                break;
            default:
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.statusGreen));
        }

        holder.editItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateActivity.class);
            intent.putExtra("editMode", true);
            intent.putExtra("swapId", item.getSwapId()); // or pass the whole item if needed
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
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
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView description, categories, address, status, publishedDate;
        ImageView editItemButton;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage2);
            description = itemView.findViewById(R.id.itemDescription);
            categories = itemView.findViewById(R.id.itemCategories);
            address = itemView.findViewById(R.id.itemAddress);
            status = itemView.findViewById(R.id.itemStatus);
            publishedDate = itemView.findViewById(R.id.publishedDate);
            editItemButton = itemView.findViewById(R.id.editItemButton);
        }
    }
}
