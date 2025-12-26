package com.monk.coupons.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.model.Cart;
import com.monk.coupons.model.CartItem;
import com.monk.coupons.model.Coupon;
import com.monk.coupons.model.ProductWiseDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductWiseStrategy implements CouponStrategy {

    private final ObjectMapper mapper;

    @Override
    public double calculateDiscount(Coupon coupon, Cart cart) {

        JsonNode detailsNode = coupon.getDetails();
        if (detailsNode == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0;
        }

        ProductWiseDetails details = mapper.convertValue(detailsNode, ProductWiseDetails.class);

        if (details.getProductId() == null || details.getDiscount() == null) {
            return 0;
        }

        int targetProductId = details.getProductId();
        double discountPercent = details.getDiscount() / 100.0;

        return cart.getItems().stream()
                .filter(i -> i.getProductId() == targetProductId)
                .mapToDouble(i -> i.getPrice() * i.getQuantity() * discountPercent)
                .sum();
    }

    @Override
    public Cart applyCoupon(Coupon coupon, Cart cart) {

        JsonNode detailsNode = coupon.getDetails();
        if (detailsNode == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return cart;
        }

        ProductWiseDetails details = mapper.convertValue(detailsNode, ProductWiseDetails.class);

        if (details.getProductId() == null || details.getDiscount() == null) {
            return cart;
        }

        int targetProductId = details.getProductId();
        double discountPercent = details.getDiscount() / 100.0;

        for (CartItem item : cart.getItems()) {

            if (item.getProductId() == targetProductId) {
                double discount = item.getPrice() * item.getQuantity() * discountPercent;
                item.setTotalDiscount(discount);
            }
            // else → DO NOT modify total_discount
        }

        return cart;
    }
}