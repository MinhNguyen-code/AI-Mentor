package com.example.aimentor.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimentor.R;
import com.example.aimentor.models.KahootHistoryModel;

import java.util.List;

public class KahootHistoryAdapter extends RecyclerView.Adapter<KahootHistoryAdapter.HistoryViewHolder> {

    private List<KahootHistoryModel> historyList;
    private OnHistoryClickListener clickListener;
    private OnHistoryDeleteListener deleteListener;

    public interface OnHistoryClickListener {
        void onHistoryClick(KahootHistoryModel history);
    }

    public interface OnHistoryDeleteListener {
        void onHistoryDelete(KahootHistoryModel history, int position);
    }

    public KahootHistoryAdapter(List<KahootHistoryModel> historyList, OnHistoryClickListener clickListener, OnHistoryDeleteListener deleteListener) {
        this.historyList = historyList;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kahoot_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        KahootHistoryModel history = historyList.get(position);
        holder.tvCourseTitle.setText(history.getCourseTitle());
        holder.tvQuestion.setText(history.getQuestionText());
        holder.tvUserAnswer.setText("Your Answer: " + (history.getUserAnswer() != null ? history.getUserAnswer() : "Timeout/None"));
        
        if (history.isCorrect()) {
            holder.tvStatus.setText("Correct");
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.tvStatus.setText("Wrong - Correct was: " + history.getCorrectAnswer());
            holder.tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onHistoryClick(history);
            }
        });

        if (holder.btnDeleteHistory != null) {
            holder.btnDeleteHistory.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onHistoryDelete(history, holder.getAdapterPosition());
                }
            });
        }
    }

    public void removeHistoryAt(int position) {
        if (position >= 0 && position < historyList.size()) {
            historyList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, historyList.size());
        }
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseTitle, tvQuestion, tvUserAnswer, tvStatus;
        android.widget.ImageView btnDeleteHistory;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseTitle = itemView.findViewById(R.id.tvHistoryCourseTitle);
            tvQuestion = itemView.findViewById(R.id.tvHistoryQuestion);
            tvUserAnswer = itemView.findViewById(R.id.tvHistoryUserAnswer);
            tvStatus = itemView.findViewById(R.id.tvHistoryStatus);
            btnDeleteHistory = itemView.findViewById(R.id.btnDeleteHistory);
        }
    }
}
