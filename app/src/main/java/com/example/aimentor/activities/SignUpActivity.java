package com.example.aimentor.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aimentor.R;
import com.example.aimentor.repository.UserRepository;
import com.example.aimentor.utils.GamificationManager;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {
    TextInputLayout tilFullName, tilUsername, tilPassword, tilConfirmPassword, tilEmail, tilPhone;
    EditText edtFullName, edtUsername, edtPassword, edtConfirmPassword, edtEmail, edtPhone;
    TextView tvPasswordStrength, tvLogin;
    Button btnSignup;
    UserRepository userRepository;
    
    // Regex Patterns
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,15}$";
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.example.aimentor.utils.ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Bind TextInputLayouts
        tilFullName = findViewById(R.id.tilFullName);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilEmail = findViewById(R.id.tilEmail);
        tilPhone = findViewById(R.id.tilPhone);

        // Bind EditTexts
        edtFullName = findViewById(R.id.edtFullName);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);

        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        
        ImageView btnThemeToggle = findViewById(R.id.btnThemeToggle);
        btnThemeToggle.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                boolean isDark = com.example.aimentor.utils.ThemeUtils.isDarkMode(SignUpActivity.this);
                com.example.aimentor.utils.ThemeUtils.setDarkMode(SignUpActivity.this, !isDark);
            }
        });

        btnSignup   = findViewById(R.id.btnSubmit);
        tvLogin     = findViewById(R.id.tvLogin);
        
        userRepository = new UserRepository(SignUpActivity.this);

        setupRealtimeValidation();

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(login); 
                finish();
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (!validateAllFields()) {
                    Toast.makeText(SignUpActivity.this, "Please check your inputs", Toast.LENGTH_SHORT).show();
                    return;
                }

                String user = edtUsername.getText().toString().trim();
                String pass = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();

                long insert = userRepository.saveUserAccount(user, pass, email, phone);
                if (insert == -1){
                    Toast.makeText(SignUpActivity.this, "Account already exists or system error", Toast.LENGTH_SHORT).show();
                    return;
                }

                GamificationManager.awardSignupBadge(SignUpActivity.this, (int) insert);

                Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupRealtimeValidation() {
        edtFullName.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    tilFullName.setError("Full name cannot be empty");
                } else {
                    tilFullName.setError(null);
                }
            }
        });

        edtUsername.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (!Pattern.matches(USERNAME_PATTERN, input)) {
                    tilUsername.setError("3-15 chars, no spaces, alphanumeric and _ only");
                } else {
                    tilUsername.setError(null);
                }
            }
        });

        edtPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (!Pattern.matches(PASSWORD_PATTERN, input)) {
                    tilPassword.setError("Password needs at least 8 characters, uppercase, lowercase, and numbers");
                    if (tvPasswordStrength != null) {
                        tvPasswordStrength.setText("Weak 🔴");
                        tvPasswordStrength.setTextColor(getResources().getColor(R.color.error));
                    }
                } else {
                    tilPassword.setError(null);
                    if (tvPasswordStrength != null) {
                        tvPasswordStrength.setText("Strong 🟢");
                        tvPasswordStrength.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                    }
                }
                
                String confirmInput = edtConfirmPassword.getText().toString();
                if (!confirmInput.isEmpty() && !confirmInput.equals(input)) {
                    tilConfirmPassword.setError("Passwords do not match");
                } else if (!confirmInput.isEmpty()) {
                    tilConfirmPassword.setError(null);
                }
            }
        });

        edtConfirmPassword.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                String pass = edtPassword.getText().toString();
                if (!input.equals(pass)) {
                    tilConfirmPassword.setError("Passwords do not match");
                } else {
                    tilConfirmPassword.setError(null);
                }
            }
        });

        edtEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();
                if (!Pattern.matches(EMAIL_PATTERN, input)) {
                    tilEmail.setError("Invalid email format (e.g., name@domain.com)");
                } else {
                    tilEmail.setError(null);
                }
            }
        });
        
        edtPhone.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    tilPhone.setError("Phone number cannot be empty");
                } else {
                    tilPhone.setError(null);
                }
            }
        });
    }

    private boolean validateAllFields() {
        boolean isValid = true;
        
        if (edtFullName.getText().toString().trim().isEmpty()) {
            tilFullName.setError("Full name cannot be empty");
            isValid = false;
        }
        if (!Pattern.matches(USERNAME_PATTERN, edtUsername.getText().toString())) {
            tilUsername.setError("3-15 chars, no spaces, alphanumeric and _ only");
            isValid = false;
        }
        if (!Pattern.matches(PASSWORD_PATTERN, edtPassword.getText().toString())) {
            tilPassword.setError("Password needs at least 8 chars, uppercase, lowercase, and numbers");
            isValid = false;
        }
        if (!edtConfirmPassword.getText().toString().equals(edtPassword.getText().toString())) {
            tilConfirmPassword.setError("Passwords do not match");
            isValid = false;
        }
        if (!Pattern.matches(EMAIL_PATTERN, edtEmail.getText().toString())) {
            tilEmail.setError("Invalid email format (e.g., name@domain.com)");
            isValid = false;
        }
        if (edtPhone.getText().toString().trim().isEmpty()) {
            tilPhone.setError("Phone number cannot be empty");
            isValid = false;
        }
        
        return isValid;
    }

    private abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void afterTextChanged(Editable s) {}
    }
}
