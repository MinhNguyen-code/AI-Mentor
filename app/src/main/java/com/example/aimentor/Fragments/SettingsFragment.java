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
    private EditText edtEmail, edtPhone;
    private ImageView imgAvatar;
    private Button btnSaveProfile;
    private UserRepository userRepository;
    private SharedPreferences sharedPreferences;
    private int userId;

    // Register ActivityResultLauncher for gallery picking
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
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        tvDisplayUsername = view.findViewById(R.id.tvDisplayUsername);
        tvDisplayRole = view.findViewById(R.id.tvDisplayRole);
        tvDisplayUserId = view.findViewById(R.id.tvDisplayUserId);
        tvDisplayDateJoined = view.findViewById(R.id.tvDisplayDateJoined);
        edtEmail = view.findViewById(R.id.edtEmail);
        edtPhone = view.findViewById(R.id.edtPhone);
        imgAvatar = view.findViewById(R.id.imgAvatar);
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile);

        // Load profile data
        loadUserProfile();

        // Handle avatar click to change picture
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageLauncher.launch("image/*");
            }
        });

        // Handle save action
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
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
            
            // Format friendly role text
            String roleText = "ROLE: Student";
            if (user.getRole() == 3) {
                roleText = "ROLE: Administrator";
            } else if (user.getRole() == 2) {
                roleText = "ROLE: Faculty";
            }
            tvDisplayRole.setText(roleText);

            // Display registration date
            if (!TextUtils.isEmpty(user.getCreatedAt())) {
                String dateJoined = user.getCreatedAt();
                if (dateJoined.length() > 10) {
                    dateJoined = dateJoined.substring(0, 10);
                }
                tvDisplayDateJoined.setText(dateJoined);
            } else {
                tvDisplayDateJoined.setText("N/A");
            }

            // Fill inputs
            edtEmail.setText(user.getEmail());
            edtPhone.setText(user.getPhone());

            // Load avatar image
            loadAvatarImage(user.getAvatar());
        } else {
            Toast.makeText(getContext(), "Failed to load profile data.", Toast.LENGTH_SHORT).show();
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

            // Decode image stream into a Bitmap
            originalBitmap = BitmapFactory.decodeStream(inputStream);
            if (originalBitmap == null) {
                Toast.makeText(getContext(), "Failed to decode selected image.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crop image programmatically to a 1:1 square ratio
            int width = originalBitmap.getWidth();
            int height = originalBitmap.getHeight();
            int size = Math.min(width, height);
            int x = (width - size) / 2;
            int y = (height - size) / 2;
            croppedBitmap = Bitmap.createBitmap(originalBitmap, x, y, size, size);

            // Create target file in internal storage /avatars
            File avatarDir = new File(getContext().getFilesDir(), "avatars");
            if (!avatarDir.exists()) {
                avatarDir.mkdirs();
            }

            File avatarFile = new File(avatarDir, "avatar_user_" + userId + ".jpg");
            outputStream = new FileOutputStream(avatarFile);

            // Compress cropped bitmap to JPEG and save it (85% quality to save space)
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);

            // Save new path in the database
            String avatarPath = avatarFile.getAbsolutePath();
            long result = userRepository.updateUserAvatar(userId, avatarPath);
            if (result > 0) {
                // Refresh local view
                loadAvatarImage(avatarPath);
                Toast.makeText(getContext(), "Avatar updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to save avatar path to database.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving avatar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            // Clean up resources and recycle bitmaps to free memory
            if (originalBitmap != null && !originalBitmap.isRecycled()) {
                originalBitmap.recycle();
            }
            if (croppedBitmap != null && !croppedBitmap.isRecycled()) {
                croppedBitmap.recycle();
            }
            try {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserProfile() {
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Inputs Validation
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

        long result = userRepository.updateUserProfile(userId, email, phone);
        if (result > 0) {
            Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            
            // Sync with SharedPreferences
            if (sharedPreferences != null) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("EMAIL_USER", email);
                editor.apply();
            }
        } else {
            Toast.makeText(getContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
        }
    }
}