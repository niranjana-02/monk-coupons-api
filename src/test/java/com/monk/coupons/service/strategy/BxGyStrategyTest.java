package com.monk.coupons.service.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BxGyStrategyTest {

    private BxGyStrategy strategy;
    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();
        strategy = new BxGyStrategy(mapper);
    }

    // -----------------------------
    // SUCCESS CASE
    // -----------------------------
    @Test
    void testBxGyDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(
                createItem(1, 4, 50),  // buy 4 -> fulfills "buy 2" twice
                createItem(5, 10, 30)  // free product exists
        ));

        BxGyProduct buy = createBx(1, 2);
        BxGyProduct get = createBxGet(5, 1);

        BxGyDetails details = new BxGyDetails();
        details.setBuyProducts(List.of(buy));
        details.setGetProducts(List.of(get));
        details.setRepetitionLimit(5);

        Coupon coupon = new Coupon();
        coupon.setType("bxgy");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(60.0, discount);
        // Buy 4 → 2 repetitions → 2 free items → 2 × 30 = 60
    }

    // -----------------------------
    // NO BUY ITEM MATCHES
    // -----------------------------
    @Test
    void testBxGy_NoBuy_NoDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(createItem(99, 1, 10))); // no matching buy-product

        BxGyDetails details = new BxGyDetails();
        details.setBuyProducts(List.of(createBx(1, 2)));  // need 2 of product 1
        details.setGetProducts(List.of(createBxGet(5, 1)));
        details.setRepetitionLimit(5);

        Coupon coupon = new Coupon();
        coupon.setType("bxgy");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // -----------------------------
    // FREE PRODUCT NOT IN CART
    // -----------------------------
    @Test
    void testBxGy_FreeProductMissing_NoDiscount() {

        Cart cart = new Cart();
        cart.setItems(List.of(createItem(1, 4, 50)));  // qualifies for discount but no free product

        BxGyDetails details = new BxGyDetails();
        details.setBuyProducts(List.of(createBx(1, 2)));
        details.setGetProducts(List.of(createBxGet(5, 1))); // free product 5 NOT in cart
        details.setRepetitionLimit(5);

        Coupon coupon = new Coupon();
        coupon.setType("bxgy");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(0.0, discount);
    }

    // -----------------------------
    // REPETITION LIMIT APPLIED
    // -----------------------------
    @Test
    void testBxGy_RepetitionLimitApplied() {

        Cart cart = new Cart();
        cart.setItems(List.of(
                createItem(1, 10, 50), // buy 10 -> 5 repetitions, but limit is 2
                createItem(5, 10, 30)
        ));

        BxGyDetails details = new BxGyDetails();
        details.setBuyProducts(List.of(createBx(1, 2)));   // buy 2
        details.setGetProducts(List.of(createBxGet(5, 1))); // get 1
        details.setRepetitionLimit(2);  // limit repetitions

        Coupon coupon = new Coupon();
        coupon.setType("bxgy");
        coupon.setDetails(mapper.valueToTree(details));

        double discount = strategy.calculateDiscount(coupon, cart);

        assertEquals(60.0, discount);
        // even though buy=10 → 5 reps, limit=2 → only 2 free items → 2×30=60
    }

    private CartItem createItem(int id, int qty, double price) {
        CartItem item = new CartItem();
        item.setProductId(id);
        item.setQuantity(qty);
        item.setPrice(price);
        return item;
    }

    private BxGyProduct createBx(int id, int qty) {
        BxGyProduct p = new BxGyProduct();
        p.setProductId(id);
        p.setQuantity(qty);
        return p;
    }

    private BxGyProduct createBxGet(int id, int qty) {
        return createBx(id, qty);
    }
}