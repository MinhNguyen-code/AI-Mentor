package com.example.aimentor.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aimentor.R;
import com.example.aimentor.activities.MenuActivity;
import com.example.aimentor.models.UserModel;
import com.example.aimentor.repository.UserRepository;

import java.util.Random;

public class HomeFragment extends Fragment {

    private TextView tvGreeting, tvStudentMeta, tvAiRecommendation;
    private TextView chipPrompt1, chipPrompt2, chipPrompt3;
    private View btnAskAi;
    private LinearLayout btnActionRegistration, btnActionGrades, btnActionSchedule, btnActionProfile;
    private UserRepository userRepository;
    private int userId;

    private final String[] aiTips = {
            "\"Great progress! Based on your schedule, review Unit SD201 Programming today to prepare for upcoming assignments.\"",
            "\"AI Recommendation: Focus on Database Normalization in DB201 to boost your GPA to 4.0!\"",
            "\"AI Learning Tip: Practice network subnetting algorithms in NW101 before your next lab session.\"",
            "\"Awesome work! You've completed 36/120 credits. Check out new Level 5 courses in the Courses tab!\""
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(getContext());
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            userId = sharedPreferences.getInt("ID_USER", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvStudentMeta = view.findViewById(R.id.tvStudentMeta);
        tvAiRecommendation = view.findViewById(R.id.tvAiRecommendation);
        btnAskAi = view.findViewById(R.id.btnAskAi);
        chipPrompt1 = view.findViewById(R.id.chipPrompt1);
        chipPrompt2 = view.findViewById(R.id.chipPrompt2);
        chipPrompt3 = view.findViewById(R.id.chipPrompt3);
        btnActionRegistration = view.findViewById(R.id.btnActionRegistration);
        btnActionGrades = view.findViewById(R.id.btnActionGrades);
        btnActionSchedule = view.findViewById(R.id.btnActionSchedule);
        btnActionProfile = view.findViewById(R.id.btnActionProfile);

        // Load dashboard metadata
        loadDashboardData();

        // Setup click listeners for Quick Actions and AI Assistant
        setupActionListeners();
    }

    private void loadDashboardData() {
        if (userId == -1) {
            tvGreeting.setText("Welcome back!");
            tvStudentMeta.setText("Please log in to view academic records.");
            return;
        }

        UserModel user = userRepository.getUserById(userId);
        if (user != null) {
            // Set dynamic welcome greeting
            tvGreeting.setText("Welcome back, " + user.getUsername() + "!");

            // Format dynamic role or ID details
            if (user.getRole() == 3) {
                tvStudentMeta.setText("Role: Administrator Profile");
            } else if (user.getRole() == 2) {
                tvStudentMeta.setText("Role: Faculty Profile");
            } else {
                // Generate a formatted student registration code
                String studentCode = "BTEC" + String.format("%05d", user.getId());
                tvStudentMeta.setText("Student ID: " + studentCode + "  |  Major: IT");
            }
        } else {
            tvGreeting.setText("Welcome back!");
            tvStudentMeta.setText("Error loading profile metadata.");
        }
    }

    private void setupActionListeners() {
        // AI Mentor Interactive Prompt Box & Button
        if (btnAskAi != null) {
            btnAskAi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = new Random().nextInt(aiTips.length);
                    if (tvAiRecommendation != null) {
                        tvAiRecommendation.setText(aiTips[index]);
                    }
                    Toast.makeText(getContext(), "🤖 AI Mentor generated a new study tip!", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Quick Prompt Chips Click Handlers
        if (chipPrompt1 != null) {
            chipPrompt1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvAiRecommendation != null) {
                        tvAiRecommendation.setText("\"AI Tip: In Java OOP, remember to use encapsulated fields and getter/setter methods for clean code architecture.\"");
                    }
                    Toast.makeText(getContext(), "Selected: Java OOP Tip", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (chipPrompt2 != null) {
            chipPrompt2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvAiRecommendation != null) {
                        tvAiRecommendation.setText("\"AI Tip: In DB201, 3NF (Third Normal Form) ensures zero transitive dependencies in your database tables.\"");
                    }
                    Toast.makeText(getContext(), "Selected: Database SQL ERD", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (chipPrompt3 != null) {
            chipPrompt3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvAiRecommendation != null) {
                        tvAiRecommendation.setText("\"AI Tip: Your next exam is SD201 Programming on Friday at 08:00 AM in Room 201.\"");
                    }
                    Toast.makeText(getContext(), "Selected: Next Exam Schedule", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Navigation: switch to registration tab (Category/Courses tab - index 1)
        btnActionRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTab(1, "Courses");
            }
        });

        // Navigation: switch to grades tab (Quiz tab - index 2)
        btnActionGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTab(2, "Grades");
            }
        });

        // Navigation: show toast or handle calendar
        btnActionSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Displaying today's schedule below.", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation: switch to profile tab (Settings tab - index 3)
        btnActionProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTab(3, "Profile");
            }
        });
    }

    private void navigateToTab(int tabPosition, String tabName) {
        if (getActivity() instanceof MenuActivity) {
            MenuActivity parentActivity = (MenuActivity) getActivity();
            parentActivity.selectTab(tabPosition);
        } else {
            Toast.makeText(getContext(), "Navigating to " + tabName + "...", Toast.LENGTH_SHORT).show();
        }
    }
}