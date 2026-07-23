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

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<CourseModel> coursesList;
    private OnCourseDeleteListener deleteListener;

    public interface OnCourseDeleteListener {
        void onCourseDelete(int courseId, int position);
    }

    public CourseAdapter(List<CourseModel> coursesList, OnCourseDeleteListener deleteListener) {
        this.coursesList = coursesList;
        this.deleteListener = deleteListener;
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
        holder.btnDeleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteListener != null) {
                    deleteListener.onCourseDelete(course.getId(), holder.getAdapterPosition());
                }
            }
        });
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

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseCode = itemView.findViewById(R.id.tvCourseCode);
            tvCourseCredits = itemView.findViewById(R.id.tvCourseCredits);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvCourseDesc = itemView.findViewById(R.id.tvCourseDesc);
            btnDeleteCourse = itemView.findViewById(R.id.btnDeleteCourse);
        }
    }
}
