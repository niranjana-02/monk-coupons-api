package com.monk.coupons.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.monk.coupons.model.*;
import com.monk.coupons.model.ApplyCouponResponse.UpdatedCart;
import com.monk.coupons.service.CouponService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplyCouponController.class)
class ApplyCouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper mapper;

    private Cart sampleCart() {
        CartItem item = new CartItem();
        item.setProductId(1);
        item.setQuantity(2);
        item.setPrice(50.0);

        Cart cart = new Cart();
        cart.setItems(List.of(item));
        return cart;
    }

    // --------------------------
    // TEST 1: applicable coupons
    // --------------------------
    @Test
    void testApplicableCoupons() throws Exception {

        ApplicableCoupon coupon =
                new ApplicableCoupon(1L, "cart-wise", 20.0);

        ApplicableCouponsResponse response =
                new ApplicableCouponsResponse(List.of(coupon));

        when(couponService.getApplicableCoupons(any()))
                .thenReturn(response);

        mockMvc.perform(post("/applicable-coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sampleCart())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicable_coupons[0].coupon_id").value(1L))
                .andExpect(jsonPath("$.applicable_coupons[0].type").value("cart-wise"))
                .andExpect(jsonPath("$.applicable_coupons[0].discount").value(20.0));
    }

    // ----------------------------------------------
    // TEST 2: applicable coupons → empty list
    // ----------------------------------------------
    @Test
    void testApplicableCoupons_Empty() throws Exception {

        ApplicableCouponsResponse response =
                new ApplicableCouponsResponse(List.of());

        when(couponService.getApplicableCoupons(any()))
                .thenReturn(response);

        mockMvc.perform(post("/applicable-coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sampleCart())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applicable_coupons").isArray())
                .andExpect(jsonPath("$.applicable_coupons").isEmpty());
    }

    // ----------------------------------------------
    // TEST 3: apply coupon success
    // ----------------------------------------------
    @Test
    void testApplyCoupon() throws Exception {

        UpdatedCart updatedCart = new UpdatedCart(
                sampleCart().getItems(),
                100.0,   // total_price
                20.0,    // total_discount
                80.0     // final_price
        );

        ApplyCouponResponse response = new ApplyCouponResponse(updatedCart);

        when(couponService.applyCoupon(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(post("/apply-coupon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sampleCart())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated_cart.total_price").value(100.0))
                .andExpect(jsonPath("$.updated_cart.total_discount").value(20.0))
                .andExpect(jsonPath("$.updated_cart.final_price").value(80.0))
                .andExpect(jsonPath("$.updated_cart.items").isArray());
    }

    // ----------------------------------------------
    // TEST 4: apply coupon → zero discount
    // ----------------------------------------------
    @Test
    void testApplyCoupon_ZeroDiscount() throws Exception {

        UpdatedCart updatedCart = new UpdatedCart(
                sampleCart().getItems(),
                100.0,
                0.0,
                100.0
        );

        ApplyCouponResponse response = new ApplyCouponResponse(updatedCart);

        when(couponService.applyCoupon(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(post("/apply-coupon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sampleCart())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.updated_cart.total_discount").value(0.0))
                .andExpect(jsonPath("$.updated_cart.final_price").value(100.0));
    }

    // ----------------------------------------------
    // TEST 5: apply coupon → coupon not found / error
    // ----------------------------------------------
    @Test
    void testApplyCoupon_CouponNotFound() throws Exception {

        when(couponService.applyCoupon(eq(999L), any()))
                .thenThrow(new RuntimeException("Coupon not found"));

        mockMvc.perform(post("/apply-coupon/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(sampleCart())))
                .andExpect(status().is5xxServerError());
    }
}