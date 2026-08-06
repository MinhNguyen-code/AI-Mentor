package com.example.aimentor.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.aimentor.databases.SqliteDbHelper;
import com.example.aimentor.models.ChatMessageModel;

import java.util.ArrayList;
import java.util.List;

public class ChatRepository {
    private final SqliteDbHelper dbHelper;

    public ChatRepository(Context context) {
        this.dbHelper = new SqliteDbHelper(context);
    }

    public long insertChatMessage(int userId, String message, boolean isUser, String modelUsed) {
        return insertChatMessage(userId, message, isUser, modelUsed, null);
    }

    public long insertChatMessage(int userId, String message, boolean isUser, String modelUsed, String imageUri) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.USER_ID_CHAT, userId);
        values.put(SqliteDbHelper.MESSAGE_CHAT, message);
        values.put(SqliteDbHelper.IS_USER_CHAT, isUser ? 1 : 0);
        values.put(SqliteDbHelper.MODEL_USED_CHAT, modelUsed);
        values.put(SqliteDbHelper.IS_BOOKMARKED_CHAT, 0);
        values.put(SqliteDbHelper.IMAGE_URI_CHAT, imageUri);
        values.put(SqliteDbHelper.TIMESTAMP_CHAT, System.currentTimeMillis());

        long result = db.insert(SqliteDbHelper.TABLE_CHAT_HISTORY, null, values);
        db.close();
        return result;
    }

    public List<ChatMessageModel> getChatHistory(int userId) {
        List<ChatMessageModel> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                SqliteDbHelper.TABLE_CHAT_HISTORY,
                null,
                SqliteDbHelper.USER_ID_CHAT + " = ?",
                new String[]{String.valueOf(userId)},
                null, null,
                SqliteDbHelper.TIMESTAMP_CHAT + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ChatMessageModel msg = new ChatMessageModel();
                msg.setId(cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_CHAT)));
                msg.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.USER_ID_CHAT)));
                msg.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.MESSAGE_CHAT)));
                msg.setUser(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.IS_USER_CHAT)) == 1);
                msg.setModelUsed(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.MODEL_USED_CHAT)));
                msg.setBookmarked(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.IS_BOOKMARKED_CHAT)) == 1);
                
                int imageUriIndex = cursor.getColumnIndex(SqliteDbHelper.IMAGE_URI_CHAT);
                if (imageUriIndex != -1 && !cursor.isNull(imageUriIndex)) {
                    msg.setImageUri(cursor.getString(imageUriIndex));
                }
                
                msg.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.TIMESTAMP_CHAT)));
                list.add(msg);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    public List<ChatMessageModel> searchChatHistory(int userId, String query) {
        List<ChatMessageModel> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                SqliteDbHelper.TABLE_CHAT_HISTORY,
                null,
                SqliteDbHelper.USER_ID_CHAT + " = ? AND " + SqliteDbHelper.MESSAGE_CHAT + " LIKE ?",
                new String[]{String.valueOf(userId), "%" + query + "%"},
                null, null,
                SqliteDbHelper.TIMESTAMP_CHAT + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ChatMessageModel msg = new ChatMessageModel();
                msg.setId(cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.ID_CHAT)));
                msg.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.USER_ID_CHAT)));
                msg.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.MESSAGE_CHAT)));
                msg.setUser(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.IS_USER_CHAT)) == 1);
                msg.setModelUsed(cursor.getString(cursor.getColumnIndexOrThrow(SqliteDbHelper.MODEL_USED_CHAT)));
                msg.setBookmarked(cursor.getInt(cursor.getColumnIndexOrThrow(SqliteDbHelper.IS_BOOKMARKED_CHAT)) == 1);
                
                int imageUriIndex = cursor.getColumnIndex(SqliteDbHelper.IMAGE_URI_CHAT);
                if (imageUriIndex != -1 && !cursor.isNull(imageUriIndex)) {
                    msg.setImageUri(cursor.getString(imageUriIndex));
                }
                
                msg.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(SqliteDbHelper.TIMESTAMP_CHAT)));
                list.add(msg);
            }
            cursor.close();
        }
        db.close();
        return list;
    }

    public boolean toggleBookmark(long id, boolean isBookmarked) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SqliteDbHelper.IS_BOOKMARKED_CHAT, isBookmarked ? 1 : 0);

        int rows = db.update(
                SqliteDbHelper.TABLE_CHAT_HISTORY,
                values,
                SqliteDbHelper.ID_CHAT + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return rows > 0;
    }

    public int getQuestionCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + SqliteDbHelper.TABLE_CHAT_HISTORY +
                        " WHERE " + SqliteDbHelper.USER_ID_CHAT + " = ? AND " + SqliteDbHelper.IS_USER_CHAT + " = 1",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count;
    }

    public int getBookmarkCount(int userId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + SqliteDbHelper.TABLE_CHAT_HISTORY +
                        " WHERE " + SqliteDbHelper.USER_ID_CHAT + " = ? AND " + SqliteDbHelper.IS_BOOKMARKED_CHAT + " = 1",
                new String[]{String.valueOf(userId)}
        );
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        db.close();
        return count;
    }

    public void clearChatHistory(int userId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(
                SqliteDbHelper.TABLE_CHAT_HISTORY,
                SqliteDbHelper.USER_ID_CHAT + " = ?",
                new String[]{String.valueOf(userId)}
        );
        db.close();
    }
}
