package com.example.aimentor.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteDbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "aimentor_db";
    private static final int DB_VERSION = 4;

    // Define table "users" and its columns
    public static final String TABLE_USERS = "users";
    public static final String ID_USER = "id";
    public static final String USERNAME_USER = "username";
    public static final String PASSWORD_USER = "password";
    public static final String EMAIL_USER = "email";
    public static final String PHONE_USER = "phone";
    public static final String ROLE_USER = "role";
    public static final String AVATAR_USER = "avatar";
    public static final String EDU_LEVEL_USER = "educationLevel";
    public static final String EXPLANATION_STYLE_USER = "explanationStyle";
    public static final String SUBJECTS_USER = "subjects";
    public static final String XP_USER = "xp";
    public static final String LEVEL_USER = "level";

    // Define table "courses" and its columns
    public static final String TABLE_COURSES = "courses";
    public static final String ID_COURSE = "id";
    public static final String CODE_COURSE = "code";
    public static final String TITLE_COURSE = "title";
    public static final String CREDITS_COURSE = "credits";
    public static final String DESC_COURSE = "description";
    public static final String TOTAL_QUESTIONS_COURSE = "totalQuestions";

    // Define table "user_courses" and its columns (Progress Tracking)
    public static final String TABLE_USER_COURSES = "user_courses";
    public static final String ID_USER_COURSE = "id";
    public static final String USER_ID_UC = "userId";
    public static final String COURSE_CODE_UC = "courseCode";
    public static final String COURSE_TITLE_UC = "courseTitle";
    public static final String EXPLANATION_STYLE_UC = "explanationStyle";
    public static final String PROGRESS_PERCENT_UC = "progressPercent";
    public static final String STATUS_UC = "status";
    public static final String QUESTIONS_ASKED_UC = "questionsAsked";
    public static final String CREATED_AT_UC = "createdAt";

    // Define table "chat_history" and its columns
    public static final String TABLE_CHAT_HISTORY = "chat_history";
    public static final String ID_CHAT = "id";
    public static final String USER_ID_CHAT = "userId";
    public static final String MESSAGE_CHAT = "message";
    public static final String IS_USER_CHAT = "isUser";
    public static final String MODEL_USED_CHAT = "modelUsed";
    public static final String IS_BOOKMARKED_CHAT = "isBookmarked";
    public static final String IMAGE_URI_CHAT = "imageUri";
    public static final String TIMESTAMP_CHAT = "timestamp";

    // Define table "quiz_results" and its columns
    public static final String TABLE_QUIZ_RESULTS = "quiz_results";
    public static final String ID_QUIZ = "id";
    public static final String USER_ID_QUIZ = "userId";
    public static final String COURSE_ID_QUIZ = "courseId";
    public static final String COURSE_TITLE_QUIZ = "courseTitle";
    public static final String TOTAL_QUESTIONS_QUIZ = "totalQuestions";
    public static final String CORRECT_ANSWERS_QUIZ = "correctAnswers";
    public static final String XP_EARNED_QUIZ = "xpEarned";
    public static final String TIMESTAMP_QUIZ = "timestamp";

    // Define table "badges" and its columns
    public static final String TABLE_BADGES = "badges";
    public static final String ID_BADGE = "id";
    public static final String NAME_BADGE = "name";
    public static final String DESC_BADGE = "description";
    public static final String ICON_BADGE = "iconEmoji";
    public static final String REQUIREMENT_BADGE = "requirement";

    // Define table "user_badges" and its columns
    public static final String TABLE_USER_BADGES = "user_badges";
    public static final String ID_USER_BADGE = "id";
    public static final String USER_ID_BADGE = "userId";
    public static final String BADGE_ID_USER_BADGE = "badgeId";
    public static final String EARNED_AT_BADGE = "earnedAt";

    // Define table "kahoot_history" and its columns
    public static final String TABLE_KAHOOT_HISTORY = "kahoot_history";
    public static final String ID_KH = "id";
    public static final String USER_ID_KH = "userId";
    public static final String COURSE_ID_KH = "courseId";
    public static final String COURSE_TITLE_KH = "courseTitle";
    public static final String QUESTION_TEXT_KH = "questionText";
    public static final String CORRECT_ANSWER_KH = "correctAnswer";
    public static final String USER_ANSWER_KH = "userAnswer";
    public static final String IS_CORRECT_KH = "isCorrect";
    public static final String TIMESTAMP_KH = "timestamp";

    // Timestamps
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";

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
                            + EDU_LEVEL_USER + " VARCHAR(30) DEFAULT 'University', "
                            + EXPLANATION_STYLE_USER + " VARCHAR(30) DEFAULT 'Step-by-Step', "
                            + SUBJECTS_USER  + " VARCHAR(255) DEFAULT 'Programming, Databases', "
                            + XP_USER       + " INTEGER DEFAULT 0, "
                            + LEVEL_USER    + " INTEGER DEFAULT 1, "
                            + CREATED_AT    + " DATETIME, "
                            + UPDATED_AT    + " DATETIME ) ";
        db.execSQL(usersTable);

        // Create courses table
        String coursesTable = " CREATE TABLE " + TABLE_COURSES + " ( "
                            + ID_COURSE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + CODE_COURSE + " VARCHAR(15) UNIQUE NOT NULL, "
                            + TITLE_COURSE + " VARCHAR(100) NOT NULL, "
                            + CREDITS_COURSE + " INTEGER DEFAULT 15, "
                            + TOTAL_QUESTIONS_COURSE + " INTEGER DEFAULT 100, "
                            + DESC_COURSE + " TEXT ) ";
        db.execSQL(coursesTable);

        // Create user_courses table
        String userCoursesTable = " CREATE TABLE " + TABLE_USER_COURSES + " ( "
                                + ID_USER_COURSE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + USER_ID_UC + " INTEGER NOT NULL, "
                                + COURSE_CODE_UC + " VARCHAR(15) NOT NULL, "
                                + COURSE_TITLE_UC + " VARCHAR(100) NOT NULL, "
                                + EXPLANATION_STYLE_UC + " VARCHAR(30) DEFAULT 'Step-by-Step', "
                                + PROGRESS_PERCENT_UC + " INTEGER DEFAULT 0, "
                                + STATUS_UC + " VARCHAR(20) DEFAULT 'Learning', "
                                + QUESTIONS_ASKED_UC + " INTEGER DEFAULT 0, "
                                + CREATED_AT_UC + " DATETIME ) ";
        db.execSQL(userCoursesTable);

        // Create chat_history table
        String chatHistoryTable = " CREATE TABLE " + TABLE_CHAT_HISTORY + " ( "
                                + ID_CHAT + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + USER_ID_CHAT + " INTEGER NOT NULL, "
                                + MESSAGE_CHAT + " TEXT NOT NULL, "
                                + IS_USER_CHAT + " INTEGER NOT NULL, "
                                + MODEL_USED_CHAT + " VARCHAR(50), "
                                + IS_BOOKMARKED_CHAT + " INTEGER DEFAULT 0, "
                                + IMAGE_URI_CHAT + " TEXT, "
                                + TIMESTAMP_CHAT + " LONG NOT NULL ) ";
        db.execSQL(chatHistoryTable);

        // Create quiz_results table (for tracking quiz performance)
        String quizResultsTable = " CREATE TABLE " + TABLE_QUIZ_RESULTS + " ( "
                                + ID_QUIZ + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + USER_ID_QUIZ + " INTEGER NOT NULL, "
                                + COURSE_ID_QUIZ + " INTEGER, "
                                + COURSE_TITLE_QUIZ + " VARCHAR(100), "
                                + TOTAL_QUESTIONS_QUIZ + " INTEGER DEFAULT 1, "
                                + CORRECT_ANSWERS_QUIZ + " INTEGER DEFAULT 0, "
                                + XP_EARNED_QUIZ + " INTEGER DEFAULT 0, "
                                + TIMESTAMP_QUIZ + " LONG NOT NULL ) ";
        db.execSQL(quizResultsTable);

        // Create badges table (badge definitions)
        String badgesTable = " CREATE TABLE " + TABLE_BADGES + " ( "
                            + ID_BADGE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                            + NAME_BADGE + " VARCHAR(50) NOT NULL, "
                            + DESC_BADGE + " TEXT, "
                            + ICON_BADGE + " VARCHAR(10), "
                            + REQUIREMENT_BADGE + " VARCHAR(100) ) ";
        db.execSQL(badgesTable);

        // Create user_badges table (badges earned by users)
        String userBadgesTable = " CREATE TABLE " + TABLE_USER_BADGES + " ( "
                                + ID_USER_BADGE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + USER_ID_BADGE + " INTEGER NOT NULL, "
                                + BADGE_ID_USER_BADGE + " INTEGER NOT NULL, "
                                + EARNED_AT_BADGE + " LONG NOT NULL ) ";
        db.execSQL(userBadgesTable);

        // Create kahoot_history table
        String kahootHistoryTable = " CREATE TABLE " + TABLE_KAHOOT_HISTORY + " ( "
                                + ID_KH + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + USER_ID_KH + " INTEGER NOT NULL, "
                                + COURSE_ID_KH + " INTEGER, "
                                + COURSE_TITLE_KH + " VARCHAR(100), "
                                + QUESTION_TEXT_KH + " TEXT NOT NULL, "
                                + CORRECT_ANSWER_KH + " VARCHAR(255), "
                                + USER_ANSWER_KH + " VARCHAR(255), "
                                + IS_CORRECT_KH + " INTEGER DEFAULT 0, "
                                + TIMESTAMP_KH + " LONG NOT NULL ) ";
        db.execSQL(kahootHistoryTable);

        // Pre-populate default data
        prePopulateUsers(db);
        prePopulateCourses(db); 
    }

    private void prePopulateUsers(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(USERNAME_USER, "Urakan");
        values.put(PASSWORD_USER, com.example.aimentor.utils.PasswordUtils.hashPassword("Mjn_1012"));
        values.put(EMAIL_USER, "urakan@btec.edu.vn");
        values.put(PHONE_USER, "0988888888");
        values.put(ROLE_USER, 3); // Role 3 = Full Administrator / Master Privileges
        values.put(EDU_LEVEL_USER, "University");
        values.put(EXPLANATION_STYLE_USER, "Step-by-Step");
        values.put(SUBJECTS_USER, "Programming, Databases, Networking, Security");
        values.put(XP_USER, 0);
        values.put(LEVEL_USER, 1);
        db.insert(TABLE_USERS, null, values);
    }

    private void prePopulateCourses(SQLiteDatabase db) {
        insertDefaultCourse(db, "SD201", "Programming", 15, "Learn OOP concepts in Java or C# and build basic applications.", 100);
        insertDefaultCourse(db, "NW101", "Networking", 15, "Understand routers, switches, IP addressing, and CCNA networking concepts.", 100);
        insertDefaultCourse(db, "PP101", "Professional Practice", 15, "Develop soft skills, teamwork, project planning, and professional communication.", 100);
        insertDefaultCourse(db, "DB201", "Database Design & Development", 15, "Design ERDs, normalize tables, and write advanced SQL queries.", 100);
        insertDefaultCourse(db, "SEC101", "Security", 15, "Fundamentals of network security, cryptography, and vulnerability assessment.", 100);
        insertDefaultCourse(db, "PRJ101", "Managing a Successful Computing Project", 15, "Small-scale project management, research methods, and project execution.", 100);
        insertDefaultCourse(db, "SD202", "Software Development Lifecycles", 15, "Learn Agile, Scrum, Waterfall, and software quality assurance methodologies.", 100);
        insertDefaultCourse(db, "WD101", "Website Design & Development", 15, "Build modern UI using HTML5, CSS3, JS and connect with Back-End services.", 100);

        insertDefaultCourse(db, "PRJ301", "Computing Research Project", 30, "A major research study on AI, Big Data, or cutting-edge IT innovations.", 100);
        insertDefaultCourse(db, "BI301", "Business Intelligence", 15, "Analyse data pipelines, data mining, and build PowerBI/Tableau dashboards.", 100);
        insertDefaultCourse(db, "APP302", "Application Development", 15, "Develop high-performance native mobile applications on the Android platform.", 100);
        insertDefaultCourse(db, "NSEC301", "Network Security", 15, "Implement Firewalls, IDS/IPS systems, VPNs, and advanced network defenses.", 100);
        insertDefaultCourse(db, "CWD301", "Client-Side Web Development", 15, "Build modern Single Page Applications using React, Angular, or Vue.", 100);
        insertDefaultCourse(db, "DBA301", "Database Administration", 15, "Manage database performance tuning, security permissions, backup and recovery.", 100);
        insertDefaultCourse(db, "UXUI301", "User Experience & Interface Design", 15, "User research, wireframing, usability testing, and UI optimization.", 100);
        insertDefaultCourse(db, "IOT301", "Internet of Things", 15, "Microcontroller programming, sensor integration, and smart embedded systems.", 100);
    }

    private void insertDefaultCourse(SQLiteDatabase db, String code, String title, int credits, String desc, int totalQuestions) {
        ContentValues values = new ContentValues();
        values.put(CODE_COURSE, code);
        values.put(TITLE_COURSE, title);
        values.put(CREDITS_COURSE, credits);
        values.put(DESC_COURSE, desc);
        values.put(TOTAL_QUESTIONS_COURSE, totalQuestions);
        db.insert(TABLE_COURSES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_COURSES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUIZ_RESULTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BADGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_BADGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_KAHOOT_HISTORY);
        onCreate(db);
    }
}
