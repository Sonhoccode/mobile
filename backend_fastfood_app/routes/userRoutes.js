const express = require('express');
const router = express.Router();
const User = require('../models/user');
const sequelize = require('../config/database');

// Lấy user theo id
router.get('/:id', async (req, res) => {
  try {
    const user = await User.findByPk(req.params.id);
    if (!user) return res.status(404).json({ message: 'User not found!' });
    res.json(user);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Đăng ký
router.post('/register', async (req, res) => {
  try {
    const { name, phone, password, email, address } = req.body;
    const exist = await User.findOne({ where: { phone } });
    if (exist) return res.status(409).json({ message: 'Phone already registered!' });
    const user = await User.create({ name, phone, password, email, address });
    res.json(user);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Đăng nhập
router.post('/login', async (req, res) => {
  try {
    const { phone, password } = req.body;
    const user = await User.findOne({ where: { phone, password } });
    if (!user) return res.status(401).json({ message: 'Sai số điện thoại hoặc mật khẩu!' });
    res.json(user);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Đổi mật khẩu
router.post('/change-password', async (req, res) => {
  try {
    const { phone, oldPassword, newPassword } = req.body;
    const user = await User.findOne({ where: { phone, password: oldPassword } });
    if (!user) return res.status(401).json({ message: 'Sai mật khẩu!' });
    user.password = newPassword;
    await user.save();
    res.json({ message: 'Đổi mật khẩu thành công!' });
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

module.exports = router;
