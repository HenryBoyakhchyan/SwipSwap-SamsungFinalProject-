package com.example.swipswapsamsungfinalproject;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private Context context;
    private FirebaseFirestore firestoreDb;

    private List<ChatItem> chatList;
    private String currentUserEmail;
    public boolean youOwner;


    public ChatListAdapter(Context context, List<ChatItem> chatList, String authEmail) {
        this.context = context;
        this.chatList = chatList;
        this.currentUserEmail = authEmail;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position ) {

        ChatItem chat = chatList.get(position);
        youOwner = (chat.getSwapOwnerEmail() != null
                && chat.getSwapOwnerEmail().equals(currentUserEmail));

        if (youOwner) {
            holder.ownerLabel.setVisibility(View.VISIBLE);
        } else {
            holder.ownerLabel.setVisibility(View.GONE);
        }

        holder.itemDescription.setText(chat.getSwapItemDescription() != null ? chat.getSwapItemDescription() : "");
        holder.itemAddress.setText(chat.getSwapItemAddress() != null ? chat.getSwapItemAddress() : "");
       if( youOwner ) {
           holder.itemContact.setText(chat.getClientUserEmail() != null ? "contact: " + chat.getClientUserEmail() : "");
       } else{
           holder.itemContact.setText(chat.getSwapOwnerEmail() != null ? "contact: " + chat.getSwapOwnerEmail() : "");
       }
        // Decode and set image from Base64
        if (chat.getSwapItemImageBlob() != null && !chat.getSwapItemImageBlob().isEmpty()) {
            try {
                byte[] decodedString = Base64.decode(chat.getSwapItemImageBlob(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.itemImage.setImageBitmap(decodedByte);
            } catch (Exception e) {
                holder.itemImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.itemImage.setImageResource(R.drawable.placeholder_image);
        }

        // Set status icon
        switch (chat.getStatus()) {
            case "chosen":
                holder.statusIcon.setImageResource(R.drawable.ic_checked);
                holder.statusIcon.setVisibility(View.VISIBLE);
                break;
            case "accepted":
                holder.statusIcon.setImageResource(R.drawable.ic_heart);
                holder.statusIcon.setVisibility(View.VISIBLE);
                break;
            case "declined":
                holder.statusIcon.setImageResource(R.drawable.rejected);
                holder.statusIcon.setVisibility(View.VISIBLE);
                break;

            case "given":
                holder.statusIcon.setImageResource(R.drawable.given);
                holder.statusIcon.setVisibility(View.VISIBLE);
                break;
            default:
                holder.statusIcon.setVisibility(View.GONE);
        }

        // Set unread badge
        int unreadCount = chat.getUnreadMessageCount();
        if (unreadCount > 0) {
            holder.unreadMessagesCount.setText(String.valueOf(unreadCount));
            holder.unreadMessagesCount.setVisibility(View.VISIBLE);
        } else {
            holder.unreadMessagesCount.setVisibility(View.GONE);
        }

        // Open MessagesActivity on click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessagesActivity.class);
            intent.putExtra("chatId", chat.getChatId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    static class ChatViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage, statusIcon;
        TextView itemDescription, itemAddress, itemContact, ownerLabel, unreadMessagesCount;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.itemImage);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            itemDescription = itemView.findViewById(R.id.chat_item_description);
            itemAddress = itemView.findViewById(R.id.chat_item_address);
            itemContact = itemView.findViewById(R.id.chat_item_contact);
            ownerLabel = itemView.findViewById(R.id.owner_label);
            unreadMessagesCount = itemView.findViewById(R.id.unreadMessagesCount);
        }
    }
}
