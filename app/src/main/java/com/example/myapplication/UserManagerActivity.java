package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class UserManagerActivity extends AppCompatActivity {

    private ListView lvUsers;
    private Button btnAddUser, btnBack; // 添加返回按钮
    private List<String> userList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("用户管理");
        }

        lvUsers = findViewById(R.id.lv_users);
        btnAddUser = findViewById(R.id.btn_add_user);
        btnBack = findViewById(R.id.btn_back); // 添加这行

        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());

        // 初始化用户列表
        userList = new ArrayList<>();
        userList.add("小明 (xiaoming)");
        userList.add("小红 (xiaohong)");
        userList.add("小猫 (xiaocat)");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        lvUsers.setAdapter(adapter);

        // 添加用户
        btnAddUser.setOnClickListener(v -> showAddUserDialog());

        // 长按删除用户
        lvUsers.setOnItemLongClickListener((parent, view, position, id) -> {
            showDeleteDialog(position);
            return true;
        });
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加用户");

        EditText input = new EditText(this);
        input.setHint("请输入用户名");
        builder.setView(input);

        builder.setPositiveButton("添加", (dialog, which) -> {
            String username = input.getText().toString().trim();
            if (!username.isEmpty()) {
                userList.add(username + " (新用户)");
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "用户添加成功", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(this)
                .setTitle("删除用户")
                .setMessage("确定删除 " + userList.get(position) + " 吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    userList.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "用户已删除", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
