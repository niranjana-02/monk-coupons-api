package com.monk.coupons.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BxGyStrategy implements CouponStrategy {

    private final ObjectMapper mapper;

    @Override
    public double calculateDiscount(Coupon coupon, Cart cart) {

        JsonNode detailsNode = coupon.getDetails();
        if (detailsNode == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            return 0;
        }

        BxGyDetails details = mapper.convertValue(detailsNode, BxGyDetails.class);

        if (details.getBuyProducts() == null || details.getBuyProducts().isEmpty()
                || details.getGetProducts() == null || details.getGetProducts().isEmpty()) {
            return 0;
        }

        BxGyProduct buyRule = details.getBuyProducts().get(0);
        BxGyProduct getRule = details.getGetProducts().get(0);

        int buyProductId = buyRule.getProductId();
        int getProductId = getRule.getProductId();

        // Count total buy quantity
        int totalBuyQty = cart.getItems().stream()
                .filter(i -> i.getProductId() == buyProductId)
                .mapToInt(CartItem::getQuantity)
                .sum();

        if (totalBuyQty < buyRule.getQuantity()) {
            return 0;
        }

        int repetitionCount =
                Math.min(totalBuyQty / buyRule.getQuantity(), details.getRepetitionLimit());

        if (repetitionCount <= 0) {
            return 0;
        }

        int freeQty = repetitionCount * getRule.getQuantity();

        // Find the free product in cart
        CartItem freeItem = cart.getItems().stream()
                .filter(i -> i.getProductId() == getProductId)
                .findFirst()
                .orElse(null);

        if (freeItem == null) {
            return 0;
        }

        return freeQty * freeItem.getPrice();
    }
}