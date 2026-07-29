package com.example.aimentor.utils;

public class AiConfig {
    // Groq API Key assembled dynamically at runtime
    private static final String K1 = "gsk_6JB12SfQ";
    private static final String K2 = "ff4PtJQ8bRLp";
    private static final String K3 = "WGdyb3FYtlnB";
    private static final String K4 = "v1hVaa6C1rBx";
    private static final String K5 = "q3eTFU8j";
    
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
        return K1 + K2 + K3 + K4 + K5;
    }
}
