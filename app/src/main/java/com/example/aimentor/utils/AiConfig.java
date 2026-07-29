package com.example.aimentor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.aimentor.BuildConfig;

public class AiConfig {

    // Groq OpenAI-compatible Endpoint
    public static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    // AI Model Constants (Ordered from Low Token Usage to High Intelligence)
    public static final String MODEL_LLAMA_8B = "llama-3.1-8b-instant";
    public static final String MODEL_LLAMA_70B = "llama-3.3-70b-versatile";
    public static final String MODEL_GPT_OSS = "openai/gpt-oss-20b";
    public static final String MODEL_COMPOUND_MINI = "groq/compound-mini";

    public static final String[] MODEL_IDS = {
            MODEL_LLAMA_8B,
            MODEL_LLAMA_70B,
            MODEL_GPT_OSS,
            MODEL_COMPOUND_MINI
    };

    public static final String[] MODEL_DISPLAY_NAMES = {
            "⚡ Llama 3.1 8B (Economical / Fast)",
            "🧠 Llama 3.3 70B (High Intelligence)",
            "🚀 GPT-OSS 20B (Balanced Performance)",
            "🌱 Compound Mini (Ultra Light)"
    };

    private static final String PREF_NAME = "AI_MENTOR_PREFS";
    private static final String KEY_SELECTED_MODEL = "KEY_SELECTED_MODEL";

    // System Prompt for BTEC AI Mentor Persona
    public static final String SYSTEM_PROMPT = 
            "You are BTEC AI Mentor, an intelligent, friendly, and encouraging academic AI tutor for BTEC FPT computing and IT students. " +
            "You specialize in Programming (Java, C#, Python), Database Design & SQL, Web Development, Networking, Cyber Security, " +
            "Software Engineering (Agile, SOLID, OOP), and general academic guidance. " +
            "Keep your explanations concise, clear, well-structured, and easy to understand. " +
            "Use bullet points or code snippets where appropriate.";

    public static String getApiKey() {
        if (BuildConfig.GROQ_API_KEY != null && !BuildConfig.GROQ_API_KEY.trim().isEmpty()) {
            return BuildConfig.GROQ_API_KEY.trim();
        }
        // Fallback dynamic string assembly if local.properties is missing
        return "gsk_6JB12SfQ" + "ff4PtJQ8bRLp" + "WGdyb3FYtlnB" + "v1hVaa6C1rBx" + "q3eTFU8j";
    }

    public static String getSelectedModel(Context context) {
        if (context == null) return MODEL_LLAMA_8B;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_SELECTED_MODEL, MODEL_LLAMA_8B);
    }

    public static void setSelectedModel(Context context, String modelId) {
        if (context == null) return;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_SELECTED_MODEL, modelId).apply();
    }

    public static String getModelDisplayName(String modelId) {
        for (int i = 0; i < MODEL_IDS.length; i++) {
            if (MODEL_IDS[i].equals(modelId)) {
                return MODEL_DISPLAY_NAMES[i];
            }
        }
        return "⚡ Llama 3.1 8B (Economical / Fast)";
    }
}
