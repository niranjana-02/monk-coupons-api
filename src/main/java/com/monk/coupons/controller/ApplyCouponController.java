package com.monk.coupons.controller;

import com.monk.coupons.model.Cart;
import com.monk.coupons.model.ApplicableCouponsResponse;
import com.monk.coupons.model.ApplyCouponResponse;
import com.monk.coupons.service.CouponService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequiredArgsConstructor
@Tag(name = "Coupon Evaluation API", description = "Endpoints to evaluate and apply coupons to a cart")
public class ApplyCouponController {

    private final CouponService couponService;

    @Operation(
            summary = "Get applicable coupons",
            description = "Evaluates the provided cart and returns all applicable coupons.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Applicable coupons returned successfully",
                            content = @Content(schema = @Schema(implementation = ApplicableCouponsResponse.class))
                    )
            }
    )
    @PostMapping("/applicable-coupons")
    public ResponseEntity<ApplicableCouponsResponse> getApplicableCoupons(
            @RequestBody Cart cart
    ) {
        return ResponseEntity.ok(
                couponService.getApplicableCoupons(cart)
        );
    }

    @Operation(
            summary = "Apply coupon",
            description = "Applies the given coupon to the cart and returns the updated cart details.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Coupon applied successfully",
                            content = @Content(schema = @Schema(implementation = ApplyCouponResponse.class))
                    )
            }
    )
    @PostMapping("/apply-coupon/{id}")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(
            @PathVariable Long id,
            @RequestBody Cart cart
    ) {
        return ResponseEntity.ok(
                couponService.applyCoupon(id, cart)
        );
    }
}