const Food = require('../models/food');

exports.getFoods = async (req, res) => {
  try {
    const foods = await Food.findAll();
    res.json(foods);
  } catch (error) {
    res.status(500).json({ message: 'Lỗi khi lấy danh sách món ăn' });
  }
};

exports.createFood = async (req, res) => {
  try {
    const { name, price, imageUrl, category } = req.body;
    if (!name || !price) {
      return res.status(400).json({ message: 'Thiếu tên hoặc giá món ăn.' });
    }
    const food = await Food.create({ name, price, imageUrl, category });
    res.status(201).json(food);
  } catch (error) {
    res.status(500).json({ message: 'Lỗi khi thêm món ăn mới' });
  }
};
