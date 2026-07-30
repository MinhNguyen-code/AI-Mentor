package com.example.aimentor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimentor.R;
import com.example.aimentor.models.ChatMessageModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.HistoryViewHolder> {

    private final List<ChatMessageModel> historyList;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
    private OnHistoryItemClickListener clickListener;

    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(ChatMessageModel message);
    }

    public ChatHistoryAdapter(List<ChatMessageModel> historyList) {
        this.historyList = historyList;
    }

    public void setOnHistoryItemClickListener(OnHistoryItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history_sidebar, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        ChatMessageModel item = historyList.get(position);

        String title = item.getMessage();
        if (title != null && title.startsWith("[📷")) {
            int closingBracket = title.indexOf("]\n");
            if (closingBracket != -1) {
                title = title.substring(closingBracket + 2);
            }
        }
        holder.tvHistoryTitle.setText(title);

        String model = item.getModelUsed() != null ? item.getModelUsed() : "AI Mentor";
        String dateStr = timeFormat.format(new Date(item.getTimestamp()));
        holder.tvHistorySub.setText(model + " • " + dateStr);

        holder.tvHistoryIcon.setText(item.isUser() ? "❓" : "🤖");

        if (item.isBookmarked()) {
            holder.tvBookmarkIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.tvBookmarkIndicator.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHistoryItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryIcon, tvHistoryTitle, tvHistorySub, tvBookmarkIndicator;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryIcon = itemView.findViewById(R.id.tvHistoryIcon);
            tvHistoryTitle = itemView.findViewById(R.id.tvHistoryTitle);
            tvHistorySub = itemView.findViewById(R.id.tvHistorySub);
            tvBookmarkIndicator = itemView.findViewById(R.id.tvBookmarkIndicator);
        }
    }
}
