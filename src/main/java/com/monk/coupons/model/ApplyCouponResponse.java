package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Data
@AllArgsConstructor
@Schema(description = "Wrapper for the updated cart after applying a coupon.")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ApplyCouponResponse {

    @Schema(description = "Cart after applying coupon, containing updated price calculations.")
    private UpdatedCart updatedCart;

    @Data
    @AllArgsConstructor
    @Schema(description = "Updated cart values after coupon application.")
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UpdatedCart {

        @Schema(description = "List of cart items after applying the coupon, including updated quantities, prices, and discounts.")
        private List<CartItem> items;


        @Schema(description = "Original total price before discount.")
        private Double totalPrice;

        @Schema(description = "Total discount applied.")
        private Double totalDiscount;

        @Schema(description = "Final payable price after applying discount.")
        private Double finalPrice;
    }
}