package com.example.aimentor.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.aimentor.databases.SqliteDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository for quiz results and learning statistics.
 */
public class StatsRepository {
    private final SqliteDbHelper dbHelper;

    public StatsRepository(Context context) {
        this.dbHelper = new SqliteDbHelper(context);
    }

    /**
     * Save a quiz result to the database.
     */
    public long insertQuizResult(int userId, int courseId, String courseTitle,
                                  int totalQuestions, int correctAnswers, int xpEarned) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.USER_ID_QUIZ, userId);
        values.put(SqliteDbHelper.COURSE_ID_QUIZ, courseId);
        values.put(SqliteDbHelper.COURSE_TITLE_QUIZ, courseTitle);
        values.put(SqliteDbHelper.TOTAL_QUESTIONS_QUIZ, totalQuestions);
        values.put(SqliteDbHelper.CORRECT_ANSWERS_QUIZ, correctAnswers);
        values.put(SqliteDbHelper.XP_EARNED_QUIZ, xpEarned);
        values.put(SqliteDbHelper.TIMESTAMP_QUIZ, System.currentTimeMillis());
        long result = db.insert(SqliteDbHelper.TABLE_QUIZ_RESULTS, null, values);
        db.close();
        return result;
    }

    /**
     * Get total number of quizzes taken by user.
     */
    public int getTotalQuizCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + SqliteDbHelper.TABLE_QUIZ_RESULTS +
                " WHERE " + SqliteDbHelper.USER_ID_QUIZ + " = ?",
                new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return count;
    }

    /**
     * Get overall quiz accuracy percentage (0-100).
     */
    public int getOverallQuizAccuracy(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + SqliteDbHelper.CORRECT_ANSWERS_QUIZ + "), SUM(" + SqliteDbHelper.TOTAL_QUESTIONS_QUIZ + ")" +
                " FROM " + SqliteDbHelper.TABLE_QUIZ_RESULTS +
                " WHERE " + SqliteDbHelper.USER_ID_QUIZ + " = ?",
                new String[]{String.valueOf(userId)});
        int accuracy = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int totalCorrect = cursor.getInt(0);
            int totalQuestions = cursor.getInt(1);
            if (totalQuestions > 0) {
                accuracy = (int) ((totalCorrect / (float) totalQuestions) * 100);
            }
            cursor.close();
        }
        db.close();
        return accuracy;
    }

    /**
     * Get quiz accuracy by course title.
     * @return Map of courseTitle -> accuracy percentage
     */
    public Map<String, Integer> getQuizAccuracyByCourse(int userId) {
        Map<String, Integer> result = new HashMap<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + SqliteDbHelper.COURSE_TITLE_QUIZ +
                ", SUM(" + SqliteDbHelper.CORRECT_ANSWERS_QUIZ + "), SUM(" + SqliteDbHelper.TOTAL_QUESTIONS_QUIZ + ")" +
                " FROM " + SqliteDbHelper.TABLE_QUIZ_RESULTS +
                " WHERE " + SqliteDbHelper.USER_ID_QUIZ + " = ?" +
                " GROUP BY " + SqliteDbHelper.COURSE_TITLE_QUIZ,
                new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String title = cursor.getString(0);
                int correct = cursor.getInt(1);
                int total = cursor.getInt(2);
                int accuracy = total > 0 ? (int) ((correct / (float) total) * 100) : 0;
                result.put(title, accuracy);
            }
            cursor.close();
        }
        db.close();
        return result;
    }

    /**
     * Get the most studied courses (by number of quizzes taken).
     * @return List of maps with "title" and "count" keys, sorted descending
     */
    public List<Map<String, Object>> getMostStudiedCourses(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + SqliteDbHelper.COURSE_TITLE_QUIZ +
                ", COUNT(*) as quiz_count" +
                " FROM " + SqliteDbHelper.TABLE_QUIZ_RESULTS +
                " WHERE " + SqliteDbHelper.USER_ID_QUIZ + " = ?" +
                " GROUP BY " + SqliteDbHelper.COURSE_TITLE_QUIZ +
                " ORDER BY quiz_count DESC" +
                " LIMIT 5",
                new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> item = new HashMap<>();
                item.put("title", cursor.getString(0));
                item.put("count", cursor.getInt(1));
                result.add(item);
            }
            cursor.close();
        }
        db.close();
        return result;
    }

    /**
     * Get total XP earned from quizzes.
     */
    public int getTotalQuizXP(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + SqliteDbHelper.XP_EARNED_QUIZ + ")" +
                " FROM " + SqliteDbHelper.TABLE_QUIZ_RESULTS +
                " WHERE " + SqliteDbHelper.USER_ID_QUIZ + " = ?",
                new String[]{String.valueOf(userId)});
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return total;
    }

    /**
     * Get the count of correct answers total.
     */
    public int getTotalCorrectAnswers(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + SqliteDbHelper.CORRECT_ANSWERS_QUIZ + ")" +
                " FROM " + SqliteDbHelper.TABLE_QUIZ_RESULTS +
                " WHERE " + SqliteDbHelper.USER_ID_QUIZ + " = ?",
                new String[]{String.valueOf(userId)});
        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return total;
    }

    /**
     * Get recent quiz results for display (last 10).
     */
    public List<Map<String, Object>> getRecentQuizResults(int userId) {
        List<Map<String, Object>> results = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + SqliteDbHelper.COURSE_TITLE_QUIZ + ", " +
                SqliteDbHelper.CORRECT_ANSWERS_QUIZ + ", " +
                SqliteDbHelper.TOTAL_QUESTIONS_QUIZ + ", " +
                SqliteDbHelper.XP_EARNED_QUIZ + ", " +
                SqliteDbHelper.TIMESTAMP_QUIZ +
                " FROM " + SqliteDbHelper.TABLE_QUIZ_RESULTS +
                " WHERE " + SqliteDbHelper.USER_ID_QUIZ + " = ?" +
                " ORDER BY " + SqliteDbHelper.TIMESTAMP_QUIZ + " DESC" +
                " LIMIT 10",
                new String[]{String.valueOf(userId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Map<String, Object> item = new HashMap<>();
                item.put("courseTitle", cursor.getString(0));
                item.put("correct", cursor.getInt(1));
                item.put("total", cursor.getInt(2));
                item.put("xp", cursor.getInt(3));
                item.put("timestamp", cursor.getLong(4));
                results.add(item);
            }
            cursor.close();
        }
        db.close();
        return results;
    }
}
