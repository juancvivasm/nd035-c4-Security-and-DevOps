package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void create_user_happy_path(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest r = createUserHappy();

        final ResponseEntity<User> response = userController.createUser(r);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void find_by_id(){
        CreateUserRequest r = createUserHappy();
        final ResponseEntity<User> response = userController.createUser(r);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());

        final ResponseEntity<User> responseById = userController.findById(u.getId());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(u.getId(), user.getId());
        assertEquals(u.getUsername(), user.getUsername());

    }

    @Test
    public void find_by_user_name(){
        CreateUserRequest r = createUserHappy();
        final ResponseEntity<User> response = userController.createUser(r);
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());

        final ResponseEntity<User> responseById = userController.findByUserName(u.getUsername());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(u.getId(), user.getId());
        assertEquals(u.getUsername(), user.getUsername());

    }

    private static CreateUserRequest createUserHappy() {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setConfirmPassword("testPassword");
        return user;
    }
}
