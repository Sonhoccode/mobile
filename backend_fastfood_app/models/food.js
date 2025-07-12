const { DataTypes } = require('sequelize');
const sequelize = require('../config/database');

const Food = sequelize.define('Food', {
  name: { type: DataTypes.STRING, allowNull: false },
  price: { type: DataTypes.DOUBLE, allowNull: false },
  imageUrl: { type: DataTypes.STRING },
  category: { type: DataTypes.STRING },
}, { tableName: 'foods' });

module.exports = Food;
