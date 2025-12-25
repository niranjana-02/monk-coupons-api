package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        name = "CartItem",
        description = "Represents a single item inside the customer's cart.",
        example = """
                {
                  "product_id": 101,
                  "quantity": 2,
                  "price": 49.99
                }
                """
)
public class CartItem {

    @Schema(
            description = "Unique identifier of the product added to the cart.",
            example = "101",
            required = true
    )
    private Integer productId;

    @Schema(
            description = "Number of units of this product in the cart.",
            example = "2",
            required = true
    )
    private Integer quantity;

    @Schema(
            description = "Unit price of the product at the time of cart creation.",
            example = "49.99",
            required = true
    )
    private Double price;
}