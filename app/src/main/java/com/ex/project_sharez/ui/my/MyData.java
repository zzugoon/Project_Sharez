package com.ex.project_sharez.ui.my;

public class MyData {

    private String userId;
    private String userName;
    private String userAddress;
    private String userImage;

    public MyData(String userId, String userName, String userAddress) {
        this.userId = userId;
        this.userName = userName;
        this.userAddress = userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }
}
