package com.example.aimentor.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.aimentor.services.AiMentorService;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class CategoryFragment extends Fragment implements CourseAdapter.OnCourseDeleteListener {

    private Button btnCreateCourse;
    private RecyclerView rvCourses;
    private CourseAdapter courseAdapter;
    private CourseRepository courseRepository;
    private List<CourseModel> coursesList;
    private int kahootScore = 0;

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
        btnCreateCourse.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddCourseActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCourses();
    }

    private void loadCourses() {
        if (courseRepository != null) {
            coursesList = courseRepository.getAllCourses();
            courseAdapter = new CourseAdapter(coursesList, this);
            courseAdapter.setOnKahootQuizClickListener(this::launchKahootQuiz);
            rvCourses.setAdapter(courseAdapter);
        }
    }

    private void launchKahootQuiz(CourseModel course) {
        if (getContext() == null || course == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_kahoot_quiz, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.Theme_AIMentor)
                .setView(dialogView)
                .create();

        TextView tvKahootHeader = dialogView.findViewById(R.id.tvKahootHeader);
        TextView tvKahootScore = dialogView.findViewById(R.id.tvKahootScore);
        TextView tvKahootQuestion = dialogView.findViewById(R.id.tvKahootQuestion);
        ProgressBar progressKahootLoading = dialogView.findViewById(R.id.progressKahootLoading);
        LinearLayout layoutOptionsContainer = dialogView.findViewById(R.id.layoutOptionsContainer);

        MaterialButton btnOptionA = dialogView.findViewById(R.id.btnOptionA);
        MaterialButton btnOptionB = dialogView.findViewById(R.id.btnOptionB);
        MaterialButton btnOptionC = dialogView.findViewById(R.id.btnOptionC);
        MaterialButton btnOptionD = dialogView.findViewById(R.id.btnOptionD);
        MaterialButton btnCloseKahoot = dialogView.findViewById(R.id.btnCloseKahoot);

        tvKahootHeader.setText("🎮 KAHOOT AI: " + course.getCode());
        tvKahootScore.setText("Score: " + kahootScore + " Pts");
        tvKahootQuestion.setText("AI is generating Kahoot question for " + course.getTitle() + "...");
        progressKahootLoading.setVisibility(View.VISIBLE);
        layoutOptionsContainer.setVisibility(View.GONE);

        String prompt = "Generate 1 multiple choice Kahoot question for course: " + course.getTitle() +
                ". Format: Question\nA) Option 1\nB) Option 2\nC) Option 3\nD) Option 4\nCorrect: A";

        AiMentorService.sendMessageToAi("llama-3.1-8b-instant", "University", "Short", null, prompt, new AiMentorService.AiResponseCallback() {
            @Override
            public void onSuccess(String aiReply) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressKahootLoading.setVisibility(View.GONE);
                    layoutOptionsContainer.setVisibility(View.VISIBLE);
                    tvKahootQuestion.setText(aiReply);

                    btnOptionA.setOnClickListener(v -> handleAnswer(true, dialog, tvKahootScore));
                    btnOptionB.setOnClickListener(v -> handleAnswer(false, dialog, tvKahootScore));
                    btnOptionC.setOnClickListener(v -> handleAnswer(false, dialog, tvKahootScore));
                    btnOptionD.setOnClickListener(v -> handleAnswer(false, dialog, tvKahootScore));
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressKahootLoading.setVisibility(View.GONE);
                    layoutOptionsContainer.setVisibility(View.VISIBLE);
                    tvKahootQuestion.setText("Question: What is the main objective of " + course.getTitle() + "?");
                    btnOptionA.setText("▲ Building scalable applications");
                    btnOptionB.setText("◆ Deleting database tables");
                    btnOptionC.setText("● Formatting text documents");
                    btnOptionD.setText("■ Disconnecting network routers");

                    btnOptionA.setOnClickListener(v -> handleAnswer(true, dialog, tvKahootScore));
                    btnOptionB.setOnClickListener(v -> handleAnswer(false, dialog, tvKahootScore));
                    btnOptionC.setOnClickListener(v -> handleAnswer(false, dialog, tvKahootScore));
                    btnOptionD.setOnClickListener(v -> handleAnswer(false, dialog, tvKahootScore));
                });
            }
        });

        btnCloseKahoot.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void handleAnswer(boolean isCorrect, AlertDialog dialog, TextView tvScore) {
        if (isCorrect) {
            kahootScore += 1000;
            Toast.makeText(getContext(), "🎉 CORRECT ANSWER! +1000 Pts!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "❌ Incorrect! Keep practicing!", Toast.LENGTH_SHORT).show();
        }
        if (tvScore != null) tvScore.setText("Score: " + kahootScore + " Pts");
        dialog.dismiss();
    }

    @Override
    public void onCourseDelete(final int courseId, final int position) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AIMentor);
        builder.setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course from your catalog?")
                .setPositiveButton("Delete", (dialog, which) -> performCourseDeletion(courseId, position))
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