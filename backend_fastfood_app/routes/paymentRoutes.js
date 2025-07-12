const express = require('express');
const router = express.Router();
const moment = require('moment');
const qs = require('qs');
const crypto = require('crypto');

// Hàm sắp xếp object
function sortObject(obj) {
    let sorted = {};
    let str = [];
    let key;
    for (key in obj) {
        if (obj.hasOwnProperty(key)) {
            str.push(encodeURIComponent(key));
        }
    }
    str.sort();
    for (key = 0; key < str.length; key++) {
        sorted[str[key]] = encodeURIComponent(obj[str[key]]).replace(/%20/g, "+");
    }
    return sorted;
}

// API TẠO URL THANH TOÁN
router.post('/create_payment_url', (req, res) => {
    process.env.TZ = 'Asia/Ho_Chi_Minh';

    let date = new Date();
    let createDate = moment(date).format('YYYYMMDDHHmmss');

    let ipAddr = req.headers['x-forwarded-for'] ||
        req.connection.remoteAddress ||
        req.socket.remoteAddress ||
        req.connection.socket.remoteAddress;

    let tmnCode = process.env.VNP_TMNCODE;
    let secretKey = process.env.VNP_HASHSECRET;
    let vnpUrl = process.env.VNP_URL;
    let returnUrl = process.env.VNP_RETURNURL;
    
    let orderId = moment(date).format('DDHHmmss');
    let amount = req.body.amount; // Lấy số tiền từ request
    let bankCode = req.body.bankCode; // Có thể để trống
    
    let orderInfo = req.body.orderDescription;
    let orderType = req.body.orderType || 'other'; 
    let locale = req.body.language;
    if (locale === null || locale === '' || !locale) {
        locale = 'vn';
    }
    let currCode = 'VND';

    let vnp_Params = {};
    vnp_Params['vnp_Version'] = '2.1.0';
    vnp_Params['vnp_Command'] = 'pay';
    vnp_Params['vnp_TmnCode'] = tmnCode;
    vnp_Params['vnp_Locale'] = locale;
    vnp_Params['vnp_CurrCode'] = currCode;
    vnp_Params['vnp_TxnRef'] = orderId;
    vnp_Params['vnp_OrderInfo'] = 'Thanh toan cho ma GD:' + orderId;
    vnp_Params['vnp_OrderType'] = orderType;
    vnp_Params['vnp_Amount'] = amount * 100; // Nhân 100 theo quy định của VNPay
    vnp_Params['vnp_ReturnUrl'] = returnUrl;
    vnp_Params['vnp_IpAddr'] = ipAddr;
    vnp_Params['vnp_CreateDate'] = createDate;
    if (bankCode !== null && bankCode !== '') {
        vnp_Params['vnp_BankCode'] = bankCode;
    }

    vnp_Params = sortObject(vnp_Params);

    let signData = qs.stringify(vnp_Params, { encode: false });
    let hmac = crypto.createHmac("sha512", secretKey);
    let signed = hmac.update(Buffer.from(signData, 'utf-8')).digest("hex");
    vnp_Params['vnp_SecureHash'] = signed;
    vnpUrl += '?' + qs.stringify(vnp_Params, { encode: false });

    // Trả về URL cho client
    res.json({ paymentUrl: vnpUrl });
});

// API NHẬN KẾT QUẢ TRẢ VỀ
router.get('/vnpay_return', (req, res) => {
    let vnp_Params = req.query;
    let secureHash = vnp_Params['vnp_SecureHash'];

    delete vnp_Params['vnp_SecureHash'];
    delete vnp_Params['vnp_SecureHashType'];

    vnp_Params = sortObject(vnp_Params);
    let secretKey = process.env.VNP_HASHSECRET;
    let signData = qs.stringify(vnp_Params, { encode: false });
    let hmac = crypto.createHmac("sha512", secretKey);
    let signed = hmac.update(Buffer.from(signData, 'utf-8')).digest("hex");

    if (secureHash === signed) {
        // TODO: Cập nhật trạng thái đơn hàng trong Database
        // res.render('success', {code: vnp_Params['vnp_ResponseCode']})
        res.json({ message: 'Thanh toán thành công!', code: vnp_Params['vnp_ResponseCode'] });
    } else {
        // res.render('fail', {code: '97'})
        res.json({ message: 'Chữ ký không hợp lệ', code: '97' });
    }
});


module.exports = router;
