# Fastfood Backend API

## Mô tả
Đây là phần backend của ứng dụng Fastfood, xây dựng bằng Node.js với Express. Hệ thống hỗ trợ quản lý người dùng, xác thực JWT, quản lý món ăn, và kết nối cơ sở dữ liệu PostgreSQL (sử dụng Sequelize).

## Cài đặt

1. **Clone dự án:**
   ```bash
   git clone <repo-url>
   cd backend_fastfood_app
   ```

2. **Cài đặt các package cần thiết:**
   ```bash
   npm install express sequelize pg pg-hstore bcryptjs jsonwebtoken cors dotenv
   ```
   - `express`: Framework web
   - `sequelize`: ORM cho Node.js
   - `pg` và `pg-hstore`: Driver cho PostgreSQL
   - `bcryptjs`, `jsonwebtoken`, `cors`, `dotenv`: Các thư viện hỗ trợ khác

3. **Tạo file `.env`**
   Tạo file `.env` ở thư mục gốc với nội dung ví dụ:
   ```env
   DATABASE_URL=postgres://<user>:<password>@<host>:<port>/<dbname>
   JWT_SECRET=your_jwt_secret
   PORT=3000
   ```
   - Chỉ cần `DATABASE_URL` cho PostgreSQL.

4. **Chạy server:**
   ```bash
   node index.js
   ```

## Các API chính

### Người dùng
- `POST /register` — Đăng ký tài khoản
- `POST /login` — Đăng nhập, trả về JWT

### Món ăn
- `GET /foods` — Lấy danh sách món ăn
- `POST /foods` — Thêm món ăn mới

## Cấu trúc thư mục
- `index.js` — File chính khởi động server
- `models/` — Định nghĩa các model (User, Food, ...)
- `routes/` — Định nghĩa các route (nếu có)
- `config/database.js` — Kết nối cơ sở dữ liệu PostgreSQL (Sequelize)

## Thư viện sử dụng
- express
- sequelize
- pg
- pg-hstore
- bcryptjs
- jsonwebtoken
- cors
- dotenv

## Ghi chú
- Đảm bảo đã cài Node.js >= 14
- Đảm bảo đã cấu hình đúng biến môi trường trong `.env`
- Đã tạo database PostgreSQL trước khi chạy

## License
MIT
