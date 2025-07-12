package com.example.fastfood.data.model;

public class SupportRequest {
    private String phone;    // user phone
    private String content;  // nội dung hỗ trợ

    public SupportRequest(String phone, String content) {
        this.phone = phone;
        this.content = content;
    }

    // Getter và Setter
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
