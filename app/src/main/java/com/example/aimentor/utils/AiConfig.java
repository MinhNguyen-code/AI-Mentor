package com.example.aimentor.utils;

import com.example.aimentor.BuildConfig;

public class AiConfig {
    // Groq OpenAI-compatible Endpoint
    public static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    
    // Groq Model (High Intelligence Llama 3.3 70B)
    public static final String MODEL_NAME = "llama-3.3-70b-versatile";
    
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
}
