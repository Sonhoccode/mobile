const express = require('express');
const router = express.Router();
const User = require('../models/user');
const bcrypt = require('bcryptjs'); // Thêm bcrypt

// Lấy user theo id
router.get('/:id', async (req, res) => {
  try {
    const user = await User.findByPk(req.params.id);
    if (!user) return res.status(404).json({ message: 'User not found!' });
    res.json(user);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Cập nhật user theo id
router.put('/:id', async (req, res) => {
  try {
    const [updated] = await User.update(req.body, {
      where: { id: req.params.id }
    });
    if (updated) {
      const updatedUser = await User.findByPk(req.params.id);
      res.status(200).json(updatedUser);
    } else {
      res.status(404).json({ message: 'User not found to update' });
    }
  } catch (e) {
    res.status(500).json({ message: 'Server error on update', error: e.message });
  }
});


// **[PHẦN CẬP NHẬT QUAN TRỌNG]**
// Đổi mật khẩu an toàn hơn
router.post('/change-password', async (req, res) => {
  try {
    const { phone, oldPassword, newPassword } = req.body;
    
    const user = await User.findOne({ where: { phone } });
    if (!user) {
      return res.status(404).json({ message: 'Không tìm thấy tài khoản.' });
    }

    // --- BẮT ĐẦU THÊM LOG ĐỂ DEBUG ---
    console.log("--- DEBUG ĐỔI MẬT KHẨU ---");
    console.log("Mật khẩu cũ người dùng nhập (plain text):", oldPassword);
    console.log("Mật khẩu đã mã hóa trong DB:", user.password);
    // --- KẾT THÚC THÊM LOG ---

    const isMatch = await bcrypt.compare(oldPassword, user.password);
    if (!isMatch) {
      return res.status(401).json({ message: 'Mật khẩu cũ không đúng!' });
    }
    
    user.password = await bcrypt.hash(newPassword, 10);
    await user.save();
    
    res.json({ message: 'Đổi mật khẩu thành công!' });
  } catch (e) { 
    res.status(500).json({ message: 'Lỗi server', error: e.message }); 
  }
});


// Các hàm đăng ký, đăng nhập không cần thiết ở đây vì đã có trong authRoutes
// ...

module.exports = router;