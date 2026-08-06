package com.example.aimentor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.aimentor.R;
import com.example.aimentor.models.UserModel;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    private List<UserModel> userList;
    private int currentUserId;

    public LeaderboardAdapter(List<UserModel> userList, int currentUserId) {
        this.userList = userList;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        UserModel user = userList.get(position);
        
        int rank = position + 1;
        if (rank == 1) {
            holder.tvRank.setText("🥇");
        } else if (rank == 2) {
            holder.tvRank.setText("🥈");
        } else if (rank == 3) {
            holder.tvRank.setText("🥉");
        } else {
            holder.tvRank.setText(String.valueOf(rank));
        }

        String username = user.getUsername();
        if (username != null && !username.isEmpty()) {
            holder.tvAvatarInitial.setText(username.substring(0, 1).toUpperCase());
            if (user.getId() == currentUserId) {
                holder.tvUsername.setText(username + " (You)");
                holder.tvUsername.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.accent_blue));
            } else {
                holder.tvUsername.setText(username);
                holder.tvUsername.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
            }
        } else {
            holder.tvAvatarInitial.setText("?");
            holder.tvUsername.setText("Unknown");
            holder.tvUsername.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_primary));
        }

        holder.tvLevel.setText("Level " + user.getLevel());

        NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
        holder.tvXp.setText(format.format(user.getXp()) + " XP");
    }

    @Override
    public int getItemCount() {
        return userList == null ? 0 : userList.size();
    }

    public static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank;
        TextView tvAvatarInitial;
        TextView tvUsername;
        TextView tvLevel;
        TextView tvXp;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvAvatarInitial = itemView.findViewById(R.id.tvAvatarInitial);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvXp = itemView.findViewById(R.id.tvXp);
        }
    }
}
