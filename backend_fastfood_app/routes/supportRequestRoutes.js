const express = require('express');
const router = express.Router();
const SupportRequest = require('../models/supportRequest'); // Import model
const User = require('../models/user'); // Import User để lấy tên
const Shop = require('../models/shop'); // Import Shop để lấy thông tin liên hệ

// Định tuyến để xử lý yêu cầu POST
router.post('/', async (req, res) => {
  try {
    const { phone, content } = req.body;

    if (!phone || !content) {
      return res.status(400).json({ message: 'Thiếu thông tin số điện thoại hoặc nội dung.' });
    }

    // Tìm người dùng để lấy tên (tùy chọn nhưng nên có)
    const user = await User.findOne({ where: { phone } });
    const shop = await Shop.findOne(); // Lấy thông tin shop

    if (!user) {
        return res.status(404).json({ message: 'Không tìm thấy người dùng với số điện thoại này.' });
    }
    if (!shop) {
        return res.status(404).json({ message: 'Không tìm thấy thông tin cửa hàng.' });
    }

    // Tạo yêu cầu hỗ trợ mới trong database
    const newSupportRequest = await SupportRequest.create({
      name: user.name, // Lấy tên từ user
      phone: phone,
      content: content,
      shop_contact: shop.phone // Lấy SĐT của shop
    });

    res.status(201).json({ message: 'Đã gửi yêu cầu hỗ trợ thành công!', data: newSupportRequest });

  } catch (error) {
    console.error('Lỗi khi tạo yêu cầu hỗ trợ:', error);
    res.status(500).json({ message: 'Lỗi server khi xử lý yêu cầu' });
  }
});

module.exports = router;