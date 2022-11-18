package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.exceptions.MyResourceBadRequestException;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private User sampleUser;

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepo);
        TestUtils.injectObjects(userController, "cartRepository", cartRepo);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);

        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("sample");
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
        when(userRepo.findById(anyLong())).thenReturn(Optional.ofNullable(sampleUser));

        final ResponseEntity<User> responseById = userController.findById(sampleUser.getId());
        User user = responseById.getBody();
        assertNotNull(user);
        assertEquals(sampleUser.getId(), user.getId());
        assertEquals(sampleUser.getUsername(), user.getUsername());
    }

    @Test
    public void create_user_sad_path() throws Exception {
        try {
            when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
            CreateUserRequest r = createUserSad();
            final ResponseEntity<User> response = userController.createUser(r);
        }catch (Exception e){
            assertNotNull(e);
        }
    }

    @Test
    public void find_by_user_name(){
        when(userRepo.findByUsername(anyString())).thenReturn(sampleUser);

        final ResponseEntity<User> response = userController.findByUserName(sampleUser.getUsername());
        User user = response.getBody();
        assertNotNull(user);
        assertEquals(sampleUser.getId(), user.getId());
        assertEquals(sampleUser.getUsername(), user.getUsername());
    }

    private static CreateUserRequest createUserHappy() {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setConfirmPassword("testPassword");
        return user;
    }

    private static CreateUserRequest createUserSad() {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setConfirmPassword("testPasword");
        return user;
    }
}
