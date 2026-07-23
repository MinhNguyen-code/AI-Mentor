package databse;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class SqLiteDbHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "aimentor.db";
    public static final int DB_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String ID_USER = "id_user";
    public static final String USERNAME_USER = "username_user";
    public static final String PASSWORD_USER = "password_user";
    public static final String EMAIL_USER = "email_user";
    public static final String PHONE_USER = "phone_user";
    public static final String ROLE_USER = "role_user";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    public SqLiteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String usersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME_USER + " VARCHAR(30) NOT NULL, "
                + PASSWORD_USER + " VARCHAR(200) NOT NULL, "
                + EMAIL_USER + " VARCHAR(60) NOT NULL, "
                + PHONE_USER + " VARCHAR(20), "
                + ROLE_USER + " TINYINT DEFAULT(1), "
                + CREATED_AT + " DATETIME, "
                + UPDATED_AT + " DATETIME ) ";
        db.execSQL(usersTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Insert user into database
    public boolean insertUser(String username, String password, String phone, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME_USER, username);
        values.put(PASSWORD_USER, password);
        values.put(PHONE_USER, phone);
        values.put(EMAIL_USER, email);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    // Check if user exists with matching password
    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ID_USER};
        String selection = USERNAME_USER + " = ?" + " AND " + PASSWORD_USER + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    // Check if username is already registered
    public boolean checkUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ID_USER};
        String selection = USERNAME_USER + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }
}
