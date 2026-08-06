package com.example.aimentor.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.aimentor.databases.SqliteDbHelper;
import com.example.aimentor.models.KahootHistoryModel;

import java.util.ArrayList;
import java.util.List;

public class KahootRepository {
    private SqliteDbHelper dbHelper;

    public KahootRepository(Context context) {
        dbHelper = new SqliteDbHelper(context);
    }

    public long insertKahootHistory(KahootHistoryModel history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.USER_ID_KH, history.getUserId());
        values.put(SqliteDbHelper.COURSE_ID_KH, history.getCourseId());
        values.put(SqliteDbHelper.COURSE_TITLE_KH, history.getCourseTitle());
        values.put(SqliteDbHelper.QUESTION_TEXT_KH, history.getQuestionText());
        values.put(SqliteDbHelper.CORRECT_ANSWER_KH, history.getCorrectAnswer());
        values.put(SqliteDbHelper.USER_ANSWER_KH, history.getUserAnswer());
        values.put(SqliteDbHelper.IS_CORRECT_KH, history.isCorrect() ? 1 : 0);
        values.put(SqliteDbHelper.TIMESTAMP_KH, history.getTimestamp());
        return db.insert(SqliteDbHelper.TABLE_KAHOOT_HISTORY, null, values);
    }

    public List<KahootHistoryModel> getKahootHistoryByUser(int userId) {
        List<KahootHistoryModel> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SqliteDbHelper.TABLE_KAHOOT_HISTORY, null, 
                SqliteDbHelper.USER_ID_KH + "=?", new String[]{String.valueOf(userId)}, 
                null, null, SqliteDbHelper.TIMESTAMP_KH + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                KahootHistoryModel history = new KahootHistoryModel();
                history.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_KH)));
                history.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.USER_ID_KH)));
                history.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.COURSE_ID_KH)));
                history.setCourseTitle(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.COURSE_TITLE_KH)));
                history.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.QUESTION_TEXT_KH)));
                history.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.CORRECT_ANSWER_KH)));
                history.setUserAnswer(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.USER_ANSWER_KH)));
                history.setCorrect(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.IS_CORRECT_KH)) == 1);
                history.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.TIMESTAMP_KH)));
                list.add(history);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public List<KahootHistoryModel> getKahootHistoryByCourse(int userId, int courseId) {
        List<KahootHistoryModel> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(SqliteDbHelper.TABLE_KAHOOT_HISTORY, null, 
                SqliteDbHelper.USER_ID_KH + "=? AND " + SqliteDbHelper.COURSE_ID_KH + "=?", 
                new String[]{String.valueOf(userId), String.valueOf(courseId)}, 
                null, null, SqliteDbHelper.TIMESTAMP_KH + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                KahootHistoryModel history = new KahootHistoryModel();
                history.setId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_KH)));
                history.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.USER_ID_KH)));
                history.setCourseId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.COURSE_ID_KH)));
                history.setCourseTitle(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.COURSE_TITLE_KH)));
                history.setQuestionText(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.QUESTION_TEXT_KH)));
                history.setCorrectAnswer(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.CORRECT_ANSWER_KH)));
                history.setUserAnswer(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.USER_ANSWER_KH)));
                history.setCorrect(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.IS_CORRECT_KH)) == 1);
                history.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.TIMESTAMP_KH)));
                list.add(history);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public int deleteKahootHistory(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(SqliteDbHelper.TABLE_KAHOOT_HISTORY, SqliteDbHelper.ID_KH + "=?", new String[]{String.valueOf(id)});
    }
}
