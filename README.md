# 🛒 Monk Commerce – Coupon Management API (Backend Task 2025)

This project implements a flexible and extensible **Coupon Management System** for an e-commerce platform.  
It supports:

- **Cart-wise coupons**
- **Product-wise coupons**
- **BxGy (Buy X Get Y) coupons**
- JSON-based rule storage
- Strategy pattern for dynamic coupon evaluation
- CRUD operations for coupons
- Evaluating & applying coupons to a cart

---

# 📌 Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Web**
- **Spring Data JPA**
- **H2 in-memory database**
- **Maven**
- **Lombok**
- **Jackson (JsonNode for rule storage)**

---

# 🧠 Architecture Overview

## **1️⃣ Flexible JSON-Based Coupon Rule Storage**

Each coupon stores its rule in a `JsonNode` field:

```java
@Column(columnDefinition = "TEXT")
private JsonNode details;
```

This allows:

- Dynamic rule definitions
- No schema updates when adding new coupon types
- Easy extensibility for future coupon logic

---

## **2️⃣ Strategy Pattern for Coupon Logic**

```
CouponStrategy
   ├── CartWiseStrategy
   ├── ProductWiseStrategy
   └── BxGyStrategy
```

The `CouponStrategyFactory` picks the correct strategy based on coupon type.

---

## **3️⃣ Service Layer**

`CouponService` provides:

- Create, Update, Delete coupons
- Get applicable coupons
- Apply a coupon and return an updated cart summary

---

## **4️⃣ Controllers**

### `/coupons`
CRUD APIs for coupon management.

### `/applicable-coupons`
Returns **all coupons that apply** to the provided cart.

### `/apply-coupon/{id}`
Applies one coupon and returns:

- Total cart price
- Discount applied
- Final price
- Items in the cart

---

# 🎯 Implemented Coupon Types

## **1. Cart-wise Coupons**

> Example: 10% off when cart total ≥ ₹100

✔ Implements
- Threshold check
- Percentage discount

---

## **2. Product-wise Coupons**

> Example: 20% off on productId = 101

✔ Applies discount on matching items only

---

## **3. BxGy Coupons**

> Example: Buy 2 of product 1 → Get 1 of product 5 free (up to repetition limit)

✔ Supports
- Buy-product matching
- Free-product matching
- Repetition limit
- Free item price calculation

---

# 🚧 Thoughtful Considerations (Not Implemented but Planned in Design)

- Coupon stacking rules
- Category/brand-based coupons
- Cart-level discount caps
- User-level usage limits
- Multi-currency support
- Time-window-based coupons (e.g., Happy Hours)
- Complex BxGy (multiple free-product choices)
- Free product substitution logic
- Coupon expiration
---

# 📌 Assumptions

- Product prices are included in cart request
- All products exist
- JSON rule structure is valid
- Only one coupon is applied at a time
- All discount percentages are valid (0–100)
- BxGy free items must exist in cart for discount to apply

---

# ⚠️ Limitations

- No authentication or user-level coupon tracking
- No coupon priority or conflict resolution
- BxGy supports only one free-product type per coupon
- Does not support applying **multiple coupons** at once
- Free products are *not* added to cart; only discount value is applied
- No persistence beyond in-memory H2 database
---

# 🧪 API Documentation

---

## **➡️ POST /coupons** – Create a Coupon

### Example (Cart-wise)
```json
{
  "type": "cart-wise",
  "details": {
    "threshold": 100,
    "discount": 10
  }
}
```

### Example (Product-wise)
```json
{
  "type": "product-wise",
  "details": {
    "productId": 101,
    "discount": 20
  }
}
```

### Example (BxGy)
```json
{
  "type": "bxgy",
  "details": {
    "buyProducts": [{ "productId": 1, "quantity": 2 }],
    "getProducts": [{ "productId": 5, "quantity": 1 }],
    "repetitionLimit": 3
  }
}
```

---

## **➡️ GET /coupons**
Returns all coupons.

---

## **➡️ POST /applicable-coupons**

### Request:
```json
{
  "cart": {
  "items": [
    { "productId": 1, "quantity": 6, "price": 50 },
    { "productId": 2, "quantity": 3, "price": 30 }
  ]
  }
}
```

### Response:
```json
{
  "applicable_coupons": [
    {
      "coupon_id": 3,
      "type": "bxgy",
      "discount": 100
    }
  ]
}
```

---

## **➡️ POST /apply-coupon/{id}**

### Response:
```json
{
  "updated_cart": {
    "items": [
      { "productId": 1, "quantity": 2, "price": 50, "total_discount": 20 }
    ],
    "total_price": 100,
    "total_discount": 20,
    "final_price": 80
  }
}
```

---

# ▶️ Running the Application

### Clone repository
```sh
git clone https://github.com/niranjana-02/monk-coupons-api.git
cd monk-coupons-api
```

### Run the application
```sh
mvn spring-boot:run
```

### H2 Console
```
http://localhost:8080/h2-console
```

JDBC URL:
```
jdbc:h2:mem:couponsdb
user: sa
password:
```

---

# 🧑‍💻 Author
**Niranjana R**  
Monk Commerce Backend Task 2025   
