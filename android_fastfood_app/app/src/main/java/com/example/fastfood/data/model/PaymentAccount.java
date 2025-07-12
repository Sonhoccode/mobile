package com.example.fastfood.data.model;

public class PaymentAccount {
    private String _id; // hoặc id nếu backend trả về là id
    private String userPhone;
    private String cardHolder;
    private String cardNumber;
    private String expiry;
    private String cvv;
    private String type;

    // Constructor mặc định (không có tham số) - bắt buộc cho Retrofit/Gson
    public PaymentAccount() {}

    // Constructor đầy đủ tham số (bạn tự thêm vào)
    public PaymentAccount(String userPhone, String cardHolder, String cardNumber, String expiry, String cvv, String type) {
        this.userPhone = userPhone;
        this.cardHolder = cardHolder;
        this.cardNumber = cardNumber;
        this.expiry = expiry;
        this.cvv = cvv;
        this.type = type;
    }

    // ... getter/setter bên dưới
    public String get_id() { return _id; }
    public void set_id(String _id) { this._id = _id; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
