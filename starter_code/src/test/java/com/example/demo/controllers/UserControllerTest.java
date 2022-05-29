package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.h2.command.ddl.CreateUser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private final UserRepository userRepo = mock(UserRepository.class);

    private final CartRepository cartRepo = mock(CartRepository.class);

    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository",userRepo);
        TestUtils.injectObjects(userController,"cartRepository",cartRepo);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder",encoder);
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

         ResponseEntity<User> response = userController.createUser(r);
         assertNotNull(response);
         assertEquals(200,response.getStatusCodeValue());

         User u = response.getBody();
         assertNotNull(u);
         assertEquals(0,u.getId());
         assertEquals("test",u.getUsername());
         assertEquals("thisIsHashed",u.getPassword());
    }
    @Test
    public void create_user_password_too_short() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("test");
        r.setConfirmPassword("test");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400,response.getStatusCodeValue());
    }

    @Test
    public void create_user_password_not_equal_confirmPassword() {
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("test1");
        r.setConfirmPassword("test2");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(400,response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_id_not_exist(){
        ResponseEntity<User> response = userController.findById(0L);
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }
    @Test
    public void find_user_by_id_happy_path(){
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        when(userRepo.findById(0L)).thenReturn(java.util.Optional.of(user));
        ResponseEntity<User> response1 = userController.findById(0L);
        assertNotNull(response1);
        assertEquals(200,response.getStatusCodeValue());
        User u = response1.getBody();
        assertNotNull(u);
        assertEquals(0,u.getId());

    }

    @Test
    public void find_user_by_username_not_exist(){
        ResponseEntity<User> response = userController.findByUserName("kunle");
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_username_happy_path(){
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);

        User user = new User();
        Cart cart = new Cart();
        user.setId(0);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        when(userRepo.findByUsername("test")).thenReturn(user);
        ResponseEntity<User> response1 = userController.findByUserName("test");
        assertNotNull(response1);
        assertEquals(200,response.getStatusCodeValue());
        User u = response1.getBody();
        assertNotNull(u);
        assertEquals(0,u.getId());
        assertEquals("test",u.getUsername());

    }
}
