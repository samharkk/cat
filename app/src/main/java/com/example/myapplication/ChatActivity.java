package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
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
    private Button btnSend, btnBack;
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
                getSupportActionBar().hide(); // 隐藏默认标题栏，使用自定义的
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
        btnBack = findViewById(R.id.btn_back);

        // 检查控件是否找到
        if (lvChat == null || etMessage == null || btnSend == null || btnBack == null) {
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
        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());

        // 发送按钮点击事件
        btnSend.setOnClickListener(v -> sendMessage());

        // 输入框回车键发送消息
        etMessage.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();

        // 检查消息是否为空
        if (message.isEmpty()) {
            Toast.makeText(this, "请输入消息内容", Toast.LENGTH_SHORT).show();
            return;
        }

        // 添加用户消息
        messageList.add("👤 " + username + ": " + message);

        // 生成猫咪回复
        String[] replies = {
                "喵~ 我听到了！",
                "有趣！继续说~",
                "喵喵喵！",
                "我也这么想！",
                "真的吗？好棒！",
                "哈哈，你说得对呢~",
                "让我想想... 嗯！",
                "太棒了！继续聊吧~",
                "喵呜~ 好开心！"
        };

        String reply = replies[(int)(Math.random() * replies.length)];
        messageList.add("🐱 喵喵: " + reply);

        // 更新列表
        adapter.notifyDataSetChanged();

        // 清空输入框
        etMessage.setText("");

        // 滚动到最新消息
        lvChat.smoothScrollToPosition(messageList.size() - 1);

        // 显示发送成功提示
        Toast.makeText(this, "消息已发送", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
