package com.example.aimentor.repository;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.aimentor.databases.SqliteDbHelper;
import com.example.aimentor.models.UserModel;
import com.example.aimentor.utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UserRepository extends SqliteDbHelper {
    public UserRepository(@Nullable Context context) {
        super(context);
    }
    
    private String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public long saveUserAccount(String username, String password, String email, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if username or email already exists
        Cursor cursor = db.query(TABLE_USERS, new String[]{ID_USER},
                USERNAME_USER + " =? OR " + EMAIL_USER + " =?",
                new String[]{username, email},
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            db.close();
            return -1; // User already exists
        }
        if (cursor != null) {
            cursor.close();
        }

        String currentDate = getCurrentDate();
        ContentValues values = new ContentValues();
        values.put(USERNAME_USER, username);
        values.put(PASSWORD_USER, PasswordUtils.hashPassword(password));
        values.put(EMAIL_USER, email);
        values.put(PHONE_USER, phone);
        values.put(ROLE_USER, 1);
        values.put(EDU_LEVEL_USER, "University");
        values.put(EXPLANATION_STYLE_USER, "Step-by-Step");
        values.put(SUBJECTS_USER, "Programming, Databases");
        values.put(CREATED_AT, currentDate);
        
        long insert = db.insert(TABLE_USERS, null, values);
        db.close();
        return insert;
    }

    @SuppressLint("Range")
    public UserModel loginUser(String username, String password){
        UserModel user = null;
        String[] cols = {ID_USER, USERNAME_USER, EMAIL_USER, PHONE_USER, ROLE_USER, AVATAR_USER, EDU_LEVEL_USER, EXPLANATION_STYLE_USER, SUBJECTS_USER, XP_USER, LEVEL_USER};
        String condition = USERNAME_USER + " =? AND " + PASSWORD_USER + " =? ";
        String[] params = { username, PasswordUtils.hashPassword(password) };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.query(TABLE_USERS, cols, condition, params, null, null, null);
        if (data.getCount() > 0){
            data.moveToFirst();
            user = new UserModel();
            user.setId(data.getInt(data.getColumnIndex(ID_USER)));
            user.setUsername(data.getString(data.getColumnIndex(USERNAME_USER)));
            user.setEmail(data.getString(data.getColumnIndex(EMAIL_USER)));
            user.setPhone(data.getString(data.getColumnIndex(PHONE_USER)));
            user.setRole(data.getInt(data.getColumnIndex(ROLE_USER)));
            user.setAvatar(data.getString(data.getColumnIndex(AVATAR_USER)));
            user.setEducationLevel(data.getString(data.getColumnIndex(EDU_LEVEL_USER)));
            user.setExplanationStyle(data.getString(data.getColumnIndex(EXPLANATION_STYLE_USER)));
            user.setSubjects(data.getString(data.getColumnIndex(SUBJECTS_USER)));
            user.setXp(data.getInt(data.getColumnIndex(XP_USER)));
            user.setLevel(data.getInt(data.getColumnIndex(LEVEL_USER)));
        }
        data.close();
        db.close();
        return user;
    }

    @SuppressLint("Range")
    public UserModel getUserById(int id){
        UserModel user = null;
        String[] cols = {ID_USER, USERNAME_USER, EMAIL_USER, PHONE_USER, ROLE_USER, AVATAR_USER, EDU_LEVEL_USER, EXPLANATION_STYLE_USER, SUBJECTS_USER, CREATED_AT, XP_USER, LEVEL_USER};
        String condition = ID_USER + " =? ";
        String[] params = { String.valueOf(id) };
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.query(TABLE_USERS, cols, condition, params, null, null, null);
        if (data.getCount() > 0){
            data.moveToFirst();
            user = new UserModel();
            user.setId(data.getInt(data.getColumnIndex(ID_USER)));
            user.setUsername(data.getString(data.getColumnIndex(USERNAME_USER)));
            user.setEmail(data.getString(data.getColumnIndex(EMAIL_USER)));
            user.setPhone(data.getString(data.getColumnIndex(PHONE_USER)));
            user.setRole(data.getInt(data.getColumnIndex(ROLE_USER)));
            user.setAvatar(data.getString(data.getColumnIndex(AVATAR_USER)));
            user.setEducationLevel(data.getString(data.getColumnIndex(EDU_LEVEL_USER)));
            user.setExplanationStyle(data.getString(data.getColumnIndex(EXPLANATION_STYLE_USER)));
            user.setSubjects(data.getString(data.getColumnIndex(SUBJECTS_USER)));
            user.setCreatedAt(data.getString(data.getColumnIndex(CREATED_AT)));
            user.setXp(data.getInt(data.getColumnIndex(XP_USER)));
            user.setLevel(data.getInt(data.getColumnIndex(LEVEL_USER)));
        }
        data.close();
        db.close();
        return user;
    }

    public boolean validatePassword(int id, String rawPassword) {
        if (rawPassword == null) return false;
        String hashedPassword = PasswordUtils.hashPassword(rawPassword);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{ID_USER},
                ID_USER + " =? AND " + PASSWORD_USER + " =?",
                new String[]{String.valueOf(id), hashedPassword},
                null, null, null);
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public boolean resetPassword(String username, String email, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD_USER, PasswordUtils.hashPassword(newPassword));
        values.put(UPDATED_AT, getCurrentDate());

        int rowsAffected = db.update(TABLE_USERS, values, 
            USERNAME_USER + " = ? AND " + EMAIL_USER + " = ?", 
            new String[]{username, email});
            
        db.close();
        return rowsAffected > 0;
    }

    public long updateUserFullProfile(int id, String username, String email, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_USER, username);
        values.put(EMAIL_USER, email);
        values.put(PHONE_USER, phone);
        values.put(UPDATED_AT, getCurrentDate());

        long result = db.update(TABLE_USERS, values, ID_USER + " =? ", new String[]{String.valueOf(id)});
        db.close();
        return result;
    }

    public long updateUserProfile(int id, String email, String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EMAIL_USER, email);
        values.put(PHONE_USER, phone);
        values.put(UPDATED_AT, getCurrentDate());

        long result = db.update(TABLE_USERS, values, ID_USER + " =? ", new String[]{ String.valueOf(id) });
        db.close();
        return result;
    }

    public long updateUserPreferences(int id, String educationLevel, String explanationStyle, String subjects){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EDU_LEVEL_USER, educationLevel);
        values.put(EXPLANATION_STYLE_USER, explanationStyle);
        values.put(SUBJECTS_USER, subjects);
        values.put(UPDATED_AT, getCurrentDate());

        long result = db.update(TABLE_USERS, values, ID_USER + " =? ", new String[]{ String.valueOf(id) });
        db.close();
        return result;
    }

    public long updateUserAvatar(int id, String avatarPath){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AVATAR_USER, avatarPath);
        values.put(UPDATED_AT, getCurrentDate());

        long result = db.update(TABLE_USERS, values, ID_USER + " =? ", new String[]{ String.valueOf(id) });
        db.close();
        return result;
    }

    public long updateUserPassword(int id, String newPassword){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PASSWORD_USER, PasswordUtils.hashPassword(newPassword));
        values.put(UPDATED_AT, getCurrentDate());

        long result = db.update(TABLE_USERS, values, ID_USER + " =? ", new String[]{ String.valueOf(id) });
        db.close();
        return result;
    }

    // ===== GAMIFICATION: XP & Level Methods =====

    public int getXP(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + XP_USER + " FROM " + TABLE_USERS + " WHERE " + ID_USER + " = ?",
                new String[]{String.valueOf(userId)});
        int xp = 0;
        if (cursor != null && cursor.moveToFirst()) {
            xp = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return xp;
    }

    public void updateXP(int userId, int newXp) {
        SQLiteDatabase db = this.getWritableDatabase();
        int level = (newXp / 5000) + 1;
        ContentValues values = new ContentValues();
        values.put(XP_USER, newXp);
        values.put(LEVEL_USER, level);
        values.put(UPDATED_AT, getCurrentDate());
        db.update(TABLE_USERS, values, ID_USER + " =? ", new String[]{String.valueOf(userId)});
        db.close();
    }

    public int getLevel(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + LEVEL_USER + " FROM " + TABLE_USERS + " WHERE " + ID_USER + " = ?",
                new String[]{String.valueOf(userId)});
        int level = 1;
        if (cursor != null && cursor.moveToFirst()) {
            level = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return level;
    }

    // ===== LEADERBOARD =====

    @SuppressLint("Range")
    public java.util.List<UserModel> getTopUsers(int limit) {
        java.util.List<UserModel> users = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + ID_USER + ", " + USERNAME_USER + ", " + AVATAR_USER + ", " +
                XP_USER + ", " + LEVEL_USER +
                " FROM " + TABLE_USERS +
                " ORDER BY " + XP_USER + " DESC" +
                " LIMIT " + limit, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                UserModel user = new UserModel();
                user.setId(cursor.getInt(cursor.getColumnIndex(ID_USER)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME_USER)));
                user.setAvatar(cursor.getString(cursor.getColumnIndex(AVATAR_USER)));
                user.setXp(cursor.getInt(cursor.getColumnIndex(XP_USER)));
                user.setLevel(cursor.getInt(cursor.getColumnIndex(LEVEL_USER)));
                users.add(user);
            }
            cursor.close();
        }
        db.close();
        return users;
    }

    /**
     * Get the rank of a specific user (1-based).
     */
    public int getUserRank(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) + 1 FROM " + TABLE_USERS +
                " WHERE " + XP_USER + " > (SELECT " + XP_USER + " FROM " + TABLE_USERS +
                " WHERE " + ID_USER + " = ?)",
                new String[]{String.valueOf(userId)});
        int rank = 1;
        if (cursor != null && cursor.moveToFirst()) {
            rank = cursor.getInt(0);
            cursor.close();
        }
        db.close();
        return rank;
    }
}
