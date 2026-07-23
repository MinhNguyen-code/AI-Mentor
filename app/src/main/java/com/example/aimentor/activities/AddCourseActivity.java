package com.example.aimentor.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aimentor.R;
import com.example.aimentor.repository.CourseRepository;

public class AddCourseActivity extends AppCompatActivity {

    private EditText edtCourseCode, edtCourseTitle, edtCourseCredits, edtCourseDesc;
    private Button btnSaveCourse, btnBackCourse;
    private CourseRepository courseRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        // Bind form elements
        edtCourseCode = findViewById(R.id.edtCourseCode);
        edtCourseTitle = findViewById(R.id.edtCourseTitle);
        edtCourseCredits = findViewById(R.id.edtCourseCredits);
        edtCourseDesc = findViewById(R.id.edtCourseDesc);
        btnSaveCourse = findViewById(R.id.btnSaveCourse);
        btnBackCourse = findViewById(R.id.btnBackCourse);

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
        String code = edtCourseCode.getText().toString().trim().toUpperCase();
        String title = edtCourseTitle.getText().toString().trim();
        String creditsStr = edtCourseCredits.getText().toString().trim();
        String desc = edtCourseDesc.getText().toString().trim();

        // Inputs Validation
        if (TextUtils.isEmpty(code)) {
            edtCourseCode.setError("Course code is required");
            return;
        }
        if (TextUtils.isEmpty(title)) {
            edtCourseTitle.setError("Course title is required");
            return;
        }

        int credits = 15; // default HND Hons credits
        if (!TextUtils.isEmpty(creditsStr)) {
            try {
                credits = Integer.parseInt(creditsStr);
            } catch (NumberFormatException e) {
                edtCourseCredits.setError("Credits must be a valid number");
                return;
            }
        }

        // Save to SQLite
        long result = courseRepository.saveCourse(code, title, credits, desc);
        if (result == -1) {
            Toast.makeText(AddCourseActivity.this, "Save failed. Code might already exist.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddCourseActivity.this, "Course saved successfully!", Toast.LENGTH_SHORT).show();
            finish(); // return
        }
    }
}
