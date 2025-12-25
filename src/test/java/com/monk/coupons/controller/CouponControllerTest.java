package com.monk.coupons.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monk.coupons.exception.CouponNotFoundException;
import com.monk.coupons.model.Coupon;
import com.monk.coupons.service.CouponService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CouponService couponService;

    @Autowired
    private ObjectMapper mapper;

    private Coupon mockCoupon(Long id) {
        Coupon c = new Coupon();
        c.setId(id);
        c.setType("cart-wise");

        JsonNode node = mapper.createObjectNode()
                .put("threshold", 100)
                .put("discount", 10);

        c.setDetails(node);
        return c;
    }

    // ----------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------
    @Test
    void testCreateCoupon() throws Exception {
        Coupon created = mockCoupon(1L);

        when(couponService.createCoupon(any())).thenReturn(created);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "cart-wise",
                                  "details": { "threshold": 100, "discount": 10 }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("cart-wise"))
                .andExpect(jsonPath("$.details.threshold").value(100));
    }

    @Test
    void testCreateCoupon_InvalidJSON() throws Exception {
        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json"))
                .andExpect(status().isBadRequest());
    }

    // ----------------------------------------------------------
    // GET ALL
    // ----------------------------------------------------------
    @Test
    void testGetAllCoupons() throws Exception {
        when(couponService.getAllCoupons())
                .thenReturn(List.of(mockCoupon(1L), mockCoupon(2L)));

        mockMvc.perform(get("/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testGetAllCoupons_Empty() throws Exception {
        when(couponService.getAllCoupons()).thenReturn(List.of());

        mockMvc.perform(get("/coupons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ----------------------------------------------------------
    // GET BY ID
    // ----------------------------------------------------------
    @Test
    void testGetCouponById() throws Exception {
        when(couponService.getCouponById(1L)).thenReturn(mockCoupon(1L));

        mockMvc.perform(get("/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.type").value("cart-wise"));
    }

    @Test
    void testGetCouponById_NotFound() throws Exception {
        when(couponService.getCouponById(10L))
                .thenThrow(new CouponNotFoundException(10L));

        mockMvc.perform(get("/coupons/10"))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------
    @Test
    void testUpdateCoupon() throws Exception {
        Coupon updated = mockCoupon(1L);
        updated.setDetails(
                mapper.readTree("{\"threshold\":200,\"discount\":15}")
        );

        when(couponService.updateCoupon(eq(1L), any()))
                .thenReturn(updated);

        mockMvc.perform(put("/coupons/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "cart-wise",
                                  "details": { "threshold": 200, "discount": 15 }
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.details.threshold").value(200))
                .andExpect(jsonPath("$.details.discount").value(15));
    }

    @Test
    void testUpdateCoupon_NotFound() throws Exception {
        when(couponService.updateCoupon(eq(999L), any()))
                .thenThrow(new CouponNotFoundException(999L));

        mockMvc.perform(put("/coupons/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "type": "cart-wise",
                                  "details": { "threshold": 100, "discount": 10 }
                                }
                                """))
                .andExpect(status().isNotFound());
    }

    // ----------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------
    @Test
    void testDeleteCoupon() throws Exception {
        doNothing().when(couponService).deleteCoupon(1L);

        mockMvc.perform(delete("/coupons/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Coupon deleted successfully."));
    }

    @Test
    void testDeleteCoupon_NotFound() throws Exception {
        doThrow(new CouponNotFoundException(5L))
                .when(couponService).deleteCoupon(5L);

        mockMvc.perform(delete("/coupons/5"))
                .andExpect(status().isNotFound());
    }
}