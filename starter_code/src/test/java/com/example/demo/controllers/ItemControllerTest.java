package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository",itemRepo);

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

        when(itemRepo.findById(1L)).thenReturn(Optional.of(item2));
        when(itemRepo.findByName("Round Widget")).thenReturn(Collections.singletonList(item1));
        when(itemRepo.findAll()).thenReturn(List.of(item1,item2));

    }

    @Test
    public void get_items_happy_path() throws Exception{
        ResponseEntity<List<Item>> response= itemController.getItems();
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        List<Item> u = response.getBody();
        assertNotNull(u);
        assertEquals(2,u.size());
    }

    @Test
    public void get_items_by_id_happy_path() throws Exception{
        ResponseEntity<Item> response= itemController.getItemById(1L);
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        Item u = response.getBody();
        assertNotNull(u);
        assertEquals("A widget that is square",u.getDescription());
    }

    @Test
    public void get_items_by_name_happy_path() throws Exception{
        ResponseEntity<List<Item>> response= itemController.getItemsByName("Round Widget");
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        List<Item> u = response.getBody();
        assertNotNull(u);
        assertEquals(1,u.size());
        assertEquals("Round Widget",u.get(0).getName());

    }
}
