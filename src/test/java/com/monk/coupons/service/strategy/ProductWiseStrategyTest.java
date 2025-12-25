package com.monk.coupons.service.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductWiseStrategyTest {

    private ProductWiseStrategy strategy;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        strategy = new ProductWiseStrategy(mapper);
    }

    // ---------------------------------------------------
    // DISCOUNT APPLIED SUCCESSFULLY
    // ---------------------------------------------------
    @Test
    void testProductWiseDiscountApplied() {

        Cart cart = new Cart();
        cart.setItems(List.of(
                createItem(1, 2, 100), // eligible → discount = 40
                createItem(2, 1, 50)
        ));

        ProductWiseDetails details = new ProductWiseDetails();
        details.setProductId(1);
        details.setDiscount(20.0); // 20%

        Coupon coupon = new Coupon();
        coupon.setType("product-wise");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(40.0, discount); // 2 * 100 * 20% = 40
    }

    // ---------------------------------------------------
    // PRODUCT NOT FOUND → DISCOUNT = 0
    // ---------------------------------------------------
    @Test
    void testProductNotEligible_NoDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(
                createItem(2, 1, 50)
        ));

        ProductWiseDetails details = new ProductWiseDetails();
        details.setProductId(1);
        details.setDiscount(20.0);

        Coupon coupon = new Coupon();
        coupon.setType("product-wise");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // ---------------------------------------------------
    // INVALID / MISSING DETAILS → DISCOUNT = 0
    // ---------------------------------------------------
    @Test
    void testMissingDetails_NoDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(createItem(1, 2, 100)));

        Coupon coupon = new Coupon();
        coupon.setType("product-wise");
        coupon.setDetails(null); // simulate missing JSON object

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // ---------------------------------------------------
    // EMPTY CART → DISCOUNT = 0
    // ---------------------------------------------------
    @Test
    void testEmptyCart_NoDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of());

        ProductWiseDetails details = new ProductWiseDetails();
        details.setProductId(1);
        details.setDiscount(20.0);

        Coupon coupon = new Coupon();
        coupon.setType("product-wise");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // ---------------------------------------------------
    // ZERO DISCOUNT VALUE → DISCOUNT = 0
    // ---------------------------------------------------
    @Test
    void testZeroDiscountPercentage_NoDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(createItem(1, 2, 100)));

        ProductWiseDetails details = new ProductWiseDetails();
        details.setProductId(1);
        details.setDiscount(0.0); // no discount

        Coupon coupon = new Coupon();
        coupon.setType("product-wise");
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