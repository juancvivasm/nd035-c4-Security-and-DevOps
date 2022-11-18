package com.example.demo.controllers;

import java.util.Optional;

import com.example.demo.exceptions.MyResourceBadRequestException;
import com.example.demo.exceptions.MyResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) throws MyResourceNotFoundException {
		log.info("UserController:findById execution started...");
		log.info("UserController:findById id set with: {}", id);
		final Optional<User> userOptional = userRepository.findById(id);
		if(!userOptional.isPresent()){
			log.info("UserController:findById Error - User id: {} not found...", id);
			throw new MyResourceNotFoundException("User id: " + id + " not found");
		}
		log.info("UserController:findById execution ended...");
		return ResponseEntity.ok(userOptional.get());
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) throws MyResourceNotFoundException {
		log.info("UserController:findByUserName execution started...");
		log.info("UserController:findByUserName user name set with: {}", username);
		User user = userRepository.findByUsername(username);
		if(user == null){
			log.info("UserController:findByUserName Error - User {} not found...", username);
			throw new MyResourceNotFoundException("User " + username + " not found");
		}
		log.info("UserController:findByUserName execution ended...");
		return ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) throws MyResourceBadRequestException {
		log.info("UserController:createUser execution started...");
		if(createUserRequest.getPassword().length()<7 ||
				!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())){
			log.info("UserController:createUser Error - Either length is less than 7 or pass and conf pass do not match. Unable to create {}...", createUserRequest.getUsername());
			throw new MyResourceBadRequestException("Either length is less than 7 or pass and conf pass do not match");
		}

		Optional<User> optionalUser = Optional.ofNullable(userRepository.findByUsername(createUserRequest.getUsername()));
		if(optionalUser.isPresent()){
			log.info("UserController:createUser Error - User {} already exists...", createUserRequest.getUsername());
			throw new MyResourceBadRequestException("User " + createUserRequest.getUsername() + " already exists");
		}

		String encodedPassword = bCryptPasswordEncoder.encode(createUserRequest.getPassword());
		log.info("UserController:createUser user name set with: {}", createUserRequest.getUsername());
		User user = new User();
		user.setUsername(createUserRequest.getUsername());
		Cart cart = new Cart();
		cartRepository.save(cart);
		user.setCart(cart);
		user.setPassword(encodedPassword);
		userRepository.save(user);
		log.info("UserController:createUser execution ended...");
		return ResponseEntity.ok(user);
	}
}
