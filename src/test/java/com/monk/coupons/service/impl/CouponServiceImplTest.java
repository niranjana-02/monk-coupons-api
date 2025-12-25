package com.monk.coupons.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.monk.coupons.exception.CouponNotFoundException;
import com.monk.coupons.model.*;
import com.monk.coupons.repository.CouponRepository;
import com.monk.coupons.service.strategy.CouponStrategy;
import com.monk.coupons.service.strategy.CouponStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponStrategyFactory strategyFactory;

    @Mock
    private CouponStrategy couponStrategy;

    @InjectMocks
    private CouponServiceImpl service;

    private ObjectMapper mapper;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
    }

    private Coupon mockCoupon(Long id, String type) {
        Coupon coupon = new Coupon();
        coupon.setId(id);
        coupon.setType(type);

        ObjectNode details = mapper.createObjectNode();
        details.put("sample", "value");
        coupon.setDetails(details);

        return coupon;
    }

    // --------------------------------------------------------
    // CREATE COUPON
    // --------------------------------------------------------
    @Test
    void testCreateCoupon() {
        Coupon coupon = mockCoupon(1L, "cart-wise");

        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        Coupon saved = service.createCoupon(coupon);

        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(couponRepository, times(1)).save(coupon);
    }

    // --------------------------------------------------------
    // GET ALL COUPONS
    // --------------------------------------------------------
    @Test
    void testGetAllCoupons() {
        when(couponRepository.findAll()).thenReturn(
                List.of(mockCoupon(1L, "cart-wise"))
        );

        List<Coupon> result = service.getAllCoupons();

        assertEquals(1, result.size());
        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void testGetAllCoupons_Empty() {
        when(couponRepository.findAll()).thenReturn(List.of());

        List<Coupon> result = service.getAllCoupons();

        assertTrue(result.isEmpty());
    }

    // --------------------------------------------------------
    // GET COUPON BY ID
    // --------------------------------------------------------
    @Test
    void testGetCouponById() {
        Coupon coupon = mockCoupon(1L, "cart-wise");

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        Coupon result = service.getCouponById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetCoupon_NotFound() {
        when(couponRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class, () -> service.getCouponById(1L));
    }

    // --------------------------------------------------------
    // UPDATE COUPON
    // --------------------------------------------------------
    @Test
    void testUpdateCoupon() {
        Coupon oldData = mockCoupon(1L, "cart-wise");
        Coupon newData = mockCoupon(null, "product-wise");

        ObjectNode newDetails = mapper.createObjectNode().put("x", "y");
        newData.setDetails(newDetails);

        when(couponRepository.findById(1L)).thenReturn(Optional.of(oldData));
        when(couponRepository.save(any(Coupon.class))).thenReturn(oldData);

        Coupon updated = service.updateCoupon(1L, newData);

        assertEquals("product-wise", updated.getType());
        assertEquals("y", updated.getDetails().get("x").asText());
    }

    @Test
    void testUpdateCoupon_NotFound() {
        when(couponRepository.findById(99L)).thenReturn(Optional.empty());

        Coupon newData = mockCoupon(null, "bxgy");

        assertThrows(CouponNotFoundException.class,
                () -> service.updateCoupon(99L, newData));
    }

    // --------------------------------------------------------
    // DELETE COUPON
    // --------------------------------------------------------
    @Test
    void testDeleteCoupon() {
        Coupon coupon = mockCoupon(1L, "cart-wise");

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));

        service.deleteCoupon(1L);

        verify(couponRepository, times(1)).delete(coupon);
    }

    @Test
    void testDeleteCoupon_NotFound() {
        when(couponRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(CouponNotFoundException.class,
                () -> service.deleteCoupon(5L));
    }

    // --------------------------------------------------------
    // GET APPLICABLE COUPONS
    // --------------------------------------------------------
    @Test
    void testGetApplicableCoupons() {

        Coupon c1 = mockCoupon(1L, "cart-wise");
        Coupon c2 = mockCoupon(2L, "product-wise");

        when(couponRepository.findAll()).thenReturn(List.of(c1, c2));
        when(strategyFactory.getStrategy(anyString())).thenReturn(couponStrategy);
        when(couponStrategy.calculateDiscount(any(), any())).thenReturn(20.0);

        ApplicableCouponsResponse response = service.getApplicableCoupons(new Cart());

        assertEquals(2, response.getApplicableCoupons().size());
        assertEquals(20.0, response.getApplicableCoupons().get(0).getDiscount());
    }

    @Test
    void testGetApplicableCoupons_DiscountZero_Excluded() {
        Coupon coupon = mockCoupon(1L, "cart-wise");

        when(couponRepository.findAll()).thenReturn(List.of(coupon));
        when(strategyFactory.getStrategy(anyString())).thenReturn(couponStrategy);
        when(couponStrategy.calculateDiscount(any(), any())).thenReturn(0.0);

        ApplicableCouponsResponse response = service.getApplicableCoupons(new Cart());

        assertTrue(response.getApplicableCoupons().isEmpty());
    }

    @Test
    void testGetApplicableCoupons_NullDetails_Excluded() {
        Coupon coupon = mockCoupon(1L, "cart-wise");
        coupon.setDetails(null);

        when(couponRepository.findAll()).thenReturn(List.of(coupon));
        when(strategyFactory.getStrategy(nullable(String.class)))
                .thenReturn(couponStrategy);

        ApplicableCouponsResponse response = service.getApplicableCoupons(new Cart());

        assertTrue(response.getApplicableCoupons().isEmpty());
    }

    @Test
    void testGetApplicableCoupons_StrategyCalledWithCorrectType() {
        Coupon coupon = mockCoupon(1L, "product-wise");

        when(couponRepository.findAll()).thenReturn(List.of(coupon));
        when(strategyFactory.getStrategy("product-wise")).thenReturn(couponStrategy);
        when(couponStrategy.calculateDiscount(any(), any())).thenReturn(10.0);

        ApplicableCouponsResponse result = service.getApplicableCoupons(new Cart());

        assertEquals(1, result.getApplicableCoupons().size());
        verify(strategyFactory).getStrategy("product-wise");
    }

    // --------------------------------------------------------
    // APPLY COUPON
    // --------------------------------------------------------
    @Test
    void testApplyCoupon() {
        Coupon coupon = mockCoupon(1L, "cart-wise");

        Cart cart = new Cart();
        cart.setItems(List.of(createItem(1, 2, 50)));

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(strategyFactory.getStrategy("cart-wise")).thenReturn(couponStrategy);

        when(couponStrategy.calculateDiscount(any(), any())).thenReturn(30.0);

        ApplyCouponResponse response = service.applyCoupon(1L, cart);

        assertNotNull(response.getUpdatedCart());
        assertEquals(30.0, response.getUpdatedCart().getTotalDiscount());
    }

    @Test
    void testApplyCoupon_MultipleItems() {
        Coupon coupon = mockCoupon(1L, "cart-wise");

        CartItem i1 = new CartItem();
        i1.setProductId(1);
        i1.setQuantity(2);
        i1.setPrice(50.0);

        CartItem i2 = new CartItem();
        i2.setProductId(2);
        i2.setQuantity(1);
        i2.setPrice(100.0);

        Cart cart = new Cart();
        cart.setItems(List.of(i1, i2));

        when(couponRepository.findById(1L)).thenReturn(Optional.of(coupon));
        when(strategyFactory.getStrategy("cart-wise")).thenReturn(couponStrategy);
        when(couponStrategy.calculateDiscount(any(), any())).thenReturn(30.0);

        ApplyCouponResponse response = service.applyCoupon(1L, cart);

        assertEquals(200.0, response.getUpdatedCart().getTotalPrice());
        assertEquals(30.0, response.getUpdatedCart().getTotalDiscount());
        assertEquals(170.0, response.getUpdatedCart().getFinalPrice());
    }

    @Test
    void testApplyCoupon_NotFound() {
        when(couponRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrowsExactly(
                CouponNotFoundException.class,
                () -> service.applyCoupon(123L, new Cart())
        );
    }

    private CartItem createItem(int id, int qty, double price) {
        CartItem item = new CartItem();
        item.setProductId(id);
        item.setQuantity(qty);
        item.setPrice(price);
        return item;
    }
}