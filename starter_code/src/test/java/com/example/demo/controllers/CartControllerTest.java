package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private User sampleUser;
    private Cart cart;
    private Item sampleItem01;
    private Item sampleItem02;

    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);

        sampleItem01 = new Item();
        sampleItem01.setId(1L);
        sampleItem01.setName("Item 01");
        sampleItem01.setPrice(BigDecimal.valueOf(1.99));
        sampleItem01.setDescription("Description item 01");

        sampleItem02 = new Item();
        sampleItem02.setId(2L);
        sampleItem02.setName("Item 02");
        sampleItem02.setPrice(BigDecimal.valueOf(2.99));
        sampleItem02.setDescription("Description item 02");

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("test");

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(sampleUser);
        cart.addItem(sampleItem01);

        sampleUser.setCart(cart);
    }

    @Test
    public void add_to_cart(){
        when(userRepo.findByUsername(anyString())).thenReturn(sampleUser);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.ofNullable(sampleItem02));
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        final ModifyCartRequest request = modifyCartRequest();
        request.setItemId(sampleItem02.getId());
        request.setQuantity(1);
        final ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final Cart rCart = response.getBody();
        assertEquals(cart.getId(), rCart.getId());
        assertNotNull(rCart.getItems());
        assertTrue(rCart.getItems().size()==2);
        assertEquals(cart.getUser().getId(), rCart.getUser().getId());
        assertEquals(cart.getUser().getUsername(), rCart.getUser().getUsername());
        assertEquals(BigDecimal.valueOf(4.98), rCart.getTotal());
    }

    @Test
    public void remove_from_cart(){
        when(userRepo.findByUsername(anyString())).thenReturn(sampleUser);
        when(itemRepo.findById(anyLong())).thenReturn(Optional.ofNullable(sampleItem01));
        when(cartRepo.save(any(Cart.class))).thenReturn(cart);

        final ModifyCartRequest request = modifyCartRequest();
        request.setItemId(sampleItem01.getId());
        request.setQuantity(1);
        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final Cart rCart = response.getBody();
        assertNotNull(rCart.getItems());
        assertTrue(rCart.getItems().size()==0);
        assertEquals(cart.getUser().getId(), rCart.getUser().getId());
        assertEquals(cart.getUser().getUsername(), rCart.getUser().getUsername());
        assertThat(BigDecimal.valueOf(0.00), Matchers.comparesEqualTo(rCart.getTotal()));
    }

    private static ModifyCartRequest modifyCartRequest() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("test");
        return modifyCartRequest;
    }
}
