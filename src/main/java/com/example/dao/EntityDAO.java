package com.example.dao;

import com.example.model.Entity;
import java.util.List;
import java.util.UUID;

public interface EntityDAO {
    void create(Entity entity);
    Entity read(UUID id);
    List<Entity> readAll(int page, int size);
    void update(Entity entity);
    void delete(UUID id);
}