package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;

public class OrderControllerTest {
    private OrderController orderController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final OrderRepository orderRepo = mock(OrderRepository.class);


    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController,"userRepository",userRepo);
        TestUtils.injectObjects(orderController,"orderRepository",orderRepo);

        ModifyCartRequest modifyCartRequest= new ModifyCartRequest();
        modifyCartRequest.setItemId(0L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test");

        Item item1 = new Item();
        item1.setId(0L);
        item1.setName("Round Widget");
        item1.setDescription("A widget that is round");
        item1.setPrice(BigDecimal.valueOf(2.99));

        Item item2 = new Item();
        item2.setId(1L);
        item2.setName("Square Widget");
        item2.setDescription("A widget that is square");
        item2.setPrice(BigDecimal.valueOf(1.99));


        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");

        cart.setUser(user);
        cart.setId(0L);
        cart.addItem(item1);
        cart.addItem(item2);
        user.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(user);
        UserOrder order=UserOrder.createFromCart(cart);


        when(orderRepo.findByUser(user)).thenReturn(Collections.singletonList(order));
    }
    @Test
    public void submit_order_happy_path() throws Exception{
        ResponseEntity<UserOrder> response= orderController.submit("test");

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        UserOrder u = response.getBody();
        assertNotNull(u);
        assertEquals(2,u.getItems().size());
    }

    @Test
    public void submit_order_error(){
        ResponseEntity<UserOrder> response= orderController.submit("kunle");
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void get_order_for_user_happy_path(){
        ResponseEntity<List<UserOrder>> response= orderController.getOrdersForUser("test");
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        List<UserOrder> u = response.getBody();
        assertNotNull(u);
        assertEquals(1,u.size());
    }
    @Test
    public void get_order_for_user_not_found(){
        ResponseEntity<List<UserOrder>> response= orderController.getOrdersForUser("kunle");
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }
}
