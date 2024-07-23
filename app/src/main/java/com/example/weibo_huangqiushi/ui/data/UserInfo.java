package com.example.weibo_huangqiushi.ui.data;

public class UserInfo {
    private Long userId;
    private String username;
    private String phone;
    private String avatar;
    private Boolean loginStatus;

    public UserInfo(Long id, String username, String phone, String avatar, Boolean loginStatus) {
        this.userId = id;
        this.username = username;
        this.phone = phone;
        this.avatar = avatar;
        this.loginStatus = loginStatus;
    }

    public Long getId() {
        return userId;
    }

    public void setId(Long id) {
        this.userId = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(Boolean loginStatus) {
        this.loginStatus = loginStatus;
    }
}
