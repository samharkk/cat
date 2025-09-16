package com.example.myapplication;

public class User {
    private int id;
    private String username;
    private String password;
    private String avatar;
    private String createTime;

    public User() {}

    public User(String username, String password, String avatar) {
        this.username = username;
        this.password = password;
        this.avatar = avatar;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    @Override
    public String toString() {
        return username + " (" + getAvatarName() + ")";
    }

    private String getAvatarName() {
        switch (avatar) {
            case "cat1": return "橘猫";
            case "cat2": return "白猫";
            case "cat3": return "黑猫";
            default: return "猫咪";
        }
    }
}
