const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Order = sequelize.define('Order', {
  customerName: { type: DataTypes.STRING, allowNull: false },
  customerAddress: { type: DataTypes.STRING, allowNull: false },
  customerPhone: { type: DataTypes.STRING, allowNull: false },
  totalPrice: { type: DataTypes.DOUBLE, allowNull: false },
  status: { type: DataTypes.STRING, defaultValue: 'Đang xử lý' }
}, { tableName: 'orders' });

module.exports = Order;
