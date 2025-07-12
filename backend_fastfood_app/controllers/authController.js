const User = require('../models/User');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const crypto = require('crypto');

exports.register = async (req, res) => {
  const { name, phone, password, confirmPassword } = req.body;
  if (!name || !phone || !password || !confirmPassword) {
    return res.status(400).json({ message: 'Vui lòng nhập đầy đủ thông tin.' });
  }
  if (password.length < 6) {
    return res.status(400).json({ message: 'Mật khẩu phải có ít nhất 6 ký tự.' });
  }
  if (password !== confirmPassword) {
    return res.status(400).json({ message: 'Mật khẩu nhập lại không khớp.' });
  }
  const phoneRegex = /^(0|\+84)[0-9]{9,10}$/;
  if (!phoneRegex.test(phone)) {
    return res.status(400).json({ message: 'Số điện thoại không hợp lệ.' });
  }
  try {
    const existingUser = await User.findOne({ where: { phone } });
    if (existingUser) {
      return res.status(409).json({ message: 'Số điện thoại đã được sử dụng.' });
    }
    const hashedPassword = await bcrypt.hash(password, 10);
    await User.create({ name: name.trim(), phone, password: hashedPassword });
    res.status(201).json({ message: 'Đăng ký thành công!' });
  } catch (error) {
    res.status(500).json({ message: 'Lỗi khi đăng ký.', error: error.message });
  }
};

exports.login = async (req, res) => {
  const { phone, password, rememberMe } = req.body;
  if (!phone || !password) {
    return res.status(400).json({ message: 'Vui lòng nhập số điện thoại và mật khẩu.' });
  }
  try {
    const user = await User.findOne({ where: { phone } });
    if (!user) {
      return res.status(401).json({ message: 'Số điện thoại không tồn tại.' });
    }
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ message: 'Mật khẩu không đúng.' });
    }
    // Ghi nhớ đăng nhập: token sống lâu hơn nếu rememberMe true
    const expiresIn = rememberMe ? '7d' : '1h';
    const token = jwt.sign(
      { id: user.id, phone: user.phone },
      process.env.JWT_SECRET || 'your_jwt_secret',
      { expiresIn }
    );
    res.status(200).json({
      message: 'Đăng nhập thành công!',
      token,
      user: { id: user.id, name: user.name, phone: user.phone }
    });
  } catch (error) {
    res.status(500).json({ message: 'Lỗi máy chủ khi đăng nhập.', error: error.message });
  }
};

// Quên mật khẩu: gửi mã reset (giả lập, thực tế nên gửi qua SMS/email)
const resetTokens = {};
exports.forgotPassword = async (req, res) => {
  const { phone } = req.body;
  if (!phone) return res.status(400).json({ message: 'Vui lòng nhập số điện thoại.' });
  try {
    const user = await User.findOne({ where: { phone } });
    if (!user) return res.status(404).json({ message: 'Không tìm thấy tài khoản.' });
    // Sinh mã reset (giả lập, thực tế nên gửi qua SMS/email)
    const resetToken = Math.floor(100000 + Math.random() * 900000).toString(); // 6 số
    resetTokens[phone] = { token: resetToken, expires: Date.now() + 10 * 60 * 1000, verified: false };
    // Trả về mã reset (demo), thực tế nên gửi qua SMS/email
    res.json({ message: 'Mã OTP đã được gửi.', resetToken });
  } catch (error) {
    res.status(500).json({ message: 'Lỗi khi gửi mã OTP.', error: error.message });
  }
};

exports.verifyOtp = async (req, res) => {
  const { phone, otp } = req.body;
  if (!phone || !otp) return res.status(400).json({ message: 'Thiếu thông tin.' });
  const record = resetTokens[phone];
  if (!record || record.token !== otp || Date.now() > record.expires) {
    return res.status(400).json({ message: 'Mã OTP không hợp lệ hoặc đã hết hạn.' });
  }
  resetTokens[phone].verified = true;
  res.json({ message: 'Xác thực OTP thành công.' });
};

exports.resetPassword = async (req, res) => {
  const { phone, newPassword, confirmPassword } = req.body;
  if (!phone || !newPassword || !confirmPassword) {
    return res.status(400).json({ message: 'Thiếu thông tin.' });
  }
  if (newPassword.length < 6) {
    return res.status(400).json({ message: 'Mật khẩu phải có ít nhất 6 ký tự.' });
  }
  if (newPassword !== confirmPassword) {
    return res.status(400).json({ message: 'Mật khẩu nhập lại không khớp.' });
  }
  try {
    const record = resetTokens[phone];
    if (!record || !record.verified || Date.now() > record.expires) {
      return res.status(400).json({ message: 'Bạn chưa xác thực OTP hoặc OTP đã hết hạn.' });
    }
    const user = await User.findOne({ where: { phone } });
    if (!user) return res.status(404).json({ message: 'Không tìm thấy tài khoản.' });
    user.password = await bcrypt.hash(newPassword, 10);
    await user.save();
    delete resetTokens[phone];
    res.json({ message: 'Đặt lại mật khẩu thành công!' });
  } catch (error) {
    res.status(500).json({ message: 'Lỗi khi đặt lại mật khẩu.', error: error.message });
  }
};
