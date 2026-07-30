package com.example.aimentor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimentor.R;
import com.example.aimentor.models.ChatMessageModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_AI = 2;

    private final List<ChatMessageModel> chatList;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private OnBookmarkClickListener bookmarkListener;

    public interface OnBookmarkClickListener {
        void onBookmarkClick(ChatMessageModel message, int position);
    }

    public ChatAdapter(List<ChatMessageModel> chatList) {
        this.chatList = chatList;
    }

    public void setOnBookmarkClickListener(OnBookmarkClickListener listener) {
        this.bookmarkListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return chatList.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_AI;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_ai, parent, false);
            return new AiViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageModel chatMessage = chatList.get(position);
        String formattedTime = timeFormat.format(new Date(chatMessage.getTimestamp()));

        if (holder instanceof UserViewHolder) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.tvUserMessage.setText(chatMessage.getMessage());
            userHolder.tvUserTime.setText(formattedTime);
        } else if (holder instanceof AiViewHolder) {
            AiViewHolder aiHolder = (AiViewHolder) holder;
            if (chatMessage.isTyping()) {
                aiHolder.layoutTyping.setVisibility(View.VISIBLE);
                aiHolder.tvAiMessage.setVisibility(View.GONE);
                if (aiHolder.btnBookmark != null) aiHolder.btnBookmark.setVisibility(View.GONE);
            } else {
                aiHolder.layoutTyping.setVisibility(View.GONE);
                aiHolder.tvAiMessage.setVisibility(View.VISIBLE);
                aiHolder.tvAiMessage.setText(chatMessage.getMessage());
                if (aiHolder.btnBookmark != null) {
                    aiHolder.btnBookmark.setVisibility(View.VISIBLE);
                    aiHolder.btnBookmark.setText(chatMessage.isBookmarked() ? "⭐ Saved" : "⭐ Save");
                    aiHolder.btnBookmark.setOnClickListener(v -> {
                        if (bookmarkListener != null) {
                            bookmarkListener.onBookmarkClick(chatMessage, holder.getAdapterPosition());
                        }
                    });
                }
            }
            aiHolder.tvAiTime.setText(formattedTime);
        }
    }

    @Override
    public int getItemCount() {
        return chatList != null ? chatList.size() : 0;
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserMessage, tvUserTime;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserMessage = itemView.findViewById(R.id.tvUserMessage);
            tvUserTime = itemView.findViewById(R.id.tvUserTime);
        }
    }

    public static class AiViewHolder extends RecyclerView.ViewHolder {
        TextView tvAiMessage, tvAiTime, btnBookmark;
        LinearLayout layoutTyping;

        public AiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAiMessage = itemView.findViewById(R.id.tvAiMessage);
            tvAiTime = itemView.findViewById(R.id.tvAiTime);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            layoutTyping = itemView.findViewById(R.id.layoutTyping);
        }
    }
}
