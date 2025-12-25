package com.monk.coupons.service.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartWiseStrategyTest {

    private CartWiseStrategy strategy;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        strategy = new CartWiseStrategy(mapper);
    }

    // ---------------------------------------------------
    // SUCCESS CASE — DISCOUNT APPLIES
    // ---------------------------------------------------
    @Test
    void testApplyCartWiseDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(
                createItem(1, 2, 50),  // 100
                createItem(2, 1, 50)   // 50 → total = 150
        ));

        CartWiseDetails details = new CartWiseDetails();
        details.setThreshold(100.0);
        details.setDiscount(10.0);

        Coupon coupon = new Coupon();
        coupon.setType("cart-wise");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(15.0, discount);  // 150 * 10% = 15
    }

    // ---------------------------------------------------
    // BELOW THRESHOLD → DISCOUNT = 0
    // ---------------------------------------------------
    @Test
    void testCartDoesNotMeetThreshold_NoDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(
                createItem(1, 1, 50) // total = 50
        ));

        CartWiseDetails details = new CartWiseDetails();
        details.setThreshold(100.0);
        details.setDiscount(10.0);

        Coupon coupon = new Coupon();
        coupon.setType("cart-wise");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // ---------------------------------------------------
    // MISSING DETAILS → NO DISCOUNT
    // ---------------------------------------------------
    @Test
    void testMissingDetails_NoDiscount() {
        Cart cart = new Cart();
        cart.setItems(List.of(createItem(1, 2, 50)));

        Coupon coupon = new Coupon();
        coupon.setType("cart-wise");
        coupon.setDetails(null);     //simulate missing JSON

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // ---------------------------------------------------
    // EMPTY CART → NO DISCOUNT
    // ---------------------------------------------------
    @Test
    void testEmptyCart_NoDiscount() {
        Cart cart = new Cart();
        cart.setItems(List.of());   // empty cart

        CartWiseDetails details = new CartWiseDetails();
        details.setThreshold(100.0);
        details.setDiscount(10.0);

        Coupon coupon = new Coupon();
        coupon.setType("cart-wise");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // ---------------------------------------------------
    // NEGATIVE DISCOUNT OR THRESHOLD → NO DISCOUNT
    // ---------------------------------------------------
    @Test
    void testInvalidDiscountValues_NoDiscount() {
        Cart cart = new Cart();
        cart.setItems(List.of(createItem(1, 2, 50))); // total = 100

        CartWiseDetails details = new CartWiseDetails();
        details.setThreshold(-10.0);  // invalid
        details.setDiscount(-5.0);    // invalid

        Coupon coupon = new Coupon();
        coupon.setType("cart-wise");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    private CartItem createItem(int id, int qty, double price) {
        CartItem item = new CartItem();
        item.setProductId(id);
        item.setQuantity(qty);
        item.setPrice(price);
        return item;
    }
}