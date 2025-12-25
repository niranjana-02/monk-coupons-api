package com.monk.coupons.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI couponAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Monk Commerce - Coupon API")
                        .description("Backend task showcasing a flexible coupon engine using Strategy Pattern & JSON rule storage.")
                        .version("1.0.0")
                        .license(null))
                .externalDocs(new ExternalDocumentation()
                        .description("Monk Commerce Assignment")
                        .url("https://monkcommerce.com"));
    }
}