package com.example.aimentor.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aimentor.R;
import com.example.aimentor.adapters.ChatAdapter;
import com.example.aimentor.models.ChatMessageModel;
import com.example.aimentor.models.UserModel;
import com.example.aimentor.repository.ChatRepository;
import com.example.aimentor.repository.UserRepository;
import com.example.aimentor.services.AiMentorService;
import com.example.aimentor.utils.AiConfig;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {

    private RecyclerView rvChatMessages;
    private EditText edtChatMessage;
    private View btnSendMessage, btnSelectModel;
    private ImageView btnClearChat;
    private TextView tvSelectedModel, btnAttachImage, btnShowHistory;
    private TextView chipChat1, chipChat2, chipChat3, chipChat4, chipChat5;

    private static final List<ChatMessageModel> chatList = new ArrayList<>();
    private static String pendingPrompt = null;

    private ChatAdapter chatAdapter;
    private ChatRepository chatRepository;
    private UserRepository userRepository;
    private int userId = -1;
    private boolean isAiThinking = false;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Toast.makeText(getContext(), "📷 Homework photo attached! Ready to analyze.", Toast.LENGTH_SHORT).show();
                    if (edtChatMessage != null && TextUtils.isEmpty(edtChatMessage.getText().toString())) {
                        edtChatMessage.setText("📷 Please solve this attached homework diagram / equation.");
                    }
                }
            }
    );

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatRepository = new ChatRepository(getContext());
        userRepository = new UserRepository(getContext());
        if (getActivity() != null) {
            SharedPreferences prefs = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            userId = prefs.getInt("ID_USER", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        btnAttachImage = view.findViewById(R.id.btnAttachImage);
        btnShowHistory = view.findViewById(R.id.btnShowHistory);

        chipChat1 = view.findViewById(R.id.chipChat1);
        chipChat2 = view.findViewById(R.id.chipChat2);
        chipChat3 = view.findViewById(R.id.chipChat3);
        chipChat4 = view.findViewById(R.id.chipChat4);
        chipChat5 = view.findViewById(R.id.chipChat5);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(chatList);
        rvChatMessages.setAdapter(chatAdapter);

        // Bookmark listener
        chatAdapter.setOnBookmarkClickListener((msg, position) -> {
            boolean newBookmarkState = !msg.isBookmarked();
            msg.setBookmarked(newBookmarkState);
            if (msg.getId() > 0) {
                chatRepository.toggleBookmark(msg.getId(), newBookmarkState);
            }
            chatAdapter.notifyItemChanged(position);
            Toast.makeText(getContext(), newBookmarkState ? "⭐ Answer saved to Personal Library" : "Removed from Library", Toast.LENGTH_SHORT).show();
        });

        // Initialize Selected Model UI
        updateModelSelectorUi();

        if (btnSelectModel != null) {
            btnSelectModel.setOnClickListener(v -> showModelSelectionDialog());
        }

        // Image Attachment listener
        if (btnAttachImage != null) {
            btnAttachImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        }

        // Recent History Sidebar List listener (Gần đây)
        if (btnShowHistory != null) {
            btnShowHistory.setOnClickListener(v -> showChatHistoryDialog());
        }

        // Load Persistent Chat History from SQLite
        loadPersistentChatHistory();

        // Setup Send Button Listener
        btnSendMessage.setOnClickListener(v -> {
            String input = edtChatMessage.getText().toString().trim();
            if (!TextUtils.isEmpty(input)) {
                sendUserMessage(input);
            }
        });

        // Setup Clear Chat Listener
        btnClearChat.setOnClickListener(v -> {
            if (userId != -1) {
                chatRepository.clearChatHistory(userId);
            }
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
            pendingPrompt = null;
            sendUserMessage(promptToSend);
        }
    }

    private void loadPersistentChatHistory() {
        if (userId != -1) {
            List<ChatMessageModel> savedHistory = chatRepository.getChatHistory(userId);
            if (savedHistory != null && !savedHistory.isEmpty()) {
                chatList.clear();
                chatList.addAll(savedHistory);
                chatAdapter.notifyDataSetChanged();
                rvChatMessages.scrollToPosition(chatList.size() - 1);
                return;
            }
        }

        if (chatList.isEmpty()) {
            chatList.add(new ChatMessageModel(
                    "Hello! I am your BTEC AI Mentor 🤖.\n\nI am connected and ready to assist you with Programming (Java/C#), Database SQL, Web Development, Networking, Security, or any homework questions!\n\n💡 Tip: You can attach homework photos 📷, switch AI models, or bookmark ⭐ key answers.\n\nWhat would you like to explore today?",
                    false
            ));
            chatAdapter.notifyItemInserted(chatList.size() - 1);
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

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_model_selector_compact, null);
        LinearLayout containerModels = dialogView.findViewById(R.id.containerModels);

        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.Theme_AIMentor)
                .setView(dialogView)
                .create();

        for (int i = 0; i < AiConfig.MODEL_IDS.length; i++) {
            final String modelId = AiConfig.MODEL_IDS[i];
            String name = AiConfig.MODEL_SHORT_NAMES[i];
            String badge = AiConfig.MODEL_BADGES[i];

            View itemView = LayoutInflater.from(getContext()).inflate(R.layout.item_model_compact, containerModels, false);
            TextView tvModelName = itemView.findViewById(R.id.tvModelName);
            TextView tvModelBadge = itemView.findViewById(R.id.tvModelBadge);
            TextView tvCheckmark = itemView.findViewById(R.id.tvCheckmark);

            tvModelName.setText(name);
            tvModelBadge.setText(badge + " ⓘ");

            if (modelId.equals(currentModelId)) {
                tvCheckmark.setVisibility(View.VISIBLE);
                tvModelName.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.accent_blue));
                tvModelBadge.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.accent_blue));
            } else {
                tvCheckmark.setVisibility(View.GONE);
                tvModelName.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary));
                tvModelBadge.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary));
            }

            itemView.setOnClickListener(v -> {
                AiConfig.setSelectedModel(getContext(), modelId);
                updateModelSelectorUi();
                Toast.makeText(getContext(), "Selected: " + name, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });

            containerModels.addView(itemView);
        }

        dialog.show();
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
            chipChat5.setOnClickListener(v -> sendUserMessage("Quiz me! Give me 3 multiple choice questions on Java Programming (SD201) with answers and explanations."));
        }
    }

    private void sendUserMessage(String userPrompt) {
        if (isAiThinking) {
            Toast.makeText(getContext(), "AI is processing your previous question. Please wait...", Toast.LENGTH_SHORT).show();
            return;
        }

        String finalPrompt = userPrompt;
        if (selectedImageUri != null) {
            finalPrompt = "[📷 Homework Image Attached: " + selectedImageUri.getLastPathSegment() + "]\n" + userPrompt;
            selectedImageUri = null; // reset attachment after sending
        }

        // Get currently selected AI model and student preferences
        String selectedModel = AiConfig.getSelectedModel(getContext());
        String eduLevel = "University";
        String explanationStyle = "Step-by-Step";

        if (userId != -1) {
            UserModel user = userRepository.getUserById(userId);
            if (user != null) {
                if (!TextUtils.isEmpty(user.getEducationLevel())) eduLevel = user.getEducationLevel();
                if (!TextUtils.isEmpty(user.getExplanationStyle())) explanationStyle = user.getExplanationStyle();
            }
        }

        // 1. Add & Save User Message
        ChatMessageModel userMessage = new ChatMessageModel(finalPrompt, true);
        userMessage.setUserId(userId);
        userMessage.setModelUsed(selectedModel);
        if (userId != -1) {
            long insertedId = chatRepository.insertChatMessage(userId, finalPrompt, true, selectedModel);
            userMessage.setId(insertedId);
        }
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

        // 4. Call Groq AI API Service
        AiMentorService.sendMessageToAi(selectedModel, eduLevel, explanationStyle, chatList, finalPrompt, new AiMentorService.AiResponseCallback() {
            @Override
            public void onSuccess(String aiReply) {
                isAiThinking = false;
                // Remove typing indicator
                if (!chatList.isEmpty() && chatList.get(chatList.size() - 1).isTyping()) {
                    chatList.remove(chatList.size() - 1);
                }

                // Add & Save AI Reply
                ChatMessageModel aiMessage = new ChatMessageModel(aiReply, false);
                aiMessage.setUserId(userId);
                aiMessage.setModelUsed(selectedModel);
                if (userId != -1) {
                    long insertedId = chatRepository.insertChatMessage(userId, aiReply, false, selectedModel);
                    aiMessage.setId(insertedId);
                }

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

    private void showChatHistoryDialog() {
        if (getContext() == null || userId == -1) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_chat_history, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.Theme_AIMentor)
                .setView(dialogView)
                .create();

        RecyclerView rvHistoryList = dialogView.findViewById(R.id.rvHistoryList);
        EditText edtSearchHistory = dialogView.findViewById(R.id.edtSearchHistory);
        TextView tvEmptyHistory = dialogView.findViewById(R.id.tvEmptyHistory);
        TextView btnCloseHistory = dialogView.findViewById(R.id.btnCloseHistory);

        rvHistoryList.setLayoutManager(new LinearLayoutManager(getContext()));

        List<ChatMessageModel> allHistory = chatRepository.getChatHistory(userId);
        List<ChatMessageModel> filteredList = new ArrayList<>();
        if (allHistory != null) {
            filteredList.addAll(allHistory);
        }

        if (filteredList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            rvHistoryList.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            rvHistoryList.setVisibility(View.VISIBLE);
        }

        com.example.aimentor.adapters.ChatHistoryAdapter historyAdapter =
                new com.example.aimentor.adapters.ChatHistoryAdapter(filteredList);

        historyAdapter.setOnHistoryItemClickListener(selectedItem -> {
            dialog.dismiss();
            for (int i = 0; i < chatList.size(); i++) {
                ChatMessageModel msg = chatList.get(i);
                if (msg.getId() > 0 && msg.getId() == selectedItem.getId()) {
                    rvChatMessages.scrollToPosition(i);
                    Toast.makeText(getContext(), "Jumped to selected chat 💬", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        rvHistoryList.setAdapter(historyAdapter);

        // Real-time search filter
        edtSearchHistory.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                List<ChatMessageModel> searchResults = chatRepository.searchChatHistory(userId, query);
                filteredList.clear();
                if (searchResults != null) filteredList.addAll(searchResults);
                historyAdapter.notifyDataSetChanged();

                if (filteredList.isEmpty()) {
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                    rvHistoryList.setVisibility(View.GONE);
                } else {
                    tvEmptyHistory.setVisibility(View.GONE);
                    rvHistoryList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        btnCloseHistory.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}