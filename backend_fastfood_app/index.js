require('dotenv').config();

const express = require('express');
const cors = require('cors');
const sequelize = require('./config/database');
const Food = require('./models/food');
const Order = require('./models/order');
const OrderItem = require('./models/orderItem');
const User = require('./models/user');
const Shop = require('./models/shop');
const PaymentAccount = require('./models/PaymentAccount');
const SupportRequest = require('./models/supportRequest');
const paymentRoutes = require('./routes/paymentRoutes');

const foodRoutes = require('./routes/foodRoutes');
const orderRoutes = require('./routes/orderRoutes');
const authRoutes = require('./routes/authRoutes');
const userRoutes = require('./routes/userRoutes');
const shopRoutes = require('./routes/shopRoutes');
const paymentAccountRoutes = require('./routes/paymentAccountRoutes');
const supportRequestRoutes = require('./routes/supportRequestRoutes');


const app = express();
app.use(cors());
app.use(express.json());

const PORT = process.env.PORT || 3000;

// Thiết lập quan hệ nếu cần (ở đây chỉ là ví dụ, bạn có thể mở rộng)
// Order.hasMany(OrderItem, { foreignKey: 'orderId' });
// OrderItem.belongsTo(Order, { foreignKey: 'orderId' });

Order.hasMany(OrderItem, { as: 'orderItems', foreignKey: 'orderId' });
OrderItem.belongsTo(Order, { foreignKey: 'orderId' });

// Kết nối và sync database
async function setupDatabase() {
  try {
    await sequelize.authenticate();
    console.log('>>> Đã kết nối thành công tới Supabase (PostgreSQL)! <<<');
    await sequelize.sync({ alter: true });
    console.log('Đã đồng bộ tất cả các model với database.');
  } catch (error) {
    console.error('Lỗi kết nối hoặc đồng bộ database:', error);
  }
}
setupDatabase();

// Routes
app.use('/foods', foodRoutes);
app.use('/orders', orderRoutes);
app.use('/auth', authRoutes);
app.use('/api/users', userRoutes);
app.use('/api/shops', shopRoutes);
app.use('/api/payment-account', paymentAccountRoutes);
app.use('/api/support-request', supportRequestRoutes);
app.use('/api/payment', paymentRoutes);

app.listen(PORT, () => console.log(`Backend đang chạy tại http://localhost:${PORT}`));