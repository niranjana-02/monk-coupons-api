package com.monk.coupons.controller;

import com.monk.coupons.model.Coupon;
import com.monk.coupons.service.CouponService;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "CRUD endpoints for creating and managing coupons.")
public class CouponController {

    private final CouponService couponService;

    /**
     * Create a new coupon.
     */
    @Operation(
            summary = "Create coupon",
            description = "Creates a new coupon. `type` must be one of: cart-wise, product-wise, bxgy.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Cart-wise Coupon",
                                            value = """
                                                    {
                                                      "type": "cart-wise",
                                                      "details": {
                                                        "threshold": 100,
                                                        "discount": 10
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Product-wise Coupon",
                                            value = """
                                                    {
                                                      "type": "product-wise",
                                                      "details": {
                                                        "product_id": 101,
                                                        "discount": 20
                                                      }
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "BxGy Coupon",
                                            value = """
                                                    {
                                                      "type": "bxgy",
                                                      "details": {
                                                        "buy_products": [
                                                          { "product_id": 1, "quantity": 3 },
                                                          { "product_id": 2, "quantity": 3 }
                                                        ],
                                                        "get_products": [
                                                          { "product_id": 3, "quantity": 1 }
                                                        ],
                                                        "repetition_limit": 2
                                                      }
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    )
    @PostMapping
    public ResponseEntity<?> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(couponService.createCoupon(coupon));
    }

    /**
     * Retrieve all coupons.
     */
    @Operation(
            summary = "Get all coupons",
            description = "Returns a list of all coupons.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of coupons returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = Coupon.class))
                            )
                    )
            }
    )
    @GetMapping
    public ResponseEntity<?> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    /**
     * Retrieve a coupon by its ID.
     */
    @Operation(
            summary = "Get coupon by ID",
            description = "Returns the coupon identified by the given ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Coupon found",
                            content = @Content(schema = @Schema(implementation = Coupon.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Coupon not found"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    /**
     * Update a coupon.
     */
    @Operation(
            summary = "Update coupon",
            description = "Updates an existing coupon.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = Coupon.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Coupon updated successfully",
                            content = @Content(schema = @Schema(implementation = Coupon.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Coupon not found"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(
            @PathVariable Long id,
            @RequestBody Coupon coupon
    ) {
        return ResponseEntity.ok(couponService.updateCoupon(id, coupon));
    }

    /**
     * Delete a coupon.
     */
    @Operation(
            summary = "Delete coupon",
            description = "Deletes the coupon identified by ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Coupon deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Coupon not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Coupon deleted successfully.");
    }
}