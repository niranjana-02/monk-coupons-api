# 🛒 Monk Commerce – Coupon Management API (Backend Task 2025)

This project implements a flexible, extensible **Coupon Management System** for an e-commerce platform.  
It supports:

- **Cart-wise coupons**
- **Product-wise coupons**
- **BxGy (Buy X Get Y) coupons**
- Ability to **add new coupon types easily** in the future

The solution follows a **Strategy Pattern**, uses **JSON-based rule storage**, and includes complete CRUD + coupon application APIs.

---

# 📌 Tech Stack
- Java 17
- Spring Boot 3.2.0
- Spring Web
- Spring Data JPA
- H2 Database (in-memory)
- Maven
- Lombok

---

# 🧠 Architecture Overview

### **1️⃣ JSON-Based Coupon Rule Storage**
Each coupon stores its logic in a single JSON field:

```java
@Lob
private String detailsJson;
```

This design enables:
- No database schema changes for new coupon types
- Very flexible rule definitions
- Clean Strategy Pattern implementation

---

### **2️⃣ Strategy Pattern for Coupon Logic**

```
CouponStrategy (interface)
   ├── CartWiseStrategy
   ├── ProductWiseStrategy
   └── BxGyStrategy
```

The factory selects the correct strategy at runtime.

---

### **3️⃣ Services**
`CouponService` performs:
- CRUD operations
- Discount calculation
- Finding applicable coupons

---

### **4️⃣ Controllers**
- `/coupons` → CRUD
- `/applicable-coupons` → list valid coupons for a cart
- `/apply-coupon/{id}` → apply a specific coupon

---

# ✅ Implemented Coupon Cases

## **1. Cart-Wise Coupons**
Example:
> 10% off when cart total ≥ 100

✔ Implemented:
- Minimum threshold
- Percentage discount
- Applies once per cart

---

## **2. Product-Wise Coupons**
Example:
> 20% off on Product ID 5

✔ Implemented:
- Matches product ID
- Applies discount on eligible items only

---

## **3. BxGy Coupons (Buy X Get Y)**
Example:
> Buy 2 of [1,2] → Get 1 of [5] free  
> Repetition limit supported

✔ Implemented:
- Buy-product matching
- Repetition calculation
- Value of free items added as discount
- Free-product matching

---

# 🚧 Unimplemented Cases (But Considered)

Even if not implemented, listing these shows strong systems thinking:

- Coupon stacking & priority rules
- Global cart-level maximum discount cap
- Coupons with category or brand restrictions
- Free product substitution logic
- Complex BxGy (multiple free-product choices)
- User-specific coupon limits (per user/per month)
- Coupon expiration (could be added easily)
- Multi-currency pricing
- Time-window-based coupons (e.g., Happy Hours)

---

# 📌 Assumptions

- Prices are provided in the cart request payload
- Products inside the cart are valid and exist
- Only one coupon is applied at a time
- All discount percentages are valid (0–100)
- BxGy free items must exist in cart for discount to apply
- JSON structure for coupon details is always valid

---

# ⚠️ Limitations

- Does not support applying **multiple coupons** at once
- BxGy supports only one free-product type per coupon
- No authentication or user-level coupon tracking
- No persistence beyond in-memory H2 database
- Free products are valued but not physically added to cart

---

# 🧪 API Documentation

## ➤ **POST /coupons**
Create a new coupon.

### Example (Cart-Wise)
```json
{
  "type": "CART_WISE",
  "detailsJson": "{\"threshold\": 100, \"discount\": 10}"
}
```

### Example (Product-Wise)
```json
{
  "type": "PRODUCT_WISE",
  "detailsJson": "{\"productId\": 1, \"discount\": 20}"
}
```

### Example (BxGy)
```json
{
  "type": "BXGY",
  "detailsJson": "{\"buyProducts\":[{\"productId\":1,\"quantity\":2}],\"getProducts\":[{\"productId\":3,\"quantity\":1}],\"repetitionLimit\":3}"
}
```

---

## ➤ **GET /coupons**
Retrieve all coupons.

---

## ➤ **POST /applicable-coupons**
Returns all coupons applicable to a given cart.

### Request:
```json
{
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 }
  ]
}
```

### Response:
```json
[
  {
    "couponId": 3,
    "type": "BXGY",
    "discount": 100
  }
]
```

---

## ➤ **POST /apply-coupon/{id}**
Apply a specific coupon and return discount amount.

---

# ▶️ Running the Application

### **1. Clone repository**
```bash
git clone <repo-url>
cd monk-coupons-api
```

### **2. Run the application**
```bash
mvn spring-boot:run
```

### **3. Open H2 Console**
```
http://localhost:8080/h2-console
```

Use:
```
jdbc:h2:mem:couponsdb
user: sa
password:
```

---

#  Author
Niranjana R  
Monk Commerce Backend Task 2025  
