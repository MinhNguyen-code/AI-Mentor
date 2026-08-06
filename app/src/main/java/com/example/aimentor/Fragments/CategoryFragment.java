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
import com.example.aimentor.activities.CourseProgressActivity;
import com.example.aimentor.adapters.CourseAdapter;
import com.example.aimentor.models.CourseModel;
import com.example.aimentor.models.UserCourseModel;
import com.example.aimentor.repository.CourseRepository;
import com.example.aimentor.repository.StatsRepository;
import com.example.aimentor.services.AiMentorService;
import com.example.aimentor.utils.GamificationManager;
import com.google.android.material.button.MaterialButton;

import com.example.aimentor.repository.KahootRepository;
import com.example.aimentor.models.KahootHistoryModel;
import com.example.aimentor.adapters.KahootHistoryAdapter;
import com.example.aimentor.activities.MenuActivity;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.os.CountDownTimer;
import com.google.android.material.materialswitch.MaterialSwitch;
import java.util.List;

public class CategoryFragment extends Fragment implements CourseAdapter.OnCourseDeleteListener {

    private Button btnCreateCourse;
    private RecyclerView rvMyCourses, rvAvailableCourses;
    private TextView tvMyCoursesHeader, tvSuggestedCoursesHeader;
    private CourseAdapter myCoursesAdapter, availableCoursesAdapter;
    private CourseRepository courseRepository;
    private List<CourseModel> myCoursesList, availableCoursesList;
    private StatsRepository statsRepository;
    private KahootRepository kahootRepository;
    private int kahootScore = 0;
    private int userId = -1;
    private CountDownTimer kahootTimer;

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
        statsRepository = new StatsRepository(getContext());
        kahootRepository = new KahootRepository(getContext());
        if (getActivity() != null) {
            android.content.SharedPreferences sp = getActivity().getSharedPreferences("USER_INFO", android.content.Context.MODE_PRIVATE);
            userId = sp.getInt("ID_USER", -1);
        }
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
        rvMyCourses = view.findViewById(R.id.rvMyCourses);
        rvAvailableCourses = view.findViewById(R.id.rvAvailableCourses);
        tvMyCoursesHeader = view.findViewById(R.id.tvMyCoursesHeader);
        tvSuggestedCoursesHeader = view.findViewById(R.id.tvSuggestedCoursesHeader);

