package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        name = "Cart",
        description = "Represents a shopping cart containing multiple items for coupon evaluation.",
        example = """
                {
                  "items": [
                    { "product_id": 101, "quantity": 2, "price": 50.0 },
                    { "product_id": 205, "quantity": 1, "price": 120.0 }
                  ]
                }
                """
)
public class Cart {

    @Schema(
            description = "List of items in the cart. Each item includes product_id, quantity, and price.",
            required = true
    )
    private List<CartItem> items;
}