package com.example.myapplication;

import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class UserManagerActivity extends AppCompatActivity {

    private ListView lvUsers;
    private Button btnAddUser, btnBack;
    private List<User> userList;
    private ArrayAdapter<User> adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manager);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("用户管理");
        }

        // 初始化数据库
        dbHelper = new DatabaseHelper(this);

        lvUsers = findViewById(R.id.lv_users);
        btnAddUser = findViewById(R.id.btn_add_user);
        btnBack = findViewById(R.id.btn_back);

        // 返回按钮点击事件
        btnBack.setOnClickListener(v -> finish());

        // 初始化用户列表
        loadUsers();

        // 添加用户
        btnAddUser.setOnClickListener(v -> showAddUserDialog());

        // 长按编辑用户
        lvUsers.setOnItemLongClickListener((parent, view, position, id) -> {
            showUserOptionsDialog(userList.get(position), position);
            return true;
        });

        // 短按查看用户信息
        lvUsers.setOnItemClickListener((parent, view, position, id) -> {
            showUserInfoDialog(userList.get(position));
        });
    }

    private void loadUsers() {
        userList = dbHelper.getAllUsers();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        lvUsers.setAdapter(adapter);
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("添加新用户");

        // 创建输入布局
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // 用户名输入
        EditText etUsername = new EditText(this);
        etUsername.setHint("请输入用户名");
        layout.addView(etUsername);

        // 密码输入
        EditText etPassword = new EditText(this);
        etPassword.setHint("请输入密码");
        etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(etPassword);

        // 头像选择
        RadioGroup rgAvatar = new RadioGroup(this);
        rgAvatar.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton rbCat1 = new RadioButton(this);
        rbCat1.setText("橘猫");
        rbCat1.setId(1);
        rbCat1.setChecked(true);

        RadioButton rbCat2 = new RadioButton(this);
        rbCat2.setText("白猫");
        rbCat2.setId(2);

        RadioButton rbCat3 = new RadioButton(this);
        rbCat3.setText("黑猫");
        rbCat3.setId(3);

        rgAvatar.addView(rbCat1);
        rgAvatar.addView(rbCat2);
        rgAvatar.addView(rbCat3);
        layout.addView(rgAvatar);

        builder.setView(layout);

        builder.setPositiveButton("添加", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取选择的头像
            String avatar = "cat1";
            int selectedId = rgAvatar.getCheckedRadioButtonId();
            if (selectedId == 2) avatar = "cat2";
            else if (selectedId == 3) avatar = "cat3";

            // 添加到数据库
            if (dbHelper.addUser(username, password, avatar)) {
                Toast.makeText(this, "用户添加成功", Toast.LENGTH_SHORT).show();
                loadUsers(); // 刷新列表
            } else {
                Toast.makeText(this, "用户名已存在", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showUserOptionsDialog(User user, int position) {
        String[] options = {"查看信息", "编辑用户", "删除用户"};

        new AlertDialog.Builder(this)
                .setTitle("用户操作: " + user.getUsername())
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            showUserInfoDialog(user);
                            break;
                        case 1:
                            showEditUserDialog(user);
                            break;
                        case 2:
                            showDeleteDialog(user);
                            break;
                    }
                })
                .show();
    }

    private void showUserInfoDialog(User user) {
        String info = "用户名: " + user.getUsername() + "\n" +
                "密码: " + user.getPassword() + "\n" +
                "头像: " + getAvatarName(user.getAvatar()) + "\n" +
                "创建时间: " + user.getCreateTime();

        new AlertDialog.Builder(this)
                .setTitle("用户信息")
                .setMessage(info)
                .setPositiveButton("确定", null)
                .show();
    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("编辑用户: " + user.getUsername());

        // 创建输入布局
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 10);

        // 用户名输入
        EditText etUsername = new EditText(this);
        etUsername.setText(user.getUsername());
        etUsername.setHint("用户名");
        layout.addView(etUsername);

        // 密码输入
        EditText etPassword = new EditText(this);
        etPassword.setText(user.getPassword());
        etPassword.setHint("密码");
        layout.addView(etPassword);

        // 头像选择
        RadioGroup rgAvatar = new RadioGroup(this);
        rgAvatar.setOrientation(RadioGroup.HORIZONTAL);

        RadioButton rbCat1 = new RadioButton(this);
        rbCat1.setText("黑猫");
        rbCat1.setId(1);
        if ("cat1".equals(user.getAvatar())) rbCat1.setChecked(true);

        RadioButton rbCat2 = new RadioButton(this);
        rbCat2.setText("橘猫");
        rbCat2.setId(2);
        if ("cat2".equals(user.getAvatar())) rbCat2.setChecked(true);

        RadioButton rbCat3 = new RadioButton(this);
        rbCat3.setText("花猫");
        rbCat3.setId(3);
        if ("cat3".equals(user.getAvatar())) rbCat3.setChecked(true);

        rgAvatar.addView(rbCat1);
        rgAvatar.addView(rbCat2);
        rgAvatar.addView(rbCat3);
        layout.addView(rgAvatar);

        builder.setView(layout);

        builder.setPositiveButton("保存", (dialog, which) -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取选择的头像
            String avatar = "cat1";
            int selectedId = rgAvatar.getCheckedRadioButtonId();
            if (selectedId == 2) avatar = "cat2";
            else if (selectedId == 3) avatar = "cat3";

            // 更新数据库
            if (dbHelper.updateUser(user.getId(), username, password, avatar)) {
                Toast.makeText(this, "用户更新成功", Toast.LENGTH_SHORT).show();
                loadUsers(); // 刷新列表
            } else {
                Toast.makeText(this, "更新失败", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showDeleteDialog(User user) {
        String message = "确定要删除用户 \"" + user.getUsername() + "\" 吗？\n注意: 此操作不可恢复！";

        new AlertDialog.Builder(this)
                .setTitle("删除用户")
                .setMessage(message)
                .setPositiveButton("删除", (dialog, which) -> {
                    if (dbHelper.deleteUser(user.getId())) {
                        Toast.makeText(this, "用户已删除", Toast.LENGTH_SHORT).show();
                        loadUsers(); // 刷新列表
                    } else {
                        Toast.makeText(this, "删除失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private String getAvatarName(String avatar) {
        switch (avatar) {
            case "cat1": return "黑猫";
            case "cat2": return "橘猫";
            case "cat3": return "花猫";
            default: return "猫咪";
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}