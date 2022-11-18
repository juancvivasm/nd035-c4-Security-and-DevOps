package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import com.example.demo.exceptions.MyResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	private static final Logger log = LoggerFactory.getLogger(CartController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		log.info("CartController:addTocart execution started...");
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.info("CartController:addTocart Error - username: {} not found...", request.getUsername());
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			throw new MyResourceNotFoundException("Username: " + request.getUsername() + " not found");
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.info("CartController:addTocart Error - item id: {} not found...", request.getItemId());
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			throw new MyResourceNotFoundException("Item id: " + request.getItemId() + " not found");
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		log.info("CartController:addTocart execution ended...");
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		log.info("CartController:removeFromcart execution started...");
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			log.info("CartController:removeFromcart Error - username: {} not found...", request.getUsername());
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			throw new MyResourceNotFoundException("Username: " + request.getUsername() + " not found");
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			log.info("CartController:removeFromcart Error - item id: {} not found...", request.getItemId());
			//return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			throw new MyResourceNotFoundException("Item id: " + request.getItemId() + " not found");
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		log.info("CartController:removeFromcart execution ended...");
		return ResponseEntity.ok(cart);
	}
		
}
