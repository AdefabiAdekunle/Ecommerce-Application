package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepo);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepo);




        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Round Widget");
        item1.setDescription("A widget that is round");
        item1.setPrice(BigDecimal.valueOf(2.99));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item1));



        User user = new User();
        Cart cart = new Cart();

        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        when(userRepo.findByUsername("test")).thenReturn(user);
    }
    @Test
    public void add_to_cart_happy_path() throws Exception{
        ModifyCartRequest modifyCartRequest= new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test");
        ResponseEntity<Cart> response= cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(5.98),cart.getTotal());
    }

    @Test
    public void add_to_cart_username_not_found() throws Exception{
        ModifyCartRequest modifyCartRequest= new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test5");
        ResponseEntity<Cart> response= cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }
    @Test
    public void add_to_cart_item_not_found() throws Exception{
        ModifyCartRequest modifyCartRequest= new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test");
        ResponseEntity<Cart> response= cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }
    @Test
    public void remove_from_cart_username_not_found() throws Exception{
        ModifyCartRequest modifyCartRequest= new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test5");
        ResponseEntity<Cart> response= cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_item_not_found() throws Exception{
        ModifyCartRequest modifyCartRequest= new ModifyCartRequest();
        modifyCartRequest.setItemId(2L);
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername("test");
        ResponseEntity<Cart> response= cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_happy_path() throws Exception{
        ModifyCartRequest modifyCartRequest= new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setQuantity(1);
        modifyCartRequest.setUsername("test");
        ResponseEntity<Cart> response= cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(BigDecimal.valueOf(-2.99),cart.getTotal());
        assertEquals(0,cart.getItems().size());
    }
}
