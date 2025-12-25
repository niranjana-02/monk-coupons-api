package com.monk.coupons.service.strategy;

import com.monk.coupons.model.Cart;
import com.monk.coupons.model.Coupon;

public interface CouponStrategy {

    /**
     * Calculates the discount amount for a given coupon and cart.
     *
     * @param coupon the coupon to evaluate
     * @param cart   the cart for which the discount is calculated
     * @return the computed discount amount
     */
    double calculateDiscount(Coupon coupon, Cart cart);
}