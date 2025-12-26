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

        if (coupon.getType() == null || coupon.getType().isBlank()) {
            throw new IllegalArgumentException("Field 'type' is required.");
        }
        if (coupon.getDetails() == null) {
            throw new IllegalArgumentException("Field 'details' is required and must be valid JSON.");
        }

        // Normalize the type value
        String type = coupon.getType().trim().toLowerCase();

        switch (type) {
            case "cart-wise", "product-wise", "bxgy" -> {
                // valid → do nothing
            }
            default -> throw new IllegalArgumentException(
                    "Invalid coupon type. Allowed values: cart-wise, product-wise, bxgy."
            );
        }

        coupon.setType(type);

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

        if (updated.getType() == null || updated.getDetails() == null) {
            throw new IllegalArgumentException("Both 'type' and 'details' fields are required for updating a coupon.");
        }

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

        if (cart == null || cart.getItems() == null) {
            UpdatedCart updated = new UpdatedCart(
                    List.of(),
                    0.0,
                    0.0,
                    0.0
            );
            return new ApplyCouponResponse(updated);
        }

        CouponStrategy strategy = strategyFactory.getStrategy(coupon.getType());
        double discount = strategy.calculateDiscount(coupon, cart);

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