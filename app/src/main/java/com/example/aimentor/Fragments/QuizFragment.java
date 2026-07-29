package com.example.aimentor.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimentor.R;
import com.example.aimentor.adapters.ChatAdapter;
import com.example.aimentor.models.ChatMessageModel;
import com.example.aimentor.services.AiMentorService;
import com.example.aimentor.utils.AiConfig;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private RecyclerView rvChatMessages;
    private EditText edtChatMessage;
    private View btnSendMessage, btnSelectModel;
    private ImageView btnClearChat;
    private TextView tvSelectedModel;
    private TextView chipChat1, chipChat2, chipChat3, chipChat4, chipChat5;

    private static final List<ChatMessageModel> chatList = new ArrayList<>();
    private static String pendingPrompt = null;

    private ChatAdapter chatAdapter;
    private boolean isAiThinking = false;

    public QuizFragment() {
        // Required empty public constructor
    }

    public static QuizFragment newInstance() {
        return new QuizFragment();
    }

    public static void setPendingPrompt(String prompt) {
        pendingPrompt = prompt;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind Views
        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        edtChatMessage = view.findViewById(R.id.edtChatMessage);
        btnSendMessage = view.findViewById(R.id.btnSendMessage);
        btnClearChat = view.findViewById(R.id.btnClearChat);
        btnSelectModel = view.findViewById(R.id.btnSelectModel);
        tvSelectedModel = view.findViewById(R.id.tvSelectedModel);

        chipChat1 = view.findViewById(R.id.chipChat1);
        chipChat2 = view.findViewById(R.id.chipChat2);
        chipChat3 = view.findViewById(R.id.chipChat3);
        chipChat4 = view.findViewById(R.id.chipChat4);
        chipChat5 = view.findViewById(R.id.chipChat5);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Keep scrolled to bottom
        rvChatMessages.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(chatList);
        rvChatMessages.setAdapter(chatAdapter);

        // Initialize Selected Model UI
        updateModelSelectorUi();

        // Setup Model Selection Click Listener
        if (btnSelectModel != null) {
            btnSelectModel.setOnClickListener(v -> showModelSelectionDialog());
        }

        // Add Initial AI Welcome Message if empty
        if (chatList.isEmpty()) {
            chatList.add(new ChatMessageModel(
                    "Hello! I am your BTEC AI Mentor 🤖.\n\nI'm ready to assist you with Programming (Java/C#), Database SQL, Web Development, Networking, Security, or any BTEC course questions!\n\n💡 Tip: You can switch AI models at the top to save tokens/credits (e.g. Llama 3.1 8B) or boost intelligence (Llama 3.3 70B).\n\nWhat would you like to explore today?",
                    false
            ));
            chatAdapter.notifyItemInserted(chatList.size() - 1);
        }

        // Setup Send Button Listener
        btnSendMessage.setOnClickListener(v -> {
            String input = edtChatMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(input)) {
                sendUserMessage(input);
            }
        });

        // Setup Clear Chat Listener
        btnClearChat.setOnClickListener(v -> {
            chatList.clear();
            chatList.add(new ChatMessageModel(
                    "Chat history cleared! 🤖 Ask me any question to start a fresh discussion.",
                    false
            ));
            chatAdapter.notifyDataSetChanged();
            Toast.makeText(getContext(), "Chat history cleared", Toast.LENGTH_SHORT).show();
        });

        // Setup Quick Suggestion Chips
        setupChipListeners();

        // Check if there is a pending prompt from HomeFragment
        if (pendingPrompt != null && !pendingPrompt.trim().isEmpty()) {
            String promptToSend = pendingPrompt;
            pendingPrompt = null; // Reset
            sendUserMessage(promptToSend);
        }
    }

    private void updateModelSelectorUi() {
        if (tvSelectedModel != null && getContext() != null) {
            String currentModelId = AiConfig.getSelectedModel(getContext());
            String displayName = AiConfig.getModelDisplayName(currentModelId);
            tvSelectedModel.setText(displayName + " ▼");
        }
    }

    private void showModelSelectionDialog() {
        if (getContext() == null) return;

        String currentModelId = AiConfig.getSelectedModel(getContext());
        int checkedItem = 0;
        for (int i = 0; i < AiConfig.MODEL_IDS.length; i++) {
            if (AiConfig.MODEL_IDS[i].equals(currentModelId)) {
                checkedItem = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.Theme_AIMentor);
        builder.setTitle("Select AI Model (Token Efficiency)");
        builder.setSingleChoiceItems(AiConfig.MODEL_DISPLAY_NAMES, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedId = AiConfig.MODEL_IDS[which];
                AiConfig.setSelectedModel(getContext(), selectedId);
                updateModelSelectorUi();
                Toast.makeText(getContext(), "Switched AI Model: " + AiConfig.MODEL_DISPLAY_NAMES[which], Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void setupChipListeners() {
        if (chipChat1 != null) {
            chipChat1.setOnClickListener(v -> sendUserMessage("Explain Object-Oriented Programming (OOP) concepts in Java with clear examples."));
        }
        if (chipChat2 != null) {
            chipChat2.setOnClickListener(v -> sendUserMessage("What is Third Normal Form (3NF) in Database Design? Why is it important?"));
        }
        if (chipChat3 != null) {
            chipChat3.setOnClickListener(v -> sendUserMessage("Explain REST API architectural principles and HTTP methods (GET, POST, PUT, DELETE)."));
        }
        if (chipChat4 != null) {
            chipChat4.setOnClickListener(v -> sendUserMessage("What are the fundamentals of Network Security, Firewalls, and Encryption?"));
        }
        if (chipChat5 != null) {
            chipChat5.setOnClickListener(v -> sendUserMessage("Quiz me! Give me 3 multiple choice questions on Java Programming (SD201)."));
        }
    }

    private void sendUserMessage(String userPrompt) {
        if (isAiThinking) {
            Toast.makeText(getContext(), "AI is processing your previous question. Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Add User Message to Chat List
        ChatMessageModel userMessage = new ChatMessageModel(userPrompt, true);
        chatList.add(userMessage);
        int userPos = chatList.size() - 1;
        chatAdapter.notifyItemInserted(userPos);
        rvChatMessages.scrollToPosition(userPos);

        // 2. Clear input text
        if (edtChatMessage != null) {
            edtChatMessage.setText("");
        }

        // 3. Add Typing Indicator Message
        ChatMessageModel typingMsg = new ChatMessageModel("", false, true);
        chatList.add(typingMsg);
        int typingPos = chatList.size() - 1;
        chatAdapter.notifyItemInserted(typingPos);
        rvChatMessages.scrollToPosition(typingPos);

        isAiThinking = true;

        // Get currently selected AI model
        String selectedModel = AiConfig.getSelectedModel(getContext());

        // 4. Call Groq AI API Service
        AiMentorService.sendMessageToAi(selectedModel, chatList, userPrompt, new AiMentorService.AiResponseCallback() {
            @Override
            public void onSuccess(String aiReply) {
                isAiThinking = false;
                // Remove typing indicator
                if (!chatList.isEmpty() && chatList.get(chatList.size() - 1).isTyping()) {
                    chatList.remove(chatList.size() - 1);
                }

                // Add AI Reply
                ChatMessageModel aiMessage = new ChatMessageModel(aiReply, false);
                chatList.add(aiMessage);
                chatAdapter.notifyDataSetChanged();
                rvChatMessages.scrollToPosition(chatList.size() - 1);
            }

            @Override
            public void onError(String errorMessage) {
                isAiThinking = false;
                // Remove typing indicator
                if (!chatList.isEmpty() && chatList.get(chatList.size() - 1).isTyping()) {
                    chatList.remove(chatList.size() - 1);
                }

                // Add Error Message
                ChatMessageModel errorMsg = new ChatMessageModel("⚠️ " + errorMessage, false);
                chatList.add(errorMsg);
                chatAdapter.notifyDataSetChanged();
                rvChatMessages.scrollToPosition(chatList.size() - 1);

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}