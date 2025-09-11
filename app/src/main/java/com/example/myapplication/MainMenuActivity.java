package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenuActivity extends AppCompatActivity {

    private CircleImageView ivAvatar;
    private TextView tvWelcome;
    private Button btnChat, btnUserManager, btnGame, btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ivAvatar = findViewById(R.id.iv_avatar);
        tvWelcome = findViewById(R.id.tv_welcome);
        btnChat = findViewById(R.id.btn_chat);
        btnUserManager = findViewById(R.id.btn_user_manager);
        btnGame = findViewById(R.id.btn_game);
        btnLogout = findViewById(R.id.btn_logout);

        // 显示用户信息
        loadUserInfo();

        // 按钮点击事件
        btnChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        btnUserManager.setOnClickListener(v -> startActivity(new Intent(this, UserManagerActivity.class)));
        btnGame.setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserInfo() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        String username = sp.getString("username", "用户");
        String avatar = sp.getString("avatar", "cat1");

        tvWelcome.setText("欢迎, " + username + "!");

        // 设置头像
        int avatarRes = R.drawable.cat1;
        if ("cat2".equals(avatar)) avatarRes = R.drawable.cat2;
        else if ("cat3".equals(avatar)) avatarRes = R.drawable.cat3;

        ivAvatar.setImageResource(avatarRes);
    }

    private void logout() {
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        sp.edit().clear().apply();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
