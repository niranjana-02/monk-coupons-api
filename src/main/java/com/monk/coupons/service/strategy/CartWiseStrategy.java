package com.monk.coupons.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.model.Cart;
import com.monk.coupons.model.CartWiseDetails;
import com.monk.coupons.model.Coupon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartWiseStrategy implements CouponStrategy {

    private final ObjectMapper mapper;

    @Override
    public double calculateDiscount(Coupon coupon, Cart cart) {

        JsonNode detailsNode = coupon.getDetails();
        if (detailsNode == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0;
        }

        CartWiseDetails details = mapper.convertValue(detailsNode, CartWiseDetails.class);

        // Validate rule
        if (details.getThreshold() == null || details.getThreshold() <= 0) return 0;
        if (details.getDiscount() == null || details.getDiscount() <= 0) return 0;

        // Total cart amount
        double total = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        // If total does not meet threshold → no discount
        if (total < details.getThreshold()) return 0;

        // Apply percentage discount
        return total * (details.getDiscount() / 100.0);
    }

    @Override
    public Cart applyCoupon(Coupon coupon, Cart cart) {
        // Ensure every item explicitly has total_discount = 0
        if (cart.getItems() != null) {
            cart.getItems().forEach(i -> i.setTotalDiscount(0.0));
        }
        return cart;
    }
}