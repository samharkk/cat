package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private RadioGroup rgAvatar;
    private Button btnLogin;
    private ImageView ivAvatarPreview; // 头像预览
    private ProgressBar pbLoading;     // 进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        rgAvatar = findViewById(R.id.rg_avatar);
        btnLogin = findViewById(R.id.btn_login);
        ivAvatarPreview = findViewById(R.id.iv_avatar_preview);
        pbLoading = findViewById(R.id.pb_loading);

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

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

            // 显示进度条，禁用登录按钮
            pbLoading.setVisibility(ProgressBar.VISIBLE);
            btnLogin.setEnabled(false);
            btnLogin.setText("登录中...");

            // 模拟登录延迟（2秒）
            new Handler().postDelayed(() -> {
                // 隐藏进度条，恢复按钮
                pbLoading.setVisibility(ProgressBar.GONE);
                btnLogin.setEnabled(true);
                btnLogin.setText("登录");

                // 获取选择的头像
                int selectedId = rgAvatar.getCheckedRadioButtonId();
                String avatar = "cat1"; // 默认头像
                if (selectedId == R.id.rb_cat2) avatar = "cat2";
                else if (selectedId == R.id.rb_cat3) avatar = "cat3";

                // 保存用户信息
                SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
                sp.edit()
                        .putString("username", username)
                        .putString("avatar", avatar)
                        .apply();

                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();

                // 跳转到主菜单
                Intent intent = new Intent(this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }, 2000); // 延迟2秒
        });
    }
}
