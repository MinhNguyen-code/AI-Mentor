package com.example.aimentor.activities;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.aimentor.R;
import com.example.aimentor.models.UserCourseModel;
import com.example.aimentor.repository.CourseRepository;
import com.google.android.material.button.MaterialButton;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CourseProgressActivity extends AppCompatActivity {

    private Toolbar toolbarProgress;
    private TextView tvCourseCode, tvCourseTitle, tvProgressPercent, tvQuestionsAsked, tvExplanationStyle, tvAiAnalysis, tvScoreDetail;
    private ProgressBar pbCourseProgress;
    private MaterialButton btnStudyNow;
    
    private CourseRepository courseRepository;
    private int userCourseId = -1;
    private UserCourseModel currentCourse = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.example.aimentor.utils.ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_progress);

        userCourseId = getIntent().getIntExtra("USER_COURSE_ID", -1);

        toolbarProgress = findViewById(R.id.toolbarProgress);
        setSupportActionBar(toolbarProgress);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbarProgress.setNavigationOnClickListener(v -> finish());

        tvCourseCode = findViewById(R.id.tvCourseCode);
        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        tvProgressPercent = findViewById(R.id.tvProgressPercent);
        tvQuestionsAsked = findViewById(R.id.tvQuestionsAsked);
        tvExplanationStyle = findViewById(R.id.tvExplanationStyle);
        tvAiAnalysis = findViewById(R.id.tvAiAnalysis);
        pbCourseProgress = findViewById(R.id.pbCourseProgress);
        btnStudyNow = findViewById(R.id.btnStudyNow);
        tvScoreDetail = findViewById(R.id.tvScoreDetail);

        courseRepository = new CourseRepository(this);
        
        loadCourseData();

        btnStudyNow.setOnClickListener(v -> {
            Toast.makeText(this, "Resuming course: " + tvCourseTitle.getText(), Toast.LENGTH_SHORT).show();
            // In a real app, this might navigate to a specific study fragment or quiz for this course
            finish();
        });
    }

    private void loadCourseData() {
        if (userCourseId == -1) {
            Toast.makeText(this, "Course not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // We need to fetch the course by ID. Since we only have getUserEnrolledCourses, we can iterate
        int userId = getSharedPreferences("USER_INFO", MODE_PRIVATE).getInt("ID_USER", -1);
        List<UserCourseModel> enrolled = courseRepository.getUserEnrolledCourses(userId);
        
        for (UserCourseModel model : enrolled) {
            if (model.getId() == userCourseId) {
                currentCourse = model;
                break;
            }
        }

        if (currentCourse != null) {
            tvCourseCode.setText(currentCourse.getCourseCode());
            tvCourseTitle.setText(currentCourse.getCourseTitle());
            tvProgressPercent.setText(currentCourse.getProgressPercent() + "%");
            pbCourseProgress.setProgress(currentCourse.getProgressPercent());
            tvQuestionsAsked.setText(String.valueOf(currentCourse.getQuestionsAsked()));
            tvExplanationStyle.setText(currentCourse.getExplanationStyle());
            
            // Get accurate score details
            int courseId = -1;
            int totalQuestions = 100;
            int correctCount = 0;
            
            android.database.sqlite.SQLiteDatabase db = courseRepository.getReadableDatabase();
            
            // 1. Get Course ID and Total Questions
            android.database.Cursor cCourse = db.rawQuery("SELECT id, totalQuestions FROM courses WHERE code=?", new String[]{currentCourse.getCourseCode()});
            if (cCourse.moveToFirst()) {
                courseId = cCourse.getInt(0);
                totalQuestions = cCourse.getInt(1);
            }
            cCourse.close();
            
            if (totalQuestions <= 0) totalQuestions = 1;
            
            // 2. Get correct count
            if (courseId != -1) {
                android.database.Cursor cKahoot = db.rawQuery("SELECT COUNT(*) FROM kahoot_history WHERE userId=? AND courseId=? AND isCorrect=1", new String[]{String.valueOf(userId), String.valueOf(courseId)});
                if (cKahoot.moveToFirst()) {
                    correctCount = cKahoot.getInt(0);
                }
                cKahoot.close();
            }
            
            tvScoreDetail.setText(correctCount + " / " + totalQuestions + " Correct");
            
            generateSmartAnalysis();
        }
    }

    private void generateSmartAnalysis() {
        String prompt = "Act as an AI study mentor. I am studying " + currentCourse.getCourseTitle() 
            + " (" + currentCourse.getCourseCode() + "). "
            + "My progress is " + currentCourse.getProgressPercent() + "% and I have asked " 
            + currentCourse.getQuestionsAsked() + " questions using a " 
            + currentCourse.getExplanationStyle() + " explanation style. "
            + "Give me a 2-sentence encouraging analysis in English on my study habit for this course.";

        com.example.aimentor.services.AiMentorService.sendMessageToAi(
                com.example.aimentor.utils.AiConfig.getSelectedModel(this),
                "University",
                currentCourse.getExplanationStyle(),
                null,
                prompt,
                new com.example.aimentor.services.AiMentorService.AiResponseCallback() {
                    @Override
                    public void onSuccess(String aiReply) {
                        if (!isFinishing()) {
                            tvAiAnalysis.setText(aiReply);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        if (!isFinishing()) {
                            tvAiAnalysis.setText("Keep up the great work! You are doing very well in " + currentCourse.getCourseTitle() + ".");
                        }
                    }
                }
        );
    }
}
