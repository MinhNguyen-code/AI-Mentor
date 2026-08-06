package com.example.aimentor.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.aimentor.databases.SqliteDbHelper;
import com.example.aimentor.models.CourseModel;
import com.example.aimentor.models.UserCourseModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.List;

public class CourseRepository extends SqliteDbHelper {

    public CourseRepository(@Nullable Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<CourseModel> getAllCourses() {
        List<CourseModel> coursesList = new ArrayList<>();
        String[] cols = {ID_COURSE, CODE_COURSE, TITLE_COURSE, CREDITS_COURSE, DESC_COURSE, TOTAL_QUESTIONS_COURSE};
        
        SQLiteDatabase db = this.getReadableDatabase();
        // Query courses sorted by Course Code
        Cursor cursor = db.query(TABLE_COURSES, cols, null, null, null, null, CODE_COURSE + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                CourseModel course = new CourseModel();
                course.setId(cursor.getInt(cursor.getColumnIndex(ID_COURSE)));
                course.setCode(cursor.getString(cursor.getColumnIndex(CODE_COURSE)));
                course.setTitle(cursor.getString(cursor.getColumnIndex(TITLE_COURSE)));
                course.setCredits(cursor.getInt(cursor.getColumnIndex(CREDITS_COURSE)));
                course.setDescription(cursor.getString(cursor.getColumnIndex(DESC_COURSE)));
                course.setTotalQuestions(cursor.getInt(cursor.getColumnIndex(TOTAL_QUESTIONS_COURSE)));
                coursesList.add(course);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return coursesList;
    }

    public long saveCourse(String code, String title, int credits, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CODE_COURSE, code);
        values.put(TITLE_COURSE, title);
        values.put(CREDITS_COURSE, credits);
        values.put(DESC_COURSE, description);
        
        long result = db.insert(TABLE_COURSES, null, values);
        db.close();
        return result;
    }

    public int deleteCourse(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_COURSES, ID_COURSE + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    // --- Progress Tracking Methods ---

    public long enrollUserInCourse(int userId, String code, String title, String explanationStyle) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USER_ID_UC, userId);
        values.put(COURSE_CODE_UC, code);
        values.put(COURSE_TITLE_UC, title);
        values.put(EXPLANATION_STYLE_UC, explanationStyle);
        values.put(PROGRESS_PERCENT_UC, 0);
        values.put(STATUS_UC, "Learning");
        values.put(QUESTIONS_ASKED_UC, 0);
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put(CREATED_AT_UC, timestamp);

        long result = db.insert(TABLE_USER_COURSES, null, values);
        db.close();
        return result;
    }

    @SuppressLint("Range")
    public List<UserCourseModel> getUserEnrolledCourses(int userId) {
        List<UserCourseModel> userCourses = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER_COURSES, null, USER_ID_UC + " = ?", 
                                 new String[]{String.valueOf(userId)}, null, null, ID_USER_COURSE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                UserCourseModel uc = new UserCourseModel();
                uc.setId(cursor.getInt(cursor.getColumnIndex(ID_USER_COURSE)));
                uc.setUserId(cursor.getInt(cursor.getColumnIndex(USER_ID_UC)));
                uc.setCourseCode(cursor.getString(cursor.getColumnIndex(COURSE_CODE_UC)));
                uc.setCourseTitle(cursor.getString(cursor.getColumnIndex(COURSE_TITLE_UC)));
                uc.setExplanationStyle(cursor.getString(cursor.getColumnIndex(EXPLANATION_STYLE_UC)));
                uc.setProgressPercent(cursor.getInt(cursor.getColumnIndex(PROGRESS_PERCENT_UC)));
                uc.setStatus(cursor.getString(cursor.getColumnIndex(STATUS_UC)));
                uc.setQuestionsAsked(cursor.getInt(cursor.getColumnIndex(QUESTIONS_ASKED_UC)));
                uc.setCreatedAt(cursor.getString(cursor.getColumnIndex(CREATED_AT_UC)));
                userCourses.add(uc);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userCourses;
    }

    public void incrementCourseQuestions(int userCourseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_USER_COURSES + " SET " + QUESTIONS_ASKED_UC + " = " + QUESTIONS_ASKED_UC + " + 1 WHERE " + ID_USER_COURSE + " = " + userCourseId);
        db.close();
    }

    public void updateCourseProgress(int userCourseId, int percent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROGRESS_PERCENT_UC, percent);
        if (percent >= 100) {
            values.put(STATUS_UC, "Completed");
        } else {
            values.put(STATUS_UC, "Learning");
        }
        db.update(TABLE_USER_COURSES, values, ID_USER_COURSE + " = ?", new String[]{String.valueOf(userCourseId)});
        db.close();
    }

    public void recalculateProgressForCourse(int userId, int courseId, String courseCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // 1. Get total questions for this course
        int totalQuestions = 100;
        Cursor cCourse = db.rawQuery("SELECT " + TOTAL_QUESTIONS_COURSE + " FROM " + TABLE_COURSES + " WHERE " + ID_COURSE + "=?", new String[]{String.valueOf(courseId)});
        if (cCourse.moveToFirst()) {
            totalQuestions = cCourse.getInt(0);
        }
        cCourse.close();
        if (totalQuestions <= 0) totalQuestions = 1;

        // 2. Count correct kahoot answers for this course and user
        int correctCount = 0;
        Cursor cKahoot = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_KAHOOT_HISTORY + " WHERE " + USER_ID_KH + "=? AND " + COURSE_ID_KH + "=? AND " + IS_CORRECT_KH + "=1", new String[]{String.valueOf(userId), String.valueOf(courseId)});
        if (cKahoot.moveToFirst()) {
            correctCount = cKahoot.getInt(0);
        }
        cKahoot.close();

        // 3. Calculate percentage
        int percent = (int) (((float) correctCount / totalQuestions) * 100);
        if (percent > 100) percent = 100;

        // 4. Update UserCourse
        ContentValues values = new ContentValues();
        values.put(PROGRESS_PERCENT_UC, percent);
        if (percent >= 100) {
            values.put(STATUS_UC, "Completed");
        } else {
            values.put(STATUS_UC, "Learning");
        }
        db.update(TABLE_USER_COURSES, values, USER_ID_UC + "=? AND " + COURSE_CODE_UC + "=?", new String[]{String.valueOf(userId), courseCode});
        db.close();
    }
}
