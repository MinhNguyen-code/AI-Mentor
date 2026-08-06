# Changelog & Jira Tracker

All notable changes to the AI Mentor project are documented in this file, mapped directly to Jira tickets on the Agile Board.

## [Release 1.0.0] - 2026-08-06

### Epic: Database & Architecture [AIM-100]
- **[AIM-101]**: Initialize `SqliteDbHelper` with local SQLite structure (Users, Courses, Chat History tables).
- **[AIM-102]**: Implement User Authentication (Login/SignUp) with MD5 password hashing.
- **[AIM-103]**: Create `UserRepository` for clean database access (Repository Pattern).

### Epic: AI Integration [AIM-200]
- **[AIM-205]**: Integrate **GroqCloud API** (`api.groq.com`) using HTTPURLConnection.
- **[AIM-206]**: Build Custom System Prompts for AI Tutor personas based on user's education level.
- **[AIM-208]**: Fix JSON parsing logic for AI response handling.
- **[AIM-210]**: (Mock) Local image URI attachment for Multimodal simulation.

### Epic: Gamification & Quiz UI [AIM-300]
- **[AIM-301]**: Implement XP and Leveling system (`GamificationManager.java`).
- **[AIM-302]**: Create Leaderboard fragment with live SQL ranking logic.
- **[AIM-304]**: Design Kahoot-style interface for interactive learning (Red/Green visual feedback).
- **[AIM-305]**: Refactor `QuizFragment` logic to handle dynamic JSON question generation from AI.

### Epic: UI/UX Enhancements [AIM-400]
- **[AIM-401]**: Implement Dark/Light Mode using SharedPreferences.
- **[AIM-402]**: Display dynamic Username on Drawer Navigation Menu.
- **[AIM-403]**: Add "Forgot Password" dialog flow.

---
*Note: Ticket IDs correspond to the internal Jira workspace of the development team.*
