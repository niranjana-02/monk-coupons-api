package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        name = "ProductWiseDetails",
        description = "Defines percentage-based discounts applied to a specific product in the cart.",
        example = """
                {
                  "product_id": 101,
                  "discount": 20
                }
                """
)
public class ProductWiseDetails {

    @Schema(
            description = "ID of the product to which the discount applies.",
            example = "101",
            required = true
    )
    private Integer productId;

    @Schema(
            description = "Discount percentage applied to the selected product.",
            example = "20",
            required = true
    )
    private Double discount;
}