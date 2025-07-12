const express = require('express');
const router = express.Router();
const Shop = require('../models/shop');

// Lấy thông tin shop
router.get('/', async (req, res) => {
  try {
    const shop = await Shop.findOne();
    res.json(shop);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

module.exports = router;
