package com.monk.coupons.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.monk.coupons.config.JsonNodeConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Schema(
        name = "Coupon",
        description = "Represents a coupon with its type and the associated discount rule."
)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(
            description = "Auto-generated unique coupon ID.",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "type is required")
    @Schema(
            description = """
                    Type of coupon. Determines the discount strategy applied.
                    Allowed values:
                      - cart-wise
                      - product-wise
                      - bxgy
                    """,
            example = "cart-wise",
            required = true
    )
    private String type;

    @NotNull(message = "details is required")
    @Convert(converter = JsonNodeConverter.class)
    @Column(columnDefinition = "TEXT")
    @Schema(
            description = """
                    Structured discount rule stored as JSON.
                    
                    Examples:
                    
                    • Cart-wise coupon:
                      {
                        "threshold": 100,
                        "discount": 10
                      }
                    
                    • Product-wise coupon:
                      {
                        "product_id": 1,
                        "discount": 20
                      }
                    
                    • BxGy coupon:
                      {
                        "buy_products": [
                          { "product_id": 1, "quantity": 3 },
                          { "product_id": 2, "quantity": 3 }
                        ],
                        "get_products": [
                          { "product_id": 3, "quantity": 1 }
                        ],
                        "repetition_limit": 2
                      }
                    """,
            anyOf = {
                    CartWiseDetails.class,
                    ProductWiseDetails.class,
                    BxGyDetails.class
            },
            required = true,
            example = """
                    { "threshold": 100, "discount": 10 }
                    """
    )
    private JsonNode details;
}