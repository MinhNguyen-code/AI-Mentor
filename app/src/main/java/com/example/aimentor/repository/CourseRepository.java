package com.example.aimentor.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.aimentor.databases.SqliteDbHelper;
import com.example.aimentor.models.CourseModel;

import java.util.ArrayList;
import java.util.List;

public class CourseRepository extends SqliteDbHelper {

    public CourseRepository(@Nullable Context context) {
        super(context);
    }

    @SuppressLint("Range")
    public List<CourseModel> getAllCourses() {
        List<CourseModel> coursesList = new ArrayList<>();
        String[] cols = {ID_COURSE, CODE_COURSE, TITLE_COURSE, CREDITS_COURSE, DESC_COURSE};
        
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
}
