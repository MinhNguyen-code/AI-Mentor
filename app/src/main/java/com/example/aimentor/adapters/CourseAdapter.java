package com.example.aimentor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimentor.R;
import com.example.aimentor.models.CourseModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseModel> coursesList;
    private OnCourseDeleteListener deleteListener;
    private OnKahootQuizClickListener kahootListener;
    private OnProgressClickListener progressListener;
    private OnEnrollClickListener enrollListener;
    private boolean isEnrolledView;

    public interface OnEnrollClickListener {
        void onEnrollClick(CourseModel course);
    }

    public interface OnCourseDeleteListener {
        void onCourseDelete(int courseId, int position);
    }

    public interface OnKahootQuizClickListener {
        void onKahootQuizClick(CourseModel course);
    }

    public interface OnProgressClickListener {
        void onProgressClick(CourseModel course);
    }

    public CourseAdapter(List<CourseModel> coursesList, boolean isEnrolledView, OnCourseDeleteListener deleteListener) {
        this.coursesList = coursesList;
        this.isEnrolledView = isEnrolledView;
        this.deleteListener = deleteListener;
    }

    public void setOnKahootQuizClickListener(OnKahootQuizClickListener kahootListener) {
        this.kahootListener = kahootListener;
    }

    public void setOnProgressClickListener(OnProgressClickListener progressListener) {
        this.progressListener = progressListener;
    }

    public void setOnEnrollClickListener(OnEnrollClickListener enrollListener) {
        this.enrollListener = enrollListener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseModel course = coursesList.get(position);
        holder.tvCourseCode.setText(course.getCode());
        holder.tvCourseTitle.setText(course.getTitle());
        holder.tvCourseCredits.setText(course.getCredits() + " Credits");
        holder.tvCourseDesc.setText(course.getDescription());

        // Handle delete action
        holder.btnDeleteCourse.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onCourseDelete(course.getId(), holder.getAdapterPosition());
            }
        });

        // Handle Kahoot Quiz Action
        if (holder.btnKahootQuiz != null) {
            holder.btnKahootQuiz.setOnClickListener(v -> {
                if (kahootListener != null) {
                    kahootListener.onKahootQuizClick(course);
                }
            });
        }

        // Handle Progress Action
        if (holder.btnViewProgress != null) {
            holder.btnViewProgress.setOnClickListener(v -> {
                if (progressListener != null) {
                    progressListener.onProgressClick(course);
                }
            });
        }

        // Handle Enroll Action
        if (holder.btnEnrollCourse != null) {
            holder.btnEnrollCourse.setOnClickListener(v -> {
                if (enrollListener != null) {
                    enrollListener.onEnrollClick(course);
                }
            });
        }

        if (isEnrolledView) {
            if (holder.btnKahootQuiz != null) holder.btnKahootQuiz.setVisibility(View.VISIBLE);
            if (holder.btnViewProgress != null) holder.btnViewProgress.setVisibility(View.VISIBLE);
            if (holder.btnEnrollCourse != null) holder.btnEnrollCourse.setVisibility(View.GONE);
        } else {
            if (holder.btnKahootQuiz != null) holder.btnKahootQuiz.setVisibility(View.GONE);
            if (holder.btnViewProgress != null) holder.btnViewProgress.setVisibility(View.GONE);
            if (holder.btnEnrollCourse != null) holder.btnEnrollCourse.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return coursesList != null ? coursesList.size() : 0;
    }

    public void removeCourseAt(int position) {
        if (position >= 0 && position < coursesList.size()) {
            coursesList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, coursesList.size());
        }
    }

    public static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseCode, tvCourseCredits, tvCourseTitle, tvCourseDesc;
        ImageView btnDeleteCourse;
        MaterialButton btnKahootQuiz, btnViewProgress, btnEnrollCourse;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvCourseCredits = itemView.findViewById(R.id.tvCourseCredits);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvCourseDesc = itemView.findViewById(R.id.tvCourseDesc);
            btnDeleteCourse = itemView.findViewById(R.id.btnDeleteCourse);
            btnKahootQuiz = itemView.findViewById(R.id.btnKahootQuiz);
            btnViewProgress = itemView.findViewById(R.id.btnViewProgress);
            btnEnrollCourse = itemView.findViewById(R.id.btnEnrollCourse);
        }
    }
}
