package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private ListView lvChat;
    private EditText etMessage;
    private Button btnSend;
    private List<String> messageList;
    private ArrayAdapter<String> adapter;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 设置标题栏
        try {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("猫咪聊天");
            }
        } catch (Exception e) {
            // 忽略ActionBar错误
        }

        // 获取用户名
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        username = sp.getString("username", "用户");

        // 初始化控件
        initViews();

        // 初始化聊天数据
        initChatData();

        // 设置点击事件
        setupClickListeners();
    }

    private void initViews() {
        lvChat = findViewById(R.id.lv_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);

        // 检查控件是否找到
        if (lvChat == null || etMessage == null || btnSend == null) {
            Toast.makeText(this, "界面加载失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initChatData() {
        messageList = new ArrayList<>();
        messageList.add("🐱 喵喵: 欢迎来到猫咪聊天室！");
        messageList.add("🐱 喵喵: 有什么想聊的吗？");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageList);
        lvChat.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                // 添加用户消息
                messageList.add("👤 " + username + ": " + message);

                // 添加猫咪回复
                String[] replies = {
                        "喵~ 我听到了！",
                        "有趣！继续说~",
                        "喵喵喵！",
                        "我也这么想！",
                        "真的吗？好棒！"
                };
                String reply = replies[(int)(Math.random() * replies.length)];
                messageList.add("🐱 喵喵: " + reply);

                adapter.notifyDataSetChanged();
                etMessage.setText("");

                // 滚动到最新消息
                lvChat.smoothScrollToPosition(messageList.size() - 1);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
