package com.example.aimentor.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimentor.R;
import com.example.aimentor.activities.AddCourseActivity;
import com.example.aimentor.adapters.CourseAdapter;
import com.example.aimentor.models.CourseModel;
import com.example.aimentor.repository.CourseRepository;

import java.util.List;

public class CategoryFragment extends Fragment implements CourseAdapter.OnCourseDeleteListener {

    private Button btnCreateCourse;
    private RecyclerView rvCourses;
    private CourseAdapter courseAdapter;
    private CourseRepository courseRepository;
    private List<CourseModel> coursesList;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance() {
        return new CategoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseRepository = new CourseRepository(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind layout views
        btnCreateCourse = view.findViewById(R.id.btnCreateCourse);
        rvCourses = view.findViewById(R.id.rvCourses);

        // Setup LayoutManager
        rvCourses.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup button action to navigate to AddCourseActivity
        btnCreateCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddCourseActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load or reload database course items when fragment becomes active
        loadCourses();
    }

    private void loadCourses() {
        if (courseRepository != null) {
            coursesList = courseRepository.getAllCourses();
            courseAdapter = new CourseAdapter(coursesList, this);
            rvCourses.setAdapter(courseAdapter);
        }
    }

    @Override
    public void onCourseDelete(final int courseId, final int position) {
        // Show confirmation dialog before deleting course
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AIMentor);
        builder.setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course from the catalog?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performCourseDeletion(courseId, position);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performCourseDeletion(int courseId, int position) {
        int result = courseRepository.deleteCourse(courseId);
        if (result > 0) {
            if (courseAdapter != null) {
                courseAdapter.removeCourseAt(position);
            }
            Toast.makeText(getContext(), "Course deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to delete course.", Toast.LENGTH_SHORT).show();
        }
    }
}