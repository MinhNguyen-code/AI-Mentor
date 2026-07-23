package com.example.aimentor.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "studyAI";
    private static final int DB_VERSION = 6;

    // Define table "users" and its columns
    protected static final String TABLE_USERS = "users";
    protected static final String ID_USER = "id";
    protected static final String USERNAME_USER = "username";
    protected static final String PASSWORD_USER = "password";
    protected static final String EMAIL_USER = "email";
    protected static final String PHONE_USER = "phone";
    protected static final String ROLE_USER = "role";
    protected static final String AVATAR_USER = "avatar";

    // Define table "courses" and its columns
    protected static final String TABLE_COURSES = "courses";
    protected static final String ID_COURSE = "id";
    protected static final String CODE_COURSE = "code";
    protected static final String TITLE_COURSE = "title";
    protected static final String CREDITS_COURSE = "credits";
    protected static final String DESC_COURSE = "description";

    // Timestamps
    protected static final String CREATED_AT = "createdAt";
    protected static final String UPDATED_AT = "updatedAt";

    public SqliteDbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String usersTable = " CREATE TABLE " + TABLE_USERS + " ( "
                            + ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + USERNAME_USER + " VARCHAR(30) NOT NULL, "
                            + PASSWORD_USER + " VARCHAR(200) NOT NULL, "
                            + EMAIL_USER    + " VARCHAR(60) NOT NULL, "
                            + PHONE_USER    + " VARCHAR(20), "
                            + ROLE_USER     + " TINYINT DEFAULT(1), "
                            + AVATAR_USER   + " VARCHAR(255), "
                            + CREATED_AT    + " DATETIME, "
                            + UPDATED_AT    + " DATETIME ) ";
        db.execSQL(usersTable);

        // Create courses table
        String coursesTable = " CREATE TABLE " + TABLE_COURSES + " ( "
                            + ID_COURSE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + CODE_COURSE + " VARCHAR(15) UNIQUE NOT NULL, "
                            + TITLE_COURSE + " VARCHAR(100) NOT NULL, "
                            + CREDITS_COURSE + " INTEGER DEFAULT 15, "
                            + DESC_COURSE + " TEXT ) ";
        db.execSQL(coursesTable);

        // Pre-populate default data
        prePopulateUsers(db);
        prePopulateCourses(db);
    }

    private void prePopulateUsers(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(USERNAME_USER, "student1");
        values.put(PASSWORD_USER, com.example.aimentor.utils.PasswordUtils.hashPassword("Password123"));
        values.put(EMAIL_USER, "student1@btec.edu.vn");
        values.put(PHONE_USER, "0988888888");
        values.put(ROLE_USER, 1);
        db.insert(TABLE_USERS, null, values);
    }

    private void prePopulateCourses(SQLiteDatabase db) {
        // Level 4 Core Units
        insertDefaultCourse(db, "SD201", "Programming", 15, "Learn OOP concepts in Java or C# and build basic applications.");
        insertDefaultCourse(db, "NW101", "Networking", 15, "Understand routers, switches, IP addressing, and CCNA networking concepts.");
        insertDefaultCourse(db, "PP101", "Professional Practice", 15, "Develop soft skills, teamwork, project planning, and professional communication.");
        insertDefaultCourse(db, "DB201", "Database Design & Development", 15, "Design ERDs, normalize tables, and write advanced SQL queries.");
        insertDefaultCourse(db, "SEC101", "Security", 15, "Fundamentals of network security, cryptography, and vulnerability assessment.");
        insertDefaultCourse(db, "PRJ101", "Managing a Successful Computing Project", 15, "Small-scale project management, research methods, and project execution.");
        insertDefaultCourse(db, "SD202", "Software Development Lifecycles", 15, "Learn Agile, Scrum, Waterfall, and software quality assurance methodologies.");
        insertDefaultCourse(db, "WD101", "Website Design & Development", 15, "Build modern UI using HTML5, CSS3, JS and connect with Back-End services.");

        // Level 5 Core & Specialist Units
        insertDefaultCourse(db, "PRJ301", "Computing Research Project", 30, "A major research study on AI, Big Data, or cutting-edge IT innovations.");
        insertDefaultCourse(db, "BI301", "Business Intelligence", 15, "Analyse data pipelines, data mining, and build PowerBI/Tableau dashboards.");
        insertDefaultCourse(db, "APP302", "Application Development", 15, "Develop high-performance native mobile applications on the Android platform.");
        insertDefaultCourse(db, "NSEC301", "Network Security", 15, "Implement Firewalls, IDS/IPS systems, VPNs, and advanced network defenses.");
        insertDefaultCourse(db, "CWD301", "Client-Side Web Development", 15, "Build modern Single Page Applications using React, Angular, or Vue.");
        insertDefaultCourse(db, "DBA301", "Database Administration", 15, "Manage database performance tuning, security permissions, backup and recovery.");
        insertDefaultCourse(db, "UXUI301", "User Experience & Interface Design", 15, "User research, wireframing, usability testing, and UI optimization.");
        insertDefaultCourse(db, "IOT301", "Internet of Things", 15, "Microcontroller programming, sensor integration, and smart embedded systems.");
    }

    private void insertDefaultCourse(SQLiteDatabase db, String code, String title, int credits, String desc) {
        ContentValues values = new ContentValues();
        values.put(CODE_COURSE, code);
        values.put(TITLE_COURSE, title);
        values.put(CREDITS_COURSE, credits);
        values.put(DESC_COURSE, desc);
        db.insert(TABLE_COURSES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
            onCreate(db);
        }
    }
}
