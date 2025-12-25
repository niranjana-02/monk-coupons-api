package com.monk.coupons.service;

import com.monk.coupons.model.Cart;
import com.monk.coupons.model.Coupon;
import com.monk.coupons.model.ApplicableCouponsResponse;
import com.monk.coupons.model.ApplyCouponResponse;

import java.util.List;

public interface CouponService {

    Coupon createCoupon(Coupon coupon);

    List<Coupon> getAllCoupons();

    Coupon getCouponById(Long id);

    Coupon updateCoupon(Long id, Coupon updated);

    void deleteCoupon(Long id);

    ApplicableCouponsResponse getApplicableCoupons(Cart cart);

    ApplyCouponResponse applyCoupon(Long couponId, Cart cart);
}