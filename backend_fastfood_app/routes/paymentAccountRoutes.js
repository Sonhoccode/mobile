const express = require('express');
const router = express.Router();
const PaymentAccount = require('../models/PaymentAccount');

// Lấy danh sách thẻ theo userPhone
router.get('/:userPhone', async (req, res) => {
  try {
    const accounts = await PaymentAccount.findAll({ where: { userPhone: req.params.userPhone } });
    res.json(accounts);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Thêm thẻ
router.post('/add', async (req, res) => {
  try {
    const { userPhone, cardHolder, cardNumber, expiry, cvv } = req.body;
    const newCard = await PaymentAccount.create({ userPhone, cardHolder, cardNumber, expiry, cvv });
    res.json(newCard);
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

// Xóa thẻ
router.delete('/:id', async (req, res) => {
  try {
    const result = await PaymentAccount.destroy({ where: { id: req.params.id } });
    if (result === 0) return res.status(404).json({ message: 'Card not found!' });
    res.json({ message: 'Deleted!' });
  } catch (e) { res.status(500).json({ message: 'Server error' }); }
});

module.exports = router;
