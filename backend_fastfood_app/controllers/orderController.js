// **SỬA LẠI CÁC DÒNG REQUIRE Ở ĐÂY**
const Order = require('../models/order');
const OrderItem = require('../models/orderItem');
const sequelize = require('../config/database'); // Lấy sequelize từ tệp config

// Tạo đơn hàng mới
exports.createOrder = async (req, res) => {
  const t = await sequelize.transaction();
  try {
    const { userId, customerInfo, items, totalPrice, paymentMethod } = req.body;

    if (!userId || !customerInfo || !items || !totalPrice || items.length === 0) {
      return res.status(400).json({ message: 'Dữ liệu đơn hàng không hợp lệ.' });
    }

    const newOrder = await Order.create({
      userId: userId,
      customerName: customerInfo.name,
      customerAddress: customerInfo.address,
      customerPhone: customerInfo.phone,
      totalPrice: totalPrice,
      paymentMethod: paymentMethod
    }, { transaction: t });

    for (const item of items) {
      await OrderItem.create({
        orderId: newOrder.id,
        foodName: item.name,
        quantity: item.quantity,
        price: item.price,
        imageUrl: item.imageUrl
      }, { transaction: t });
    }

    await t.commit();
    res.status(201).json({ message: 'Đặt hàng thành công!', order: newOrder });
  } catch (error) {
    await t.rollback();
    console.error('Lỗi khi xử lý đơn hàng:', error);
    res.status(500).json({ message: 'Lỗi máy chủ khi xử lý đơn hàng' });
  }
};

// Lấy lịch sử đơn hàng của một user
exports.getOrderHistory = async (req, res) => {
    try {
        const { userId } = req.params;
        const orders = await Order.findAll({
            where: { userId: userId },
            include: [{ model: OrderItem, as: 'orderItems' }],
            order: [['createdAt', 'DESC']]
        });
        res.status(200).json(orders);
    } catch (error) {
        console.error('Lỗi khi lấy lịch sử đơn hàng:', error);
        res.status(500).json({ message: 'Lỗi máy chủ' });
    }
};
