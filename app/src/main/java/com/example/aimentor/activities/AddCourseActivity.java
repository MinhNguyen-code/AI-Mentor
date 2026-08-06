package com.example.aimentor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aimentor.R;
import com.example.aimentor.repository.CourseRepository;

public class AddCourseActivity extends AppCompatActivity {

    private EditText edtCourseTitle, edtCourseDesc;
    private RadioGroup rgExplanationStyle;
    private Button btnSaveCourse, btnBackCourse;
    private CourseRepository courseRepository;
    private int currentUserId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.example.aimentor.utils.ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        // Bind form elements
        edtCourseTitle = findViewById(R.id.edtCourseTitle);
        edtCourseDesc = findViewById(R.id.edtCourseDesc);
        rgExplanationStyle = findViewById(R.id.rgExplanationStyle);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);
        btnBackCourse = findViewById(R.id.btnBackCourse);

        SharedPreferences prefs = getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("ID_USER", -1);

        courseRepository = new CourseRepository(AddCourseActivity.this);

        // Set action listeners
        btnBackCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close activity and return to list
            }
        });

        btnSaveCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewCourse();
            }
        });
    }

    private void saveNewCourse() {
        String title = edtCourseTitle.getText().toString().trim();
        String desc = edtCourseDesc.getText().toString().trim();

        // Inputs Validation
        if (TextUtils.isEmpty(title)) {
            edtCourseTitle.setError("Course title is required");
            return;
        }

        // Generate Code automatically since we removed it from UI
        String code = "C" + System.currentTimeMillis();
        int credits = 15; // default HND Hons credits

        // Save to SQLite
        long result = courseRepository.saveCourse(code, title, credits, desc);
        if (result == -1) {
            Toast.makeText(AddCourseActivity.this, "Save failed. Code might already exist.", Toast.LENGTH_SHORT).show();
        } else {
            // Enroll User with specific Explanation Style
            if (currentUserId != -1) {
                String explanationStyle = "Step-by-Step";
                int checkedId = rgExplanationStyle.getCheckedRadioButtonId();
                if (checkedId == R.id.rbStyleShort) {
                    explanationStyle = "Short";
                } else if (checkedId == R.id.rbStyleDetailed) {
                    explanationStyle = "Detailed";
                }
                courseRepository.enrollUserInCourse(currentUserId, code, title, explanationStyle);
            }

            Toast.makeText(AddCourseActivity.this, "Course saved and enrolled successfully!", Toast.LENGTH_SHORT).show();
            finish(); // return
        }
    }
}
