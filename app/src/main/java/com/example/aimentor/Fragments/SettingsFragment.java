package com.example.aimentor.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.aimentor.R;
import com.example.aimentor.models.UserModel;
import com.example.aimentor.repository.UserRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SettingsFragment extends Fragment {

    private TextView tvDisplayUsername, tvDisplayRole, tvDisplayUserId, tvDisplayDateJoined;
    private EditText edtUsername, edtEmail, edtPhone, edtCurrentPassword, edtNewPassword;
    private ImageView imgAvatar;
    private Button btnSaveProfile;
    private UserRepository userRepository;
    private SharedPreferences sharedPreferences;
    private int userId;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        saveAvatarToInternalStorage(uri);
                    }
                }
            }
    );

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRepository = new UserRepository(getContext());
        if (getActivity() != null) {
            sharedPreferences = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            userId = sharedPreferences.getInt("ID_USER", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    private com.google.android.material.materialswitch.MaterialSwitch switchDarkMode;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        tvDisplayUsername = view.findViewById(R.id.tvDisplayUsername);
        tvDisplayRole = view.findViewById(R.id.tvDisplayRole);
        tvDisplayUserId = view.findViewById(R.id.tvDisplayUserId);
        tvDisplayDateJoined = view.findViewById(R.id.tvDisplayDateJoined);
        edtUsername = view.findViewById(R.id.edtUsername);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        edtCurrentPassword = view.findViewById(R.id.edtCurrentPassword);
        edtNewPassword = view.findViewById(R.id.edtNewPassword);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
        switchDarkMode = view.findViewById(R.id.switchDarkMode);

        // Initialize theme switch state
        if (switchDarkMode != null && getContext() != null) {
            boolean isDark = com.example.aimentor.utils.ThemeUtils.isDarkMode(getContext());
            switchDarkMode.setChecked(isDark);
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                com.example.aimentor.utils.ThemeUtils.setDarkMode(getContext(), isChecked);
                Toast.makeText(getContext(), isChecked ? "Switched to Dark Mode 🌙" : "Switched to Light Mode ☀️", Toast.LENGTH_SHORT).show();
            });
        }

        loadUserProfile();

        imgAvatar.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        btnSaveProfile.setOnClickListener(v -> saveUserProfile());
    }

    private void loadUserProfile() {
        if (userId == -1) {
            Toast.makeText(getContext(), "User details not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        UserModel user = userRepository.getUserById(userId);
        if (user != null) {
            tvDisplayUsername.setText(user.getUsername());
            tvDisplayUserId.setText(String.valueOf(user.getId()));
            
            String roleText = "ROLE: Student";
            if (user.getRole() == 3) {
                roleText = "ROLE: Administrator";
            } else if (user.getRole() == 2) {
                roleText = "ROLE: Faculty";
            }
            tvDisplayRole.setText(roleText);

            if (!TextUtils.isEmpty(user.getCreatedAt())) {
                String dateJoined = user.getCreatedAt();
                if (dateJoined.length() > 10) {
                    dateJoined = dateJoined.substring(0, 10);
                }
                tvDisplayDateJoined.setText(dateJoined);
            } else {
                tvDisplayDateJoined.setText("N/A");
            }

            edtUsername.setText(user.getUsername());
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());

            loadAvatarImage(user.getAvatar());
        }
    }

    private void loadAvatarImage(String path) {
        if (getContext() == null) return;
        
        if (TextUtils.isEmpty(path)) {
            setDefaultAvatar();
        } else {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                if (bitmap != null) {
                    imgAvatar.setImageBitmap(bitmap);
                    imgAvatar.setImageTintList(null);
                } else {
                    setDefaultAvatar();
                }
            } else {
                setDefaultAvatar();
            }
        }
    }

    private void setDefaultAvatar() {
        if (getContext() == null) return;
        imgAvatar.setImageResource(R.drawable.account_icon);
        imgAvatar.setImageTintList(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(requireContext(), R.color.accent_blue)
        ));
    }

    private void saveAvatarToInternalStorage(Uri uri) {
        if (getContext() == null || userId == -1) return;

        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        Bitmap originalBitmap = null;
        Bitmap croppedBitmap = null;
        try {
            inputStream = getContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return;

            originalBitmap = BitmapFactory.decodeStream(inputStream);
            if (originalBitmap == null) {
                Toast.makeText(getContext(), "Failed to decode image.", Toast.LENGTH_SHORT).show();
                return;
            }

            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();
            int size = Math.min(width, height);
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            croppedBitmap = Bitmap.createBitmap(originalBitmap, x, y, size, size);

            File avatarDir = new File(getContext().getFilesDir(), "avatars");
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }

            File avatarFile = new File(avatarDir, "avatar_user_" + userId + ".jpg");
            outputStream = new FileOutputStream(avatarFile);
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);

            String avatarPath = avatarFile.getAbsolutePath();
            long result = userRepository.updateUserAvatar(userId, avatarPath);
            if (result > 0) {
                loadAvatarImage(avatarPath);
                Toast.makeText(getContext(), "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (originalBitmap != null && !originalBitmap.isRecycled()) originalBitmap.recycle();
            if (croppedBitmap != null && !croppedBitmap.isRecycled()) croppedBitmap.recycle();
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserProfile() {
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String currentPassword = edtCurrentPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Username is required");
            return;
        }
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email is required");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Invalid email format");
            return;
        }
        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Phone number is required");
            return;
        }
        if (TextUtils.isEmpty(currentPassword)) {
            edtCurrentPassword.setError("Password required to confirm changes 🔒");
            return;
        }

        // Validate security password before saving
        boolean isValidPassword = userRepository.validatePassword(userId, currentPassword);
        if (!isValidPassword) {
            edtCurrentPassword.setError("Incorrect password! Cannot save profile settings.");
            Toast.makeText(getContext(), "❌ Incorrect password verification failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = userRepository.updateUserFullProfile(userId, username, email, phone);
        
        String newPassword = edtNewPassword.getText().toString().trim();
        if (!TextUtils.isEmpty(newPassword)) {
            if (!java.util.regex.Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z\\d]).{8,}$", newPassword)) {
                edtNewPassword.setError("Password needs at least 8 chars, uppercase, lowercase, numbers, and special chars");
                return;
            }
            userRepository.updateUserPassword(userId, newPassword);
            Toast.makeText(getContext(), "🔐 Password updated successfully!", Toast.LENGTH_SHORT).show();
            edtNewPassword.setText("");
        }

        if (result > 0) {
            Toast.makeText(getContext(), "✅ Profile updated successfully!", Toast.LENGTH_SHORT).show();
            edtCurrentPassword.setText("");
            
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("USERNAME_USER", username);
                editor.putString("EMAIL_USER", email);
                editor.apply();
            }
            loadUserProfile();
        } else {
            Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
        }
    }
}