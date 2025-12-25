package com.monk.coupons.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CouponStrategyFactory {

    private final CartWiseStrategy cartWiseStrategy;
    private final ProductWiseStrategy productWiseStrategy;
    private final BxGyStrategy bxGyStrategy;

    public CouponStrategy getStrategy(String type) {

        if (type == null) {
            throw new IllegalArgumentException("Coupon type cannot be null");
        }

        String normalized = type.toLowerCase();

        return switch (normalized) {
            case "cart-wise" -> cartWiseStrategy;
            case "product-wise" -> productWiseStrategy;
            case "bxgy" -> bxGyStrategy;
            default -> throw new IllegalArgumentException("Unknown coupon type: " + type);
        };
    }
}