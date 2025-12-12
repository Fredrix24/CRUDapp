package com.example.service;

import com.example.dao.EntityDAO;
import com.example.model.Entity;
import java.util.List;
import java.util.UUID;

public class EntityService {
    private EntityDAO entityDAO;

    public EntityService(EntityDAO entityDAO) {
        this.entityDAO = entityDAO;
    }

    public void createEntity(Entity entity) {
        entityDAO.create(entity);
    }

    public Entity readEntity(UUID id) {
        return entityDAO.read(id);
    }

    public List<Entity> readAllEntities(int page, int size) {
        return entityDAO.readAll(page, size);
    }

    public void updateEntity(Entity entity) {
        entityDAO.update(entity);
    }

    public void deleteEntity(UUID id) {
        entityDAO.delete(id);
    }
}