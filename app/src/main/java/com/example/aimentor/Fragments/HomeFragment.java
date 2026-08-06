package com.example.aimentor.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aimentor.R;
import com.example.aimentor.activities.MenuActivity;
import com.example.aimentor.models.UserModel;
import com.example.aimentor.repository.UserRepository;
import com.example.aimentor.repository.StatsRepository;
import com.example.aimentor.services.AiMentorService;
import com.example.aimentor.utils.GamificationManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    // Main Header Views
    private TextView tvGreeting, tvStudentMeta, tvAiRecommendation, tvStreakBadge;
    
    // Gamification Views
    private TextView tvUserLevel, tvUserXp, tvXpToNextLevel;
    private ProgressBar pbXpProgress;
    
    // Statistics Views
    private TextView tvQuizAccuracy, tvQuizRatio, tvMostStudiedCourse, tvMostStudiedSessions;
    
    // AI Analysis View
    private TextView tvAiSmartAnalysis;

    private TextView chipPrompt1, chipPrompt2, chipPrompt3;
    private View btnAskAi;
    private LinearLayout btnActionRegistration, btnActionGrades, btnActionRanking, btnActionProfile;
    
    private UserRepository userRepository;
    private com.example.aimentor.repository.ChatRepository chatRepository;
    private StatsRepository statsRepository;
    private int userId = -1;

    private final String[] aiTips = {
            "\"Great progress! Based on your schedule, review Unit SD201 Programming today to prepare for upcoming assignments.\"",
            "\"AI Recommendation: Focus on Database Normalization in DB201 for your upcoming coursework.\"",
            "\"AI Learning Tip: Practice network subnetting algorithms in NW101 before your next lab session.\"",
            "\"Awesome work! Keep asking questions and answering quizzes to earn badges and XP!\""
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
        chatRepository = new com.example.aimentor.repository.ChatRepository(getContext());
        statsRepository = new StatsRepository(getContext());
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

        // Bind standard views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvStudentMeta = view.findViewById(R.id.tvStudentMeta);
        tvAiRecommendation = view.findViewById(R.id.tvAiRecommendation);
        tvStreakBadge = view.findViewById(R.id.tvStreakBadge);
        btnAskAi = view.findViewById(R.id.btnAskAi);
        btnActionRegistration = view.findViewById(R.id.btnActionRegistration);
        btnActionGrades = view.findViewById(R.id.btnActionGrades);
        btnActionRanking = view.findViewById(R.id.btnActionRanking);
        btnActionProfile = view.findViewById(R.id.btnActionProfile);

        // Bind gamification views
        tvUserLevel = view.findViewById(R.id.tvUserLevel);
        tvUserXp = view.findViewById(R.id.tvUserXp);
        tvXpToNextLevel = view.findViewById(R.id.tvXpToNextLevel);
        pbXpProgress = view.findViewById(R.id.pbXpProgress);

        // Bind statistics views
        tvQuizAccuracy = view.findViewById(R.id.tvQuizAccuracy);
        tvQuizRatio = view.findViewById(R.id.tvQuizRatio);
        tvMostStudiedCourse = view.findViewById(R.id.tvMostStudiedCourse);
        tvMostStudiedSessions = view.findViewById(R.id.tvMostStudiedSessions);

        // Bind AI Analysis view
        tvAiSmartAnalysis = view.findViewById(R.id.tvAiSmartAnalysis);

        // Setup click listeners for Quick Actions and AI Assistant
        setupActionListeners();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load fresh data whenever fragment becomes active
        loadDashboardData();
    }

    private void loadDashboardData() {
        if (getContext() == null) return;
        
        if (userRepository == null) userRepository = new UserRepository(getContext());
        if (chatRepository == null) chatRepository = new com.example.aimentor.repository.ChatRepository(getContext());
        if (statsRepository == null) statsRepository = new StatsRepository(getContext());

        if (userId == -1 && getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            userId = sharedPreferences.getInt("ID_USER", -1);
        }

        if (userId == -1) {
            if (tvGreeting != null) tvGreeting.setText("Welcome back!");
            if (tvStudentMeta != null) tvStudentMeta.setText("Please log in to view academic records.");
            return;
        }

        UserModel user = userRepository.getUserById(userId);
        if (user != null) {
            // Set dynamic welcome greeting
            tvGreeting.setText("Welcome back, " + user.getUsername() + "!");

            // Format dynamic role or ID details
            if (user.getRole() == 3 || user.getRole() == 2) {
                tvStudentMeta.setVisibility(View.GONE);
            } else {
                tvStudentMeta.setVisibility(View.VISIBLE);
                String studentCode = "BTEC" + String.format("%05d", user.getId());
                String eduLevel = (user.getEducationLevel() != null) ? user.getEducationLevel() : "University";
                tvStudentMeta.setText("Student ID: " + studentCode + "  |  " + eduLevel);
            }

            // 1. Load Gamification (Level, XP, Progress Bar)
            int xp = user.getXp();
            int level = GamificationManager.getLevel(xp);
            int xpInLevel = GamificationManager.getXpInCurrentLevel(xp);
            int xpToNext = GamificationManager.getXpToNextLevel(xp);
            int progressPercent = GamificationManager.getProgressPercent(xp);

            if (tvUserLevel != null) tvUserLevel.setText("LEVEL " + level);
            if (tvUserXp != null) tvUserXp.setText(xpInLevel + " / " + GamificationManager.XP_PER_LEVEL + " XP");
            if (tvXpToNextLevel != null) tvXpToNextLevel.setText(xpToNext + " XP to Level " + (level + 1));
            if (pbXpProgress != null) pbXpProgress.setProgress(progressPercent);

            // 2. Load Real-time Streak
            int questionCount = chatRepository.getQuestionCount(userId);
            int streakDays = 1 + (questionCount > 0 ? Math.min(6, questionCount / 2) : 0);
            if (tvStreakBadge != null) {
                tvStreakBadge.setText("🔥 " + streakDays + (streakDays == 1 ? " Day Streak" : " Days Streak"));
            }

            // Award streak badges
            GamificationManager.checkStreakBadge(getContext(), userId, streakDays);

            // 3. Load Statistics
            loadLearningStatistics();

            // 4. Generate AI Smart Analysis
            generateAiSmartAnalysis(user, questionCount, streakDays);

        } else {
            tvGreeting.setText("Welcome back!");
            tvStudentMeta.setText("Error loading profile metadata.");
        }

        // Load a random dynamic recommendation
        if (tvAiRecommendation != null) {
            int index = new Random().nextInt(aiTips.length);
            tvAiRecommendation.setText(aiTips[index]);
        }
    }

    private void loadLearningStatistics() {
        if (statsRepository == null || userId == -1) return;

        // Quiz Accuracy
        int accuracy = statsRepository.getOverallQuizAccuracy(userId);
        int totalCorrect = statsRepository.getTotalCorrectAnswers(userId);
        int totalQuizzes = statsRepository.getTotalQuizCount(userId);

        if (tvQuizAccuracy != null) {
            tvQuizAccuracy.setText(accuracy + "%");
        }
        if (tvQuizRatio != null) {
            if (totalQuizzes > 0) {
                tvQuizRatio.setText(totalCorrect + " correct of " + totalQuizzes + " answers");
            } else {
                tvQuizRatio.setText("No quizzes completed yet");
            }
        }

        // Most Studied Course
        List<Map<String, Object>> mostStudied = statsRepository.getMostStudiedCourses(userId);
        if (tvMostStudiedCourse != null) {
            if (mostStudied != null && !mostStudied.isEmpty()) {
                String topCourse = (String) mostStudied.get(0).get("title");
                tvMostStudiedCourse.setText(topCourse);
            } else {
                tvMostStudiedCourse.setText("None");
            }
        }
        if (tvMostStudiedSessions != null) {
            if (mostStudied != null && !mostStudied.isEmpty()) {
                int count = (int) mostStudied.get(0).get("count");
                tvMostStudiedSessions.setText(count + (count == 1 ? " quiz session completed" : " quiz sessions completed"));
            } else {
                tvMostStudiedSessions.setText("0 quiz sessions completed");
            }
        }
    }

    private void generateAiSmartAnalysis(UserModel user, int questionCount, int streakDays) {
        if (tvAiSmartAnalysis == null || statsRepository == null || userId == -1) return;

        int totalQuizzes = statsRepository.getTotalQuizCount(userId);
        int accuracy = statsRepository.getOverallQuizAccuracy(userId);
        List<Map<String, Object>> mostStudied = statsRepository.getMostStudiedCourses(userId);
        String topCourse = (mostStudied != null && !mostStudied.isEmpty()) ? (String) mostStudied.get(0).get("title") : "None";

        // Formulate a prompt for Groq AI
        String prompt = "You are a smart AI learning mentor. Analyse the following student statistics:\n" +
                "- Name: " + user.getUsername() + "\n" +
                "- Education Level: " + user.getEducationLevel() + "\n" +
                "- Questions asked to AI: " + questionCount + "\n" +
                "- Streak: " + streakDays + " days\n" +
                "- Quizzes Taken: " + totalQuizzes + "\n" +
                "- Average Quiz Accuracy: " + accuracy + "%\n" +
                "- Most studied subject: " + topCourse + "\n\n" +
                "Provide 2-3 personalized, encouraging lines of study advice in English. Be concise and reference their actual stats (e.g. 'You are focusing a lot on Programming...', 'Your accuracy is...'). Speak directly to the student in a friendly, motivational tone.";

        // Use a default recommendation instantly in case Groq is offline
        String defaultAnalysis = "Hello " + user.getUsername() + "! ";
        if (totalQuizzes == 0 && questionCount == 0) {
            defaultAnalysis += "You haven't started any learning activities yet. Try asking your first question to the AI in the AI Chat tab or take a Quiz in the Courses tab to begin your journey and earn XP!";
        } else {
            defaultAnalysis += "An analysis of your study habits shows: ";
            if (!topCourse.equals("None")) {
                defaultAnalysis += "You are focusing the most on '" + topCourse + "'. ";
            }
            if (totalQuizzes > 0) {
                defaultAnalysis += "Your multiple-choice quiz accuracy is " + accuracy + "%. ";
                if (accuracy >= 80) {
                    defaultAnalysis += "This is an excellent result! ";
                } else if (accuracy >= 50) {
                    defaultAnalysis += "Pretty good result, keep practicing to improve your accuracy. ";
                } else {
                    defaultAnalysis += "Don't be discouraged, ask the AI to explain your mistakes in detail to improve your score. ";
                }
            }
            defaultAnalysis += "Maintaining a " + streakDays + "-day learning streak is a great habit. Keep it up!";
        }
        
        tvAiSmartAnalysis.setText(defaultAnalysis);

        // Fetch deep AI response asynchronously
        if (totalQuizzes > 0 || questionCount > 0) {
            AiMentorService.sendMessageToAi("llama-3.1-8b-instant", user.getEducationLevel(), "Short", null, prompt, new AiMentorService.AiResponseCallback() {
                @Override
                public void onSuccess(String aiReply) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        if (tvAiSmartAnalysis != null && aiReply != null && !aiReply.trim().isEmpty()) {
                            tvAiSmartAnalysis.setText(aiReply.trim());
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    // Fail silently, keep the dynamic offline advice
                }
            });
        }
    }

    private void setupActionListeners() {
        if (btnAskAi != null) {
            btnAskAi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    QuizFragment.setPendingPrompt("Hello AI Mentor! Give me an interactive study tip and advice for today.");
                    navigateToTab(2, "AI Chat");
                }
            });
        }

        btnActionRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTab(1, "Courses");
            }
        });

        btnActionGrades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTab(2, "AI Chat");
            }
        });

        btnActionRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToTab(4, "Leaderboard");
            }
        });

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