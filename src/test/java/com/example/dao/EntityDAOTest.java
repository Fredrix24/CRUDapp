package com.example.dao;

import com.example.model.Entity;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class EntityDAOTest {
    private EntityDAO entityDAO;

    @BeforeEach
    public void setUp() {
        entityDAO = new EntityDAOImpl();
    }

    @Test
    public void testCreateAndRead() {
        Entity entity = new Entity();
        entity.setId(UUID.randomUUID());
        entity.setName("Test Entity");
        entity.setDescription("This is a test entity.");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityDAO.create(entity);
        Entity retrievedEntity = entityDAO.read(entity.getId());

        assertNotNull(retrievedEntity);
        assertEquals(entity.getId(), retrievedEntity.getId());
        assertEquals(entity.getName(), retrievedEntity.getName());
        assertEquals(entity.getDescription(), retrievedEntity.getDescription());
    }

    @Test
    public void testReadAll() {
        List<Entity> entities = entityDAO.readAll(1, 10);
        assertNotNull(entities);
    }

    @Test
    public void testUpdate() {
        Entity entity = new Entity();
        entity.setId(UUID.randomUUID());
        entity.setName("Test Entity");
        entity.setDescription("This is a test entity.");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityDAO.create(entity);
        entity.setName("Updated Entity");
        entity.setUpdatedAt(LocalDateTime.now());
        entityDAO.update(entity);

        Entity retrievedEntity = entityDAO.read(entity.getId());
        assertEquals("Updated Entity", retrievedEntity.getName());
    }

    @Test
    public void testDelete() {
        Entity entity = new Entity();
        entity.setId(UUID.randomUUID());
        entity.setName("Test Entity");
        entity.setDescription("This is a test entity.");
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        entityDAO.create(entity);
        entityDAO.delete(entity.getId());

        Entity retrievedEntity = entityDAO.read(entity.getId());
        assertNull(retrievedEntity);
    }
}