package com.example.aimentor.services;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.aimentor.models.ChatMessageModel;
import com.example.aimentor.utils.AiConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AiMentorService {

    private static final String TAG = "AiMentorService";
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface AiResponseCallback {
        void onSuccess(String aiReply);
        void onError(String errorMessage);
    }

    public static void sendMessageToAi(String selectedModel, String educationLevel, String explanationStyle, List<ChatMessageModel> chatHistory, String newUserPrompt, AiResponseCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(AiConfig.GROQ_API_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setRequestProperty("Authorization", "Bearer " + AiConfig.getApiKey());
                    conn.setRequestProperty("User-Agent", "AIMentor-AndroidApp/1.0");
                    conn.setConnectTimeout(15000);
                    conn.setReadTimeout(30000);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);

                    // Build JSON Payload
                    JSONObject jsonPayload = new JSONObject();
                    String modelToUse = (selectedModel != null && !selectedModel.trim().isEmpty())
                            ? selectedModel.trim()
                            : AiConfig.MODEL_LLAMA_8B;
                    jsonPayload.put("model", modelToUse);
                    jsonPayload.put("temperature", 0.7);

                    JSONArray messagesArray = new JSONArray();

                    // 1. Add System Prompt customized for student's level & style
                    StringBuilder systemPromptBuilder = new StringBuilder(AiConfig.SYSTEM_PROMPT);
                    if (educationLevel != null && !educationLevel.trim().isEmpty()) {
                        systemPromptBuilder.append(" Target Student Level: ").append(educationLevel).append(".");
                    }
                    if (explanationStyle != null && !explanationStyle.trim().isEmpty()) {
                        systemPromptBuilder.append(" Preferred Style: ").append(explanationStyle).append(".");
                    }

                    JSONObject systemObj = new JSONObject();
                    systemObj.put("role", "system");
                    systemObj.put("content", systemPromptBuilder.toString());
                    messagesArray.put(systemObj);

                    // 2. Add Recent Chat History (up to 10 previous messages for context)
                    if (chatHistory != null) {
                        int startIndex = Math.max(0, chatHistory.size() - 10);
                        for (int i = startIndex; i < chatHistory.size(); i++) {
                            ChatMessageModel msg = chatHistory.get(i);
                            if (msg.isTyping()) continue; // Skip typing indicators
                            
                            JSONObject msgObj = new JSONObject();
                            msgObj.put("role", msg.isUser() ? "user" : "assistant");
                            msgObj.put("content", msg.getMessage());
                            messagesArray.put(msgObj);
                        }
                    }

                    // 3. Add latest user prompt if not already in history
                    if (newUserPrompt != null && !newUserPrompt.trim().isEmpty()) {
                        JSONObject userObj = new JSONObject();
                        userObj.put("role", "user");
                        userObj.put("content", newUserPrompt.trim());
                        messagesArray.put(userObj);
                    }

                    jsonPayload.put("messages", messagesArray);

                    // Write JSON body
                    String jsonString = jsonPayload.toString();
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = jsonString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    Log.d(TAG, "Groq API Response Code: " + responseCode);

                    InputStream is = (responseCode >= 200 && responseCode < 300)
                            ? conn.getInputStream()
                            : conn.getErrorStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                    StringBuilder responseBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    reader.close();

                    String responseStr = responseBuilder.toString();

                    if (responseCode >= 200 && responseCode < 300) {
                        JSONObject jsonResponse = new JSONObject(responseStr);
                        JSONArray choices = jsonResponse.getJSONArray("choices");
                        if (choices.length() > 0) {
                            JSONObject firstChoice = choices.getJSONObject(0);
                            JSONObject messageObj = firstChoice.getJSONObject("message");
                            String aiReply = messageObj.getString("content");

                            mainHandler.post(() -> callback.onSuccess(aiReply));
                        } else {
                            mainHandler.post(() -> callback.onError("No response choices returned by AI."));
                        }
                    } else {
                        Log.e(TAG, "Groq API Error Response: " + responseStr);
                        mainHandler.post(() -> callback.onError("AI Service Error (HTTP " + responseCode + ")"));
                    }

                } catch (Exception e) {
                    Log.e(TAG, "Exception during AI request", e);
                    mainHandler.post(() -> callback.onError("Connection failed: " + e.getLocalizedMessage()));
                }
            }
        });
    }
}
