package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Wrapper response containing all applicable coupons.")
public class ApplicableCouponsResponse {

    @Schema(
            description = "List of applicable coupons for the provided cart.",
            required = true
    )
    private List<ApplicableCoupon> applicableCoupons;
}