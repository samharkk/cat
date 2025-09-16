package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private RadioGroup rgAvatar;
    private Button btnLogin, btnRegister;
    private ImageView ivAvatarPreview;
    private ProgressBar pbLoading;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 初始化数据库
        dbHelper = new DatabaseHelper(this);

        // 检查是否已经登录
        if (isUserLoggedIn()) {
            jumpToMainMenu();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        rgAvatar = findViewById(R.id.rg_avatar);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        ivAvatarPreview = findViewById(R.id.iv_avatar_preview);
        pbLoading = findViewById(R.id.pb_loading);
    }

    private void setupListeners() {
        // 监听头像选择变化
        rgAvatar.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_cat1) {
                ivAvatarPreview.setImageResource(R.drawable.cat1);
            } else if (checkedId == R.id.rb_cat2) {
                ivAvatarPreview.setImageResource(R.drawable.cat2);
            } else if (checkedId == R.id.rb_cat3) {
                ivAvatarPreview.setImageResource(R.drawable.cat3);
            }
        });

        // 登录按钮
        btnLogin.setOnClickListener(v -> handleLogin());

        // 注册按钮
        btnRegister.setOnClickListener(v -> showRegisterDialog());
    }

    private boolean isUserLoggedIn() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = sp.getString("username", "");
        return !username.isEmpty();
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }

        // 显示进度条，禁用按钮
        showLoading(true);

        // 模拟登录延迟（1.5秒）
        new Handler().postDelayed(() -> {
            // 验证用户
            if (dbHelper.validateUser(username, password)) {
                // 获取用户信息
                User user = dbHelper.getUserInfo(username);
                if (user != null) {
                    // 保存用户登录状态
                    saveUserLoginState(user);

                    Toast.makeText(this, "登录成功！欢迎回来 " + username, Toast.LENGTH_SHORT).show();

                    // 跳转到主菜单
                    jumpToMainMenu();
                } else {
                    showLoading(false);
                    Toast.makeText(this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                showLoading(false);
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
            }
        }, 1500);
    }

    private void showRegisterDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("注册新用户");

        // 创建输入布局
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // 用户名输入
        EditText etRegUsername = new EditText(this);
        etRegUsername.setHint("请输入用户名 (3-20字符)");
        layout.addView(etRegUsername);

        // 密码输入
        EditText etRegPassword = new EditText(this);
        etRegPassword.setHint("请输入密码 (6-20字符)");
        etRegPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etRegPassword);

        // 确认密码输入
        EditText etConfirmPassword = new EditText(this);
        etConfirmPassword.setHint("请确认密码");
        etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etConfirmPassword);

        // 头像选择
        RadioGroup rgRegAvatar = new RadioGroup(this);
        rgRegAvatar.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton rbRegCat1 = new RadioButton(this);
        rbRegCat1.setText("黑猫");
        rbRegCat1.setId(1001);
        rbRegCat1.setChecked(true);

        RadioButton rbRegCat2 = new RadioButton(this);
        rbRegCat2.setText("橘猫");
        rbRegCat2.setId(1002);

        RadioButton rbRegCat3 = new RadioButton(this);
        rbRegCat3.setText("花猫");
        rbRegCat3.setId(1003);

        rgRegAvatar.addView(rbRegCat1);
        rgRegAvatar.addView(rbRegCat2);
        rgRegAvatar.addView(rbRegCat3);
        layout.addView(rgRegAvatar);

        builder.setView(layout);

        builder.setPositiveButton("注册", (dialog, which) -> {
            String username = etRegUsername.getText().toString().trim();
            String password = etRegPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            // 验证输入
            if (!validateRegisterInput(username, password, confirmPassword)) {
                return;
            }

            // 获取选择的头像
            String avatar = "cat1";
            int selectedId = rgRegAvatar.getCheckedRadioButtonId();
            if (selectedId == 1002) avatar = "cat2";
            else if (selectedId == 1003) avatar = "cat3";

            // 注册用户
            if (dbHelper.addUser(username, password, avatar)) {
                Toast.makeText(this, "注册成功！请登录", Toast.LENGTH_LONG).show();
                // 自动填充登录信息
                etUsername.setText(username);
                etPassword.setText(password);
                setAvatarSelection(avatar);
            } else {
                Toast.makeText(this, "注册失败，用户名可能已存在", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateRegisterInput(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (username.length() < 3 || username.length() > 20) {
            Toast.makeText(this, "用户名长度应为3-20字符", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.length() < 6 || password.length() > 20) {
            Toast.makeText(this, "密码长度应为6-20字符", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "两次输入的密码不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setAvatarSelection(String avatar) {
        switch (avatar) {
            case "cat1":
                rgAvatar.check(R.id.rb_cat1);
                ivAvatarPreview.setImageResource(R.drawable.cat1);
                break;
            case "cat2":
                rgAvatar.check(R.id.rb_cat2);
                ivAvatarPreview.setImageResource(R.drawable.cat2);
                break;
            case "cat3":
                rgAvatar.check(R.id.rb_cat3);
                ivAvatarPreview.setImageResource(R.drawable.cat3);
                break;
        }
    }

    private void saveUserLoginState(User user) {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        sp.edit()
                .putString("username", user.getUsername())
                .putString("avatar", user.getAvatar())
                .putInt("user_id", user.getId())
                .putLong("login_time", System.currentTimeMillis())
                .apply();
    }

    private void jumpToMainMenu() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        if (show) {
            pbLoading.setVisibility(ProgressBar.VISIBLE);
            btnLogin.setEnabled(false);
            btnRegister.setEnabled(false);
            btnLogin.setText("登录中...");
        } else {
            pbLoading.setVisibility(ProgressBar.GONE);
            btnLogin.setEnabled(true);
            btnRegister.setEnabled(true);
            btnLogin.setText("登录");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
