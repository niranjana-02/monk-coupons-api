package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Represents a single applicable coupon and its discount.")
public class ApplicableCoupon {

    @Schema(example = "3")
    private Long couponId;

    @Schema(example = "bxgy")
    private String type;

    @Schema(example = "120.0")
    private Double discount;
}