        // Setup LayoutManager
        rvMyCourses.setLayoutManager(new LinearLayoutManager(getContext()));
        rvAvailableCourses.setLayoutManager(new LinearLayoutManager(getContext()));

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
        if (courseRepository != null && userId != -1) {
            List<CourseModel> allCourses = courseRepository.getAllCourses();
            List<UserCourseModel> enrolled = courseRepository.getUserEnrolledCourses(userId);
            
            myCoursesList = new java.util.ArrayList<>();
            availableCoursesList = new java.util.ArrayList<>();
            
            for (CourseModel course : allCourses) {
                boolean isEnrolled = false;
                for (UserCourseModel uc : enrolled) {
                    if (uc.getCourseCode().equals(course.getCode())) {
                        isEnrolled = true;
                        break;
                    }
                }
                if (isEnrolled) {
                    myCoursesList.add(course);
                } else {
                    availableCoursesList.add(course);
                }
            }
            
            if (myCoursesList.isEmpty()) {
                tvMyCoursesHeader.setVisibility(View.GONE);
                rvMyCourses.setVisibility(View.GONE);
            } else {
                tvMyCoursesHeader.setVisibility(View.VISIBLE);
                rvMyCourses.setVisibility(View.VISIBLE);
            }

            myCoursesAdapter = new CourseAdapter(myCoursesList, true, this);
            myCoursesAdapter.setOnKahootQuizClickListener(this::launchKahootQuiz);
            myCoursesAdapter.setOnProgressClickListener(this::viewProgress);
            rvMyCourses.setAdapter(myCoursesAdapter);
            
            availableCoursesAdapter = new CourseAdapter(availableCoursesList, false, this);
            availableCoursesAdapter.setOnEnrollClickListener(course -> {
                long res = courseRepository.enrollUserInCourse(userId, course.getCode(), course.getTitle(), "Step-by-Step");
                if (res != -1) {
                    Toast.makeText(getContext(), "Enrolled successfully in " + course.getCode(), Toast.LENGTH_SHORT).show();
                    loadCourses(); // Refresh lists
                } else {
                    Toast.makeText(getContext(), "Enrollment failed", Toast.LENGTH_SHORT).show();
                }
            });
            rvAvailableCourses.setAdapter(availableCoursesAdapter);
        }
    }

    private void viewProgress(CourseModel course) {
        if (getContext() == null || userId == -1) return;
        
        List<UserCourseModel> enrolled = courseRepository.getUserEnrolledCourses(userId);
        UserCourseModel matchedCourse = null;
        for (UserCourseModel uc : enrolled) {
            if (uc.getCourseCode().equals(course.getCode())) {
                matchedCourse = uc;
                break;
            }
        }
        
        if (matchedCourse != null) {
            Intent intent = new Intent(getActivity(), CourseProgressActivity.class);
            intent.putExtra("USER_COURSE_ID", matchedCourse.getId());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "You are not enrolled in this course yet. Please create/add it.", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchKahootQuiz(CourseModel course) {
        if (getContext() == null || course == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_kahoot_menu, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        Spinner spinnerQuestionCount = dialogView.findViewById(R.id.spinnerQuestionCount);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"5", "10", "15"});
        spinnerQuestionCount.setAdapter(spinnerAdapter);
        spinnerQuestionCount.setSelection(1); // Default to 10

        MaterialSwitch switchTimer = dialogView.findViewById(R.id.switchTimer);
        
        MaterialButton btnQuizHistory = dialogView.findViewById(R.id.btnQuizHistory);
        btnQuizHistory.setOnClickListener(v -> {
            dialog.dismiss();
            showKahootHistory(course);
        });

        android.widget.Button btnStartQuiz = dialogView.findViewById(R.id.btnStartQuiz);
        btnStartQuiz.setOnClickListener(v -> {
            int numQuestions = Integer.parseInt(spinnerQuestionCount.getSelectedItem().toString());
            boolean isTimerEnabled = switchTimer.isChecked();
            dialog.dismiss();
            startKahootSession(course, numQuestions, isTimerEnabled);
        });

        MaterialButton btnCloseMenu = dialogView.findViewById(R.id.btnCloseMenu);
        btnCloseMenu.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showKahootHistory(CourseModel course) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_kahoot_history, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        RecyclerView rvKahootHistory = dialogView.findViewById(R.id.rvKahootHistory);
        TextView tvEmptyHistory = dialogView.findViewById(R.id.tvEmptyHistory);
        MaterialButton btnCloseHistory = dialogView.findViewById(R.id.btnCloseHistory);

        rvKahootHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        List<KahootHistoryModel> historyList = kahootRepository.getKahootHistoryByCourse(userId, course.getId());

        if (historyList == null || historyList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
        } else {
            KahootHistoryAdapter[] adapterHolder = new KahootHistoryAdapter[1];
            adapterHolder[0] = new KahootHistoryAdapter(historyList, history -> {
                dialog.dismiss();
                String prompt = "Explain the answer to this question academically and concisely:\n\n" + 
                        history.getQuestionText() + 
                        "\n\nCorrect Answer is: " + history.getCorrectAnswer() +
                        "\n\nREQUIREMENT: Do not use any Markdown characters (like ** or #). Provide a direct academic definition, followed by how it applies to this question. Keep it clean and focused.";
                QuizFragment.setPendingPrompt(prompt);
                if (getActivity() instanceof MenuActivity) {
                    ((MenuActivity) getActivity()).selectTab(2); // AI Chat Tab
                }
            }, (history, position) -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delete History")
                        .setMessage("Are you sure you want to delete this quiz history?")
                        .setPositiveButton("Delete", (d, which) -> {
                            int res = kahootRepository.deleteKahootHistory(history.getId());
                            if (res > 0) {
                                if (adapterHolder[0] != null) {
                                    adapterHolder[0].removeHistoryAt(position);
                                }
                                if (historyList.isEmpty()) {
                                    tvEmptyHistory.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
            rvKahootHistory.setAdapter(adapterHolder[0]);
        }

        btnCloseHistory.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void startKahootSession(CourseModel course, int maxQuestions, boolean isTimerEnabled) {
        kahootScore = 0;
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_kahoot_quiz, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();
                
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        int[] currentKahootQuestion = {1};
        int[] kahootCorrectAnswers = {0};

        MaterialButton btnCloseKahoot = dialogView.findViewById(R.id.btnCloseKahoot);
        btnCloseKahoot.setOnClickListener(v -> {
            if (kahootTimer != null) kahootTimer.cancel();
            dialog.dismiss();
        });

        fetchNextKahootQuestion(dialogView, course, currentKahootQuestion, kahootCorrectAnswers, maxQuestions, isTimerEnabled);
        dialog.show();
    }

    private void fetchNextKahootQuestion(View dialogView, CourseModel course, int[] currentQuestion, int[] correctAnswers, int maxQuestions, boolean isTimerEnabled) {
        TextView tvKahootHeader = dialogView.findViewById(R.id.tvKahootHeader);
        TextView tvKahootScore = dialogView.findViewById(R.id.tvKahootScore);
        TextView tvKahootProgress = dialogView.findViewById(R.id.tvKahootProgress);
        TextView tvKahootQuestion = dialogView.findViewById(R.id.tvKahootQuestion);
        ProgressBar progressKahootLoading = dialogView.findViewById(R.id.progressKahootLoading);
        LinearLayout layoutOptionsContainer = dialogView.findViewById(R.id.layoutOptionsContainer);
        MaterialButton btnOptionA = dialogView.findViewById(R.id.btnOptionA);
        MaterialButton btnOptionB = dialogView.findViewById(R.id.btnOptionB);
        MaterialButton btnOptionC = dialogView.findViewById(R.id.btnOptionC);
        MaterialButton btnOptionD = dialogView.findViewById(R.id.btnOptionD);
        MaterialButton btnCloseKahoot = dialogView.findViewById(R.id.btnCloseKahoot);

        if (kahootTimer != null) kahootTimer.cancel();

        if (currentQuestion[0] > maxQuestions) {
            tvKahootHeader.setText("KAHOOT FINISHED!");
            tvKahootQuestion.setText("Quiz Finished!\nYou got " + correctAnswers[0] + "/" + maxQuestions + " correct.");
            layoutOptionsContainer.setVisibility(View.GONE);
            progressKahootLoading.setVisibility(View.GONE);
            btnCloseKahoot.setVisibility(View.VISIBLE);
            btnCloseKahoot.setText("Close");
            if (tvKahootProgress != null) tvKahootProgress.setVisibility(View.GONE);
            return;
        }

        tvKahootHeader.setText("KAHOOT AI: Q" + currentQuestion[0] + "/" + maxQuestions);
        tvKahootScore.setText("Score: " + kahootScore + " Pts");
        if (tvKahootProgress != null) {
            tvKahootProgress.setVisibility(View.VISIBLE);
            tvKahootProgress.setText("Question " + currentQuestion[0] + " / " + maxQuestions + (isTimerEnabled ? " (15s)" : ""));
        }
        tvKahootQuestion.setText("AI is generating Question " + currentQuestion[0] + "...");
        progressKahootLoading.setVisibility(View.VISIBLE);
        layoutOptionsContainer.setVisibility(View.GONE);
        btnCloseKahoot.setVisibility(View.GONE);

        String prompt = "Generate 1 multiple choice Kahoot question for course: " + course.getTitle() +
                ". You MUST return ONLY a valid JSON object with this exact structure: {\"question\": \"...\", \"A\": \"...\", \"B\": \"...\", \"C\": \"...\", \"D\": \"...\", \"correct\": \"A\"}";

        AiMentorService.sendMessageToAi("llama-3.1-8b-instant", "University", "Short", null, prompt, new AiMentorService.AiResponseCallback() {
            @Override
            public void onSuccess(String aiReply) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressKahootLoading.setVisibility(View.GONE);
                    layoutOptionsContainer.setVisibility(View.VISIBLE);
                    btnCloseKahoot.setVisibility(View.VISIBLE);
                    
                    dialogView.findViewById(R.id.layoutExplanation).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.layoutNextActions).setVisibility(View.GONE);
                    
                    try {
                        String cleanJson = aiReply;
                        if (cleanJson.contains("```json")) {
                            cleanJson = cleanJson.substring(cleanJson.indexOf("```json") + 7, cleanJson.lastIndexOf("```"));
                        } else if (cleanJson.contains("{")) {
                            cleanJson = cleanJson.substring(cleanJson.indexOf("{"), cleanJson.lastIndexOf("}") + 1);
                        }
                        org.json.JSONObject json = new org.json.JSONObject(cleanJson);
                        String q = json.getString("question");
                        String a = json.getString("A");
                        String b = json.getString("B");
                        String c = json.getString("C");
                        String d = json.getString("D");
                        String correct = json.getString("correct").toUpperCase().trim();
                        if (correct.startsWith("OPTION ")) correct = correct.substring(7);

                        tvKahootQuestion.setText(q);
                        btnOptionA.setText("▲ " + a);
                        btnOptionB.setText("◆ " + b);
                        btnOptionC.setText("● " + c);
                        btnOptionD.setText("■ " + d);
                        
                        // Reset colors
                        btnOptionA.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E21B3C")));
                        btnOptionB.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1368CE")));
                        btnOptionC.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D89E00")));
                        btnOptionD.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#26890C")));

                        setupOptionsAndTimer(dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled, q, correct);
                    } catch (Exception e) {
                        onError(e.getMessage());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressKahootLoading.setVisibility(View.GONE);
                    layoutOptionsContainer.setVisibility(View.VISIBLE);
                    btnCloseKahoot.setVisibility(View.VISIBLE);
                    
                    dialogView.findViewById(R.id.layoutExplanation).setVisibility(View.GONE);
                    dialogView.findViewById(R.id.layoutNextActions).setVisibility(View.GONE);
                    
                    String fallbackQ = "What is the main objective of " + course.getTitle() + "?";
                    tvKahootQuestion.setText(fallbackQ);
                    btnOptionA.setText("▲ Building scalable applications");
                    btnOptionB.setText("◆ Deleting database tables");
                    btnOptionC.setText("● Formatting text documents");
                    btnOptionD.setText("■ Disconnecting network routers");
                    
                    btnOptionA.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E21B3C")));
                    btnOptionB.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#1368CE")));
                    btnOptionC.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#D89E00")));
                    btnOptionD.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#26890C")));

                    setupOptionsAndTimer(dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled, fallbackQ, "A");
                });
            }
        });
    }

    private void setupOptionsAndTimer(View dialogView, CourseModel course, int[] currentQuestion, int[] correctAnswers, int maxQuestions, boolean isTimerEnabled, String fullQuestionText, String correctLetter) {
        MaterialButton btnOptionA = dialogView.findViewById(R.id.btnOptionA);
        MaterialButton btnOptionB = dialogView.findViewById(R.id.btnOptionB);
        MaterialButton btnOptionC = dialogView.findViewById(R.id.btnOptionC);
        MaterialButton btnOptionD = dialogView.findViewById(R.id.btnOptionD);
        TextView tvKahootProgress = dialogView.findViewById(R.id.tvKahootProgress);
        
        btnOptionA.setEnabled(true);
        btnOptionB.setEnabled(true);
        btnOptionC.setEnabled(true);
        btnOptionD.setEnabled(true);

        View.OnClickListener listenerA = v -> processAnswer("A", correctLetter, dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled, fullQuestionText, btnOptionA.getText().toString());
        View.OnClickListener listenerB = v -> processAnswer("B", correctLetter, dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled, fullQuestionText, btnOptionB.getText().toString());
        View.OnClickListener listenerC = v -> processAnswer("C", correctLetter, dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled, fullQuestionText, btnOptionC.getText().toString());
        View.OnClickListener listenerD = v -> processAnswer("D", correctLetter, dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled, fullQuestionText, btnOptionD.getText().toString());

        btnOptionA.setOnClickListener(listenerA);
        btnOptionB.setOnClickListener(listenerB);
        btnOptionC.setOnClickListener(listenerC);
        btnOptionD.setOnClickListener(listenerD);

        if (isTimerEnabled) {
            kahootTimer = new CountDownTimer(15000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (tvKahootProgress != null) {
                        tvKahootProgress.setText("Question " + currentQuestion[0] + " / " + maxQuestions + " (" + (millisUntilFinished / 1000) + "s)");
                    }
                }
                @Override
                public void onFinish() {
                    Toast.makeText(getContext(), "⏰ Time's up!", Toast.LENGTH_SHORT).show();
                    processAnswer("TIMEOUT", correctLetter, dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled, fullQuestionText, "Timeout");
                }
            }.start();
        }
    }

    private void processAnswer(String userSelectedLetter, String correctLetter, View dialogView, CourseModel course, int[] currentQuestion, int[] correctAnswers, int maxQuestions, boolean isTimerEnabled, String questionText, String userAnswerStr) {
        if (kahootTimer != null) kahootTimer.cancel();
        
        MaterialButton btnOptionA = dialogView.findViewById(R.id.btnOptionA);
        MaterialButton btnOptionB = dialogView.findViewById(R.id.btnOptionB);
        MaterialButton btnOptionC = dialogView.findViewById(R.id.btnOptionC);
        MaterialButton btnOptionD = dialogView.findViewById(R.id.btnOptionD);
        
        btnOptionA.setEnabled(false);
        btnOptionB.setEnabled(false);
        btnOptionC.setEnabled(false);
        btnOptionD.setEnabled(false);
        
        int greenColor = androidx.core.content.ContextCompat.getColor(getContext(), R.color.success);
        int redColor = androidx.core.content.ContextCompat.getColor(getContext(), R.color.error);
        
        if (correctLetter.equals("A")) btnOptionA.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greenColor));
        if (correctLetter.equals("B")) btnOptionB.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greenColor));
        if (correctLetter.equals("C")) btnOptionC.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greenColor));
        if (correctLetter.equals("D")) btnOptionD.setBackgroundTintList(android.content.res.ColorStateList.valueOf(greenColor));
        
        boolean isCorrect = userSelectedLetter.equals(correctLetter);
        
        if (!isCorrect && !userSelectedLetter.equals("TIMEOUT")) {
            if (userSelectedLetter.equals("A")) btnOptionA.setBackgroundTintList(android.content.res.ColorStateList.valueOf(redColor));
            if (userSelectedLetter.equals("B")) btnOptionB.setBackgroundTintList(android.content.res.ColorStateList.valueOf(redColor));
            if (userSelectedLetter.equals("C")) btnOptionC.setBackgroundTintList(android.content.res.ColorStateList.valueOf(redColor));
            if (userSelectedLetter.equals("D")) btnOptionD.setBackgroundTintList(android.content.res.ColorStateList.valueOf(redColor));
        }

        int xpAmount;
        if (isCorrect) {
            kahootScore += 1000;
            correctAnswers[0]++;
            xpAmount = GamificationManager.XP_QUIZ_CORRECT;
        } else {
            xpAmount = GamificationManager.XP_QUIZ_WRONG;
        }
        
        TextView tvKahootScore = dialogView.findViewById(R.id.tvKahootScore);
        if (tvKahootScore != null) tvKahootScore.setText("Score: " + kahootScore + " Pts");

        if (userId != -1 && getContext() != null) {
            String fullCorrectStr = "A";
            if(correctLetter.equals("A")) fullCorrectStr = btnOptionA.getText().toString();
            else if(correctLetter.equals("B")) fullCorrectStr = btnOptionB.getText().toString();
            else if(correctLetter.equals("C")) fullCorrectStr = btnOptionC.getText().toString();
            else if(correctLetter.equals("D")) fullCorrectStr = btnOptionD.getText().toString();
            
            KahootHistoryModel history = new KahootHistoryModel(
                    userId,
                    course != null ? course.getId() : 0,
                    course != null ? course.getTitle() : "Unknown",
                    questionText,
                    fullCorrectStr,
                    userAnswerStr,
                    isCorrect,
                    System.currentTimeMillis()
            );
            kahootRepository.insertKahootHistory(history);
            courseRepository.recalculateProgressForCourse(userId, course.getId(), course.getCode());

            GamificationManager.awardXP(getContext(), userId, xpAmount);
            statsRepository.insertQuizResult(userId, history.getCourseId(), history.getCourseTitle(), 1, isCorrect ? 1 : 0, xpAmount);
            int totalQuizzes = statsRepository.getTotalQuizCount(userId);
            GamificationManager.checkQuizBadges(getContext(), userId, totalQuizzes, isCorrect);
        }
        
        LinearLayout layoutNextActions = dialogView.findViewById(R.id.layoutNextActions);
        MaterialButton btnNextQuestion = dialogView.findViewById(R.id.btnNextQuestion);
        MaterialButton btnExplainWithAi = dialogView.findViewById(R.id.btnExplainWithAi);
        
        layoutNextActions.setVisibility(View.VISIBLE);
        
        if (!isCorrect) {
            btnExplainWithAi.setVisibility(View.VISIBLE);
            btnExplainWithAi.setOnClickListener(v -> {
                btnExplainWithAi.setEnabled(false);
                LinearLayout layoutExplanation = dialogView.findViewById(R.id.layoutExplanation);
                TextView tvAiExplanation = dialogView.findViewById(R.id.tvAiExplanation);
                ProgressBar progressExplanation = dialogView.findViewById(R.id.progressExplanation);
                
                layoutExplanation.setVisibility(View.VISIBLE);
                progressExplanation.setVisibility(View.VISIBLE);
                tvAiExplanation.setText("");
                
                String fullCorrectStr = "A";
                if(correctLetter.equals("A")) fullCorrectStr = btnOptionA.getText().toString();
                else if(correctLetter.equals("B")) fullCorrectStr = btnOptionB.getText().toString();
                else if(correctLetter.equals("C")) fullCorrectStr = btnOptionC.getText().toString();
                else if(correctLetter.equals("D")) fullCorrectStr = btnOptionD.getText().toString();
                
                String prompt = "Explain briefly why the correct answer to this question is: " + fullCorrectStr + 
                                "\nQuestion: " + questionText + 
                                "\nThe user chose: " + userAnswerStr + 
                                "\nKeep it very short (1-2 sentences) and helpful.";
                                
                AiMentorService.sendMessageToAi("llama-3.1-8b-instant", "University", "Short", null, prompt, new AiMentorService.AiResponseCallback() {
                    @Override
                    public void onSuccess(String aiReply) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            progressExplanation.setVisibility(View.GONE);
                            tvAiExplanation.setText(aiReply);
                        });
                    }
                    @Override
                    public void onError(String errorMessage) {
                        if (getActivity() == null) return;
                        getActivity().runOnUiThread(() -> {
                            progressExplanation.setVisibility(View.GONE);
                            tvAiExplanation.setText("Could not load AI explanation. Please try again later.");
                            btnExplainWithAi.setEnabled(true);
                        });
                    }
                });
            });
        } else {
            btnExplainWithAi.setVisibility(View.GONE);
        }
        
        btnNextQuestion.setOnClickListener(v -> {
            currentQuestion[0]++;
            fetchNextKahootQuestion(dialogView, course, currentQuestion, correctAnswers, maxQuestions, isTimerEnabled);
        });
    }

    @Override
    public void onCourseDelete(final int courseId, final int position) {
        if (getContext() == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Course")
                .setMessage("Are you sure you want to delete this course from your catalog?")
                .setPositiveButton("Delete", (dialog, which) -> performCourseDeletion(courseId, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performCourseDeletion(int courseId, int position) {
        int result = courseRepository.deleteCourse(courseId);
        if (result > 0) {
            loadCourses();
            Toast.makeText(getContext(), "Course deleted successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Failed to delete course.", Toast.LENGTH_SHORT).show();
        }
    }
}