package com.monk.coupons.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(
        name = "BxGyDetails",
        description = "Defines the rule structure for Buy-X-Get-Y (BxGy) promotional coupons."
)
public class BxGyDetails {

    @Schema(
            description = "List of products the customer must purchase to qualify for the BxGy offer.",
            example = """
                    [
                      { "product_id": 1, "quantity": 3 },
                      { "product_id": 2, "quantity": 3 }
                    ]
                    """
    )
    private List<BxGyProduct> buyProducts;

    @Schema(
            description = "List of products the customer receives for free when the offer is applied.",
            example = """
                    [
                      { "product_id": 3, "quantity": 1 }
                    ]
                    """
    )
    private List<BxGyProduct> getProducts;

    @Schema(
            description = "Maximum number of times this BxGy offer can be applied to a single cart.",
            example = "2",
            required = true
    )
    private Integer repetitionLimit;
}