package com.example.aimentor.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aimentor.MainActivity;
import com.example.aimentor.R;
import com.example.aimentor.models.UserModel;
import com.example.aimentor.repository.UserRepository;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.android.material.textfield.TextInputLayout;
import android.widget.LinearLayout;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout tilUsername, tilPassword;
    EditText edtUsername, edtPassword;
    Button btnLogin;
    TextView tvForgotPassword;
    UserRepository userRepository;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        com.example.aimentor.utils.ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_layout_login);
        btnLogin = findViewById(R.id.btnSubmit); 
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        
        userRepository = new UserRepository(LoginActivity.this);

        TextView tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showForgotPasswordDialog();
            }
        });
        
        checkLoginUser();
    }
    
    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Password Recovery");
        builder.setMessage("Please enter your Username and Email to create a new password.");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        final EditText inputUser = new EditText(this);
        inputUser.setHint("Username");
        layout.addView(inputUser);

        final EditText inputEmail = new EditText(this);
        inputEmail.setHint("Registered Email");
        layout.addView(inputEmail);

        final EditText inputNewPassword = new EditText(this);
        inputNewPassword.setHint("New Password");
        inputNewPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(inputNewPassword);

        builder.setView(layout);

        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String u = inputUser.getText().toString().trim();
                String e = inputEmail.getText().toString().trim();
                String p = inputNewPassword.getText().toString().trim();
                
                if (TextUtils.isEmpty(u) || TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
                    Toast.makeText(LoginActivity.this, "Fields cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                if (p.length() < 8) {
                    Toast.makeText(LoginActivity.this, "New password must be at least 8 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                // Reset password with user's new password
                boolean success = userRepository.resetPassword(u, e, p);
                if (success) {
                    Toast.makeText(LoginActivity.this, "✅ Password changed successfully! Please log in again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "❌ Username or Email does not match our records!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private void checkLoginUser(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                tilUsername.setError(null);
                tilPassword.setError(null);
                
                if (TextUtils.isEmpty(username)){
                    tilUsername.setError("Username cannot be empty");
                    return;
                }
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(password)){
                    tilPassword.setError("Password cannot be empty");
                    return;
                }
                // xu ly kiem tra xem tai khoan co ton tai trong co so du lieu hay ko?
                UserModel user = userRepository.loginUser(username, password);
                if (user != null && user.getId() > 0 && !TextUtils.isEmpty(user.getUsername())){
                    // dang nhap thanh cong
                    // luu thong tin tai khoan - de xu ly o nhung man hinh khac
                    SharedPreferences sharePf = getSharedPreferences("USER_INFO", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharePf.edit();
                    editor.putInt("ID_USER", user.getId());
                    editor.putString("USERNAME_USER", user.getUsername());
                    editor.putString("EMAIL_USER", user.getEmail());
                    editor.putInt("ROLE_USER", user.getRole());
                    editor.apply();
                    // chuyen sang man hinh menu
                    Intent menu = new Intent(LoginActivity.this, MenuActivity.class);
                    startActivity(menu);
                    finish();
                } else {
                    // dang nhap that bai
                    tilPassword.setError("Invalid credentials or incorrect password!");
                }
            }
        });
    }
    private void checkLoginWithFileData(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)){
                    edtUsername.setError("Username is required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    edtPassword.setError("Password is required");
                    return;
                }
                // xu ly doc du lieu tu file
                try {
                    FileInputStream inputStream = openFileInput("account.txt");
                    StringBuilder builder = new StringBuilder();
                    int read = -1;
                    while ((read = inputStream.read()) != -1){
                        builder.append((char) read);
                    }
                    inputStream.close();// dong file da mo lai
                    String[] infoAccount = null; // mang rong chua thong tin tai khoan
                    infoAccount = builder.toString().trim().split("\n");
                    boolean checkLogin = false;
                    for (int i = 0; i <infoAccount.length; i++){
                        String user = infoAccount[i].substring(0, infoAccount[i].indexOf("|"));
                        String pass = infoAccount[i].substring(infoAccount[i].indexOf("|")+1);
                        if (user.equals(username) && pass.equals(password)){
                            checkLogin = true;
                            break;
                        }
                    }
                    if (checkLogin){
                        // dang nhap thanh cong
                        Intent intentLogin = new Intent(LoginActivity.this, MenuActivity.class);
                        startActivity(intentLogin);
                        finish();// khong cho back lai
                    } else {
                        // dang nhap that bai - sai thong tin tai khoan
                        Toast.makeText(LoginActivity.this, "Account Invalid",Toast.LENGTH_SHORT).show();
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
