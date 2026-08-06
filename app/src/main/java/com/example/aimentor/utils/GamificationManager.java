package com.example.aimentor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.example.aimentor.databases.SqliteDbHelper;
import com.example.aimentor.repository.UserRepository;

/**
 * Manages XP, Level, and Badge progression for the gamification system.
 *
 * XP Rewards:
 * - Ask AI question: +10 XP
 * - Quiz correct answer: +500 XP
 * - Quiz wrong answer: +50 XP (encouragement)
 * - Bookmark an answer: +5 XP
 * - Daily login: +20 XP
 *
 * Level formula: level = (totalXP / 5000) + 1
 */
public class GamificationManager {

    public static final int XP_ASK_AI = 10;
    public static final int XP_QUIZ_CORRECT = 500;
    public static final int XP_QUIZ_WRONG = 50;
    public static final int XP_BOOKMARK = 5;
    public static final int XP_DAILY_LOGIN = 20;
    public static final int XP_PER_LEVEL = 5000;

    /**
     * Award XP to a user and check for level up.
     * @return the new total XP
     */
    public static int awardXP(Context context, int userId, int amount) {
        if (context == null || userId <= 0 || amount <= 0) return 0;

        UserRepository userRepository = new UserRepository(context);
        int currentXp = userRepository.getXP(userId);
        int oldLevel = getLevel(currentXp);

        int newXp = currentXp + amount;
        userRepository.updateXP(userId, newXp);

        int newLevel = getLevel(newXp);

        // Check for level up
        if (newLevel > oldLevel) {
            Toast.makeText(context, "🎉 Level Up! You are now Level " + newLevel + "!", Toast.LENGTH_LONG).show();
            // Check badge unlocks for level milestones
            checkAndAwardBadge(context, userId, "LEVEL_5", newLevel >= 5);
            checkAndAwardBadge(context, userId, "LEVEL_10", newLevel >= 10);
        }

        return newXp;
    }

    /**
     * Calculate level from total XP. Level = (xp / 5000) + 1
     */
    public static int getLevel(int xp) {
        return (xp / XP_PER_LEVEL) + 1;
    }

    /**
     * Get XP progress within current level (0 to XP_PER_LEVEL).
     */
    public static int getXpInCurrentLevel(int xp) {
        return xp % XP_PER_LEVEL;
    }

    /**
     * Get XP remaining to next level.
     */
    public static int getXpToNextLevel(int xp) {
        return XP_PER_LEVEL - getXpInCurrentLevel(xp);
    }

    /**
     * Get progress percentage (0-100) within current level.
     */
    public static int getProgressPercent(int xp) {
        return (int) ((getXpInCurrentLevel(xp) / (float) XP_PER_LEVEL) * 100);
    }

    /**
     * Check question-based badges and award them.
     */
    public static void checkQuestionBadges(Context context, int userId, int questionCount) {
        checkAndAwardBadge(context, userId, "QUESTIONS_10", questionCount >= 10);
        checkAndAwardBadge(context, userId, "QUESTIONS_50", questionCount >= 50);
        checkAndAwardBadge(context, userId, "QUESTIONS_100", questionCount >= 100);
    }

    /**
     * Check quiz-based badges.
     */
    public static void checkQuizBadges(Context context, int userId, int totalQuizzes, boolean isPerfect) {
        checkAndAwardBadge(context, userId, "QUIZ_5", totalQuizzes >= 5);
        checkAndAwardBadge(context, userId, "QUIZ_20", totalQuizzes >= 20);
        if (isPerfect) {
            checkAndAwardBadge(context, userId, "QUIZ_PERFECT", true);
        }
    }

    /**
     * Check streak badge.
     */
    public static void checkStreakBadge(Context context, int userId, int streakDays) {
        checkAndAwardBadge(context, userId, "STREAK_7", streakDays >= 7);
    }

    /**
     * Check bookmark badge.
     */
    public static void checkBookmarkBadge(Context context, int userId, int bookmarkCount) {
        checkAndAwardBadge(context, userId, "BOOKMARK_20", bookmarkCount >= 20);
    }

    /**
     * Award a badge if condition is met and not already earned.
     */
    private static void checkAndAwardBadge(Context context, int userId, String requirement, boolean conditionMet) {
        if (!conditionMet || context == null) return;

        SqliteDbHelper dbHelper = new SqliteDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Find badge ID by requirement
        Cursor badgeCursor = db.rawQuery(
                "SELECT " + SqliteDbHelper.ID_BADGE + ", " + SqliteDbHelper.NAME_BADGE + ", " + SqliteDbHelper.ICON_BADGE +
                " FROM " + SqliteDbHelper.TABLE_BADGES +
                " WHERE " + SqliteDbHelper.REQUIREMENT_BADGE + " = ?",
                new String[]{requirement});

        if (badgeCursor != null && badgeCursor.moveToFirst()) {
            int badgeId = badgeCursor.getInt(0);
            String badgeName = badgeCursor.getString(1);
            String badgeIcon = badgeCursor.getString(2);

            // Check if already earned
            Cursor earnedCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + SqliteDbHelper.TABLE_USER_BADGES +
                    " WHERE " + SqliteDbHelper.USER_ID_BADGE + " = ? AND " + SqliteDbHelper.BADGE_ID_USER_BADGE + " = ?",
                    new String[]{String.valueOf(userId), String.valueOf(badgeId)});

            boolean alreadyEarned = false;
            if (earnedCursor != null && earnedCursor.moveToFirst()) {
                alreadyEarned = earnedCursor.getInt(0) > 0;
                earnedCursor.close();
            }

            if (!alreadyEarned) {
                // Award the badge
                SQLiteDatabase writeDb = dbHelper.getWritableDatabase();
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(SqliteDbHelper.USER_ID_BADGE, userId);
                values.put(SqliteDbHelper.BADGE_ID_USER_BADGE, badgeId);
                values.put(SqliteDbHelper.EARNED_AT_BADGE, System.currentTimeMillis());
                writeDb.insert(SqliteDbHelper.TABLE_USER_BADGES, null, values);
                writeDb.close();

                Toast.makeText(context, badgeIcon + " Badge Unlocked: " + badgeName + "!", Toast.LENGTH_LONG).show();
            }

            badgeCursor.close();
        }
        db.close();
    }

    /**
     * Get the number of badges earned by a user.
     */
    public static int getEarnedBadgeCount(Context context, int userId) {
        SqliteDbHelper dbHelper = new SqliteDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + SqliteDbHelper.TABLE_USER_BADGES +
                " WHERE " + SqliteDbHelper.USER_ID_BADGE + " = ?",
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
     * Award the "Tân binh" (Newcomer) badge on signup.
     */
    public static void awardSignupBadge(Context context, int userId) {
        checkAndAwardBadge(context, userId, "SIGNUP", true);
    }
}
