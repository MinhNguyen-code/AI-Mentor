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
        SQLiteDatabase db = this.getWritableDatabase();
        long insert = db.insert(TABLE_USERS, null, values);
        db.close();
        return insert;
    }

    @SuppressLint("Range")
    public UserModel loginUser(String username, String password){
        UserModel user = null;
        String[] cols = {ID_USER, USERNAME_USER, EMAIL_USER, PHONE_USER, ROLE_USER, AVATAR_USER, EDU_LEVEL_USER, EXPLANATION_STYLE_USER, SUBJECTS_USER};
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
        }
        data.close();
        db.close();
        return user;
    }

    @SuppressLint("Range")
    public UserModel getUserById(int id){
        UserModel user = null;
        String[] cols = {ID_USER, USERNAME_USER, EMAIL_USER, PHONE_USER, ROLE_USER, AVATAR_USER, EDU_LEVEL_USER, EXPLANATION_STYLE_USER, SUBJECTS_USER, CREATED_AT};
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
        }
        data.close();
        db.close();
        return user;
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
}
