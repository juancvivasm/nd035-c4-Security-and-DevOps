package com.example.demo.controllers;

import java.util.List;
import java.util.Optional;

import com.example.demo.exceptions.MyResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;

@RestController
@RequestMapping("/api/item")
public class ItemController {
	private static final Logger log = LoggerFactory.getLogger(ItemController.class);

	@Autowired
	private ItemRepository itemRepository;
	
	@GetMapping
	public ResponseEntity<List<Item>> getItems() {
		log.info("ItemController:getItems execution started...");
		return ResponseEntity.ok(itemRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		log.info("ItemController:getItemById execution started...");
		log.info("ItemController:getItemById id set with: {}", id);
		final Optional<Item> itemOptional = itemRepository.findById(id);
		if(!itemOptional.isPresent()){
			log.info("ItemController:getItemById Error - Item id: {} not found...", id);
			throw new MyResourceNotFoundException("Item id: " + id + " not found");
		}
		log.info("ItemController:getItemById execution ended...");
		return ResponseEntity.ok(itemOptional.get());
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
		log.info("ItemController:getItemsByName execution started...");
		log.info("ItemController:getItemsByName name set with: {}", name);
		List<Item> items = itemRepository.findByName(name);
		if(items == null || items.isEmpty()){
			log.info("ItemController:getItemsByName Items not found: {}...", name);
			throw new MyResourceNotFoundException("Items not found: " + name);
		}
		return ResponseEntity.ok(items);
	}
	
}
