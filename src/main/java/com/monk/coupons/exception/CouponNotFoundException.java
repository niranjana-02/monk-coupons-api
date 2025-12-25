package com.monk.coupons.exception;

public class CouponNotFoundException extends RuntimeException {

    public CouponNotFoundException(Long id) {
        super("Coupon not found with ID: " + id);
    }
}