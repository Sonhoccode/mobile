const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const SupportRequest = sequelize.define('SupportRequest', {
  id: { type: DataTypes.INTEGER, primaryKey: true, autoIncrement: true },
  name: { type: DataTypes.STRING, allowNull: false },
  phone: { type: DataTypes.STRING, allowNull: false },
  content: { type: DataTypes.TEXT, allowNull: false },
  shop_contact: { type: DataTypes.STRING, allowNull: false }
}, {
  tableName: 'support_requests',
  timestamps: true, // createdAt, updatedAt
});

module.exports = SupportRequest;
