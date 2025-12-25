package com.monk.coupons.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.exception.CouponNotFoundException;
import com.monk.coupons.model.Cart;
import com.monk.coupons.model.Coupon;
import com.monk.coupons.model.ApplicableCoupon;
import com.monk.coupons.model.ApplicableCouponsResponse;
import com.monk.coupons.model.ApplyCouponResponse;
import com.monk.coupons.model.ApplyCouponResponse.UpdatedCart;
import com.monk.coupons.repository.CouponRepository;
import com.monk.coupons.service.CouponService;
import com.monk.coupons.service.strategy.CouponStrategy;
import com.monk.coupons.service.strategy.CouponStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository repository;
    private final CouponStrategyFactory strategyFactory;
    private final ObjectMapper mapper;

    @Override
    public Coupon createCoupon(Coupon coupon) {
        return repository.save(coupon);
    }

    @Override
    public List<Coupon> getAllCoupons() {
        return repository.findAll();
    }

    @Override
    public Coupon getCouponById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));
    }

    @Override
    public Coupon updateCoupon(Long id, Coupon updated) {
        Coupon existing = getCouponById(id);
        existing.setType(updated.getType());
        existing.setDetails(updated.getDetails());
        return repository.save(existing);
    }

    @Override
    public void deleteCoupon(Long id) {
        repository.delete(getCouponById(id));
    }

    @Override
    public ApplicableCouponsResponse getApplicableCoupons(Cart cart) {

        List<ApplicableCoupon> applicableCoupons = repository.findAll().stream()
                .map(coupon -> {
                    CouponStrategy strategy = strategyFactory.getStrategy(coupon.getType());
                    double discount = strategy.calculateDiscount(coupon, cart);

                    if (discount > 0) {
                        return new ApplicableCoupon(
                                coupon.getId(),
                                coupon.getType(),
                                discount
                        );
                    }
                    return null;
                })
                .filter(c -> c != null)
                .toList();

        return new ApplicableCouponsResponse(applicableCoupons);
    }

    @Override
    public ApplyCouponResponse applyCoupon(Long couponId, Cart cart) {

        Coupon coupon = getCouponById(couponId);
        CouponStrategy strategy = strategyFactory.getStrategy(coupon.getType());

        // calculate discount
        double discount = strategy.calculateDiscount(coupon, cart);

        // calculate totals
        double totalPrice = cart.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        double finalPrice = totalPrice - discount;

        UpdatedCart updatedCart = new UpdatedCart(
                cart.getItems(),
                totalPrice,
                discount,
                finalPrice
        );

        return new ApplyCouponResponse(updatedCart);
    }
}