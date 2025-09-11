package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private RadioGroup rgAvatar;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        rgAvatar = findViewById(R.id.rg_avatar);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                return;
            }

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

            // 跳转到主菜单
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
