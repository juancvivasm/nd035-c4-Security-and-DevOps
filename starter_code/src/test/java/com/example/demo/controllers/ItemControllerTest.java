package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepo = mock(ItemRepository.class);

    private Item sampleItem01;

    private Item sampleItem02;

    private List<Item> itemList = new ArrayList<>();

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepo);

        sampleItem01 = new Item();
        sampleItem01.setId(1L);
        sampleItem01.setName("Item 01");
        sampleItem01.setPrice(BigDecimal.valueOf(1.99));
        sampleItem01.setDescription("Description item 01");
        itemList.add(sampleItem01);

        sampleItem02 = new Item();
        sampleItem02.setId(2L);
        sampleItem02.setName("Item 02");
        sampleItem02.setPrice(BigDecimal.valueOf(2.99));
        sampleItem02.setDescription("Description item 02");
        itemList.add(sampleItem02);
    }

    @Test
    public void get_items(){
        when(itemRepo.findAll()).thenReturn(itemList);

        final ResponseEntity<List<Item>> responseEntity = itemController.getItems();
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        final List<Item> items = responseEntity.getBody();
        assertTrue(items.size()==2);
    }

    @Test
    public void get_item_by_id(){
        when(itemRepo.findById(anyLong())).thenReturn(Optional.ofNullable(sampleItem01));

        final ResponseEntity<Item> responseEntity = itemController.getItemById(sampleItem01.getId());
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        final Item item = responseEntity.getBody();
        assertNotNull(item);
        assertEquals(sampleItem01.getId(), item.getId());
        assertEquals(sampleItem01.getName(), item.getName());
        assertEquals(sampleItem01.getPrice(), item.getPrice());
        assertEquals(sampleItem01.getDescription(), item.getDescription());
    }

    @Test
    public void get_items_by_name(){
        when(itemRepo.findByName(anyString())).thenReturn(itemList);

        final ResponseEntity<List<Item>> responseEntity = itemController.getItemsByName("Item 01");
        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCodeValue());
        final List<Item> items = responseEntity.getBody();
        assertTrue(items.size()==2);
    }
}
