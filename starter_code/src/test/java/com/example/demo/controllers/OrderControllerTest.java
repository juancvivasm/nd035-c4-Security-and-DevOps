package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.MyResourceNotFoundException;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.h2.command.ddl.CreateUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);
    private UserOrder sampleUserOrder01;
    private UserOrder sampleUserOrder02;
    private List<UserOrder> userOrderList = new ArrayList<>();
    private User sampleUser;
    private Item sampleItem01;
    private Item sampleItem02;
    private Cart cart01;
    private Cart cart02;
    //private List<Item> itemList = new ArrayList<>();

    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepo);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepo);

        sampleItem01 = new Item();
        sampleItem01.setId(1L);
        sampleItem01.setName("Item 01");
        sampleItem01.setPrice(BigDecimal.valueOf(1.99));
        sampleItem01.setDescription("Description item 01");
        //itemList.add(sampleItem01);

        sampleItem02 = new Item();
        sampleItem02.setId(2L);
        sampleItem02.setName("Item 02");
        sampleItem02.setPrice(BigDecimal.valueOf(2.99));
        sampleItem02.setDescription("Description item 02");
        //itemList.add(sampleItem02);

        //BigDecimal sumTotal = itemList.stream().map(x -> x.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("test");

        cart01 = new Cart();
        cart01.setId(1L);
        cart01.setUser(sampleUser);
        cart01.addItem(sampleItem01);
        cart01.addItem(sampleItem02);

        sampleUser.setCart(cart01);

        sampleUserOrder01 = UserOrder.createFromCart(cart01);
        userOrderList.add(sampleUserOrder01);

        cart02 = new Cart();
        cart02.setId(2L);
        cart02.setUser(sampleUser);
        cart02.addItem(sampleItem01);

        sampleUserOrder02 = UserOrder.createFromCart(cart02);
        userOrderList.add(sampleUserOrder02);

        /*
        sampleUserOrder01 = new UserOrder();
        sampleUserOrder01.setId(1L);
        sampleUserOrder01.setItems(itemList);
        sampleUserOrder01.setUser(sampleUser);
        sampleUserOrder01.setTotal(sumTotal);
        userOrderList.add(sampleUserOrder01);

        sampleUserOrder02 = new UserOrder();
        sampleUserOrder02.setId(2L);
        sampleUserOrder02.setItems(itemList);
        sampleUserOrder02.setUser(sampleUser);
        sampleUserOrder02.setTotal(sumTotal);
        userOrderList.add(sampleUserOrder02);
        */
    }

    @Test
    public void submit_happy(){
        when(userRepo.findByUsername(anyString())).thenReturn(sampleUser);
        //when(UserOrder.createFromCart(any(Cart.class))).thenReturn(sampleUserOrder01);
        when(orderRepo.save(any(UserOrder.class))).thenReturn(sampleUserOrder01);

        final ResponseEntity<UserOrder> response = orderController.submit(sampleUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final UserOrder userOrder = response.getBody();
        assertNotNull(userOrder);
        assertNotNull(userOrder.getItems());
        assertTrue(userOrder.getItems().size()==2);
        assertEquals(BigDecimal.valueOf(4.98), userOrder.getTotal());
    }

    @Test
    public void submit_sad(){
        try {
            final ResponseEntity<UserOrder> response = orderController.submit(sampleUser.getUsername());
        }catch (Exception e){
            assertNotNull(e);
        }
    }

    @Test
    public void get_orders_for_user(){
        when(userRepo.findByUsername(anyString())).thenReturn(sampleUser);
        when(orderRepo.findByUser(any(User.class))).thenReturn(userOrderList);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(sampleUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> ordersForUser = response.getBody();
        assertNotNull(ordersForUser);
        assertEquals(sampleUserOrder01.getId(), ordersForUser.get(0).getId());
        assertTrue(ordersForUser.size()==2);
        assertEquals(sampleUser.getId(), ordersForUser.get(1).getUser().getId());
        assertEquals(BigDecimal.valueOf(1.99), ordersForUser.get(1).getTotal());
    }
}
