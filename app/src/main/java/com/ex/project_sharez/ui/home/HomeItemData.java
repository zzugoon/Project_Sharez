package com.ex.project_sharez.ui.home;

import android.graphics.Bitmap;

public class HomeItemData {
    private String writeuser;
    private String address;

    private String title;
    private String category;
    private String location;
    private String price;
    private String submit;
    private String substance;
    private String[] imagesPath;
    private Bitmap image;
    private Bitmap[] images;

    // 생성자
    public HomeItemData() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() { return category; }

    public String getLocation() {
        return location;
    }

    public String getPrice() {
        return price;
    }

    public String getSubstance() {
        return substance;
    }

    public String getSubmit() {
        return submit;
    }

    public String[] getImagesPath() { return imagesPath; }


    public String getWriteuser() {
        return writeuser;
    }

    public void setWriteuser(String writeuser) {
        this.writeuser = writeuser;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) { this.category = category; }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setSubstance(String substance) {
        this.substance = substance;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public void setImagesPath(String[] imagesPath) {
        this.imagesPath = imagesPath;
    }

    public int getSize() {
        return imagesPath.length;
    }
}
