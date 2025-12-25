package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        name = "CartWiseDetails",
        description = "Represents discount rules for cart-wise coupons. A percentage discount is applied when the cart total meets or exceeds a threshold.",
        example = """
                {
                  "threshold": 100,
                  "discount": 10
                }
                """
)
public class CartWiseDetails {

    @Schema(
            description = "Minimum cart value required to activate the discount.",
            example = "100",
            required = true
    )
    private Double threshold;

    @Schema(
            description = "Percentage discount applied when the threshold condition is met.",
            example = "10",
            required = true
    )
    private Double discount;
}