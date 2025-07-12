const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');
const User = require('./user');

const PaymentAccount = sequelize.define('PaymentAccount', {
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  userPhone: { type: DataTypes.STRING, allowNull: false }, // hoặc userId nếu muốn liên kết chặt
  cardHolder: { type: DataTypes.STRING, allowNull: false },
  cardNumber: { type: DataTypes.STRING, allowNull: false },
  expiry: { type: DataTypes.STRING, allowNull: false },
  cvv: { type: DataTypes.STRING, allowNull: false }
}, {
  tableName: 'payment_accounts',
  timestamps: true,
});

// Nếu dùng userId (INTEGER), có thể liên kết như sau:
// PaymentAccount.belongsTo(User, { foreignKey: 'userId' });

module.exports = PaymentAccount;
