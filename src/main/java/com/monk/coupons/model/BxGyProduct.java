package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        name = "BxGyProduct",
        description = "Represents a Buy or Get product definition in a BxGy coupon rule."
)
public class BxGyProduct {

    @Schema(
            description = "Product ID involved in the BxGy offer.",
            example = "1",
            required = true
    )
    private Integer productId;

    @Schema(
            description = "Quantity of this product required (Buy) or awarded (Get) in the BxGy rule.",
            example = "3",
            required = true
    )
    private Integer quantity;
}