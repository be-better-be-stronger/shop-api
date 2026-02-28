# Shop API – Spring Boot RESTful Backend

Backend API cho hệ thống bán hàng, xây dựng theo kiến trúc RESTful, sử dụng Spring Boot + JWT Authentication, hướng tới mô hình Fullstack (Angular frontend).

---

## 🧠 Mục tiêu project

- Xây dựng backend chuẩn doanh nghiệp cho hệ thống shop
- Áp dụng xác thực JWT + phân quyền (USER / ADMIN)
- Thiết kế API rõ ràng, thống nhất response & error handling
- Sẵn sàng tích hợp frontend Angular

---

## 🛠️ Tech Stack

- Java 17
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- MySQL
- Maven
- Postman (test API)

---

## 📦 Kiến trúc tổng thể

- Frontend: Angular (tách repo)
- Backend: Spring Boot REST API
- Authentication: JWT (Bearer Token)
- Authorization: Role-based (ADMIN / USER)

---

## 📂 Cấu trúc project (chuẩn hoá)

```
com.shop
├─ auth          # đăng nhập, đăng ký, auth service
├─ security      # spring security, jwt filter, config
├─ user          # user profile, user service
├─ catalog       # product, category, admin product
├─ cart          # giỏ hàng
├─ order         # checkout, transaction
├─ common        # response wrapper, exception, util
└─ config        # config dùng chung (nếu có)
```

---

## 🔐 Authentication & Authorization

- Login trả về JWT
- Client gửi header:

```
Authorization: Bearer <token>
```

- API public: product list, product detail
- API protected:
  - USER: cart, checkout
  - ADMIN: quản lý product

---

## 📄 API Response format (thống nhất)

```json
{
  "success": true,
  "message": "OK",
  "data": {}
}
```

Khi lỗi:

```json
{
  "success": false,
  "message": "ERR_UNAUTHORIZED",
  "data": null
}
```

---

## ▶️ Chạy project

### 1. Cấu hình database

Sửa file `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shop
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. Run application

```bash
mvn spring-boot:run
```

Hoặc chạy trực tiếp `ShopApiApplication.java`

---

## 🧪 Test API

- Test flow:
  1. Login → lấy token
  2. Gọi API protected với Bearer token
  3. Test cart + checkout

---

## 🛠 Commit Convention

This project enforces **Conventional Commits** using Husky and Commitlint.

Format:

type(scope): subject

Example:

fix(db): correct datasource configuration  
feat(product): add pagination support  
refactor(exception): improve validation handling  

Allowed types:
- feat
- fix
- refactor
- perf
- test
- docs
- style
- chore

Before committing for the first time:

npm install

---

## ⚠️ Ghi chú kỹ thuật

- Checkout sử dụng transaction để đảm bảo consistency
- JWT filter inject SecurityContext cho mỗi request
- Exception được handle tập trung tại GlobalExceptionHandler

---

## 🧠 Engineering Notes

This project documents real production-level issues encountered during development.

Topics covered include:
- Redis serialization issues
- Spring Cache proxy behavior
- Transaction consistency problems
- Validation & BindException edge cases

See [mistakes.md](./mistakes.md) for root-cause analysis and debugging notes.

## 📌 Tình trạng

- Backend core: hoàn thiện
- Frontend: Angular (đang phát triển)

---

## 👤 Author

Thanh – Java Backend Developer
Production-driven learning project.
